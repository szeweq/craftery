package main

import (
	"github.com/Szewek/mctool/rpc"
)

var rpch = rpc.NewHandler()

func initRPC() {
	rpch.Add("findAddons", rpcFindAddons)
	rpch.Add("fileURI", rpcFileURI)
	rpch.Add("zipManifest", rpcZipManifest)
	rpch.Add("scanFields", rpcScanFields)
	rpch.Add("mcVersion", mc.rpcMCVersion)
	rpch.Add("getPackage", mc.rpcGetPackage)
}
