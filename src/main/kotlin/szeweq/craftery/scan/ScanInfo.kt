package szeweq.craftery.scan

import com.electronwill.nightconfig.core.Config
import com.fasterxml.jackson.databind.node.ArrayNode
import kotlinx.coroutines.flow.*
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodInsnNode
import szeweq.craftery.mcdata.DataResourceType
import szeweq.craftery.mcdata.ResourceType
import szeweq.craftery.util.*
import szeweq.kt.KtUtil
import szeweq.kt.getList
import java.io.InputStream
import java.util.zip.ZipInputStream

class ScanInfo {
    val map = ClassNodeMap()
    private val caps by lazy {
        KtUtil.buildMap<String, CapabilitiesInfo> {
            for (c in map.classes) {
                val n = c.methods.find { m -> "getCapability" == m.name && TypeNames.GET_CAPABILITY == m.desc }
                if (n != null) {
                    it[c.name] = CapabilitiesInfo.from(c.name, n.instructions)
                }
            }
        }
    }
    val res = mutableMapOf<String, JsonInfo>()
    private val deps = mutableSetOf<String>()
    val tags = mutableMapOf<String, MutableSet<String>>()
    val parseExceptions = mutableMapOf<String, Exception>()

    suspend fun scanArchive(input: ZipInputStream) {
        input.entryStreamFlow().collect { (entry, stream) ->
            scanFile(entry.name, stream)
        }
    }

    private fun scanFile(name: String, data: InputStream) {
        when {
            name == "META-INF/mods.toml" -> {
                // Assuming this is a Forge mod
                scanModsTomlFile(name, data)
            }
            name.endsWith(".json") -> {
                if (!name.endsWith("sounds.json")) scanJsonFile(name, data)
            }
            name.endsWith(".class") -> {
                if (!(name.startsWith("kotlin") || name.startsWith("scala"))) scanClassFile(name, data)
            }
        }
    }

    private fun scanModsTomlFile(filename: String, data: InputStream) {
        try {
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
        } catch (e: Exception) {
            parseExceptions[filename + "META-INF/mods.toml"] = e
        }
    }

    private fun scanJsonFile(name: String, data: InputStream) {
        val path = name.split("/", limit = 3)
        if (path.size < 3) {
            return
        }
        val (kind, namespace, rest) = path
        try {
            val it = JsonUtil.mapper.readTree(data)
            val drt = DataResourceType.detect(kind, rest)
            if (drt.isTagType) {
                val loc = Scanner.pathToLocation(name)
                val ts = tags.getOrPut(loc) { HashSet() }
                val vs = it.withArray("values") as ArrayNode
                for (jv in vs) {
                    if (jv.isTextual) ts.add(jv.asText())
                }
            } else if (it != null) {
                val ji = JsonInfo(rest, namespace, drt)
                ji.gatherDetails(it)
                res[name] = ji
            }
        } catch (e: Exception) {
            parseExceptions[name] = e
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
                addAll(c.fields)
                for (sup in c.supclasses) {
                    val cs = caps[sup]
                    if (cs != null) {
                        addAll(cs.fields)
                    }
                }
            }
        }
    }

    fun flowStaticFields(): Flow<Pair<ClassNode, FieldNode>> = map.allClassFields
        .filter { (_, n) ->
            if ((n.access and Opcodes.ACC_STATIC) != 0 && n.desc != null && n.desc.startsWith('L')) {
                val d = n.desc.substring(1)
                !(d.startsWith("java") || d.startsWith("com/apache"))
            } else false
        }

    fun flowCapabilities() = caps.values.asFlow()

    fun flowLazyOptionals(): Flow<LazyOptionalInfo> = map.flowClasses.transform { cl ->
        val fl = cl.fields.filter { it.desc == TypeNames.LAZY_OPTIONAL }
        if (fl.isNotEmpty()) {
            val warnings = mutableMapOf<String, String>()
            fl.asFlow().filter { f ->
                map.flowUsagesOf(cl, f).firstOrNull { (_, _, i) ->
                    if (i.opcode == Opcodes.GETFIELD) {
                        val ni = i.next
                        ni is MethodInsnNode
                                && ni.opcode == Opcodes.INVOKEVIRTUAL
                                && ni.owner == f.fixedDesc
                                && ni.name == "invalidate"
                    }
                    false
                } == null
            }.map {
                val k = it.name.intern()
                k to (if (it.signature == null) "NONE" else Scanner.genericFromSignature(it.signature))
            }.collect { warnings[it.first] = it.second }
            if (warnings.isNotEmpty()) emit(LazyOptionalInfo(cl.name, warnings))
        }
    }
}