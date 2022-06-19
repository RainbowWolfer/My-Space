package com.rainbowwolfer.myspacedemo1.ui.fragments.main.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.rainbowwolfer.myspacedemo1.R
import com.rainbowwolfer.myspacedemo1.ui.activities.main.MainActivity


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
				MainActivity.Instance?.switchAppLanguage()
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
}