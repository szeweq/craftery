package szeweq.craftery.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.InsnList
import java.io.ByteArrayInputStream
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

inline fun <reified K, reified V> Map<K, V>.entryPairStream(): Stream<Pair<K, V>> =
    KtUtil.streamEntriesFrom(this)
fun InsnList.stream(): Stream<AbstractInsnNode> =
    StreamSupport.stream(
        Spliterators.spliterator(iterator(), size().toLong(), Spliterator.ORDERED),
        size() > 10
    )
fun <T> Stream<T>.toSet(): Set<T> = collect(Collectors.toUnmodifiableSet())

inline fun <reified R> Stream<*>.filterByInstance(): Stream<R> = KtUtil.streamInstances(this, R::class.java)

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

fun ZipInputStream.entryStreamFlow() = flow<Pair<ZipEntry, InputStream>> {
    var lastEntry: String? = null
    try {
        var ze = nextEntry
        while (ze != null) {
            lastEntry = ze.name
            if (!ze.isDirectory) {
                val bais = ByteArrayInputStream(readAllBytes())
                emit(ze to bais)
            }
            ze = nextEntry
        }
    } catch (ex: IOException) {
        print("Error occured while reading entry: $lastEntry")
        ex.printStackTrace()
    }
}.flowOn(Dispatchers.IO)

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
