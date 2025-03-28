package ru.netology.kotlin_for_android_hw_1.model

import android.net.Uri
import java.io.File

data class PhotoModel(val uri: Uri? = null, val file: File? = null)