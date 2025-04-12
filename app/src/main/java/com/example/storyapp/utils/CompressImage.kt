package com.example.storyapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object CompressImage {

    private const val MAXIMAL_SIZE = 1000000

    fun File.compressImage(): File {
        val file = this
        val bitmap = BitmapFactory.decodeFile(file.path)
        var quality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, bmpStream)
            val bitmaptobyte = bmpStream.toByteArray()
            streamLength = bitmaptobyte.size
            quality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, FileOutputStream(file))
        return file
    }

}