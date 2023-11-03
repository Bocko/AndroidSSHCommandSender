package com.lgbotond.androidsshcommandsender.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SettingsItemDao {
    @Query("SELECT * FROM settings")
    suspend fun getAll(): List<SettingsItem>
    @Insert
    suspend fun insert(settingsItem: SettingsItem): Long
    @Update
    suspend fun update(settingsItem: SettingsItem)
    @Delete
    suspend fun delete(settingsItem: SettingsItem)
}
