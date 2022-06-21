package com.rainbowwolfer.myspacedemo1.services.chat

import androidx.lifecycle.MutableLiveData
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.net.Socket

object ChatSocket {
	private const val host = "www.cqtest.top"
	private const val port = 4600
	
	private var client: Socket? = null
	val read: MutableLiveData<String> by lazy { MutableLiveData() }
	
	fun connect(forceRefresh: Boolean = false) {
		CoroutineScope(Dispatchers.IO).launch {
			val result = kotlin.runCatching {
				if (client == null || forceRefresh) {
					client = Socket(host, port)
				}
				BufferedReader(client!!.getInputStream().reader()).use { r ->
					r.lineSequence().forEach {
						withContext(Dispatchers.Main) {
							println(it)
							read.value = it
						}
					}
				}
			}.exceptionOrNull()
			if (result is Throwable) {
				result.printStackTrace()
			}
		}
	}
	
	fun console(string: String) {
		CoroutineScope(Dispatchers.IO).launch {
			val result = kotlin.runCatching {
				println(client!!.isConnected)
				client!!.outputStream.write("/nick ${MySpaceApplication.instance.getCurrentID()}\n".toByteArray())
			}.exceptionOrNull()
			if (result is Throwable) {
				result.printStackTrace()
			}
		}
	}
	
	
}