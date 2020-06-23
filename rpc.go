package main

import (
	"context"
	"encoding/json"
	"fmt"
	"net/http"
	"reflect"
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

var (
	mctrpc   = &mctHandler{}
	upgrader = ws.Upgrader{
		ReadBufferSize:  1024,
		WriteBufferSize: 1024,
		CheckOrigin:     func(r *http.Request) bool { return true },
	}
	writerType = reflect.TypeOf((*rpcWriter)(nil))
	errType    = reflect.TypeOf((*error)(nil)).Elem()
)

func initrpc() {
	addHandler("findAddons", rpcFindAddons)
	addHandler("fileURI", rpcFileURI)
	addHandler("zipManifest", rpcZipManifest)
	addHandler("scanFields", rpcScanFields)
	addHandler("mcVersion", mc.rpcMCVersion)
	addHandler("getPackage", mc.rpcGetPackage)
}

func rpcconn(w http.ResponseWriter, r *http.Request) {
	conn, e := upgrader.Upgrade(w, r, nil)
	if e != nil {
		return
	}
	jsonrpc2.NewConn(context.Background(), wsrpc.NewObjectStream(conn), jsonrpc2.AsyncHandler(mctrpc))
}

func addHandlerReflect(fn interface{}) rpcHandleFunc {
	rt := reflect.TypeOf(fn)
	if rt.Kind() == reflect.Func {
		if rt.NumIn() == 2 && rt.NumOut() == 1 {
			if rt.In(0).AssignableTo(writerType) && rt.Out(0).Implements(errType) && rt.In(1).Kind() == reflect.Ptr {
				dt := rt.In(1).Elem()
				rv := reflect.ValueOf(fn)
				return func(rw *rpcWriter, param *json.RawMessage) (e error) {
					dv := reflect.New(dt)
					if e = json.Unmarshal(*param, dv.Interface()); e == nil {
						ev := rv.Call([]reflect.Value{reflect.ValueOf(rw), dv})[0]
						if !ev.IsNil() {
							e = ev.Interface().(error)
						}
					}
					return
				}

			}
		}
	}
	return nil
}

func addHandler(name string, f interface{}) {
	var fn rpcHandleFunc
	fn, ok := f.(func(rw *rpcWriter, param *json.RawMessage) error)
	if !ok {
		fn = addHandlerReflect(f)
	}
	if fn != nil {
		mctrpc.m.Store(name, fn)
	} else {
		fmt.Printf("Function %s cannot be added to RPC call functions\n", name)
	}
}
func (h *mctHandler) Handle(ctx context.Context, conn *jsonrpc2.Conn, r *jsonrpc2.Request) {
	if hi, ok := h.m.Load(r.Method); ok {
		rw := rpcWriter{ctx: ctx, id: r.ID, conn: conn}
		if e := hi.(rpcHandleFunc)(&rw, r.Params); e != nil {
			fmt.Printf("[error] %s: %s", r.Method, e.Error())
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
