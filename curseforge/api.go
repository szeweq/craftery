package curseforge

import (
	"encoding/json"
	"net/http"
	"net/url"
)

type (
	API struct {
		c http.Client
	}
)

var DefaultAPI = API{}

const apiurl = "https://addons-ecs.forgesvc.net/api/v2/"

func (api *API) get(path string, a interface{}) error {
	r, e := api.c.Get(apiurl + path)
	if e != nil {
		return e
	}
	e = json.NewDecoder(r.Body).Decode(a)
	r.Body.Close()
	return e
}

func (api *API) FindAddons(query string) ([]AddonSearch, error) {
	x := new([]AddonSearch)
	e := api.get("addon/search?gameId=432&sectionId=6&searchFilter="+url.QueryEscape(query), x)
	return *x, e
}
