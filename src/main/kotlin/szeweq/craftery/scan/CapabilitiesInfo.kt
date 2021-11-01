package szeweq.craftery.scan

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodInsnNode

class CapabilitiesInfo(
    val name: String,
    val supclasses: Set<String>,
    val fields: Set<String>
)

fun CapabilitiesInfo(name: String, instructions: InsnList): CapabilitiesInfo {
    val supclasses = mutableSetOf<String>()
    val fields = mutableSetOf<String>()
    for (inst in instructions) {
        if (inst is MethodInsnNode && "getCapability" == inst.name && name != inst.owner) {
            supclasses.add(inst.owner ?: "UNKNOWN")
        } else if (inst is FieldInsnNode && TypeNames.CAPABILITY == inst.desc) {
            fields.add("${inst.owner ?: "UNKNOWN"}::${inst.name ?: "UNKNOWN"}")
        }
    }
    return CapabilitiesInfo(name, supclasses, fields)
}
