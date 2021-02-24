package szewek.craftery.cfapi

class AddonSearch(
    val id: Int,
    val name: String,
    val authors: Array<AddonAuthor>,
    val summary: String,
    val websiteUrl: String,
    val slug: String,
    val downloadCount: Long,
    val popularityScore: Double,
    val gamePopularityRank: Long,
    val attachments: Array<AddonAttachment>,
    val categorySection: AddonSection,
    val latestFiles: Array<AddonFile>
)