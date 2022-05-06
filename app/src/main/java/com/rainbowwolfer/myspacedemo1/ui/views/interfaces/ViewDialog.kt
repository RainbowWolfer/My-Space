package com.rainbowwolfer.myspacedemo1.ui.views.interfaces

import android.app.Dialog
import android.content.Context

interface ViewDialog {
	val context: Context
	val dialog: Dialog
	fun hideDialog() {
		if (!dialog.isShowing) {
			return
		}
		dialog.dismiss()
		dialog.hide()
	}
}