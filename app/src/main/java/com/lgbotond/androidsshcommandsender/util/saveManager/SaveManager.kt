package com.lgbotond.androidsshcommandsender.util.saveManager

import android.content.Context
import android.widget.EditText
import com.lgbotond.androidsshcommandsender.data.SettingsDatabase
import com.lgbotond.androidsshcommandsender.data.SettingsItem
import com.lgbotond.androidsshcommandsender.util.cryptoManager.CryptoManager
import com.lgbotond.androidsshcommandsender.util.cryptoManager.EncryptedBytesContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SaveManager(context: Context) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    companion object {
        const val BASE_PROFILE_NAME = "base"
        const val BASE_PROFILE_DB_INDEX = 1L
    }

    private val database: SettingsDatabase
    private val cryptoManager = CryptoManager()

    init {
        database = SettingsDatabase.getDatabase(context)
    }

    fun saveSettings(address: String, port: Int, username: String, password: String, command: String) = launch {
        withContext(Dispatchers.IO) {

            val encryptedBytesContainer = encryptPassword(password.encodeToByteArray())
            val newSettings = SettingsItem (
                profileName = BASE_PROFILE_NAME,
                address = address,
                port = port,
                username = username,
                password = encryptedBytesContainer.encryptedBytes,
                initializationVector = encryptedBytesContainer.initializationVector,
                command = command
            )

            val settingsList = database.settingsItemDao().getAll()
            if(settingsList.isNotEmpty()) {
                newSettings.id = BASE_PROFILE_DB_INDEX
                database.settingsItemDao().update(newSettings)
            } else {
                database.settingsItemDao().insert(newSettings)
            }
        }
    }

    fun loadSettings(addressField: EditText, portField: EditText, usernameField: EditText, passwordField: EditText, commandField: EditText) = launch {
        val settings = withContext(Dispatchers.IO) {
            database.settingsItemDao().getAll()
        }

        if(settings.isNotEmpty()) {
            val encryptedBytesContainer = EncryptedBytesContainer(settings[0].password,settings[0].initializationVector)
            val password = decryptPassword(encryptedBytesContainer)

            addressField.setText(settings[0].address)
            portField.setText(settings[0].port.toString())
            usernameField.setText(settings[0].username)
            passwordField.setText(password)
            commandField.setText(settings[0].command)
        }
    }

    private fun encryptPassword(password: ByteArray) : EncryptedBytesContainer {
        return cryptoManager.encrypt(password)
    }

    private fun decryptPassword(encryptedBytesContainer: EncryptedBytesContainer) : String {
        return cryptoManager.decrypt(encryptedBytesContainer).decodeToString()
    }

}