package main

import (
	"context"
	"encoding/json"
	"net/http"
	"sync"

	ws "github.com/gorilla/websocket"
	"github.com/sourcegraph/jsonrpc2"
	wsrpc "github.com/sourcegraph/jsonrpc2/websocket"
)

type (
	mctHandler struct {
		m sync.Map
	}
	rpcHandleFunc func(rw *rpcWriter, param *json.RawMessage) error
	rpcWriter     struct {
		ctx  context.Context
		id   jsonrpc2.ID
		conn *jsonrpc2.Conn
	}
)

var mctrpc = &mctHandler{}
var upgrader = ws.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin:     func(r *http.Request) bool { return true },
}

func initrpc() {
	addHandler("findAddons", rpcFindAddons)
	addHandler("fileURI", rpcFileURI)
	addHandler("zipManifest", rpcZipManifest)
	addHandler("scanFields", rpcScanFields)
	addHandler("mcVersion", mc.rpcMCVersion)
}

func rpcconn(w http.ResponseWriter, r *http.Request) {
	conn, e := upgrader.Upgrade(w, r, nil)
	if e != nil {
		return
	}
	jsonrpc2.NewConn(context.Background(), wsrpc.NewObjectStream(conn), jsonrpc2.AsyncHandler(mctrpc))
}

func addHandler(name string, f rpcHandleFunc) {
	mctrpc.m.Store(name, f)
}
func (h *mctHandler) Handle(ctx context.Context, conn *jsonrpc2.Conn, r *jsonrpc2.Request) {
	if hi, ok := h.m.Load(r.Method); ok {
		rw := rpcWriter{ctx: ctx, id: r.ID, conn: conn}
		if e := hi.(rpcHandleFunc)(&rw, r.Params); e != nil {
			var je jsonrpc2.Error
			je.Code = jsonrpc2.CodeInternalError
			je.Message = "Internal error"
			je.SetError(e)
			_ = conn.ReplyWithError(ctx, r.ID, &je)
		}
	} else {
		je := jsonrpc2.Error{Code: jsonrpc2.CodeInvalidRequest, Message: "Invalid request"}
		je.SetError(r.Method)
		_ = conn.ReplyWithError(ctx, r.ID, &je)
	}
}

func (rw *rpcWriter) Reply(a interface{}) error {
	return rw.conn.Reply(rw.ctx, rw.id, a)
}
func (rw *rpcWriter) Notify(method string, a interface{}) error {
	return rw.conn.Notify(rw.ctx, method, a)
}
