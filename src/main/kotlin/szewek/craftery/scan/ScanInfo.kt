package szewek.craftery.scan

import com.electronwill.nightconfig.core.Config
import com.fasterxml.jackson.databind.node.ArrayNode
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import szewek.craftery.mcdata.DataResourceType
import szewek.craftery.mcdata.ResourceType
import szewek.craftery.util.*
import java.io.InputStream
import java.util.stream.Stream
import java.util.stream.StreamSupport
import java.util.zip.ZipInputStream

class ScanInfo {
    val map = ClassNodeMap()
    private val caps by lazy {
        KtUtil.buildMap<String, CapabilitiesInfo> {
            for (c in map.classes) {
                val n = c.methods.find { m -> "getCapability" == m.name && TypeNames.GET_CAPABILITY == m.desc }
                if (n != null) {
                    it[c.name] = CapabilitiesInfo(c.name, n.instructions)
                }
            }
        }
    }
    val res = mutableMapOf<String, JsonInfo>()
    private val deps = mutableSetOf<String>()
    val tags = mutableMapOf<String, MutableSet<String>>()

    fun scanArchive(input: ZipInputStream) {
        input.eachEntry {
            if (!it.isDirectory) {
                scanFile(it.name, input)
            }
        }
    }

    private fun scanFile(name: String, data: InputStream) {
        when {
            name == "META-INF/mods.toml" -> {
                // Assuming this is a Forge mod
                scanModsTomlFile(data)
            }
            name.endsWith(".json") -> {
                if (!name.endsWith("sounds.json")) scanJsonFile(name, data)
            }
            name.endsWith(".class") -> {
                if (!(name.startsWith("kotlin") || name.startsWith("scala"))) scanClassFile(name, data)
            }
        }
    }

    private fun scanModsTomlFile(data: InputStream) {
        val cfg = Scanner.TOML.parse(data)
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

    private fun scanJsonFile(name: String, data: InputStream) {
        val path = name.split("/", limit = 3)
        if (path.size < 3) {
            return
        }
        val (kind, namespace, rest) = path
        runCatching { JsonUtil.mapper.readTree(data) }.onSuccess {
            val drt = DataResourceType.detect(kind, rest)
            if (drt.isTagType) {
                val loc = Scanner.pathToLocation(name)
                val cs = StreamSupport.stream((it.withArray("values") as ArrayNode).spliterator(), false)
                    .map { jv -> if (jv.isTextual) jv.asText() else null }
                    .filterNotNull()
                val ts = tags[loc]
                if (ts == null) {
                    tags[loc] = cs.toMutableSet()
                } else {
                    cs.forEach(ts::add)
                }
            } else if (it != null) {
                val ji = JsonInfo(rest, namespace, drt)
                ji.gatherDetails(it)
                res[name] = ji
            }
        }
    }

    private fun scanClassFile(name: String, data: InputStream) {
        if (name.endsWith("/package-info.class")) return
        val cn = ClassNode()
        ClassReader(data).accept(cn, 0)
        map.add(cn)
    }

    fun getResourceType(typename: String): ResourceType {
        val tn = map.getLastSuperClass(typename)
        for ((src, rts) in ResourceType.bySource) {
            if (tn.startsWith(src.pkg)) {
                return rts.find { it.isCompatible(tn) } ?: ResourceType.UNKNOWN
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

    fun streamStaticFields(): Stream<Pair<ClassNode, FieldNode>> = map.allClassFields
        .filter { (_, n) ->
            if (n.access and Opcodes.ACC_STATIC != 0 && n.desc != null) {
                n.desc.let { it.startsWith('L') && !it.startsWith("java/") }
            } else false
        }

    fun streamCapabilities() = caps.valueStream()

    fun streamLazyOptionals(): Stream<LazyOptionalInfo> = map.classStream
        .map { c ->
            val f = c.fields.filter { it.desc == TypeNames.LAZY_OPTIONAL }
            if (f.isEmpty()) return@map null
            LazyOptionalInfo(map, c, f)
        }
        .filterNotNull()
        .filter { it.warnings.isNotEmpty() }
}