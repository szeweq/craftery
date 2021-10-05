package szeweq.craftery.scan

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodInsnNode
import szeweq.craftery.util.fixedDesc
import szeweq.craftery.util.toSet
import szeweq.craftery.util.ClassNodeMap

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
    }.map {
        if (it.signature == null) { it.name to "NONE" }
        else { it.name to Scanner.genericFromSignature(it.signature)
        }
    }.toSet()
}