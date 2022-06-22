package com.rainbowwolfer.myspacedemo1.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.models.UserInfo
import com.rainbowwolfer.myspacedemo1.models.UserInfo.Companion.findUserInfo
import com.rainbowwolfer.myspacedemo1.models.api.GoResponse
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.models.interfaces.DatabaseID
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.callbacks.ArgsCallBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Minutes
import org.json.JSONObject
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.random.Random

object ImgDownload {
    /*
    需要网络权限
    * <uses-permission android:name="android.permission.INTERNET" />
    Glide支持网络资源、assets资源、Resources资源、File资源、Uri资源、字节数组
    * */
    fun sdas(context: Context, bitmap: Bitmap, targetView: ImageView) {
//		和Activity/Fragment的生命周期保持一致
        /*	Glide.with(Context context);
            Glide.with(Activity activity);
            Glide.with(FragmentActivity activity);
            Glide.with(Fragment fragment);*/
        Glide.with(context)
            .load(bitmap)
            .error(R.drawable.ic_img_error)
            .into(targetView)

    }


}
