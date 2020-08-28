package szewek.mctool.mcdata

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode

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
        while(scn.superName != typename) {
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
}