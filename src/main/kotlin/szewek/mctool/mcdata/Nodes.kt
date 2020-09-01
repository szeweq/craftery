package szewek.mctool.mcdata

import com.github.kittinunf.fuel.core.ProgressCallback
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.InsnList
import szewek.mctool.util.KtUtil
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
@Suppress("UNCHECKED_CAST")
fun <T> Stream<T?>.filterNotNull() = filter(Objects::nonNull) as Stream<T>

inline fun ZipInputStream.eachEntry(fn: (ZipEntry) -> Unit) {
    var ze = nextEntry
    while (ze != null) {
        fn(ze)
        ze = nextEntry
    }
}

fun InputStream.copyWithProgress(out: OutputStream, total: Long, progress: ProgressCallback): Long {
    var bytesCopied: Long = 0
    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        progress(bytesCopied, total)
        bytes = read(buffer)
    }
    return bytesCopied
}