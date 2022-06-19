package com.rainbowwolfer.myspacedemo1.ui.fragments.main.settings

import android.content.Intent
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.ui.activities.main.MainActivity
import com.rainbowwolfer.myspacedemo1.util.LocaleUtils


class SettingsFragment : PreferenceFragmentCompat() {
	
	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.root_preferences, rootKey)
		val list = findPreference<ListPreference>("language") ?: return
		list.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
			val index: Int = list.findIndexOfValue(newValue.toString())
			if (index != -1) {
//				val value = list.entryValues[index].toString()
//				Toast.makeText(
//					requireContext(),
//					"" + when (value) {
//						"cn" -> ""
//						"en" -> ""
//						else -> ""
//					}, Toast.LENGTH_LONG
//				).show()
				switchAppLanguage()
//				when (value) {
//					"cn" -> {
//						switchAppLanguage(LocaleUtils.Language.Chinese)
////						changeLanguage(requireContext(), Locale.SIMPLIFIED_CHINESE)
//					}
//					"en" -> {
//						switchAppLanguage(LocaleUtils.Language.English)
////						changeLanguage(requireContext(), Locale.ENGLISH)
//					}
//				}
			}
			true
		}
	}
	
	private fun switchAppLanguage() {
		LocaleUtils.notifyLanguageChanged(requireContext())
		val intent = Intent(requireContext(), MainActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
		startActivity(intent)
	}
}