package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentLoginBinding
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.ui.activities.user.LoginActivity
import com.rainbowwolfer.myspacedemo1.ui.views.LoadingDialog
import kotlinx.coroutines.*

class LoginFragment(
	private val loginActivity: LoginActivity,
) : Fragment(R.layout.fragment_login) {
	private val binding: FragmentLoginBinding by viewBinding()
	private var isLoading = false
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.loginButtonLogin.setOnClickListener {
			if (isLoading || !checkParametersValid()) {
				return@setOnClickListener
			}
			val dialog = LoadingDialog(requireContext()).apply {
				showDialog()
			}
			CoroutineScope(Dispatchers.IO).launch {
				isLoading = true
				val jobLogin = withTimeoutOrNull(3000) {
					delay(1500)
					//check username & password
					withContext(Dispatchers.Main) {
						loginActivity.getBack(User.getTestLogUser())
					}
				}
				if (jobLogin == null) {
					withContext(Dispatchers.Main) {
						dialog.hideDialog()
						AlertDialog.Builder(requireContext()).apply {
							setCancelable(false)
							setTitle("Error")
							setMessage("Sign in action timed out")
							setNegativeButton("Back", null)
							show()
						}
					}
				}
				isLoading = false
			}
		}
		
		binding.loginEditTextUsername.doAfterTextChanged {
			if (TextUtils.isEmpty(it.toString())) {
				binding.loginEditTextUsername.error = "Empty Username"
			} else {
				binding.loginEditTextUsername.error = null
			}
		}
		
		binding.loginEditTextPassword.doAfterTextChanged {
			if (TextUtils.isEmpty(it.toString())) {
				binding.loginEditTextPassword.error = "Empty Password"
			} else {
				binding.loginEditTextPassword.error = null
			}
		}
	}
	
	private fun checkParametersValid(): Boolean {
		var error = false
		if (TextUtils.isEmpty(binding.loginEditTextUsername.text)) {
			binding.loginEditTextUsername.error = "Empty Username"
			error = true
		}
		if (TextUtils.isEmpty(binding.loginEditTextPassword.text)) {
			binding.loginEditTextPassword.error = "Empty Password"
			error = true
		}
		return !error
		
	}
}

/**
 * --- Coroutine Sample Code ---
 *
 * CoroutineScope(Dispatchers.IO).launch {
val jobLogin = withTimeoutOrNull(2000) {
println("STARTING!")
delay(1900)
println("OK!")
withContext(Dispatchers.Main) {
loginActivity.getBack()
}
}
val cancellableJob = launch {

}
cancellableJob.join()
cancellableJob.cancel()
if (jobLogin == null) {
//canceled
}
}
 *
 *
 *
 */