package szewek.craftery.layout

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.launch
import szewek.craftery.util.ImageCache
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Composable
fun ImageURL(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    val scope = rememberCoroutineScope()
    val img = remember(url) { mutableStateOf(emptyBitmap) }
    scope.launch {
        img.value = ImageCache.bitmapFromURL(url)
    }
    Image(img.value, contentDescription, modifier, alignment, contentScale, alpha, colorFilter)
}

val emptyImageBytes: ByteArray = ByteArrayOutputStream().also {
    ImageIO.write(BufferedImage(1, 1, TYPE_INT_ARGB), "png", it)
}.toByteArray()

val emptySkijaImage: org.jetbrains.skija.Image = org.jetbrains.skija.Image.makeFromEncoded(emptyImageBytes)

val emptyBitmap = ImageBitmap(1, 1)