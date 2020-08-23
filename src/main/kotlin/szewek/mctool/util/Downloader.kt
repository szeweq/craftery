package szewek.mctool.util

import com.github.kittinunf.fuel.Fuel
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

object Downloader {

    fun downloadFile(url: String): InputStream {
        val (_, _, bytes) = Fuel.get(url).response()
        return ByteArrayInputStream(bytes.get())
    }

    //suspend fun <T> downloadJson(url: String) {
    //    return client.get(url)
    //}

    fun downloadZip(url: String): ZipInputStream {
        return ZipInputStream(downloadFile(url))
    }
}