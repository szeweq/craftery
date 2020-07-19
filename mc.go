package main

import (
	"fmt"
	"net/http"
	"time"

	"github.com/Szewek/mctool/rpc"
)

type (
	mcrpc struct {
		manifest   mcManifest
		lastUpdate time.Time
		packages   map[string]*mcPackage
		assets     map[string]*mcAssetMap
	}
	mcManifest struct {
		Latest struct {
			Release  string `json:"release"`
			Snapshot string `json:"snapshot"`
		}
		Versions []struct {
			ID          string    `json:"id"`
			Type        string    `json:"type"`
			URL         string    `json:"url"`
			Time        time.Time `json:"time"`
			ReleaseTime time.Time `json:"releaseTime"`
		}
	}
	mcPackage struct {
		ID         string `json:"id"`
		AssetIndex struct {
			ID  string `json:"id"`
			URL string `json:"url"`
		} `json:"assetIndex"`
		Downloads struct {
			Client mcURL `json:"client"`
			Server mcURL `json:"server"`
		} `json:"downloads"`
	}
	mcFileCheck struct {
		Hash string `json:"hash"`
		Size int    `json:"size"`
	}
	mcURL struct {
		SHA1 string `json:"sha1"`
		Size int    `json:"size"`
		URL  string `json:"url"`
	}
	mcAssetMap struct {
		Objects map[string]mcFileCheck `json:"objects"`
		files   map[string][]byte
	}
	mcTypeQuery struct {
		Type string
	}
	mcPkgQuery struct {
		Name string
	}
)

const mcAssetURL = "https://resources.download.minecraft.net/%s/%s"

var mc mcrpc

func (m *mcrpc) updateManifest() error {
	if time.Since(m.lastUpdate) >= time.Hour {
		fmt.Println("Updating MC Manifest...")
		if m.packages == nil {
			m.packages = make(map[string]*mcPackage, 4)
		}
		if m.assets == nil {
			m.assets = make(map[string]*mcAssetMap, 4)
		}
		if e := downloadJSON("https://launchermeta.mojang.com/mc/game/version_manifest.json", &m.manifest); e != nil {
			return e
		}
		m.lastUpdate = time.Now()
	}
	return nil
}
func (m *mcrpc) getPackage(v string) (*mcPackage, error) {
	var e error
	if e = m.updateManifest(); e != nil {
		return nil, e
	}
	var mp *mcPackage
	var ok bool
	if mp, ok = m.packages[v]; !ok {
		var vu string
		for _, mv := range m.manifest.Versions {
			if v == mv.ID {
				vu = mv.URL
				break
			}
		}
		if vu == "" {
			return nil, fmt.Errorf("package not found: %s", v)
		}
		if e = downloadJSON(vu, &mp); e != nil {
			return nil, e
		}
		m.packages[v] = mp
	}
	return mp, nil
}
func (m *mcrpc) getAssetMap(v string) (*mcAssetMap, error) {
	var e error
	if e = m.updateManifest(); e != nil {
		return nil, e
	}
	var ma *mcAssetMap
	var ok bool
	var vi, vu string
	for _, mv := range m.manifest.Versions {
		if v == mv.ID {
			mp, e := m.getPackage(v)
			if e != nil {
				return nil, e
			}
			vi = mp.AssetIndex.ID
			vu = mp.AssetIndex.URL
			break
		}
	}
	if vu == "" {
		return nil, fmt.Errorf("package not found: %s", v)
	}
	if ma, ok = m.assets[vi]; !ok {
		if e = downloadJSON(vu, &ma); e != nil {
			return nil, e
		}
		ma.files = make(map[string][]byte, 4)
		m.assets[vi] = ma
	}
	return ma, nil
}
func (m *mcrpc) getAsset(w http.ResponseWriter, r *http.Request) {
	defer func() {
		if e := recover(); e != nil {
			http.Error(w, fmt.Sprintf("[!] Error: %s\n", e), http.StatusInternalServerError)
		}
	}()
	if e := m.updateManifest(); e != nil {
		panic(e)
	}
	ma, e := m.getAssetMap(m.manifest.Latest.Release)
	if e != nil {
		panic(e)
	}
	p := r.URL.Path[4:]
	bf, ok := ma.files[p]
	if !ok {
		mf, ok := ma.Objects[p]
		if !ok {
			http.NotFound(w, r)
			return
		}
		rc, n, e := downloadFile(fmt.Sprintf(mcAssetURL, mf.Hash[:2], mf.Hash))
		if e != nil {
			panic(e)
		}
		bf, e = readBytes(rc, n)
		_ = rc.Close()
		if e != nil {
			panic(e)
		}
		fmt.Printf("Received file %q has length %d\n", p, len(bf))
		ma.files[p] = bf
	}
	_, _ = w.Write(bf)
}
func (m *mcrpc) redirectAsset(w http.ResponseWriter, r *http.Request) {
	defer func() {
		if e := recover(); e != nil {
			http.Error(w, fmt.Sprintf("[!] Error: %s\n", e), http.StatusInternalServerError)
		}
	}()
	if e := m.updateManifest(); e != nil {
		panic(e)
	}
	ma, e := m.getAssetMap(m.manifest.Latest.Release)
	if e != nil {
		panic(e)
	}
	p := r.URL.Path[4:]
	mf, ok := ma.Objects[p]
	if !ok {
		http.NotFound(w, r)
		return
	}
	http.Redirect(w, r, fmt.Sprintf(mcAssetURL, mf.Hash[:2], mf.Hash), http.StatusFound)
}

func (m *mcrpc) rpcMCVersion(rw *rpc.Writer, d *mcTypeQuery) error {
	var vd string
	var e error
	if e = m.updateManifest(); e != nil {
		return e
	}
	if d.Type == "release" {
		vd = m.manifest.Latest.Release
	} else if d.Type == "snapshot" {
		vd = m.manifest.Latest.Snapshot
	}
	for _, mv := range m.manifest.Versions {
		if vd == mv.ID {
			return rw.Reply(mv)
		}
	}
	e = rw.Reply(nil)
	return e
}
func (m *mcrpc) rpcGetPackage(rw *rpc.Writer, d *mcPkgQuery) (e error) {
	if mp, e := m.getPackage(d.Name); e == nil {
		return rw.Reply(mp)
	}
	return e
}
