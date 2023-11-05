package com.lgbotond.androidsshcommandsender

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lgbotond.androidsshcommandsender.Xtensions.fadeOut
import com.lgbotond.androidsshcommandsender.data.SettingsDatabase
import com.lgbotond.androidsshcommandsender.data.SettingsItem
import com.lgbotond.androidsshcommandsender.databinding.ActivityMainBinding
import com.lgbotond.androidsshcommandsender.util.cryptoManager.CryptoManager
import com.lgbotond.androidsshcommandsender.util.Utilities.validateIpAddress
import com.lgbotond.androidsshcommandsender.util.cryptoManager.EncryptedBytesContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: SettingsDatabase

    private val cryptoManager = CryptoManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = SettingsDatabase.getDatabase(applicationContext)
        binding.btnSend.setOnClickListener { sendEvent() }

        loadSettings()
    }

    private fun sendEvent() {
        if(!checkInputFields()) {
            saveSettings()
        }
    }

    private fun saveSettings() = launch {
        withContext(Dispatchers.IO) {
            val encryptedBytesContainer = encryptPassword(binding.etPassword.text.toString().encodeToByteArray())
            val newSettings = SettingsItem (
                profileName = BASE_PROFILE_NAME,
                address = binding.etAddress.text.toString(),
                port = binding.etPort.text.toString().toInt(),
                username = binding.etUsername.text.toString(),
                password = encryptedBytesContainer.encryptedBytes,
                initializationVector = encryptedBytesContainer.initializationVector,
                command = binding.etCommand.text.toString()
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

    private fun loadSettings() = launch {
        val settings = withContext(Dispatchers.IO) {
            database.settingsItemDao().getAll()
        }

        if(settings.isNotEmpty()) {
            val encryptedBytesContainer = EncryptedBytesContainer(settings[0].password,settings[0].initializationVector)
            val password = decryptPassword(encryptedBytesContainer)

            binding.etAddress.setText(settings[0].address)
            binding.etPort.setText(settings[0].port.toString())
            binding.etUsername.setText(settings[0].username)
            binding.etPassword.setText(password)
            binding.etCommand.setText(settings[0].command)
        }
    }

    private fun encryptPassword(password: ByteArray) : EncryptedBytesContainer {
        return cryptoManager.encrypt(password)
    }

    private fun decryptPassword(encryptedBytesContainer: EncryptedBytesContainer) : String {
        return cryptoManager.decrypt(encryptedBytesContainer).decodeToString()
    }

    private fun checkInputFields() : Boolean {
        var hasError = false

        if (binding.etAddress.text.isEmpty()) {
            binding.etAddress.error = getString(R.string.address_error_empty)
            hasError = true
        } else if (!validateIpAddress((binding.etAddress.text.toString()))) {
            binding.etAddress.error = getString(R.string.address_error_invalid)
            hasError = true
        }

        if(binding.etAddress.text.isEmpty()) {
            binding.etAddress.error = getString(R.string.address_error_empty)
            hasError = true
        }

        if(binding.etPort.text.isEmpty()) {
            binding.etPort.error = getString(R.string.port_error_empty)
            hasError = true
        }

        if(binding.etUsername.text.isEmpty()) {
            binding.etUsername.error = getString(R.string.username_error_empty)
            hasError = true
        }

        if(binding.etPassword.text.isEmpty()) {
            binding.etPassword.error = getString(R.string.password_error_empty)
            hasError = true
        }

        if(binding.etCommand.text.isEmpty()) {
            binding.etCommand.error = getString(R.string.command_error_empty)
            hasError = true
        }

        if(hasError) {
            binding.tvStatus.text = getString(R.string.general_error)
            binding.tvStatus.setTextColor(getColor(R.color.error))
            binding.tvStatus.fadeOut()
        }

        return hasError
    }
}