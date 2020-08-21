package szewek.mctool.util

import org.objectweb.asm.*

object Scanner {

    fun scanClass(name: String, data: ByteArray): List<FieldInfo> {
        val fc = FieldCollector(name)
        ClassReader(data).accept(fc, 0)
        return fc.fields
    }

    fun fixType(s: String) = s.substring(1, s.lastIndex)
        .replace('/', '.')
        .replace("<L", "<")
        .replace(";L", ", ").replace(";>", ">")

    data class FieldInfo(val name: String, val type: String, val container: String)

    class FieldCollector(private val className: String): ClassVisitor(Opcodes.ASM8) {
        val fields = mutableListOf<FieldInfo>()

        override fun visitField(access: Int, name: String?, descriptor: String?, signature: String?, value: Any?): FieldVisitor? {
            if (access and Opcodes.ACC_STATIC != 0 && descriptor?.startsWith('L') == true) {
                val f = FieldInfo(name ?: "<UNKNOWN>", fixType(signature ?: descriptor), className)
                fields += f
            }
            return null
        }
    }
}