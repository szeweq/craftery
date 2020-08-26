package szewek.mctool.util

import org.objectweb.asm.*
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream

object Scanner {
    fun scanArchive(input: ZipInputStream): ScanInfo {
        val si = ScanInfo()
        var ze = input.nextEntry;
        val out = ByteArrayOutputStream()
        while (ze != null) {
            if (!ze.isDirectory) {
                out.reset()
                input.copyTo(out)
                si.scanFile(ze.name, out.toByteArray())
            }
            ze = input.nextEntry
        }
        return si
    }

    fun fixFieldType(s: String) = s.substring(1, s.lastIndex)

    fun fixType(s: String) = s.substring(1, s.lastIndex)
        .replace('/', '.')
        .replace("<L", "<")
        .replace(";L", ", ").replace(";>", ">")

    class ScanInfo {
        val classes = mutableMapOf<String, ClassInfo>()
        val caps = mutableMapOf<String, CapabilitiesInfo>()

        fun scanFile(name: String, data: ByteArray) {
            if (name.endsWith(".class")) {
                val cr = ClassReader(data)
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
        fun getResourceType(typename: String): ResourceType? {
            val tn = getLastSuperClass(typename)
            for (rt in ResourceType.values()) {
                if (tn == rt.typ) {
                    return rt
                }
            }
            return null
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