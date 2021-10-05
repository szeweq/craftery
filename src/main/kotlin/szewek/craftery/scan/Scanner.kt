package szewek.craftery.scan

import com.electronwill.nightconfig.toml.TomlParser

object Scanner {
    val TOML = TomlParser()

    fun genericFromSignature(sig: String) = sig.substringAfter('<').substringBeforeLast('>')

    fun pathToLocation(path: String): String {
        val p = path.split("/", limit = 4)
        if (p.size < 4) {
            return p.last()
        }
        val (_, ns, _, v) = p
        return "$ns:$v"
    }

}