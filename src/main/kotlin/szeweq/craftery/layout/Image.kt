package szeweq.craftery.layout

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import szeweq.craftery.util.ImageCache

/**
 * An image displayed after downloading it using URL.
 */
@Composable
fun ImageURL(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val (img, setImg) = remember(url) { mutableStateOf(ImageCache.emptyBitmap) }
    LaunchedEffect(url) { withContext(Dispatchers.IO) { ImageCache.lazyGet(url, setImg) } }
    Image(img, contentDescription, modifier, contentScale = contentScale)
}

