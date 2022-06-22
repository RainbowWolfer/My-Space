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
				MainActivity.Instance?.switchAppLanguage()
			}
			true
		}
	}
}