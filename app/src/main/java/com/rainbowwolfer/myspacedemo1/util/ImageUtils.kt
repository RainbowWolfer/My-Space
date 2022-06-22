package com.rainbowwolfer.myspacedemo1.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
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
     *
     * @param bitmap
     * @param filePath 默认 com.rainbowwolfer.myspacedemo1
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
            File(setRootDir(filePath1), "$filename1.png")
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

    private fun setRootDir(filePath: String): String {

        var filePath1 = filePath.replace("/", File.separator)
        val rootPath =
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) { //有SD卡
                Environment.getExternalStorageDirectory().path + File.separator + filePath1
            } else {
                Environment.getDataDirectory().path + File.separator + filePath1
            }

        // 有存储权限，则创建文件夹
        val createRootDir = createRootDir(rootPath)
        Log.w("ImageUtils", "createRootDir:$createRootDir")

        return rootPath // 返回文件夹绝对路径 //storage/emulated/0/$filePath/
    }

    /**
     * 创建文件夹
     */
    private fun createRootDir(rootPath: String): Boolean {
        val dirRoot = File(rootPath)
        if (!dirRoot.exists() || !dirRoot.isDirectory) {
            return dirRoot.mkdirs()
        }
        return true
    }

}