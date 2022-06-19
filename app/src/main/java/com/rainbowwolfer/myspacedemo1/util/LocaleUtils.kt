package com.rainbowwolfer.myspacedemo1.util

import android.content.Context
import android.os.LocaleList
import androidx.preference.PreferenceManager
import java.util.*


object LocaleUtils {
	enum class Language {
		English, Chinese
	}
	
	fun notifyLanguageChanged(context: Context) {
		val metrics = context.resources.displayMetrics
		val configuration = context.resources.configuration
		context.resources.updateConfiguration(configuration, metrics)
	}
	
	fun attachBaseContext(context: Context?): Context? {
		return updateResources(context ?: return null)
	}
	
	private fun updateResources(context: Context): Context? {
		val locale = getLanguagePreference(context)
		val configuration = context.resources.configuration
		configuration.setLocale(locale)
		configuration.setLocales(LocaleList(locale))
		return context.createConfigurationContext(configuration)
	}
	
	fun getLanguagePreference(context: Context): Locale {
		val pre = PreferenceManager.getDefaultSharedPreferences(context)
		return when (pre.getString("language", "en")) {
			"en" -> Locale.ENGLISH
			"cn" -> Locale.SIMPLIFIED_CHINESE
			else -> Locale.ENGLISH
		}
	}
}