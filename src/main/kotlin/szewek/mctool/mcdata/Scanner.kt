package szewek.mctool.mcdata

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.toml.TomlParser
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import java.io.InputStream
import java.util.stream.Collectors
import java.util.zip.ZipInputStream
import javax.json.Json
import javax.json.JsonObject
import javax.json.JsonReaderFactory

object Scanner {
    val TOML = TomlParser()
    val JSON: JsonReaderFactory = Json.createReaderFactory(null)

    fun scanArchive(input: ZipInputStream): ScanInfo {
        val si = ScanInfo()
        var ze = input.nextEntry
        while (ze != null) {
            if (!ze.isDirectory) {
                si.scanFile(ze.name, input)
            }
            ze = input.nextEntry
        }
        return si
    }

    fun fixFieldType(s: String) = s.substring(1, s.lastIndex)

    class ScanInfo {
        val classNodes = mutableMapOf<String, ClassNode>()
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
            val cr = ClassReader(data)
            if (!cr.className.endsWith("/package-info")) {
                val cn = ClassNode()
                cr.accept(cn, 0)
                val ci = ClassInfo(cn)
                classes[cn.name] = ci
                classNodes[cn.name] = cn
                val cap = ci.gatherCaps()
                if (cap != null) caps[cn.name] = cap
            }
        }

        fun isCompatible(fn: FieldNode, typename: String): Boolean {
            val desc = fn.fixedDesc
            if (desc == typename) {
                return true
            }
            val ci = classes[desc]
            return if (ci != null) classExtendsFrom(ci, typename) else false
        }
        fun classExtendsFrom(ci: ClassInfo, typename: String): Boolean {
            var cn = ci.node
            var nci: ClassInfo = ci
            while(cn.superName != typename) {
                cn = classNodes[cn.superName] ?: return false
            }
            return true
        }
        fun getLastSuperClass(typename: String): String {
            var tn = typename
            do {
                val cn = classNodes[tn] ?: return tn
                if (cn.superName == null || cn.superName == "java/lang/Object") {
                    return tn
                }
                tn = cn.superName
            } while (true)
        }
        fun getAllInterfaceTypes(typename: String): Set<String> {
            val l = mutableSetOf<String>()
            val q = ArrayDeque<String>()
            var tn = typename
            do {
                val cn = classNodes[tn] ?: return l
                if (cn.superName == null || cn.superName == "java/lang/Object") {
                    return l
                }
                q += cn.interfaces
                while (q.isNotEmpty()) {
                    val iface = q.removeFirst()
                    if (iface !in l) {
                        l += iface
                    }
                    val icn = classNodes[iface]
                    if (icn != null) q += icn.interfaces
                }
                tn = cn.superName
            } while (true)
        }
        fun getResourceType(typename: String): ResourceType {
            val tn = getLastSuperClass(typename)
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
        val staticFields: Map<String, FieldNode> = node.fields.stream()
            .filter {
                if (it.access and Opcodes.ACC_STATIC != 0 && it.desc != null) {
                    it.desc.let { it.startsWith('L') && !it.startsWith("java/") }
                } else false
            }
            .collect(Collectors.toUnmodifiableMap({ it.name }, { it }))

        fun gatherCaps() = node.methodsByName("getCapability").find {
                "(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/Direction;)Lnet/minecraftforge/common/util/LazyOptional;" == it.desc
            }?.let { CapabilitiesInfo(node.name, it) }
    }

    class CapabilitiesInfo(val name: String, methodNode: MethodNode) {
        var supclasses: Set<String> = methodNode.instructions.stream()
            .filterIsInstance<MethodInsnNode>()
            .filter { "getCapability" == it.name && name != it.owner }
            .map { it.owner ?: "UNKNOWN" }
            .toSet()
        var fields = methodNode.instructions.stream()
            .filterIsInstance<FieldInsnNode>().filter { TypeNames.CAPABILITY == it.desc }
            .map { "${it.owner ?: "UNKNOWN"}::${it.name ?: "UNKNOWN"}" }
            .toSet()
    }
}