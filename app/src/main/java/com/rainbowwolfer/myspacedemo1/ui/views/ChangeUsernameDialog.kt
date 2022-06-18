package com.rainbowwolfer.myspacedemo1.ui.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import androidx.core.widget.doAfterTextChanged
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.LayoutChangeUsernameBinding
import com.rainbowwolfer.myspacedemo1.services.callbacks.ActionCallBack
import com.rainbowwolfer.myspacedemo1.ui.views.interfaces.ViewDialog

class ChangeUsernameDialog(
	override val context: Context,
	private val actionCallBack: ActionCallBack<String>
) : ViewDialog {
	override lateinit var dialog: Dialog
	private lateinit var binding: LayoutChangeUsernameBinding
	private lateinit var originUsername: String
	
	fun showDialog(originUsername: String) {
		this.originUsername = originUsername
		binding = LayoutChangeUsernameBinding.inflate(LayoutInflater.from(context))
		dialog = Dialog(context).apply {
			setContentView(binding.root)
			setCancelable(true)
			window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		}
		
		binding.newUsernameEditTextNew.doAfterTextChanged {
			val text = it.toString()
			var enable = true
			var errorMessage = ""
			when {
				TextUtils.isEmpty(text) -> {
					enable = false
					errorMessage = "Username cannot be empty"
				}
				originUsername == text -> {
					enable = false
					errorMessage = "Cannot be the same as before"
				}
				text.length > 20 -> {
					enable = false
					errorMessage = "Max length is set to 20"
				}
			}
			
			if (!enable) {
				binding.newUsernameInputNew.error = errorMessage
				binding.newUsernameButtonConfirm.isEnabled = false
			} else {
				binding.newUsernameInputNew.error = null
				binding.newUsernameButtonConfirm.isEnabled = true
			}
		}
		
		binding.newUsernameButtonConfirm.setOnClickListener {
			dialog.setCancelable(false)
			binding.newUsernameTextTitle.text = context.getString(R.string.updating)
			binding.newUsernameButtonBack.isEnabled = false
			binding.newUsernameButtonConfirm.isEnabled = false
			binding.newUsernameInputNew.isEnabled = false
			binding.newUsernameMotionLayoutRoot.transitionToEnd()
			actionCallBack.action(binding.newUsernameEditTextNew.text.toString())
		}
		
		binding.newUsernameButtonBack.setOnClickListener {
			hideDialog()
		}
		
		dialog.run {
			create()
			show()
		}
	}
}