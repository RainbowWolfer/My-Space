package com.rainbowwolfer.myspacedemo1.ui.fragments.main.user

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.ui.activities.user.LoginActivity
import com.rainbowwolfer.myspacedemo1.databinding.FragmentSignUpBinding
import com.rainbowwolfer.myspacedemo1.models.api.SignUpInfo
import com.rainbowwolfer.myspacedemo1.services.PasswordStrengthCalculator
import com.rainbowwolfer.myspacedemo1.ui.views.SignUpProcessDialog

class SignUpFragment(
	private val loginActivity: LoginActivity,
) : Fragment(R.layout.fragment_sign_up) {
	private val binding: FragmentSignUpBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		loginActivity.loginFragment = null
		loginActivity.signupFragment = this
		
		binding.signupEditTextUsername.setText("rainbow_wolfer")
		binding.signupEditTextEmail.setText("1519787190@qq.com")
		binding.signupEditTextPassword.setText("123456789")
		binding.signupEditTextConfirmPassword.setText("123456789")
		
		binding.signupEditTextUsername.doAfterTextChanged {
			val text = it.toString()
			binding.signupInputUsername.error = if (TextUtils.isEmpty(text)) {
				"Username cannot be empty"
			} else {
				null
			}
		}
		
		binding.signupEditTextEmail.doAfterTextChanged {
			val text = it.toString()
			val emailValidation = Patterns.EMAIL_ADDRESS.matcher(text).matches()
			binding.signupInputEmail.error = if (emailValidation) {
				null
			} else {
				"Please enter a valid email address"
			}
		}
		
		val passwordStrengthCalculator = PasswordStrengthCalculator()
		binding.signupEditTextPassword.addTextChangedListener(passwordStrengthCalculator)
		
		passwordStrengthCalculator.count.observe(viewLifecycleOwner) {
			binding.signupInputPassword.error = when (it) {
				0 -> "Password cannot be empty"
				in 1..6 -> "Password must be at least 7 length"
				else -> null
			}
			binding.signupInputConfirmPassword.error = null
		}
		
		passwordStrengthCalculator.strengthLevel.observe(viewLifecycleOwner) {
			if (binding.signupInputPassword.error == null) {
				binding.signupInputPassword.helperText = it.toString()
				binding.signupInputPassword.endIconMode = TextInputLayout.END_ICON_CUSTOM
			}
		}
		
		passwordStrengthCalculator.strengthColor.observe(viewLifecycleOwner) {
			if (binding.signupInputPassword.error == null) {
				val color = resources.getColorStateList(it, null)
				binding.signupInputPassword.setHelperTextColor(color)
				binding.signupInputPassword.setEndIconTintList(color)
				binding.signupInputPassword.endIconMode = TextInputLayout.END_ICON_CUSTOM
			}
		}
		
		binding.signupEditTextConfirmPassword.doAfterTextChanged {
			val password = binding.signupEditTextPassword.text.toString()
			if (TextUtils.isEmpty(password)) {
				binding.signupInputConfirmPassword.error = "No password entered yet"
				return@doAfterTextChanged
			}
			if (it.toString() == password) {
				binding.signupInputConfirmPassword.error = null
			} else {
				binding.signupInputConfirmPassword.error = "Confirm password is not matched"
			}
		}
		
		binding.signupButtonConfirm.setOnClickListener {
			val hasEmpty = arrayListOf(
				binding.signupEditTextUsername,
				binding.signupEditTextEmail,
				binding.signupEditTextPassword,
				binding.signupEditTextConfirmPassword,
			).any { input -> TextUtils.isEmpty(input.text.toString()) }
			
			if (hasEmpty) {
				Snackbar.make(view, "You have empty field(s) to fill", Snackbar.LENGTH_SHORT).show()
				return@setOnClickListener
			}
			
			val hasError = arrayListOf(
				binding.signupInputUsername,
				binding.signupInputEmail,
				binding.signupInputPassword,
				binding.signupInputConfirmPassword,
			).any { input -> input.error != null }
			
			if (hasError) {
				Snackbar.make(view, "You have error field(s) to fix", Snackbar.LENGTH_SHORT).show()
				return@setOnClickListener
			}
			val dialog = SignUpProcessDialog(requireContext()).apply {
				showDialog(
					SignUpInfo(
						binding.signupEditTextUsername.text.toString(),
						binding.signupEditTextPassword.text.toString(),
						binding.signupEditTextEmail.text.toString()
					)
				)
			}
			dialog.dialog.setOnDismissListener {
				if (dialog.success.value == true) {
					val email = binding.signupEditTextEmail.text.toString()
					val password = binding.signupEditTextPassword.text.toString()
					binding.signupEditTextConfirmPassword.setText("")
					binding.signupEditTextPassword.setText("")
					binding.signupEditTextEmail.setText("")
					binding.signupEditTextUsername.setText("")
					loginActivity.toLoginFragment(email, password)
				}
			}
		}
	}
}