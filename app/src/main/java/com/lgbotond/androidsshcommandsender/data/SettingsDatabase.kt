package com.lgbotond.androidsshcommandsender.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SettingsItem::class], version = 1)
abstract class SettingsDatabase : RoomDatabase() {
    companion object{
        fun getDatabase(applicationContext: Context): SettingsDatabase{
            return Room.databaseBuilder(
                applicationContext,
                SettingsDatabase::class.java,
                "settings"
            ).build()
        }
    }

    abstract fun settingsItemDao(): SettingsItemDao
}