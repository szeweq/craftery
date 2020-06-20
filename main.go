package main

import (
	"fmt"
	"net/http"

	"github.com/pkg/browser"
	"github.com/rs/cors"
)

var localhostURL = "http://localhost:58091"

func main() {
	fmt.Println("Launching MCTool...")
	go func() {
		fmt.Println("Opening http://localhost:58091 ...")
		ie := browser.OpenURL(localhostURL)
		if ie != nil {
			panic(ie)
		}
	}()
	initrpc()
	c := cors.New(cors.Options{
		// Allow port 8080 for testing
		AllowedOrigins: []string{localhostURL, "http://localhost:8080"},
	})
	http.HandleFunc("/ws", rpcconn)
	http.HandleFunc("/mc-get/", mc.getAsset)
	http.HandleFunc("/mc-redirect/", mc.redirectAsset)

	// TEMPORARY SOLUTION!
	http.Handle("/", http.FileServer(http.Dir("web/dist")))

	if e := http.ListenAndServe(":58091", c.Handler(http.DefaultServeMux)); e != nil {
		panic(e)
	}
}
