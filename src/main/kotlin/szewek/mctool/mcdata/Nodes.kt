package szewek.mctool.mcdata

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.InsnList
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.streams.asSequence

fun ClassNode.fieldByName(name: String) = fields.find { name == it.name }
fun ClassNode.methodsByName(name: String) = methods.filter { name == it.name }

val FieldNode.fixedDesc: String get() = if (desc.startsWith('L')) desc.substring(1, desc.length - 1) else desc

fun InsnList.stream(): Stream<AbstractInsnNode> = StreamSupport.stream(spliterator(), false)

fun <T> Stream<T>.toSet(): Set<T> = collect(Collectors.toUnmodifiableSet())
@Suppress("UNCHECKED_CAST")
inline fun <reified R> Stream<*>.filterIsInstance(): Stream<R> = filter { it is R } as Stream<R>
@Suppress("UNCHECKED_CAST")
fun <T> Stream<T?>.filterNotNull() = filter(Objects::nonNull) as Stream<T>