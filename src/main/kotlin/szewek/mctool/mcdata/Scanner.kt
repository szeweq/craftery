package szewek.mctool.mcdata

import com.electronwill.nightconfig.toml.TomlParser
import javax.json.Json
import javax.json.JsonReaderFactory

object Scanner {
    val TOML = TomlParser()
    val JSON: JsonReaderFactory = Json.createReaderFactory(null)

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