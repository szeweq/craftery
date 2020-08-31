package szewek.mctool.mcdata

import com.github.kittinunf.fuel.core.ProgressCallback
import szewek.mctool.util.Downloader
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.ZipInputStream

object MinecraftData {
    var manifest = Manifest(mapOf(), listOf())
    var updated = Date(0)
    val packages = mutableMapOf<String, Package>()
    val assets = mutableMapOf<String, AssetMap>()
    val filesFromJar = mutableMapOf<String, ByteArray>()

    fun updateManifest() {
        val d = System.currentTimeMillis()
        if (d - updated.time >= 1000 * 3600) {
            println("Updating Minecraft manifest...")
            val o = Downloader.downloadJson<Manifest>("https://launchermeta.mojang.com/mc/game/version_manifest.json")
            if (o != null) {
                manifest = o
                updated.time = d
            }
        }
    }

    fun getPackage(v: String): Package? {
        updateManifest()
        println("PKG for $v")
        val p = packages[v]
        if (p == null) {
            val vu = manifest.versions.find { v == it.id }
            if (vu != null) {
                val o = Downloader.downloadJson<Package>(vu.url)
                if (o != null) {
                    packages[v] = o
                    return o
                }
            }
        } else {
            return p
        }
        return null
    }

    fun getAssetMap(v: String): AssetMap? {
        updateManifest()
        println("AM for $v")
        val am = getPackage(v)
        if (am != null) {
            println("DL ${am.downloads.map { (k, v) -> k to v.url }}")
            val vi = am.assetIndex.id
            val vu = am.assetIndex.url
            val ma = assets[vi]
            if (ma == null) {
                val dl = Downloader.downloadJson<AssetMap>(vu)
                if (dl != null) {
                    assets[vi] = dl
                    return dl
                }
            } else {
                return ma
            }
        }
        return null
    }

    fun getAsset(p: String) {
        updateManifest()
        val v = manifest.latest["release"] ?: return
        val ma = getAssetMap(v) ?: return
        val mf = ma.objects[p]
    }

    fun getMinecraftClientJar(v: String?, progress: ProgressCallback): ZipInputStream? {
        updateManifest()
        val z = v ?: manifest.latest["release"] ?: return null
        val p = getPackage(z) ?: return null
        val u = p.downloads["client"]?.url ?: return null
        return Downloader.downloadZip(u, progress)
    }

    fun loadAllFilesFromJar(v: String?, progress: ProgressCallback) {
        val z = getMinecraftClientJar(v, progress)
        val out = ByteArrayOutputStream()
        z?.eachEntry {
            if (!it.isDirectory && (it.name.startsWith("data/") || it.name.startsWith("assets/"))) {
                out.reset()
                z.copyTo(out)
                filesFromJar[it.name] = out.toByteArray()
            }
        }
    }

    class Manifest(val latest: Map<String, String>, val versions: List<Version>)

    class Version(
            val id: String,
            val type: String,
            val url: String,
            val time: Date,
            val releaseTime: Date
    )

    class Package(
            val id: String,
            val assetIndex: AssetIndex,
            val downloads: Map<String, DownloadURL>
    )

    data class AssetIndex(val id: String, val url: String)

    class DownloadURL(val sha1: String, val size: Int, val url: String)

    class AssetMap(val objects: Map<String, FileCheck>) {
        val files = mutableMapOf<String, ByteArray>()
    }

    class FileCheck(val hash: String, val size: String)
}