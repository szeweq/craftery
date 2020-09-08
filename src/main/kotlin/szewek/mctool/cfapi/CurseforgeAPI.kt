package szewek.mctool.cfapi

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.gson.responseObject

object CurseforgeAPI {
    private const val CF_API = "https://addons-ecs.forgesvc.net/api/v2/"

    fun findAddons(query: String, type: Int): Array<AddonSearch> {
        return Fuel.get(CF_API + "addon/search", listOf("gameId" to 432, "sectionId" to type, "searchFilter" to query))
            .responseObject<Array<AddonSearch>>().third.get()
    }

    fun getAddonFiles(addonId: Int): Array<AddonFile> {
        return Fuel.get(CF_API + "addon/$addonId/files").responseObject<Array<AddonFile>>().third.get()
    }

    fun downloadURL(addon: Int, file: Int): String = Fuel.get(CF_API + "addon/$addon/file/$file/download-url")
            .responseString().third.get()
}