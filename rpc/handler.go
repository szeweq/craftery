package rpc

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
	Handler struct {
		wsup *ws.Upgrader
		m    sync.Map
	}
	HandleFunc = func(*Writer, *json.RawMessage) error
)

var (
	writerType = reflect.TypeOf((*Writer)(nil))
	errType    = reflect.TypeOf((*error)(nil)).Elem()
)

func NewHandler() *Handler {
	return &Handler{
		wsup: &ws.Upgrader{
			ReadBufferSize:  1024,
			WriteBufferSize: 1024,
			CheckOrigin:     func(r *http.Request) bool { return true },
		},
	}
}

func (h *Handler) Add(name string, f interface{}) {
	var fn HandleFunc
	fn, ok := f.(func(rw *Writer, param *json.RawMessage) error)
	if !ok {
		fn = addHandlerReflect(f)
	}
	if fn != nil {
		h.m.Store(name, fn)
	} else {
		fmt.Printf("Function %s cannot be added to RPC call functions\n", name)
	}
}

func (h *Handler) Handle(ctx context.Context, conn *jsonrpc2.Conn, r *jsonrpc2.Request) {
	rw := Writer{ctx: ctx, id: r.ID, conn: conn}
	if hi, ok := h.m.Load(r.Method); ok {
		if e := hi.(HandleFunc)(&rw, r.Params); e != nil {
			fmt.Printf("[error] %s: %s", r.Method, e.Error())
			rw.SendError(jsonrpc2.CodeInternalError, "Internal error", e)
		}
	} else {
		rw.SendError(jsonrpc2.CodeInvalidRequest, "Invalid request", r.Method)
	}
}

func (h *Handler) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	conn, e := h.wsup.Upgrade(w, r, nil)
	if e != nil {
		return
	}
	jsonrpc2.NewConn(context.Background(), wsrpc.NewObjectStream(conn), jsonrpc2.AsyncHandler(h))
}

func addHandlerReflect(fn interface{}) HandleFunc {
	rt := reflect.TypeOf(fn)
	if rt.Kind() == reflect.Func && rt.NumIn() == 2 && rt.NumOut() == 1 && rt.In(0).AssignableTo(writerType) && rt.Out(0).Implements(errType) && rt.In(1).Kind() == reflect.Ptr {
		dt := rt.In(1).Elem()
		rv := reflect.ValueOf(fn)
		return func(rw *Writer, param *json.RawMessage) (e error) {
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
	return nil
}
