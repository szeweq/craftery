@file:JvmName("Launcher")
package szewek.mctool

import szewek.mctool.app.MCToolApp
import tornadofx.launch

fun main(args: Array<String>) {
    launch<MCToolApp>(args)
}