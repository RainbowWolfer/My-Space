package com.rainbowwolfer.myspacedemo1.ui.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.LoadingDialogBinding

class LoadingDialog(
	private val context: Context,
	private val showCancelButton: Boolean = false,
) {
	private lateinit var dialog: Dialog
	private lateinit var binding: LoadingDialogBinding
	
	fun showDialog(content: String? = null) {
		binding = LoadingDialogBinding.inflate(LayoutInflater.from(context))
		dialog = Dialog(context).apply {
			setContentView(binding.root)
			setCancelable(false)
			window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		}
		
		if (!TextUtils.isEmpty(content)) {
			binding.loadingDialogTextContent.text = content
		}
		binding.loadingDialogButtonCancel.visibility = if (showCancelButton) View.VISIBLE else View.GONE
		
		dialog.run {
			create()
			show()
		}
	}
	
	fun hideDialog() {
		if (!dialog.isShowing) {
			return
		}
		dialog.hide()
	}
	
	fun dismiss() {
		dialog.dismiss()
	}
}