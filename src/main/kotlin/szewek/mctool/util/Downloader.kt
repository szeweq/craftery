package szewek.mctool.util

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Deserializable
import com.github.kittinunf.fuel.core.ProgressCallback
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.response
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.result.getOrElse
import java.io.InputStream
import java.util.zip.ZipInputStream

object Downloader {
    fun downloadFile(url: String, progress: ProgressCallback) = Fuel.get(url)
            .requestProgress(progress)
            .response(StreamDeserializer())
            .third.getOrElse {
                println("Err $it")
                InputStream.nullInputStream()
            }

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

    class StreamDeserializer: Deserializable<InputStream> {
        override fun deserialize(response: Response) = response.body().toStream()
    }
}