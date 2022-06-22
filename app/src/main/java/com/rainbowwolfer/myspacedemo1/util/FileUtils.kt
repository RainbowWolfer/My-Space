package com.rainbowwolfer.myspacedemo1.util

import android.os.Environment
import android.util.Log
import java.io.File


@Suppress("DEPRECATION")
object FileUtils {
	/**
	 * 转换为外部存储路径  /storage/emulated/0
	 */
	fun setRootDir(filePath: String): String {
		val filePath1 = filePath.replace("/", File.separator)
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
	
	private fun createRootDir(rootPath: String): Boolean {
		val dirRoot = File(rootPath)
		if (!dirRoot.exists() || !dirRoot.isDirectory) {
			return dirRoot.mkdirs()
		}
		return true
	}
	
}