package szeweq.craftery.scan

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodInsnNode
import szeweq.craftery.util.filterByInstance
import szeweq.craftery.util.stream
import szeweq.craftery.util.toSet
import java.util.function.Consumer

class CapabilitiesInfo(val name: String, instructions: InsnList) {
    var supclasses: Set<String> = instructions.stream()
        .filterByInstance<MethodInsnNode>()
        .mapMulti { t, c: Consumer<String> ->
            if ("getCapability" == t.name && name != t.owner)
                c.accept(t.owner ?: "UNKNOWN")
        }.toSet()
    var fields = instructions.stream()
        .filterByInstance<FieldInsnNode>()
        .mapMulti { t, c: Consumer<String> ->
            if (TypeNames.CAPABILITY == t.desc)
                c.accept("${t.owner ?: "UNKNOWN"}::${t.name ?: "UNKNOWN"}")
        }.toSet()
}
