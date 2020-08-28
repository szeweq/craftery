package szewek.mctool.mcdata

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.toml.TomlParser
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import java.io.InputStream
import java.util.stream.Stream
import java.util.zip.ZipInputStream
import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonReaderFactory

object Scanner {
    val TOML = TomlParser()
    val JSON: JsonReaderFactory = Json.createReaderFactory(null)

    fun scanArchive(input: ZipInputStream): ScanInfo {
        val si = ScanInfo()
        input.available()
        var ze = input.nextEntry
        while (ze != null) {
            if (!ze.isDirectory) {
                si.scanFile(ze.name, input)
            }
            ze = input.nextEntry
        }
        return si
    }

    class ScanInfo {
        val map = ClassNodeMap()
        val classes = mutableMapOf<String, ClassInfo>()
        val caps = mutableMapOf<String, CapabilitiesInfo>()
        val res = mutableMapOf<String, JsonInfo>()
        val deps = mutableSetOf<String>()

        fun scanFile(name: String, data: InputStream) {
            when {
                name == "META-INF/mods.toml" -> {
                    // Assuming this is a Forge mod
                    val cfg = TOML.parse(data)
                    val mods = cfg.getList<Config?>("mods")
                    for (m in mods) {
                        if (m == null) continue
                        val modId = m.get<String>("modId") ?: continue
                        val modDeps = m.getList<Config?>(listOf("dependencies", modId))
                        for (md in modDeps) {
                            if (md == null) continue
                            val s = md.get<String>("modId") ?: continue
                            if (s != "forge" && s != "minecraft") deps += s
                        }
                    }
                }
                name.endsWith(".json") -> {
                    scanJsonFile(name, data)
                }
                name.endsWith(".class") -> {
                    scanClassFile(name, data)
                }
            }
        }

        private fun scanJsonFile(name: String, data: InputStream) {
            val path = name.split("/", limit = 3)
            if (path.size < 3) {
                return
            }
            val (kind, namespace, rest) = path
            val jr = JSON.createReader(data)
            runCatching { jr.readObject() }.onSuccess {
                val drt = DataResourceType.detect(kind, rest)
                val ji = JsonInfo(rest, namespace, drt)
                ji.gatherDetails(it)
                res[name] = ji
            }
        }

        private fun scanClassFile(name: String, data: InputStream) {
            if (name.endsWith("/package-info.class")) return
            val cn = ClassNode()
            ClassReader(data).accept(cn, 0)
            val ci = ClassInfo(cn)
            classes[cn.name] = ci
            map.nodes[cn.name] = cn
            val cap = ci.gatherCaps()
            if (cap != null) caps[cn.name] = cap
        }

        fun getResourceType(typename: String): ResourceType {
            val tn = map.getLastSuperClass(typename)
            for ((src, rts) in ResourceType.bySource) {
                if (tn.startsWith(src.pkg)) {
                    return rts.find { tn.substring(src.pkg.length) == it.type } ?: ResourceType.UNKNOWN
                }
            }
            return ResourceType.UNKNOWN
        }
        fun getAllCapsFromType(typename: String): Set<String> {
            val l = mutableSetOf<String>()
            val c = caps[typename]
            if (c != null) {
                l += c.fields
                c.supclasses.mapNotNull { caps[it] }.forEach { l += it.fields }
            }
            return l.toSet()
        }

        fun streamStaticFields(): Stream<Pair<String, FieldNode>> = map.nodes.values.stream()
            .flatMap { c -> c.fields.stream().map { c.name to it } }
            .filter { (_, n) ->
                if (n.access and Opcodes.ACC_STATIC != 0 && n.desc != null) {
                    n.desc.let { it.startsWith('L') && !it.startsWith("java/") }
                } else false
            }

        fun streamCapabilities() = map.nodes.values.stream()
            .map { c ->
                val n = c.methods.find { "getCapability" == it.name && TypeNames.GET_CAPABILITY == it.desc }
                if (n == null) null else CapabilitiesInfo(c.name, n.instructions)
            }.filterNotNull()
    }

    class JsonInfo(val name: String, val namespace: String, val type: DataResourceType) {
        val details = mutableMapOf<String, String>()

        fun gatherDetails(obj: JsonObject) {
            when (type) {
                DataResourceType.RECIPE -> {
                    obj.getString("type", null)?.apply { details["Type"] = this }
                }
                else -> {}
            }
        }
    }

    class ClassInfo(val node: ClassNode) {

        fun gatherCaps() = node.methodsByName("getCapability").find {
                "(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/Direction;)Lnet/minecraftforge/common/util/LazyOptional;" == it.desc
            }?.let { CapabilitiesInfo(node.name, it.instructions) }
    }

    class CapabilitiesInfo(val name: String, instructions: InsnList) {
        var supclasses: Set<String> = instructions.stream()
            .filterIsInstance<MethodInsnNode>()
            .filter { "getCapability" == it.name && name != it.owner }
            .map { it.owner ?: "UNKNOWN" }
            .toSet()
        var fields = instructions.stream()
            .filterIsInstance<FieldInsnNode>().filter { TypeNames.CAPABILITY == it.desc }
            .map { "${it.owner ?: "UNKNOWN"}::${it.name ?: "UNKNOWN"}" }
            .toSet()
    }
}