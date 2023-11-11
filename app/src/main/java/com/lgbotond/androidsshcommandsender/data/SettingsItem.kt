package com.lgbotond.androidsshcommandsender.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

@Entity(tableName = "settings")
data class SettingsItem (
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "profileName") var profileName: String,
    @ColumnInfo(name = "address") var address: String,
    @ColumnInfo(name = "port") var port: Int,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "password") var password: ByteArray,
    @ColumnInfo(name = "initializationVector") var initializationVector: ByteArray,
    @ColumnInfo(name = "command") var command: String
)

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