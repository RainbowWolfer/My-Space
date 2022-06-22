package com.rainbowwolfer.myspacedemo1.services.chat

import androidx.lifecycle.MutableLiveData
import com.rainbowwolfer.myspacedemo1.models.Message
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.getDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.io.BufferedReader
import java.net.Socket
import java.net.SocketException

object ChatSocket {
	private const val host = "www.cqtest.top"
	private const val port = 4600
	
	private var client: Socket? = null
	val read: MutableLiveData<String> by lazy { MutableLiveData() }
	private val application = MySpaceApplication.instance
	
	fun connect(forceRefresh: Boolean = false) {
		if (!application.hasLoggedIn()) {
			println("Not Logged In Yet!")
			return
		}
		CoroutineScope(Dispatchers.IO).launch {
			val result = kotlin.runCatching {
				if (client == null || forceRefresh) {
					client = Socket(host, port)
				}
				console("/sign ${application.getCurrentID()}")
				BufferedReader(client!!.getInputStream().reader()).use { r ->
					r.lineSequence().forEach {
						withContext(Dispatchers.Main) {
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
				println("Sending : $string")
				client!!.outputStream.write((string + "\n").toByteArray())
			}.exceptionOrNull()
			if (result is Throwable) {
				result.printStackTrace()
				if (result is SocketException) {
					connect(true)
				}
			}
		}
	}
	
	private const val COMMAND_RECEIVE = "/receive"
	
	fun handle(input: String, onReceivedMessage: (Message) -> Unit) {
		try {
			val line = input.trim().trim('\n', '\r')
			val application = MySpaceApplication.instance
			println(line)
			when {
				line.startsWith(COMMAND_RECEIVE) -> {
					val args = line.split(' ')
					val senderID = args[1]
					val content = args.slice(2 until args.size).joinToString(" ")
					val message = Message(
						id = 0,
						senderID = senderID,
						receiverID = application.getCurrentID(),
						dateTime = DateTime.now().getDateTime(),
						textContent = content.trim(),
						hasReceived = false,
					)
					onReceivedMessage.invoke(message)
				}
			}
		} catch (ex: Exception) {
			ex.printStackTrace()
		}
	}
}