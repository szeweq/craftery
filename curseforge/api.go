package curseforge

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"net/url"
)

type (
	API struct {
		c http.Client
	}
)

var DefaultAPI = API{}

const apiURL = "https://addons-ecs.forgesvc.net/api/v2/"

func (api *API) get(path string, a interface{}) error {
	r, e := api.c.Get(apiURL + path)
	if e != nil {
		return e
	}
	e = json.NewDecoder(r.Body).Decode(a)
	if e == nil {
		e = r.Body.Close()
	}
	return e
}

func (api *API) FindAddons(query string, typ uint) ([]AddonSearch, error) {
	x := new([]AddonSearch)
	s := fmt.Sprintf("addon/search?gameId=432&sectionId=%d&searchFilter=%s", typ, url.QueryEscape(query))
	e := api.get(s, x)
	return *x, e
}

func (api *API) DownloadURL(addon uint, file uint) (string, error) {
	r, e := api.c.Get(fmt.Sprintf("%saddon/%d/file/%d/download-url", apiURL, addon, file))
	if e == nil {
		b, e := ioutil.ReadAll(r.Body)
		if e == nil {
			e = r.Body.Close()
		}
		return string(b), e
	}
	return "", e
}
