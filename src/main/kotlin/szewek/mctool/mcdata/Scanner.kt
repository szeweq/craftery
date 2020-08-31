package szewek.mctool.mcdata

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.toml.TomlParser
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import szewek.mctool.util.KtUtil
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
        input.eachEntry {
            if (!it.isDirectory) {
                si.scanFile(it.name, input)
            }
        }
        return si
    }

    class ScanInfo {
        val map = ClassNodeMap()
        private val caps by lazy {
            KtUtil.buildMap<String, CapabilitiesInfo> {
                for (c in map.nodes.values) {
                    val n = c.methods.find { m -> "getCapability" == m.name && TypeNames.GET_CAPABILITY == m.desc }
                    if (n != null) {
                        it[c.name] = CapabilitiesInfo(c.name, n.instructions)
                    }
                }
            }
        }
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
            map.nodes[cn.name] = cn
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
            return mutableSetOf<String>().apply {
                val c = caps[typename]
                if (c != null) {
                    this += c.fields
                    c.supclasses.mapNotNull { caps[it] }.forEach { this += it.fields }
                }
            }
        }

        fun streamStaticFields(): Stream<Pair<String, FieldNode>> = map.nodes.valueStream()
            .flatMap { c -> c.fields.stream().map { c.name to it } }
            .filter { (_, n) ->
                if (n.access and Opcodes.ACC_STATIC != 0 && n.desc != null) {
                    n.desc.let { it.startsWith('L') && !it.startsWith("java/") }
                } else false
            }

        fun streamCapabilities() = caps.valueStream()

        fun streamLazyOptionals() = map.nodes.valueStream()
            .map { c ->
                val f = c.fields.filter { it.desc == TypeNames.LAZY_OPTIONAL }
                if (f.isEmpty()) return@map null
                LazyOptionalInfo(map, c, f)
            }
            .filterNotNull()
            .filter { it.warnings.isNotEmpty() }
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

    class LazyOptionalInfo(classes: ClassNodeMap, classNode: ClassNode, fields: List<FieldNode>) {
        val name: String = classNode.name
        val warnings = fields.stream().filter { f ->
            !classes.streamUsagesOf(classNode, f).anyMatch { (_, _, i) ->
                if (i.opcode == Opcodes.GETFIELD) {
                    val ni = i.next
                    if (ni is MethodInsnNode
                            && ni.opcode == Opcodes.INVOKEVIRTUAL
                            && ni.owner == f.fixedDesc
                            && ni.name == "invalidate"
                    ) {
                        return@anyMatch true
                    }
                }
                false
            }
        }.map { it.name to it.signature.substringAfter('<').substringBeforeLast('>') }.toSet()
    }
}