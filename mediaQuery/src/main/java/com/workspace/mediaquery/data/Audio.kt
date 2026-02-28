package com.workspace.mediaquery.data

import android.content.ContentUris
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Audio(
    val id: Long,
    val uri: Uri,
    val name: String,
    val duration: Int,
    val size: Int,
    val mimeType: String,
    val artist: String,
    val album: String,
    val albumId: Long
) : Parcelable {
    val albumArtUri: Uri
        get() = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
}