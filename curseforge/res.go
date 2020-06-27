package curseforge

import "time"

type (
	AddonSearch struct {
		ID                 int               `json:"id"`
		Name               string            `json:"name"`
		Summary            string            `json:"summary"`
		WebsiteURL         string            `json:"websiteUrl"`
		Slug               string            `json:"slug"`
		DownloadCount      float64           `json:"downloadCount"`
		PopularityScore    float64           `json:"popularityScore"`
		GamePopularityRank int64             `json:"gamePopularityRank"`
		Attachments        []AddonAttachment `json:"attachments"`
		CategorySection    AddonSection
		LatestFiles        []AddonFile
	}
	AddonAttachment struct {
		IsDefault    bool   `json:"isDefault"`
		ThumbnailURL string `json:"thumbnailUrl"`
		URL          string `json:"url"`
	}
	AddonFile struct {
		ID          int `json:"id"`
		FileName    string
		FileDate    time.Time
		FileLength  int
		DownloadURL string `json:"downloadUrl"`
	}
	AddonSection struct {
		PackageType int `json:"packageType"`
	}
	ModpackManifest struct {
		Minecraft struct {
			Version string `json:"version"`
		} `json:"minecraft"`
		Name    string        `json:"name"`
		Version string        `json:"version"`
		Files   []ModpackFile `json:"files"`
	}
	ModpackFile struct {
		ProjectID int  `json:"projectID"`
		FileID    int  `json:"fileID"`
		Required  bool `json:"required"`
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
