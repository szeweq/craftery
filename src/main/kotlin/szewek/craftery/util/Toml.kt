package szewek.craftery.util

import com.electronwill.nightconfig.core.Config

inline fun <reified T> Config.getList(path: String): List<T> = getOrElse(path, emptyList())
inline fun <reified T> Config.getList(path: List<String>): List<T> = getOrElse(path, emptyList())