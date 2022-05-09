package com.rainbowwolfer.myspacedemo1.ui.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.rainbowwolfer.myspacedemo1.databinding.DialogSuccessBackLayoutBinding
import com.rainbowwolfer.myspacedemo1.ui.views.interfaces.ViewDialog

class SuccessBackDialog(
	override val context: Context,
) : ViewDialog {
	override lateinit var dialog: Dialog
	private lateinit var binding: DialogSuccessBackLayoutBinding
	
	fun showDialog(title: String? = null, onClickListener: View.OnClickListener) {
		binding = DialogSuccessBackLayoutBinding.inflate(LayoutInflater.from(context))
		dialog = Dialog(context).apply {
			setContentView(binding.root)
			setCancelable(false)
			window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			create()
			show()
		}
		if (!TextUtils.isEmpty(title)) {
			binding.successBackDialogTextTitle.text = title
		}
		binding.successBackDialogButtonBack.setOnClickListener {
			onClickListener.onClick(it)
		}
	}
}