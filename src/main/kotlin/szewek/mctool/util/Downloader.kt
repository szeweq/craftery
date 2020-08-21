package szewek.mctool.util

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

object Downloader {
    private val client = HttpClient {
        install(JsonFeature)
    }

    suspend fun downloadFile(url: String): InputStream {
        val res = client.get<HttpResponse>(url)
        return ByteArrayInputStream(res.readBytes())
    }

    suspend fun <T> downloadJson(url: String) {
        return client.get(url)
    }

    suspend fun downloadZip(url: String): ZipInputStream {
        val res = client.get<HttpResponse>(url)
        val input = ByteArrayInputStream(res.readBytes())
        return ZipInputStream(input)
    }
}