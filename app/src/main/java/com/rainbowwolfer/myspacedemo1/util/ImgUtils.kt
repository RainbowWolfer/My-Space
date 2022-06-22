package com.rainbowwolfer.myspacedemo1.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.BottomSheetCommentInputBinding
import com.rainbowwolfer.myspacedemo1.databinding.BottomSheetRepostInputBinding
import com.rainbowwolfer.myspacedemo1.models.Comment
import com.rainbowwolfer.myspacedemo1.models.api.NewComment
import com.rainbowwolfer.myspacedemo1.models.api.NewRepost
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.views.LoadingDialog
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream


object ImgUtils {
    val picasso: Picasso by lazy {
        Picasso.get()
    }

    fun setToView(context: Context, bitmap: Bitmap, target: ImageView) {
        Glide.with(context).load(bitmap).error(R.drawable.ic_baseline_error_outline_24)
            .into(target)
    }

    fun saveBitmap(filePath: String, bitmap: Bitmap) {
        val file: File =
            File(Environment.getExternalStorageDirectory(), "$filePath.png")
        saveBitmapFile(file, bitmap)
    }

    fun saveBitmapFile(file: File, bitmap: Bitmap) {
        var bos: BufferedOutputStream = BufferedOutputStream(FileOutputStream(file));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();

    }
}