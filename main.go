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
	e := http.ListenAndServe(":3000", nil)
	if e != nil {
		panic(e)
	}
}

func appserve(w http.ResponseWriter, r *http.Request) {
	if e := appsite.ExecuteTemplate(w, "index.html", nil); e != nil {
		http.Error(w, e.Error(), http.StatusInternalServerError)
	}
}
