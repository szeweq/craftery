package szewek.mctool.mcdata

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.toml.TomlParser
import org.objectweb.asm.*
import szewek.mctool.util.FieldInfo
import java.io.InputStream
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
                val ci = ClassInfo(this, cr.className, cr.superName, cr.interfaces)
                classes[ci.name] = ci
                cr.accept(ci, 0)
            }
        }

        fun isCompatible(fi: FieldInfo, typename: String): Boolean {
            if (fi.type == typename) {
                return true
            }
            val ci = classes[fixFieldType(fi.type)]
            return if (ci != null) classExtendsFrom(ci, typename) else false
        }
        fun classExtendsFrom(ci: ClassInfo, typename: String): Boolean {
            var nci: ClassInfo = ci
            while(nci.ext != typename) {
                nci = classes[ci.ext] ?: return false
            }
            return true
        }
        fun getLastSuperClass(typename: String): String {
            var tn = typename
            do {
                val nci = classes[tn] ?: return tn
                if (nci.ext == "java/lang/Object" && nci.ext == "") {
                    return tn
                }
                tn = nci.ext
            } while (true)
        }
        fun getAllInterfaceTypes(typename: String): Set<String> {
            val l = mutableSetOf<String>()
            val q = ArrayDeque<String>()
            var tn = typename
            do {
                val nci = classes[tn] ?: return l
                if (nci.ext == "java/lang/Object" && nci.ext == "") {
                    return l
                }
                q += nci.impl
                while (q.isNotEmpty()) {
                    val iface = q.removeFirst()
                    if (iface !in l) {
                        l += iface
                    }
                    val ici = classes[iface]
                    if (ici != null) q += ici.impl
                }
                tn = nci.ext
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

    class ClassInfo(private val scan: ScanInfo, val name: String, val ext: String, val impl: Array<String>): ClassVisitor(Opcodes.ASM8) {
        val fields = mutableMapOf<String, FieldInfo>()

        override fun visitField(
            access: Int, name: String?, descriptor: String?, signature: String?, value: Any?
        ): FieldVisitor? {
            if (access and Opcodes.ACC_STATIC != 0 && descriptor?.startsWith('L') == true) {
                val typename = fixFieldType(descriptor)
                if (!typename.startsWith("java/")) {
                    val n = name ?: "<UNKNOWN ${fields.size}>"
                    val f = FieldInfo(n, typename, signature)
                    fields[n] = f
                }
            }
            return null
        }

        override fun visitMethod(
            access: Int, name: String?, descriptor: String?, signature: String?, exceptions: Array<out String>?
        ): MethodVisitor? {
            if ("getCapability" == name && "(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/Direction;)Lnet/minecraftforge/common/util/LazyOptional;" == descriptor) {
                val cap = CapabilitiesInfo(this.name)
                scan.caps[this.name] = cap
                return cap
            }
            return null
        }
    }

    class CapabilitiesInfo(val name: String): MethodVisitor(Opcodes.ASM8) {
        var supclasses = mutableSetOf<String>()
        var fields = mutableSetOf<String>()

        override fun visitMethodInsn(
            op: Int, owner: String?, name: String?, descriptor: String?, isInterface: Boolean
        ) {
            if ("getCapability" == name && this.name != owner) {
                supclasses.add(owner ?: "UNKNOWN")
            }
        }

        override fun visitFieldInsn(op: Int, owner: String?, name: String?, descriptor: String?) {
            if ("Lnet/minecraftforge/common/capabilities/Capability;" == descriptor) {
                fields.add("${owner ?: "UNKNOWN"}::${name ?: "UNKNOWN"}")
            }
        }
    }
}