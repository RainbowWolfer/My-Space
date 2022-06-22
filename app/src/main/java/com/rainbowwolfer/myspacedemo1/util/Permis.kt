package com.rainbowwolfer.myspacedemo1.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionMediator
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.RequestCallback

object PermissionUtils {
    var REQUEST_CODE = 2022 //任意标识

    /**
     * 权限检查
     * @param ct 当前Activity
     * @param permissions 需要检查的权限
     * @return boolean
     */
    fun requestPermissions(ct: Context, permissions: List<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    ct,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //申请权限 参数分别是 上下文、权限集合(String)、请求码
                ActivityCompat.requestPermissions((ct as Activity),
                    permissions.toTypedArray(), REQUEST_CODE)
            }
        }
        return true
    }

    /**
     * Is get permissions
     * 是否获取 某权限
     * @param ct 当前Activity
     * @param permissions 需要检查的权限
     */
    fun isGetPermissions(ct: Context, permissions: List<String>): Boolean {
        var flag = false
        for (i in permissions) {
            flag = PermissionX.isGranted(ct, i)
        }
        return flag
    }

    fun requestPermissionsX(
        activity: FragmentActivity,
        permissions: List<String>,
        callback: RequestCallback?
    ) {
        PermissionX.init(activity).permissions(permissions).request(callback)
    }
}
