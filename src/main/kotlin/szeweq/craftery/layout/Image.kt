package szeweq.craftery.layout

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.launch
import szeweq.craftery.util.ImageCache

/**
 * An image displayed after downloading it using URL.
 */
@Composable
fun ImageURL(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha
) {
    val scope = rememberCoroutineScope()
    val (img, setImg) = remember(url) { mutableStateOf(ImageCache.emptyBitmap) }
    scope.launch { ImageCache.lazyGet(url, setImg) }
    Image(img, contentDescription, modifier, alignment, contentScale, alpha)
}

