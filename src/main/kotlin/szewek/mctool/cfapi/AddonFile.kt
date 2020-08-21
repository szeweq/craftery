package szewek.mctool.cfapi

import java.util.*

data class AddonFile(
    val id: Int,
    val fileName: String,
    val fileDate: Date,
    val fileLength: Int,
    val downloadUrl: String
)