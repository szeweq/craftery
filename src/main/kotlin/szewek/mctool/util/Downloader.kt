package szewek.mctool.util

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ProgressCallback
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

object Downloader {

    fun downloadFile(url: String, progress: ProgressCallback): InputStream {
        val (_, _, bytes) = Fuel.get(url).responseProgress(progress).response()
        return ByteArrayInputStream(bytes.get())
    }

    //suspend fun <T> downloadJson(url: String) {
    //    return client.get(url)
    //}

    fun downloadZip(url: String, progress: ProgressCallback): ZipInputStream {
        return ZipInputStream(downloadFile(url, progress))
    }
}