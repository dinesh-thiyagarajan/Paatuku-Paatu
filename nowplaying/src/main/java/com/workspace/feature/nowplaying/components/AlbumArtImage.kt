package com.workspace.feature.nowplaying.components

import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@Composable
fun AlbumArtImage(
    albumArtUri: Uri?,
    modifier: Modifier = Modifier,
    size: Dp = 280.dp
) {
    SubcomposeAsyncImage(
        model = albumArtUri,
        contentDescription = "Album Art",
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
        error = {
            Icon(
                imageVector = Icons.Default.Album,
                contentDescription = null,
                modifier = Modifier.size(size),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
