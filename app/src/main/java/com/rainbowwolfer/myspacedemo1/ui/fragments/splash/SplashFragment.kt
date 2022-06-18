package com.rainbowwolfer.myspacedemo1.ui.fragments.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentSplashBinding
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.services.datastore.repositories.UserPreferencesRepository.Companion.hasValue
import com.rainbowwolfer.myspacedemo1.ui.activities.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashFragment : Fragment(R.layout.fragment_splash) {
	private val binding: FragmentSplashBinding by viewBinding()
	private val application = MySpaceApplication.instance
	
	private var skip = false
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		val data = application.userPreferencesRepository.getValue()
		data.asLiveData().observe(viewLifecycleOwner) {
			skip = it.skip
			if (it.hasValue()) {
				lifecycleScope.launch(Dispatchers.IO) {
					try {
						val response = RetrofitInstance.api.tryLogin(it.email, it.password)
						val user = response.body()!!
						withContext(Dispatchers.Main) {
							application.currentUser.value = user
						}
					} catch (ex: Exception) {
						ex.printStackTrace()
					}
				}
			}
			lifecycleScope.launch(Dispatchers.Main) {
				delay(1000)
				loadNext()
			}
		}
	}
	
	private fun loadNext() {
		if (skip) {
			jumpToMainActivity(this)
		} else {
			findNavController().navigate(R.id.action_splashFragment_to_welcomeViewPagerFragment)
		}
	}
	
	companion object {
		
		fun jumpToMainActivity(fragment: Fragment) {
			fragment.lifecycleScope.launch(Dispatchers.Main) {
				MySpaceApplication.instance.userPreferencesRepository.updateSkip(true)
			}
			fragment.startActivity(Intent(fragment.requireContext(), MainActivity::class.java))
			fragment.activity?.finish()
		}
	}
}