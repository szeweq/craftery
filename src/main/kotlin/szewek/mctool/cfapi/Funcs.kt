package szewek.mctool.cfapi

import java.util.*

fun Array<out AddonFile>.latest(): AddonFile? {
    var lf: AddonFile? = null
    var ld = Date(0)
    for (f in this) {
        if (ld.before(f.fileDate)) {
            lf = f
            ld = f.fileDate
        }
    }
    return lf
}