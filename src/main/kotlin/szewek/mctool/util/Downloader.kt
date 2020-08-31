package szewek.mctool.util

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ProgressCallback
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.getOrNull
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

    inline fun <reified T : Any> downloadJson(url: String) = Fuel.get(url)
            .responseObject<T>().third.getOrElse {
                println("Err $it")
                null
            }

    inline fun <reified T : Any> downloadJson(url: String, noinline progress: ProgressCallback) = Fuel.get(url)
            .responseProgress(progress)
            .responseObject<T>().third.getOrElse {
                println("Err $it")
                null
            }

    fun downloadZip(url: String, progress: ProgressCallback): ZipInputStream {
        return ZipInputStream(downloadFile(url, progress))
    }
}