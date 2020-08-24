package szewek.mctool.util

import org.objectweb.asm.Handle
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.util.*

class PrintMethodInfo(private val name: String): MethodVisitor(Opcodes.ASM8) {
    override fun visitFrame(
        type: Int, numLocal: Int, local: Array<out Any>?, numStack: Int, stack: Array<out Any>?
    ) {
        println(" |$name LOCAL = ${Arrays.toString(local)} STACK = ${Arrays.toString(stack)}")
    }

    override fun visitTypeInsn(op: Int, type: String?) {
        println(" |$name TYPE op $op, type [${type}]")
    }

    override fun visitFieldInsn(op: Int, owner: String?, name: String?, descriptor: String?) {
        println(" |${this.name} FIELD op $op, owner [${owner}], name [${name}], descriptor [${descriptor}]")
    }

    override fun visitMethodInsn(
        op: Int, owner: String?, name: String?, descriptor: String?, isInterface: Boolean
    ) {
        println(" |${this.name} METHOD op $op, owner [${owner}], name [${name}], descriptor [${descriptor}], isInterface $isInterface")
    }

    override fun visitInvokeDynamicInsn(
        name: String?, descriptor: String?, bootstrapMethodHandle: Handle?, vararg bootstrapMethodArguments: Any?
    ) {
        println(" |${this.name} INVOKE $name, descriptor [${descriptor}], bootstrapMethodHandle = [${bootstrapMethodHandle}], bootstrapMethodArguments = [${bootstrapMethodArguments}]")
    }
}