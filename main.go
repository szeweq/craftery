package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"strconv"
	"strings"

	"github.com/Szewek/mctool/curseforge"
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
		as, e := curseforge.DefaultAPI.FindAddons(g.Param("name"), 6)
		if e != nil {
			g.Abort()
			return
		}
		g.JSON(200, as)
	})
	r.GET("/api/findpacks/:name", func(g *gin.Context) {
		as, e := curseforge.DefaultAPI.FindAddons(g.Param("name"), 4471)
		if e != nil {
			g.Abort()
			return
		}
		g.JSON(200, as)
	})
	r.GET("/api/addonuri/:addon/:file", func(g *gin.Context) {
		a, e := strconv.ParseUint(g.Param("addon"), 10, 64)
		if e != nil {
			g.Abort()
			return
		}
		f, e := strconv.ParseUint(g.Param("file"), 10, 64)
		if e != nil {
			g.Abort()
			return
		}
		s, e := curseforge.DefaultAPI.DownloadURL(uint(a), uint(f))
		if e != nil {
			g.Abort()
			return
		}
		g.JSON(200, s)
	})
	r.POST("/api/scanpack/manifest", func(g *gin.Context) {
		var b bytes.Buffer
		_, e := b.ReadFrom(g.Request.Body)
		if e != nil {
			g.Abort()
			return
		}
		g.Request.Body.Close()
		rc, e := downloadFile(b.String())
		if e == nil {
			zr, e := newZipReader(rc)
			if e == nil {
				rc.Close()
				for _, zf := range zr.File {
					if strings.Index(zf.Name, "manifest.json") >= 0 {
						xr, e := zf.Open()
						if e != nil {
							g.Abort()
							return
						}
						var man curseforge.ModpackManifest
						if e = json.NewDecoder(xr).Decode(&man); e != nil {
							g.Abort()
							return
						}
						g.JSON(200, man)
						return
					}
				}
			}
		}
		g.Abort()
	})
	r.POST("/api/scanmod/caps", modScanFieldsHandler(0x8, "/Capability;"))
	r.POST("/api/scanmod/regs", modScanFieldsHandler(0x8, "ForgeRegistry;"))
	e := r.Run("localhost:3000")
	if e != nil {
		panic(e)
	}
}

func modScanFieldsHandler(access uint16, substr string) func(*gin.Context) {
	return func(g *gin.Context) {
		var b bytes.Buffer
		_, e := b.ReadFrom(g.Request.Body)
		if e != nil {
			g.Abort()
			return
		}
		g.Request.Body.Close()
		rc, e := downloadFile(b.String())
		if e == nil {
			zr, e := newZipReader(rc)
			if e == nil {
				rc.Close()
				aa := scanForFields(zr, access, substr)
				g.JSON(200, aa)
				return
			}
		}
		g.Abort()
	}
}
