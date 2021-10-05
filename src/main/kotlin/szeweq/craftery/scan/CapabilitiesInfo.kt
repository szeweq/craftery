package szeweq.craftery.scan

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodInsnNode
import szeweq.craftery.util.filterIsInstance
import szeweq.craftery.util.stream
import szeweq.craftery.util.toSet

class CapabilitiesInfo(val name: String, instructions: InsnList) {
    var supclasses: Set<String> = instructions.stream()
        .filterIsInstance<MethodInsnNode>()
        .filter { "getCapability" == it.name && name != it.owner }
        .map { it.owner ?: "UNKNOWN" }
        .toSet()
    var fields = instructions.stream()
        .filterIsInstance<FieldInsnNode>().filter { TypeNames.CAPABILITY == it.desc }
        .map { "${it.owner ?: "UNKNOWN"}::${it.name ?: "UNKNOWN"}" }
        .toSet()
}