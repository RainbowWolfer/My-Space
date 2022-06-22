package com.rainbowwolfer.myspacedemo1.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback

object PermissionUtils {
	private const val REQUEST_CODE = 2022
	
	fun requestPermissions(context: Context, permissions: List<String>): Boolean {
		for (permission in permissions) {
			if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
				//申请权限 参数分别是 上下文、权限集合(String)、请求码
				ActivityCompat.requestPermissions(
					(context as Activity),
					permissions.toTypedArray(), REQUEST_CODE
				)
			}
		}
		return true
	}
	
	fun checkPermissions(context: Context, permissions: List<String>): Boolean {
		var flag = false
		for (i in permissions) {
			flag = PermissionX.isGranted(context, i)
		}
		return flag
	}
	
	fun requestPermissionsX(
		activity: FragmentActivity,
		permissions: List<String>,
		callback: RequestCallback? = null
	) {
		PermissionX.init(activity).permissions(permissions).request(callback)
	}
}
