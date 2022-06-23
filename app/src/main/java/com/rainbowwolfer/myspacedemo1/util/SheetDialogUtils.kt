package com.rainbowwolfer.myspacedemo1.util

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.BottomSheetCommentInputBinding
import com.rainbowwolfer.myspacedemo1.databinding.BottomSheetForgetPasswordBinding
import com.rainbowwolfer.myspacedemo1.databinding.BottomSheetRepostInputBinding
import com.rainbowwolfer.myspacedemo1.models.api.NewComment
import com.rainbowwolfer.myspacedemo1.models.api.NewRepost
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.views.LoadingDialog
import com.rainbowwolfer.myspacedemo1.ui.views.SuccessBackDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SheetDialogUtils {
	fun showRepostDialog(context: Context, postID: String, successAction: () -> Unit) {
		val application = MySpaceApplication.instance
		if (!application.hasLoggedIn()) {
			return
		}
		BottomSheetDialog(context, R.style.CustomizedBottomDialogStyle).apply {
			setOnShowListener {
				Handler(Looper.getMainLooper()).post {
					val bottomSheet = (this as? BottomSheetDialog)?.findViewById<View>(R.id.bottomSheetRepostDialog_root) as? FrameLayout?
					bottomSheet?.let {
						with(BottomSheetBehavior.from(it)) {
							skipCollapsed = true
							state = BottomSheetBehavior.STATE_EXPANDED
						}
					}
				}
			}
			setCanceledOnTouchOutside(true)
			show()
			
			val binding = BottomSheetRepostInputBinding.inflate(LayoutInflater.from(context))
			setContentView(binding.root)
			
			val imm = context.getSystemService(InputMethodManager::class.java)
			imm?.showSoftInput(binding.bottomSheetRepostDialogInput, InputMethodManager.SHOW_FORCED)
			binding.bottomSheetRepostDialogInput.requestFocus()
			
			binding.bottomSheetRepostDialogButtonSend.isEnabled = false
			binding.bottomSheetRepostDialogButtonBack.setOnClickListener {
				this.dismiss()
				this.hide()
			}
			binding.bottomSheetRepostDialogEditText.doAfterTextChanged {
				binding.bottomSheetRepostDialogButtonSend.isEnabled = !it.isNullOrEmpty() && it.length <= 200
			}
			
			binding.bottomSheetRepostDialogButtonSend.setOnClickListener {
				application.applicationScope.launch(Dispatchers.Main) {
					val dialog = LoadingDialog(context).apply {
						showDialog("Reposting")
					}
					try {
						withContext(Dispatchers.IO) {
							RetrofitInstance.api.repost(
								NewRepost(
									originPostID = postID,
									publisherID = application.currentUser.value!!.id,
									textContent = binding.bottomSheetRepostDialogEditText.text?.toString() ?: "",
									postVisibility = PostVisibility.All,
									replyLimit = PostVisibility.All,
									tags = listOf("tag1", "tag2", "tag3"),
									email = application.currentUser.value!!.email,
									password = application.currentUser.value!!.password,
								)
							)
						}
						
						successAction()
					} catch (ex: Exception) {
						ex.printStackTrace()
					} finally {
						dialog.hideDialog()
						dismiss()
						hide()
					}
				}
			}
		}
	}
	
	fun showCommentDialog(context: Context, postID: String, onCommented: () -> Unit) {
		val application = MySpaceApplication.instance
		BottomSheetDialog(context, R.style.CustomizedBottomDialogStyle).apply {
//			window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
			setOnShowListener {
				Handler(Looper.getMainLooper()).post {
					val bottomSheet = (this as? BottomSheetDialog)?.findViewById<View>(R.id.bottomSheetCommentDialog_root) as? FrameLayout?
					bottomSheet?.let {
						with(BottomSheetBehavior.from(it)) {
							skipCollapsed = true
							state = BottomSheetBehavior.STATE_EXPANDED
						}
					}
				}
			}
			
			setCanceledOnTouchOutside(true)
			show()
			
			val commentInputBinding = BottomSheetCommentInputBinding.inflate(LayoutInflater.from(context))
			setContentView(commentInputBinding.root)
			
			commentInputBinding.bottomSheetCommentDialogInput.requestFocus()
			val imm = context.getSystemService(InputMethodManager::class.java)
			imm?.showSoftInput(commentInputBinding.bottomSheetCommentDialogInput, InputMethodManager.SHOW_IMPLICIT)
			
			commentInputBinding.bottomSheetCommentDialogButtonSend.isEnabled = false
			commentInputBinding.bottomSheetCommentDialogButtonBack.setOnClickListener {
				this.dismiss()
				this.hide()
			}
			commentInputBinding.bottomSheetCommentDialogEditText.doAfterTextChanged {
				commentInputBinding.bottomSheetCommentDialogButtonSend.isEnabled = !it.isNullOrEmpty() && it.length <= 200
			}
			commentInputBinding.bottomSheetCommentDialogButtonSend.setOnClickListener {
				application.applicationScope.launch(Dispatchers.Main) {
					val dialog = LoadingDialog(context).apply {
						showDialog("Uploading Comment")
					}
					try {
						withContext(Dispatchers.IO) {
							val response = RetrofitInstance.api.postComment(
								NewComment(
									application.currentUser.value?.email ?: "",
									application.currentUser.value?.password ?: "",
									postID,
									commentInputBinding.bottomSheetCommentDialogEditText.text?.toString() ?: ""
								)
							)
							if (response.isSuccessful) {
								response.body()!!
							} else {
								throw Exception()
							}
						}
						onCommented.invoke()
					} catch (ex: Exception) {
						ex.printStackTrace()
					} finally {
						dialog.hideDialog()
						dismiss()
						hide()
					}
				}
			}
		}
	}
	
	
	fun showForgetPasswordDialog() {
		fun showRepostDialog(context: Context) {
			val application = MySpaceApplication.instance
			if (!application.hasLoggedIn()) {
				return
			}
			BottomSheetDialog(context, R.style.CustomizedBottomDialogStyle).apply {
				setOnShowListener {
					Handler(Looper.getMainLooper()).post {
						val bottomSheet = (this as? BottomSheetDialog)?.findViewById<View>(R.id.bottomSheetPasswordDialog_root) as? FrameLayout?
						bottomSheet?.let {
							with(BottomSheetBehavior.from(it)) {
								skipCollapsed = true
								state = BottomSheetBehavior.STATE_EXPANDED
							}
						}
					}
				}
				setCanceledOnTouchOutside(true)
				show()
				
				val binding = BottomSheetForgetPasswordBinding.inflate(LayoutInflater.from(context))
				setContentView(binding.root)
				
				binding.forgetPasswordDialogEditEmail.doAfterTextChanged {
					if (it.toString().isBlank()) {
						binding.forgetPasswordDialogInputEmail.error = context.getString(R.string.empty_email)
						return@doAfterTextChanged
					} else {
						binding.forgetPasswordDialogInputEmail.error = null
					}
					val email = binding.forgetPasswordDialogEditEmail.text.toString()
					val confirm = binding.forgetPasswordDialogEditConfirmEmail.text.toString()
					if (email == confirm || confirm.isEmpty()) {
						binding.forgetPasswordDialogInputEmail.error = null
					} else {
						binding.forgetPasswordDialogInputEmail.error = context.getString(R.string.confirm_email_is_not_matched)
					}
				}
				
				binding.forgetPasswordDialogEditConfirmEmail.doAfterTextChanged {
					if (it.toString().isBlank()) {
						binding.forgetPasswordDialogEditConfirmEmail.error = context.getString(R.string.empty_confirm_email)
						return@doAfterTextChanged
					}
					val email = binding.forgetPasswordDialogEditEmail.text.toString()
					val confirm = binding.forgetPasswordDialogEditConfirmEmail.text.toString()
					if (email == confirm) {
						binding.forgetPasswordDialogInputEmail.error = null
					} else {
						binding.forgetPasswordDialogInputEmail.error = context.getString(R.string.confirm_email_is_not_matched)
					}
				}
				
				binding.forgetPasswordDialogButtonSubmit.setOnClickListener {
					val email = binding.forgetPasswordDialogEditEmail.text.toString()
					val confirm = binding.forgetPasswordDialogEditConfirmEmail.text.toString()
					
					var error = false
					if (email.isBlank()) {
						binding.forgetPasswordDialogInputEmail.error = context.getString(R.string.empty_email)
						error = true
					}
					if (confirm.isBlank()) {
						binding.forgetPasswordDialogEditConfirmEmail.error = context.getString(R.string.empty_confirm_email)
						error = true
					}
					
					if (!error) {
						var success = true
						CoroutineScope(Dispatchers.Main).launch {
							val loadingDialog = LoadingDialog(context)
							try {
								loadingDialog.showDialog()
								withContext(Dispatchers.IO) {
									RetrofitInstance.api.sendResetPasswordEmail()
								}
							} catch (ex: Exception) {
								ex.printStackTrace()
								success = false
							} finally {
								try {
									loadingDialog.hideDialog()
									if (success) {
										val successDialog = SuccessBackDialog(context)
										successDialog.showDialog(context.getString(R.string.successfully_send_reset_password_email)){
											successDialog.hideDialog()
										}
									} else {
										AlertDialog.Builder(context).apply {
											setTitle(context.getString(R.string.error))
											setMessage(context.getString(R.string.error_sending_email))
											setNegativeButton(context.getString(R.string.back), null)
										}
									}
								} catch (ex: Exception) {
									ex.printStackTrace()
								}
							}
						}
					}
				}
			}
		}
	}
}