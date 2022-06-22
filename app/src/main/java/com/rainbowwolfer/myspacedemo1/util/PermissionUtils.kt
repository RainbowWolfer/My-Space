package com.rainbowwolfer.myspacedemo1.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {
	private const val REQUEST_CODE = 2022
	
	fun requestPermissions(context: Context, permissions: List<String>): Boolean {
		for (permission in permissions) {
			if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions((context as Activity), permissions.toTypedArray(), REQUEST_CODE)
			}
		}
		return true
	}
	
	fun checkPermissions(context: Context, permissions: List<String>): Boolean {
		var flag = false
		for (p in permissions) {
			flag = ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED
		}
		return flag
	}
}
