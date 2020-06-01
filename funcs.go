package main

import (
	"encoding/json"
	"strings"

	"github.com/Szewek/mctool/curseforge"
)

func rpcFindAddons(rw *rpcWriter, param *json.RawMessage) error {
	var d struct {
		Name     string `json:"name"`
		Category uint   `json:"category"`
	}
	var e error
	if e = json.Unmarshal(*param, &d); e != nil {
		return e
	}
	as, e := curseforge.DefaultAPI.FindAddons(d.Name, d.Category)
	if e != nil {
		return e
	}
	return rw.Reply(as)
}
func rpcFileURI(rw *rpcWriter, param *json.RawMessage) error {
	var d struct {
		Addon uint `json:"addon"`
		File  uint `json:"file"`
	}
	var e error
	if e = json.Unmarshal(*param, &d); e != nil {
		return e
	}
	s, e := curseforge.DefaultAPI.DownloadURL(d.Addon, d.File)
	if e != nil {
		return e
	}
	return rw.Reply(s)
}
func rpcZipManifest(rw *rpcWriter, param *json.RawMessage) error {
	var d string
	var e error
	if e = json.Unmarshal(*param, &d); e != nil {
		return e
	}
	rc, e := downloadFile(d)
	if e == nil {
		zr, e := newZipReader(rc)
		if e == nil {
			rc.Close()
			for _, zf := range zr.File {
				if strings.Index(zf.Name, "manifest.json") >= 0 {
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
	}
	return e
}
func rpcScanFields(rw *rpcWriter, param *json.RawMessage) error {
	var d struct {
		URI    string `json:"uri"`
		Access uint16 `json:"access"`
		Substr string `json:"substr"`
	}
	var e error
	if e = json.Unmarshal(*param, &d); e != nil {
		return e
	}
	rc, e := downloadFile(d.URI)
	if e == nil {
		zr, e := newZipReader(rc)
		if e == nil {
			rc.Close()
			aa := scanForFields(zr, d.Access, d.Substr)
			return rw.Reply(aa)
		}
	}
	return e
}
