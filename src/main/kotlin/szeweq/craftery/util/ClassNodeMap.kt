package szeweq.craftery.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.transform
import org.objectweb.asm.tree.*
import kotlin.reflect.KProperty

val ClassNodeMap.flowClasses get(): Flow<ClassNode> = classes.asFlow()

inline fun <T> ClassNodeMap.getFlattenedClassValues(crossinline getter: ClassNode.() -> List<T>) = flowClasses.transform {
    val l = getter(it)
    for (x in l) {
        emit(Pair(it, x))
    }
}

val ClassNodeMap.allClassFields get(): Flow<Pair<ClassNode, FieldNode>> = getFlattenedClassValues(ClassNode::fields)
val ClassNodeMap.allClassMethods get(): Flow<Pair<ClassNode, MethodNode>> = getFlattenedClassValues(ClassNode::methods)

fun <N : AbstractInsnNode> ClassNodeMap.instructionsFlow(fn: (AbstractInsnNode) -> N?) = allClassMethods.transform { (cl, m) ->
    for (node in m.instructions) {
        val n = fn(node)
        if (n !== null) emit(Triple(cl, m, n))
    }
}
fun ClassNodeMap.flowUsagesOf(cn: ClassNode, fn: FieldNode) = instructionsFlow {
    if (it is FieldInsnNode && fn.name == it.name && fn.desc == it.desc && cn.name == it.owner) it else null
}
fun ClassNodeMap.flowUsagesOf(cn: ClassNode, mn: MethodNode) = instructionsFlow {
    if (it is MethodInsnNode && mn.name == it.name && mn.desc == it.desc && cn.name == it.owner) it else null
}