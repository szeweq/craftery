package szewek.mctool.util

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ProgressCallback
import com.github.kittinunf.fuel.core.responseUnit
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.result.getOrElse
import java.io.InputStream

object Downloader {
    fun downloadFile(url: String, progress: ProgressCallback): InputStream = Fuel.get(url)
            .responseProgress(progress)
            .responseUnit().second.body().toStream()

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
}