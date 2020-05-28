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

	"github.com/Szewek/mctool/jclass"
)

func downloadFile(uri string) (io.ReadCloser, error) {
	if _, e := url.ParseRequestURI(uri); e != nil {
		return nil, e
	}
	res, e := http.Get(uri)
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

func scanForFields(zr *zip.Reader, access uint16, substr string) [][3]string {
	var b bytes.Buffer
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
				if fl.Access&access != 0 && strings.Index(fl.Desc, substr) != -1 {
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
	return aa
}
