package szewek.mctool.cfapi

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*

object CurseforgeAPI {
    private const val CF_API = "https://addons-ecs.forgesvc.net/api/v2/"
    private val client = HttpClient() {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

    suspend fun findAddons(query: String, type: Int): Array<AddonSearch> {
        return client.get(CF_API + "addon/search?gameId=432&sectionId=$type&searchFilter=$query")
    }

    suspend fun downloadURL(addon: Int, file: Int): String {
        return client.get(CF_API + "addon/$addon/file/$file/download-url")
    }
}