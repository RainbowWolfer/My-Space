package com.rainbowwolfer.myspacedemo1.util

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
import com.rainbowwolfer.myspacedemo1.databinding.BottomSheetRepostInputBinding
import com.rainbowwolfer.myspacedemo1.models.Comment
import com.rainbowwolfer.myspacedemo1.models.api.NewComment
import com.rainbowwolfer.myspacedemo1.models.api.NewRepost
import com.rainbowwolfer.myspacedemo1.models.enums.PostVisibility
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.ui.views.LoadingDialog
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
}