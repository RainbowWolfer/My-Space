package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.ui.activities.user.LoginActivity
import com.rainbowwolfer.myspacedemo1.databinding.FragmentLoginBinding
import kotlinx.coroutines.*

class LoginFragment(
	private val loginActivity: LoginActivity,
) : Fragment(R.layout.fragment_login) {
	private val binding: FragmentLoginBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.loginButtonLogin.setOnClickListener {
			val pg = ProgressDialog(requireContext())
			pg.setTitle("加载.")
			pg.setMessage("请稍等...")
			pg.show()
//			Handler(Looper.getMainLooper()).postDelayed({ pg.hide() }, 2000)
			CoroutineScope(Dispatchers.IO).launch {
				val jobLogin = withTimeoutOrNull(2000) {
					delay(1900)
					withContext(Dispatchers.Main) {
						loginActivity.getBack()
					}
				}
				if (jobLogin == null) {
					//canceled
				}
			}
		}
	}
}

/**
 * Coroutine Sample Code
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