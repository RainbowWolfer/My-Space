package com.rainbowwolfer.myspacedemo1.util

import android.graphics.Bitmap
import android.os.Environment
import org.joda.time.DateTime
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {
	@Suppress("DEPRECATION")
	fun saveBitmap(bitmap: Bitmap, fileName: String? = null): Boolean {
		return try {
			val mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
			mediaStorageDir?.mkdirs()
			val file = File(mediaStorageDir, "${if (fileName?.isBlank() == false) fileName else DateTime.now().millis}.png")
			file.createNewFile()
			saveBitmapFile(bitmap, file)
			true
		} catch (ex: IOException) {
			ex.printStackTrace()
			false
		}
	}
	
	@Suppress("MemberVisibilityCanBePrivate")
	fun saveBitmapFile(bitmap: Bitmap, file: File) {
		val bos = BufferedOutputStream(FileOutputStream(file))
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
		bos.flush()
		bos.close()
	}
}