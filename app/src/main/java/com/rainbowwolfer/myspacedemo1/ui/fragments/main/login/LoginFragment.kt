package com.rainbowwolfer.myspacedemo1.ui.fragments.main.login

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.content.res.AppCompatResources
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
import com.rainbowwolfer.myspacedemo1.util.SheetDialogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response

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
							if (response!!.isSuccessful) {
								user = response!!.body()
								if (user != null) {
									dialog.hideDialog()
									LoginActivity.Instance?.getBack(user!!)
								}
							}
						}
						if (jobLogin == null || response?.isSuccessful != true || user == null) {
							val r = response?.getHttpResponse()
							val errorMessage = when (r?.errorCode) {
								1 -> getString(R.string.your_registration_has_not_been_validated_through_email)
								2 -> getString(R.string.email_or_password_is_wrong)
								else -> getString(R.string.something_went_wrong_please_try_again_later)
							}
							dialog.hideDialog()
							AlertDialog.Builder(requireContext()).apply {
								setCancelable(false)
								setTitle(getString(R.string.error))
								setMessage(errorMessage)
								setIcon(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_close_24))
								setNegativeButton(getString(R.string.back), null)
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
		
		binding.loginButtonForgetPassword.setOnClickListener {
			SheetDialogUtils.showForgetPasswordDialog(requireContext())
		}
		
		binding.loginEditTextEmail.doAfterTextChanged {
			if (it.toString().isBlank()) {
				binding.loginEditTextEmail.error = getString(R.string.empty_email)
			} else {
				binding.loginEditTextEmail.error = null
			}
		}
		
		binding.loginEditTextPassword.doAfterTextChanged {
			if (it.toString().isBlank()) {
				binding.loginEditTextPassword.error = getString(R.string.empty_password)
			} else {
				binding.loginEditTextPassword.error = null
			}
		}
	}
	
	private fun checkParametersValid(): Boolean {
		var error = false
		if (binding.loginEditTextEmail.text?.isBlank() != false) {
			binding.loginEditTextEmail.error = getString(R.string.empty_email)
			error = true
		}
		if (binding.loginEditTextPassword.text?.isBlank() != false) {
			binding.loginEditTextPassword.error = getString(R.string.empty_password)
			error = true
		}
		return !error
	}
	
	fun fill(email: String?, password: String?) {
		if (email?.isNotBlank() == true) {
			binding.loginEditTextEmail.setText(email)
		}
		if (email?.isNotBlank() == true) {
			binding.loginEditTextPassword.setText(password)
		}
	}
}