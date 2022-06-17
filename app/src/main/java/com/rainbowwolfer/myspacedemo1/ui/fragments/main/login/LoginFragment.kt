package com.rainbowwolfer.myspacedemo1.ui.fragments.main.login

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentLoginBinding
import com.rainbowwolfer.myspacedemo1.models.User
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.ui.activities.login.LoginActivity
import com.rainbowwolfer.myspacedemo1.ui.views.LoadingDialog
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.getHttpResponse
import kotlinx.coroutines.*
import retrofit2.Response
import java.lang.Exception

class LoginFragment : Fragment(R.layout.fragment_login) {
	private val binding: FragmentLoginBinding by viewBinding()
	private var isLoading = false
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.loginButtonLogin.setOnClickListener {
			if (isLoading || !checkParametersValid()) {
				return@setOnClickListener
			}
			lifecycleScope.launch(Dispatchers.IO) {
				try {
					isLoading = true
					var response: Response<User>? = null
					var user: User? = null
					val dialog = LoadingDialog(requireContext())
					withContext(Dispatchers.Main) {
						val jobLogin = withTimeoutOrNull(3000) {
							dialog.showDialog()
							val email = binding.loginEditTextEmail.text.toString()
							val password = binding.loginEditTextPassword.text.toString()
							response = RetrofitInstance.api.tryLogin(email, password)
							println(response)
							if (response!!.isSuccessful) {
								user = response!!.body()
								println(user)
								if (user != null) {
									dialog.hideDialog()
									LoginActivity.Instance?.getBack(user!!)
								}
							}
						}
						if (jobLogin == null || response?.isSuccessful != true || user == null) {
							println("result $jobLogin - ${response?.isSuccessful} - $user")
							val r = response?.getHttpResponse()
							val errorMessage = when (r?.errorCode) {
								1 -> "Your registration has not been validated through email"
								2 -> "Email or Password is wrong"
								else -> "Something went wrong. Please try agian later"
							}
							dialog.hideDialog()
							AlertDialog.Builder(requireContext()).apply {
								setCancelable(false)
								setTitle("Error")
								setMessage(errorMessage)
								setNegativeButton("Back", null)
								show()
							}
						}
					}
					isLoading = false
				} catch (ex: Exception) {
					ex.printStackTrace()
				}
			}
		}
		
		binding.loginEditTextEmail.doAfterTextChanged {
			if (TextUtils.isEmpty(it.toString())) {
				binding.loginEditTextEmail.error = "Empty Username"
			} else {
				binding.loginEditTextEmail.error = null
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
		if (TextUtils.isEmpty(binding.loginEditTextEmail.text)) {
			binding.loginEditTextEmail.error = "Empty Username"
			error = true
		}
		if (TextUtils.isEmpty(binding.loginEditTextPassword.text)) {
			binding.loginEditTextPassword.error = "Empty Password"
			error = true
		}
		return !error
	}
	
	fun fill(email: String?, password: String?) {
		if (!TextUtils.isEmpty(email)) {
			binding.loginEditTextEmail.setText(email)
		}
		if (!TextUtils.isEmpty(password)) {
			binding.loginEditTextPassword.setText(password)
		}
	}
}