package com.rainbowwolfer.myspacedemo1.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.rainbowwolfer.myspacedemo1.R
import com.squareup.picasso.Picasso
import java.io.File

/*
* 采用 Glide 与 picasso
*/
object ImgDownload {
    private val picasso: Picasso by lazy {
        Picasso.get()
    }

/*    Glide支持网络资源、assets资源、Resources资源、File资源、Uri资源、字节数组
          Integer resourceId
          Uri uri
          String string
          Drawable drawable
          Bitmap bitmap
                */
    /**
     * Set img to
     * @param context 上下文,与其生命周期相关联
     * @param bitmap 图片
     * @param targetView 目标对象 ImageView
     */
    fun setImgTo(context: Context, bitmap: Bitmap, targetView: ImageView) {

        Glide.with(context)
            .load(bitmap)
            .placeholder(R.drawable.ic_img_error)//图片尚未加载出来 用于占位
            .error(R.drawable.ic_img_error)//图片尚未加载失败 用于占位
            .into(targetView)

    }

    /*
        load(Uri?)
        load(File)
        load(Int)
        load(String?) */
    /**
     * Get bitmap
     *
     * @param imgUrl 图片链接
     * @param targetView 目标对象
     * @return 获取 Bitmap
     */
    fun getBitmap(imgUrl: String, targetView: ImageView): Bitmap {

        return picasso
            .load(imgUrl)
            .error(R.drawable.ic_img_error)
            .get()
    }


    /*
       load(Uri?)
       load(File)
       load(Int)
       load(String?) */
    /**
     * Set img to
     *
     * @param imgUrl Uri File  Int String
     * @param targetView
     */
    private fun setImgTo(imgUrl: Any, targetView: ImageView) {
        when (imgUrl) {
            is File -> picasso
                .load(imgUrl)
                .error(R.drawable.ic_img_error)
                .into(targetView)
            is String -> picasso
                .load(imgUrl)
                .error(R.drawable.ic_img_error)
                .into(targetView)
            is Uri -> picasso
                .load(imgUrl)
                .error(R.drawable.ic_img_error)
                .into(targetView)
            is Int -> picasso
                .load(imgUrl)
                .error(R.drawable.ic_img_error)
                .into(targetView)
            else ->
                picasso.load(R.drawable.ic_img_error)
                    .error(R.drawable.ic_img_error)
                    .into(targetView)
        }
    }


}
