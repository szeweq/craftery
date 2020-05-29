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
	"sync"

	"github.com/Szewek/mctool/jclass"
)

func downloadFile(uri string) (rc io.ReadCloser, e error) {
	if _, e = url.ParseRequestURI(uri); e == nil {
		var res *http.Response
		res, e = http.Get(uri)
		if e != nil {
			return
		}
		if res.StatusCode == 200 {
			rc = res.Body
			return
		}
		e = fmt.Errorf("HTTP Error %d %s", res.StatusCode, res.Status)
	}
	return nil, e
}

func newZipReader(r io.Reader) (*zip.Reader, error) {
	bb, e := ioutil.ReadAll(r)
	if e != nil {
		return nil, e
	}
	return zip.NewReader(bytes.NewReader(bb), int64(len(bb)))
}

func scanForFields(zr *zip.Reader, access uint16, substr string) [][3]string {
	fc := make(chan *zip.File, 2)
	rc := make(chan [3]string, 2)
	aa := make([][3]string, 0, 8)
	var wg sync.WaitGroup
	wg.Add(2)
	for i := 0; i < 2; i++ {
		go scanFileFields(&wg, fc, rc, access, substr)
	}
	go func() {
		for _, f := range zr.File {
			if strings.HasSuffix(f.Name, ".class") {
				fc <- f
			}
		}
		close(fc)
	}()
	go func() {
		for x := range rc {
			aa = append(aa, x)
		}
	}()
	wg.Wait()
	close(rc)
	return aa
}

func scanFileFields(wg *sync.WaitGroup, fc <-chan *zip.File, rc chan<- [3]string, access uint16, substr string) {
	var b bytes.Buffer
	var br bytes.Reader
	var jc jclass.JavaClassScanner
	for f := range fc {
		fr, e := f.Open()
		if e != nil {
			continue
		}
		b.Reset()
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
				rc <- [3]string{f.Name, fl.Name, dtp}
			}
		}
	}
	wg.Done()
}
