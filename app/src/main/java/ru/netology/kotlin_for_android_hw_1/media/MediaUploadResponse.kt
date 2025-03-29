package ru.netology.kotlin_for_android_hw_1.media

import android.net.Uri
import java.io.File

data class MediaUploadResponse(val id: String)

enum class AttachmentType {
    IMAGE
}

data class PhotoModel(val uri: Uri? = null, val file: File? = null)