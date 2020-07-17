package main

import (
	"flag"
	"fmt"
	"net/http"

	"github.com/pkg/browser"
	"github.com/rs/cors"
)

var localhostURL = "http://localhost:58091"

var (
	noOpen = flag.Bool("noui", false, "Runs an app without providing UI")
)

func main() {
	flag.Parse()
	fmt.Println("Launching MCTool...")

	initRPC()
	c := cors.New(cors.Options{
		// Allow port 8080 for testing
		AllowedOrigins: []string{localhostURL, "http://localhost:8080"},
	})
	http.Handle("/ws", rpch)
	http.HandleFunc("/mc-get/", mc.getAsset)
	http.HandleFunc("/mc-redirect/", mc.redirectAsset)

	if !*noOpen {
		// TEMPORARY SOLUTION!
		http.Handle("/", http.FileServer(http.Dir("web/dist")))

		go func() {
			fmt.Println("Opening ", localhostURL, "...")
			ie := browser.OpenURL(localhostURL)
			if ie != nil {
				panic(ie)
			}
		}()
	}

	if e := http.ListenAndServe(":58091", c.Handler(http.DefaultServeMux)); e != nil {
		panic(e)
	}
}
