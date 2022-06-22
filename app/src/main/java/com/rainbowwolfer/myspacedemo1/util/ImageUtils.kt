package com.rainbowwolfer.myspacedemo1.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
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
     * 判断是否拥有权限
     * PermissionUtils.isGetPermissions(ct,"android.permission.WRITE_EXTERNAL_STORAGE" )

     * 默认当期时间作 文件名
     */
    fun saveBitmap(bitmap: Bitmap, filePath: String? = null) {
        var filename1: String =
            SimpleDateFormat("yyyyMMddHHmmss").format(Date(System.currentTimeMillis()))

        filePath?.let {
            filename1 = it
        }
        val file: File =
            File(Environment.getExternalStorageDirectory(), "$filename1.png")
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