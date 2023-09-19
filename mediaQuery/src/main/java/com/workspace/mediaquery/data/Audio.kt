package com.workspace.mediaquery.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Audio(
    val uri: Uri,
    val name: String,
    val duration: Int,
    val size: Int,
    val mimeType: String
) : Parcelable