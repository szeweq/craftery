package main

import (
	"context"
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
	ws "github.com/gorilla/websocket"
	"github.com/sourcegraph/jsonrpc2"
	wsrpc "github.com/sourcegraph/jsonrpc2/websocket"
)

type (
	mctHandler struct{}
)

var mctrpc *mctHandler
var upgrader = ws.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin:     func(r *http.Request) bool { return true },
}

func rpcconn(g *gin.Context) {
	conn, e := upgrader.Upgrade(g.Writer, g.Request, nil)
	if e != nil {
		g.Abort()
		return
	}
	jsonrpc2.NewConn(context.Background(), wsrpc.NewObjectStream(conn), jsonrpc2.AsyncHandler(mctrpc))
}

func (h *mctHandler) Handle(ctx context.Context, conn *jsonrpc2.Conn, r *jsonrpc2.Request) {
	if r.Method == "test" {
		fmt.Println("Client called test!")
		conn.Reply(ctx, r.ID, true)
	}
}
