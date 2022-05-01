package com.rainbowwolfer.myspacedemo1.ui.fragments.main.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.rainbowwolfer.myspacedemo1.R

class SettingsFragment : PreferenceFragmentCompat() {
	
	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.root_preferences, rootKey)
	}
}