package main

import (
	"fmt"
	"net/http"

	"github.com/pkg/browser"
	"github.com/rs/cors"
)

func main() {
	fmt.Println("Launching MCTool...")
	go func() {
		fmt.Println("Opening http://localhost:3000 ...")
		ie := browser.OpenURL("http://localhost:3000")
		if ie != nil {
			panic(ie)
		}
	}()
	initrpc()
	c := cors.New(cors.Options{
		AllowedOrigins: []string{"http://localhost:8080"},
	})
	http.HandleFunc("/ws", rpcconn)
	if e := http.ListenAndServe(":3000", c.Handler(http.DefaultServeMux)); e != nil {
		panic(e)
	}
}
