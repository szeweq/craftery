package szewek.craftery.cfapi

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

fun Array<out AddonAttachment>.default(): AddonAttachment? {
    for (a in this) {
        if (a.isDefault) {
            return a
        }
    }
    return null
}