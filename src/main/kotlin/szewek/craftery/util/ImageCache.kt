package szewek.craftery.util

import org.jetbrains.skija.Image
import java.time.Duration

object ImageCache {
    private val refreshInterval = Duration.ofMinutes(15).toNanos()
    private var lastRefresh = System.nanoTime()
    private val map = mutableMapOf<String, Image>()

    fun fromURL(url: String): Image {
        recycle()
        return map.computeIfAbsent(url) {
            val stream = Downloader.downloadFile(url) { _, _ -> }
            Image.makeFromEncoded(stream.readAllBytes())
        }
    }

    private fun recycle() {
        val shouldRecycle = System.nanoTime() - lastRefresh >= refreshInterval
        if (shouldRecycle) {
            map.clear()
        }
    }
}