package szewek.mctool.cfapi

class AddonSearch(
    val id: Int,
    val name: String,
    val summary: String,
    val websiteUrl: String,
    val slug: String,
    val downloadCount: Double,
    val popularityScore: Double,
    val gamePopularityRank: Long,
    val attachments: Array<AddonAttachment>,
    val categorySection: AddonSection,
    val latestFiles: Array<AddonFile>
)