package com.example.firstapplication.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.io.FileOutputStream

fun Bitmap.toFile(context: Context, name: String): File {
    Log.i("BitmapExtension", "creating file for bitmap $name")
    val file = File(context.cacheDir, "temp_image_${name}.jpg")
    FileOutputStream(file).use { outputStream ->
        compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }
    return file
}