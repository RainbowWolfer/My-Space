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


object ImageUtils {
    val picasso: Picasso by lazy {
        Picasso.get()
    }

    fun setToView(context: Context, bitmap: Bitmap, target: ImageView) {
        Glide.with(context).load(bitmap).error(R.drawable.ic_baseline_error_outline_24)
            .into(target)
    }

    /**
     * Save bitmap
     * @param bitmap
     * @param filePath : /storage/emulated/0/ $filePath
     *  * *  * 默认  com.rainbowwolfer.myspacedemo1
     * @param fileName 默认当前时间戳
     */
    fun saveBitmap(bitmap: Bitmap, filePath: String? = null, fileName: String? = null) {
        //默认文件名
        var filename1: String =
            SimpleDateFormat("yyyyMMddHHmmss").format(Date(System.currentTimeMillis()))
        //默认文件存储路径

        var filePath1 = "com.rainbowwolfer.myspacedemo1"

        fileName?.let {
            filename1 = it
        }
        filePath?.let {
            filePath1 = it
        }

        val file: File =
            File(FileUtils.setRootDir(filePath1), "$filename1.png")
        saveBitmapFile(bitmap, file)
    }

    /**
     * Save bitmap file
     *
     * @param file File对象
     * @param bitmap Bitmap
     */
    fun saveBitmapFile(bitmap: Bitmap, file: File) {


        var bos: BufferedOutputStream = BufferedOutputStream(FileOutputStream(file));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
    }

}