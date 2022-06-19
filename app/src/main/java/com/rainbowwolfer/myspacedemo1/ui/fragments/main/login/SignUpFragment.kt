package com.rainbowwolfer.myspacedemo1.ui.fragments.main.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentSignUpBinding
import com.rainbowwolfer.myspacedemo1.models.api.SignUpInfo
import com.rainbowwolfer.myspacedemo1.services.PasswordStrengthCalculator
import com.rainbowwolfer.myspacedemo1.ui.activities.login.LoginActivity
import com.rainbowwolfer.myspacedemo1.ui.views.SignUpProcessDialog

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
	private val binding: FragmentSignUpBinding by viewBinding()
	
	private var skip = false
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

//		binding.signupEditTextUsername.setText("rainbow_wolfer")
//		binding.signupEditTextEmail.setText("1519787190@qq.com")
//		binding.signupEditTextPassword.setText("123456789")
//		binding.signupEditTextConfirmPassword.setText("123456789")
		
		binding.signupEditTextUsername.doAfterTextChanged {
			if (skip) {
				return@doAfterTextChanged
			}
			val text = it.toString()
			binding.signupInputUsername.error = if (TextUtils.isEmpty(text)) {
				getString(R.string.username_cannot_be_empty)
			} else {
				null
			}
		}
		
		binding.signupEditTextEmail.doAfterTextChanged {
			if (skip) {
				return@doAfterTextChanged
			}
			val text = it.toString()
			val emailValidation = Patterns.EMAIL_ADDRESS.matcher(text).matches()
			binding.signupInputEmail.error = if (emailValidation) {
				null
			} else {
				getString(R.string.please_enter_a_valid_email_address)
			}
		}
		
		val passwordStrengthCalculator = PasswordStrengthCalculator()
		binding.signupEditTextPassword.addTextChangedListener(passwordStrengthCalculator)
		
		passwordStrengthCalculator.count.observe(viewLifecycleOwner) {
			if (skip) {
				return@observe
			}
			binding.signupInputPassword.error = when (it) {
				0 -> getString(R.string.password_cannot_be_empty)
				in 1..6 -> getString(R.string.password_must_be_at_least_7_length)
				else -> null
			}
			binding.signupInputConfirmPassword.error = null
		}
		
		passwordStrengthCalculator.strengthLevel.observe(viewLifecycleOwner) {
			if (skip) {
				return@observe
			}
			if (binding.signupInputPassword.error == null) {
				binding.signupInputPassword.helperText = it.toString()
				binding.signupInputPassword.endIconMode = TextInputLayout.END_ICON_CUSTOM
			}
		}
		
		passwordStrengthCalculator.strengthColor.observe(viewLifecycleOwner) {
			if (skip) {
				return@observe
			}
			if (binding.signupInputPassword.error == null) {
				val color = resources.getColorStateList(it, null)
				binding.signupInputPassword.setHelperTextColor(color)
				binding.signupInputPassword.setEndIconTintList(color)
				binding.signupInputPassword.endIconMode = TextInputLayout.END_ICON_CUSTOM
			}
		}
		
		binding.signupEditTextConfirmPassword.doAfterTextChanged {
			if (skip) {
				return@doAfterTextChanged
			}
			val password = binding.signupEditTextPassword.text.toString()
			if (TextUtils.isEmpty(password)) {
				binding.signupInputConfirmPassword.error = getString(R.string.no_password_entered_yet)
				return@doAfterTextChanged
			}
			if (it.toString() == password) {
				binding.signupInputConfirmPassword.error = null
			} else {
				binding.signupInputConfirmPassword.error = getString(R.string.confirm_password_is_not_matched)
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
				Snackbar.make(view, getString(R.string.you_have_empty_fields_to_fill), Snackbar.LENGTH_SHORT).show()
				return@setOnClickListener
			}
			
			val hasError = arrayListOf(
				binding.signupInputUsername,
				binding.signupInputEmail,
				binding.signupInputPassword,
				binding.signupInputConfirmPassword,
			).any { input -> input.error != null }
			
			if (hasError) {
				Snackbar.make(view, getString(R.string.you_have_error_fields_to_fix), Snackbar.LENGTH_SHORT).show()
				return@setOnClickListener
			}
			val dialog = SignUpProcessDialog(requireContext(), lifecycleScope).apply {
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
					skip = true
					val email = binding.signupEditTextEmail.text.toString()
					val password = binding.signupEditTextPassword.text.toString()
					binding.signupEditTextConfirmPassword.setText("")
					binding.signupEditTextPassword.setText("")
					binding.signupEditTextEmail.setText("")
					binding.signupEditTextUsername.setText("")
					binding.signupInputEmail.error = null
					binding.signupInputConfirmPassword.error = null
					binding.signupInputPassword.error = null
					binding.signupInputUsername.error = null
					binding.signupInputPassword.helperText = null
					binding.signupInputPassword.endIconMode = TextInputLayout.END_ICON_NONE
					LoginActivity.Instance?.toLoginFragment(email, password)
					skip = false
				}
			}
		}
	}
}