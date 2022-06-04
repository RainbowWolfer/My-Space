package com.rainbowwolfer.myspacedemo1.services.repositories

import android.content.Context
import android.text.TextUtils
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.rainbowwolfer.myspacedemo1.UserPreferences
import com.rainbowwolfer.myspacedemo1.services.serializer.UserPreferencesSerializer
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepository(
	private val context: Context
) {
	companion object {
		fun UserPreferences.hasValue(): Boolean {
			return !TextUtils.isEmpty(this.email) && !TextUtils.isEmpty(this.password)
		}
	}
	
	private val Context.userDataStore: DataStore<UserPreferences> by dataStore(
		fileName = "settings.pb",
		serializer = UserPreferencesSerializer,
	)
	
	fun getValue(): Flow<UserPreferences> = context.userDataStore.data
	
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