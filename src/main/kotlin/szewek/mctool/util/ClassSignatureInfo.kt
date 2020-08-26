package szewek.mctool.util

import org.objectweb.asm.Opcodes
import org.objectweb.asm.signature.SignatureVisitor

class ClassSignatureInfo: SignatureVisitor(Opcodes.ASM8) {
    private val bounds = mutableMapOf<String, String>()
    private var state = "";
    private var last = mutableListOf<String>()
    override fun visitFormalTypeParameter(name: String) {
        println("STATE [$state] TPARAM [$name]")
        state = "tp"
        last.add(name)
    }

    override fun visitClassBound(): SignatureVisitor {
        println("STATE [$state] CLASS BOUND")
        return this
    }

    override fun visitInterfaceBound(): SignatureVisitor {
        println("STATE [$state] IFACE BOUND")
        return this
    }

    override fun visitTypeVariable(name: String?) {
        println("STATE [$state] TVAR [$name]")
    }

    override fun visitInnerClassType(name: String?) {
        println("STATE [$state] INNER CLASS [$name]")
    }

    override fun visitClassType(name: String) {
        println("STATE [$state] CLASS [$name]")
        last.add(name)
        state = when (state) {
            "" -> "c"
            "c" -> "tc"
            else -> return
        }
    }

    override fun visitEnd() {
        println("STATE [$state] END = ${last.joinToString()}")
        if (state == "tp") {
            val (l, c) = last
            bounds[l] = c
        }
        last.clear()
        state = when (state) {
            "tc" -> "c"
            "c", "tp" -> ""
            else -> return
        }
    }
}