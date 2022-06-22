package com.rainbowwolfer.myspacedemo1.util

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.rainbowwolfer.myspacedemo1.R
import com.squareup.picasso.Picasso
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


@Suppress("MemberVisibilityCanBePrivate")
object ImageUtils {
	val picasso: Picasso by lazy { Picasso.get() }
	
	/**
	 * @param filePath : /storage/emulated/0/ $filePath
	 *  * *  * 默认  com.rainbowwolfer.myspacedemo1
	 * @param fileName 默认当前时间戳
	 */
	fun saveBitmap(bitmap: Bitmap, filePath: String? = null, fileName: String? = null) {
		var defaultFileName: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date(System.currentTimeMillis()))
		var defaultFilePath = "com.rainbowwolfer.myspacedemo1"
		
		fileName?.let { defaultFileName = it }
		filePath?.let { defaultFilePath = it }
		
		val file = File(FileUtils.setRootDir(defaultFilePath), "$defaultFileName.png")
		saveBitmapFile(bitmap, file)
	}
	
	fun saveBitmapFile(bitmap: Bitmap, file: File) {
		val bos = BufferedOutputStream(FileOutputStream(file))
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
		bos.flush()
		bos.close()
	}
	
}