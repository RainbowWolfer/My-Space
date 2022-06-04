package com.rainbowwolfer.myspacedemo1.services.serializer

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.rainbowwolfer.myspacedemo1.UserPreferences
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object UserPreferencesSerializer : Serializer<UserPreferences> {
	override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()
	override suspend fun readFrom(input: InputStream): UserPreferences {
		try {
			return UserPreferences.parseFrom(input)
		} catch (exception: InvalidProtocolBufferException) {
			throw CorruptionException("Cannot read proto.", exception)
		}
	}
	
	override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
		t.writeTo(output)
	}
}