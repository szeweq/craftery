package szewek.craftery.cfapi

import java.util.*

class AddonFile(
    val id: Int,
    val fileName: String,
    val fileDate: Date,
    val fileLength: Int,
    val downloadUrl: String,
    val dependencies: Array<AddonDependency>,
    val gameVersion: Array<String>
)