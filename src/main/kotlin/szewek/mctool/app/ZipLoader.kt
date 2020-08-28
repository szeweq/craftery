package szewek.mctool.app

import szewek.mctool.util.Downloader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipInputStream

interface ZipLoader {
    fun load(progress: (Long, Long) -> Unit): ZipInputStream

    class FromURL(private val url: String): ZipLoader {
        override fun load(progress: (Long, Long) -> Unit) = Downloader.downloadZip(url, progress)
    }

    class FromFile(private val file: File): ZipLoader {
        override fun load(progress: (Long, Long) -> Unit) = file.inputStream().use {
            val out = ByteArrayOutputStream(5120)
            val len = file.length()
            progress(0, len)
            var copied: Long = 0
            val buf = ByteArray(4096)
            var bytes = it.read(buf)
            while (bytes >= 0) {
                out.write(buf, 0, bytes)
                copied += bytes
                progress(copied, len)
                bytes = it.read(buf)
            }
            ZipInputStream(ByteArrayInputStream(out.toByteArray()))
        }
    }
}