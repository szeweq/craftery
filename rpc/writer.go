package rpc

import (
	"context"

	"github.com/sourcegraph/jsonrpc2"
)

type (
	Writer struct {
		ctx  context.Context
		id   jsonrpc2.ID
		conn *jsonrpc2.Conn
	}
)

func (w *Writer) Reply(a interface{}) error {
	return w.conn.Reply(w.ctx, w.id, a)
}
func (w *Writer) Notify(method string, a interface{}) error {
	return w.conn.Notify(w.ctx, method, a)
}
