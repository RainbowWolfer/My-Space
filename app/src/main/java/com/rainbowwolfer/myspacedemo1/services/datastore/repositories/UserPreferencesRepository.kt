package com.rainbowwolfer.myspacedemo1.services.datastore.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.rainbowwolfer.myspacedemo1.UserPreferences
import com.rainbowwolfer.myspacedemo1.services.datastore.serializer.UserPreferencesSerializer
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepository(
	private val context: Context
) {
	companion object {
		fun UserPreferences.hasUserValue(): Boolean {
			return this.email.isNotBlank() && this.password.isNotBlank()
		}
	}
	
	private val Context.userDataStore: DataStore<UserPreferences> by dataStore(
		fileName = "settings.pb",
		serializer = UserPreferencesSerializer,
	)
	
	fun getValue(): Flow<UserPreferences> = context.userDataStore.data
	
	suspend fun updateLanguage(language: UserPreferences.Languages) {
		context.userDataStore.updateData {
			it.toBuilder().setLanguage(language).build()
		}
	}
	
	suspend fun updateUser(email: String, password: String) {
		context.userDataStore.updateData {
			it.toBuilder().setPassword(password).setEmail(email).build()
		}
	}
	
	suspend fun clearUser() {
		context.userDataStore.updateData {
			it.toBuilder().setPassword("").setEmail("").build()
		}
	}
	
	suspend fun updateSkip(skip: Boolean) {
		context.userDataStore.updateData {
			it.toBuilder().setSkip(skip).build()
		}
	}
}