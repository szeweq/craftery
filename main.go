package main

import (
	"fmt"
	"html/template"
	"net/http"

	"github.com/pkg/browser"
)

var appsite = template.Must(template.ParseGlob("pages/*.html"))

func main() {
	fmt.Println("Launching MCTool...")
	go func() {
		fmt.Println("Opening http://localhost:3000 ...")
		ie := browser.OpenURL("http://localhost:3000")
		if ie != nil {
			panic(ie)
		}
	}()
	http.HandleFunc("/", appserve)
	http.HandleFunc("/favicon.ico", http.NotFound)
	e := http.ListenAndServe(":3000", nil)
	if e != nil {
		panic(e)
	}
}

func appserve(w http.ResponseWriter, r *http.Request) {
	path := r.URL.Path[1:]
	if path == "" {
		path = "index"
	}
	if e := appsite.ExecuteTemplate(w, "_pre.html", nil); e != nil {
		fmt.Printf("PRE Error: %s", e.Error())
		http.Error(w, e.Error(), http.StatusInternalServerError)
	}
	if e := appsite.ExecuteTemplate(w, path+".html", nil); e != nil {
		fmt.Printf("PAGE Error: %s", e.Error())
		http.Error(w, e.Error(), http.StatusInternalServerError)
	}
	if e := appsite.ExecuteTemplate(w, "_post.html", nil); e != nil {
		fmt.Printf("POST Error: %s", e.Error())
		http.Error(w, e.Error(), http.StatusInternalServerError)
	}
}
