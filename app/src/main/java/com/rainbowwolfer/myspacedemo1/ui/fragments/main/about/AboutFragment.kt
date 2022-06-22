package com.rainbowwolfer.myspacedemo1.ui.fragments.main.about

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.databinding.FragmentAboutBinding
import com.rainbowwolfer.myspacedemo1.models.exceptions.ResponseException
import com.rainbowwolfer.myspacedemo1.services.api.RetrofitInstance
import com.rainbowwolfer.myspacedemo1.services.application.AppVersion
import com.rainbowwolfer.myspacedemo1.services.application.MySpaceApplication
import com.rainbowwolfer.myspacedemo1.util.EasyFunctions.getHttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutFragment : Fragment(R.layout.fragment_about) {
	private val binding: FragmentAboutBinding by viewBinding()
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.aboutTextVersion.text = getString(R.string.version, MySpaceApplication.instance.appVersion.toString())
		
		binding.aboutButtonUpdate.setOnClickListener {
			lifecycleScope.launch(Dispatchers.IO) {
				try {
					val response = RetrofitInstance.api.getAppVersion()
					if (response.isSuccessful) {
						val version = response.body()!!.version
						val new = AppVersion.parse(version) ?: throw Exception()
						withContext(Dispatchers.Main) {
							when (MySpaceApplication.instance.appVersion.compare(new)) {
								AppVersion.CompareResult.Greater -> {
									Toast.makeText(requireContext(), getString(R.string.you_need_to_update_the_app), Toast.LENGTH_SHORT).show()
								}
								AppVersion.CompareResult.Same -> {
									Toast.makeText(requireContext(), getString(R.string.already_the_newest_version), Toast.LENGTH_SHORT).show()
								}
								AppVersion.CompareResult.Less -> {
									Toast.makeText(requireContext(), getString(R.string.you_are_ahead_of_the_newest_verion), Toast.LENGTH_SHORT).show()
								}
							}
						}
					} else {
						throw ResponseException(response.getHttpResponse())
					}
				} catch (ex: Exception) {
					ex.printStackTrace()
					if (ex is ResponseException) {
						ex.printResponseException()
					}
				}
			}
		}
	}
}