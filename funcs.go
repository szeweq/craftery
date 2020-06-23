package main

import (
	"encoding/json"
	"strings"

	"github.com/Szewek/mctool/curseforge"
)

type (
	addonQuery struct {
		Name     string `json:"name"`
		Category uint   `json:"category"`
	}
	fileID struct {
		Addon uint `json:"addon"`
		File  uint `json:"file"`
	}
	urlQuery struct {
		URL string `json:"url"`
	}
	scanFieldsQuery struct {
		URI    string `json:"uri"`
		Access uint16 `json:"access"`
		Substr string `json:"substr"`
	}
)

func rpcFindAddons(rw *rpcWriter, d *addonQuery) error {
	as, e := curseforge.DefaultAPI.FindAddons(d.Name, d.Category)
	if e != nil {
		return e
	}
	return rw.Reply(as)
}
func rpcFileURI(rw *rpcWriter, d *fileID) error {
	s, e := curseforge.DefaultAPI.DownloadURL(d.Addon, d.File)
	if e != nil {
		return e
	}
	return rw.Reply(s)
}
func rpcZipManifest(rw *rpcWriter, d *urlQuery) error {
	zr, e := downloadZip(d.URL)
	if e == nil {
		for _, zf := range zr.File {
			if strings.HasSuffix(zf.Name, "manifest.json") {
				xr, e := zf.Open()
				if e != nil {
					return e
				}
				var man curseforge.ModpackManifest
				if e = json.NewDecoder(xr).Decode(&man); e != nil {
					return e
				}
				return rw.Reply(man)
			}
		}
	}
	return e
}
func rpcScanFields(rw *rpcWriter, d *scanFieldsQuery) error {
	zr, e := downloadZip(d.URI)
	if e == nil {
		aa := scanForFields(zr, d.Access, d.Substr)
		return rw.Reply(aa)
	}
	return e
}
