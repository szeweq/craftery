package szewek.craftery.util

import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object Downloader {
    private val cli = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

    private fun <T> get(url: String, progress: LongBiConsumer, bodyHandler: HttpResponse.BodyHandler<T>) =
        cli.send(HttpRequest.newBuilder(URI.create(url)).build(), ProgressBodySubscriber.handler(bodyHandler, progress)).body()

    fun downloadFile(url: String, progress: LongBiConsumer): InputStream =
        get(url, progress, HttpResponse.BodyHandlers.ofInputStream())

    /*inline fun <reified T : Any> downloadJson(url: String) = Fuel.get(url)
            .responseObject<T>().third.getOrElse {
                println("Err $it")
                null
            }*/
    fun <T> downloadJson(url: String, progress: LongBiConsumer): T? =
        get(url, progress, GsonBodyHandler.handle(object : TypeToken<T>() {}))
}