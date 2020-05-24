package curseforge

import "time"

type (
	AddonSearch struct {
		ID              int    `json:"id"`
		Name            string `json:"name"`
		Slug            string `json:"slug"`
		CategorySection AddonSection
		LatestFiles     []AddonFile
	}
	AddonFile struct {
		ID          int `json:"id"`
		FileName    string
		FileDate    time.Time
		FileLength  int
		DownloadURL string `json:"downloadUrl"`
	}
	AddonSection struct {
		PackageType int
	}
)

var sizeTypes = []string{"B", "kB", "MB", "GB"}

func SizeBytes(s int) (float32, string) {
	var i int
	f := float32(s)
	for i = 0; i < len(sizeTypes) && f >= 1024; i, f = i+1, f/1024 {
	}
	return f, sizeTypes[i]
}

func GetLatest(aa []AddonFile) (af *AddonFile) {
	od := time.Unix(0, 0)
	for _, f := range aa {
		if od.Before(f.FileDate) {
			af, od = &f, f.FileDate
		}
	}
	return
}
