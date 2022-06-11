package com.rainbowwolfer.myspacedemo1.ui.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.DialogSignupProcessBinding
import com.rainbowwolfer.myspacedemo1.models.api.GoResponse
import com.rainbowwolfer.myspacedemo1.models.api.SignUpInfo
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.ui.views.interfaces.ViewDialog
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.Companion.getHttpResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException


class SignUpProcessDialog(
	override val context: Context,
	val lifecycleCoroutineScope: LifecycleCoroutineScope,
) : ViewDialog {
	override lateinit var dialog: Dialog
	private lateinit var binding: DialogSignupProcessBinding
	
	val success: MutableLiveData<Boolean> = MutableLiveData(false)
	var currentRunning: TextView? = null
	
	fun showDialog(signUpInfo: SignUpInfo) {
		binding = DialogSignupProcessBinding.inflate(LayoutInflater.from(context))
		dialog = Dialog(context).apply {
			setContentView(binding.root)
			setCancelable(false)
			window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			create()
			show()
		}
		
		binding.signupButtonBack.setOnClickListener {
			hideDialog()
		}
		
		lifecycleCoroutineScope.launch(Dispatchers.IO) {
			try {
				println(signUpInfo)
				
				withContext(Dispatchers.Main) {
					binding.signupTextCheckingUsername.text = context.getText(R.string.checking_username)
					binding.signupTextCheckingEmail.text = context.getText(R.string.checking_email)
					binding.signupTextSendingEmail.text = context.getText(R.string.sending_validatoin_email)
					
					startText(binding.signupTextCheckingUsername)
					pendingText(binding.signupTextCheckingEmail)
					pendingText(binding.signupTextSendingEmail)
				}
				
				val responseUsername = RetrofitInstance.api.checkUsername(signUpInfo.username)
				if (!responseUsername.isSuccessful || responseUsername.body() == true) {
					withContext(Dispatchers.Main) {
						binding.signupTextCheckingUsername.text = context.getText(R.string.username_duplicate)
						errorText(binding.signupTextCheckingUsername)
					}
					throw Exception("uesrname duplicate")
				}
				
				withContext(Dispatchers.Main) {
					binding.signupTextCheckingUsername.text = context.getText(R.string.username_checked)
					finishText(binding.signupTextCheckingUsername)
					startText(binding.signupTextCheckingEmail)
				}
				
				val responseEmail = RetrofitInstance.api.checkEmail(signUpInfo.email)
				if (!responseEmail.isSuccessful || responseEmail.body() == true) {
					withContext(Dispatchers.Main) {
						binding.signupTextCheckingEmail.text = context.getText(R.string.email_duplicate)
						errorText(binding.signupTextCheckingEmail)
					}
					throw Exception("email duplicate")
				}
				
				withContext(Dispatchers.Main) {
					binding.signupTextCheckingEmail.text = context.getText(R.string.email_checked)
					finishText(binding.signupTextCheckingEmail)
					startText(binding.signupTextSendingEmail)
				}
				
				try {
					RetrofitInstance.api.sendValidationEmail(signUpInfo)
				} catch (ex: HttpException) {
					ex.printStackTrace()
					var response = GoResponse()
					kotlin.runCatching {
						response = ex.response()?.getHttpResponse()!!
					}
					if (response.code != 200) {
						withContext(Dispatchers.Main) {
							binding.signupTextSendingEmail.text = when (response.errorCode) {
								2 -> context.getText(R.string.email_already_sent)
								else -> context.getText(R.string.email_sent_fail)
							}
							errorText(binding.signupTextSendingEmail)
						}
						throw Exception(response.content)
					}
				}
				
				withContext(Dispatchers.Main) {
					binding.signupTextSendingEmail.text = context.getText(R.string.email_sent)
					finishText(binding.signupTextSendingEmail)
					binding.signupProgressBar.visibility = View.GONE
					binding.signupImageError.setImageResource(R.drawable.ic_baseline_check_24)
					binding.signupImageError.imageTintList = context.getColorStateList(R.color.green)
					binding.signupImageError.visibility = View.VISIBLE
					binding.signupButtonBack.visibility = View.VISIBLE
					dialog.setCancelable(true)
					success.value = true
				}
				
			} catch (ex: Exception) {
				ex.printStackTrace()
				if (ex is SocketTimeoutException) {
					currentRunning?.text = context.getString(R.string.timeout_error)
				}
				withContext(Dispatchers.Main) {
					binding.signupProgressBar.visibility = View.GONE
					binding.signupImageError.setImageResource(R.drawable.ic_baseline_close_24)
					binding.signupImageError.imageTintList = context.getColorStateList(R.color.red)
					binding.signupImageError.visibility = View.VISIBLE
					binding.signupButtonBack.visibility = View.VISIBLE
				}
			}
		}
		
	}
	
	private fun pendingText(view: TextView) {
		view.typeface = Typeface.DEFAULT
		view.setTextColor(context.getColor(R.color.gray_text))
	}
	
	private fun finishText(view: TextView) {
		view.typeface = Typeface.DEFAULT
		view.setTextColor(context.getColor(R.color.green))
	}
	
	private fun startText(view: TextView) {
		currentRunning = view
		view.typeface = Typeface.DEFAULT_BOLD
		view.setTextColor(context.getColor(R.color.normal_text))
	}
	
	private fun errorText(view: TextView) {
		view.typeface = Typeface.DEFAULT_BOLD
		view.setTextColor(context.getColor(R.color.red))
	}
}