package com.rainbowwolfer.myspacedemo1.services.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rainbowwolfer.myspacedemo1.models.Draft
import com.rainbowwolfer.myspacedemo1.models.Message
import com.rainbowwolfer.myspacedemo1.models.MessageContact
import com.rainbowwolfer.myspacedemo1.services.room.dao.DraftsDao
import com.rainbowwolfer.myspacedemo1.services.room.dao.MessageContactsDao
import com.rainbowwolfer.myspacedemo1.services.room.dao.MessagesDao

@Database(
	entities = [
		Draft::class,
		Message::class,
		MessageContact::class,
	], version = 2, exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun draftsDao(): DraftsDao
	abstract fun messagesDao(): MessagesDao
	abstract fun messagesContactDao(): MessageContactsDao

	companion object {
		@Volatile
		private var instance: AppDatabase? = null
		
		fun getDatabase(context: Context): AppDatabase {
			return instance ?: synchronized(this) {
				val buildInstance = Room.databaseBuilder(
					context,
					AppDatabase::class.java,
					"myspace_app_database"
				).fallbackToDestructiveMigration().build()
				this.instance = buildInstance
				buildInstance
			}
		}
	}
}