package szeweq.craftery.util

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.InsnList
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream
import java.util.stream.StreamSupport
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

val FieldNode.fixedDesc: String get() = if (desc.startsWith('L')) desc.substring(1, desc.length - 1) else desc

inline fun <reified T> Map<*, T>.valueStream(): Stream<T> = KtUtil.streamValuesFrom(this)
fun InsnList.stream(): Stream<AbstractInsnNode> = StreamSupport.stream(spliterator(), false)
fun <T> Stream<T>.toSet(): Set<T> = collect(Collectors.toUnmodifiableSet())

@Suppress("UNCHECKED_CAST")
inline fun <reified R> Stream<*>.filterIsInstance(): Stream<R> = filter { it is R } as Stream<R>

/**
 * Iterates over each entry found in ZIP input stream.
 */
inline fun ZipInputStream.eachEntry(fn: (ZipEntry) -> Unit) {
    var lastEntry: String? = null
    try {
        var ze = nextEntry
        while (ze != null) {
            lastEntry = ze.name
            fn(ze)
            ze = nextEntry
        }
    } catch (ex: IOException) {
        print("Error occured while reading entry: $lastEntry")
        ex.printStackTrace()
    }

}

fun InputStream.copyWithProgress(out: OutputStream, total: Long, progress: LongBiConsumer): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        progress.accept(bytesCopied, total)
        bytes = read(buffer)
    }
    return bytesCopied
}