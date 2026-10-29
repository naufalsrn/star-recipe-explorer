package com.naufal.recipeexplorer.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Constraints
import com.naufal.recipeexplorer.data.remote.ImageLoader
import kotlinx.coroutines.CancellationException

/** Shows an image from the internet, with a grey placeholder while it loads. */
@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    BoxWithConstraints(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        // Decode the image at roughly the size it will be shown.
        val maxSizePx = listOf(constraints.maxWidth, constraints.maxHeight)
            .filter { it in 1 until Constraints.Infinity }
            .maxOrNull() ?: 1024

        var bitmap by remember(url, maxSizePx) { mutableStateOf<Bitmap?>(ImageLoader.cached(url, maxSizePx)) }
        var failed by remember(url, maxSizePx) { mutableStateOf(false) }

        LaunchedEffect(url, maxSizePx) {
            if (bitmap != null) return@LaunchedEffect
            try {
                bitmap = ImageLoader.load(url, maxSizePx)
            } catch (e: CancellationException) {
                throw e // The image scrolled off screen; stop quietly.
            } catch (e: Exception) {
                failed = true
            }
        }

        val loaded = bitmap
        when {
            loaded != null -> Image(
                bitmap = loaded.asImageBitmap(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.matchParentSize(),
            )
            failed -> Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
