package main

import (
	"archive/zip"
	"bytes"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"net/url"
	"strings"

	"github.com/Szewek/mctool/curseforge"
	"github.com/Szewek/mctool/jclass"
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/pkg/browser"
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
	r := gin.Default()
	corscfg := cors.DefaultConfig()
	corscfg.AllowOrigins = []string{"http://localhost:8080"}
	r.Use(cors.New(corscfg))
	//r.Static("/", "/web/dist")
	r.GET("/api/findmods/:name", func(g *gin.Context) {
		as, e := curseforge.DefaultAPI.FindAddons(g.Param("name"))
		if e != nil {
			g.Abort()
			return
		}
		g.JSON(200, as)
	})
	r.POST("/api/scanmod", func(g *gin.Context) {
		var b bytes.Buffer
		_, e := b.ReadFrom(g.Request.Body)
		if e != nil {
			g.Abort()
			return
		}
		g.Request.Body.Close()
		s := b.String()
		if _, e = url.ParseRequestURI(s); e != nil {
			g.Abort()
			return
		}
		rc, e := downloadFile(s)
		if e != nil {
			g.Abort()
			return
		}
		zr, e := newZipReader(rc)
		if e != nil {
			g.Abort()
			return
		}
		rc.Close()
		var br bytes.Reader
		var jc jclass.JavaClassScanner
		aa := make([][3]string, 0, 8)
		for _, f := range zr.File {
			if strings.LastIndex(f.Name, ".class") != -1 {
				b.Reset()
				fr, e := f.Open()
				if e != nil {
					continue
				}
				b.ReadFrom(fr)
				fr.Close()
				br.Reset(b.Bytes())
				jc.Reset(&br)
				if e = jc.Scan(); e != nil {
					continue
				}
				for _, fl := range jc.Fields {
					if fl.Access&0x8 != 0 && strings.Index(fl.Desc, "/Capability;") != -1 {
						var dtp string
						if mk, ok := fl.Attrs["Signature"]; ok {
							dtp = mk[strings.IndexByte(mk, '<')+1 : strings.LastIndexByte(mk, '>')]
						} else {
							dtp = "<INVALID>"
						}
						aa = append(aa, [3]string{f.Name, fl.Name, dtp})
					}
				}
			}
		}
		g.JSON(200, aa)
	})
	e := r.Run("localhost:3000")
	if e != nil {
		panic(e)
	}
}

func downloadFile(url string) (io.ReadCloser, error) {
	res, e := http.Get(url)
	if e != nil {
		return nil, e
	}
	if res.StatusCode != 200 {
		return nil, fmt.Errorf("HTTP Error %d %s", res.StatusCode, res.Status)
	}
	return res.Body, nil
}

func newZipReader(r io.Reader) (*zip.Reader, error) {
	bb, e := ioutil.ReadAll(r)
	if e != nil {
		return nil, e
	}
	rr := bytes.NewReader(bb)
	return zip.NewReader(rr, int64(rr.Len()))
}
