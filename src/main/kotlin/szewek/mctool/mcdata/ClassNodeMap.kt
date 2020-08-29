package szewek.mctool.mcdata

import org.objectweb.asm.tree.*

class ClassNodeMap {
    val nodes = mutableMapOf<String, ClassNode>()

    fun isCompatible(fn: FieldNode, typename: String): Boolean {
        val desc = fn.fixedDesc
        if (desc == typename) {
            return true
        }
        val cn = nodes[desc]
        return if (cn != null) classExtendsFrom(cn, typename) else false
    }

    fun classExtendsFrom(cn: ClassNode, typename: String): Boolean {
        var scn = cn
        while (scn.superName != typename) {
            scn = nodes[scn.superName] ?: return false
        }
        return true
    }

    fun getLastSuperClass(typename: String): String {
        var tn = typename
        do {
            val cn = nodes[tn] ?: return tn
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
            val cn = nodes[tn] ?: return l
            if (cn.superName == null || cn.superName == "java/lang/Object") {
                return l
            }
            q += cn.interfaces
            while (q.isNotEmpty()) {
                val iface = q.removeFirst()
                if (iface !in l) {
                    l += iface
                }
                val icn = nodes[iface]
                if (icn != null) q += icn.interfaces
            }
            tn = cn.superName
        } while (true)
    }

    fun streamUsagesOf(cn: ClassNode, fn: FieldNode) = nodes.valueStream()
            .flatMap { c -> c.methods.stream().map { c to it } }
            .flatMap { (c, m) ->
                m.instructions.stream()
                        .filterIsInstance<FieldInsnNode>()
                        .filter { fn.name == it.name && fn.desc == it.desc && cn.name == it.owner }
                        .map { Triple(c, m, it) }
            }

    fun streamUsagesOf(cn: ClassNode, mn: MethodNode) = nodes.valueStream()
            .flatMap { c -> c.methods.stream().map { c to it } }
            .flatMap { (c, m) ->
                m.instructions.stream()
                        .filterIsInstance<MethodInsnNode>()
                        .filter { mn.name == it.name && mn.desc == it.desc && cn.name == it.owner }
                        .map { Triple(c, m, it) }
            }
}