package com.lgbotond.androidsshcommandsender

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lgbotond.androidsshcommandsender.Xtensions.fadeOut
import com.lgbotond.androidsshcommandsender.data.SettingsDatabase
import com.lgbotond.androidsshcommandsender.data.SettingsItem
import com.lgbotond.androidsshcommandsender.databinding.ActivityMainBinding
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

    private val ipv4AddressCheck = "^(\\d{1,3}\\.){3}\\d{1,3}\$".toRegex()
    private val ipv6AddressCheck = "^([\\da-f]{1,4}:){7}[\\da-f]{1,4}\$/i".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = SettingsDatabase.getDatabase(applicationContext)
        binding.btnSend.setOnClickListener { sendEvent() }

        loadSettings()
    }

    private fun sendEvent() {
        if(validateAddress((binding.etAddress.text.toString()))) {
            binding.tvStatus.setTextColor(getColor(R.color.success))
            binding.tvStatus.text = "SUCCESS"
            saveSettings()
        } else {
            binding.tvStatus.setTextColor(getColor(R.color.error))
            binding.tvStatus.text = "INVALID ADDRESS"
        }

        binding.tvStatus.fadeOut()
    }

    private fun saveSettings() = launch {
        withContext(Dispatchers.IO) {
            val newSettings = SettingsItem(
                profileName = "base",
                address = binding.etAddress.text.toString(),
                username = binding.etUsername.text.toString(),
                password = hashPassword(binding.etPassword.text.toString()),
                command = binding.etCommand.text.toString())

            val settingsList = database.settingsItemDao().getAll()
            if(settingsList.isNotEmpty()) {
                newSettings.id = 1
                database.settingsItemDao().update(newSettings)
            } else {
                database.settingsItemDao().insert(newSettings)
            }
        }
    }

    private fun loadSettings() = launch {
        val settings = withContext(Dispatchers.IO){
            database.settingsItemDao().getAll()
        }

        if(settings.isNotEmpty()) {

            binding.etAddress.setText(settings[0].address)
            binding.etUsername.setText(settings[0].username)
            binding.etPassword.setText(unHashPassword(settings[0].password))
            binding.etCommand.setText(settings[0].command)
        }
    }

    private fun hashPassword(password: String) : String {
        //TODO: this
        return password
    }

    private fun unHashPassword(hashedPassword: String) : String {
        //TODO: this
        return hashedPassword
    }

    private fun validateAddress(address: String) : Boolean {
        if (ipv4AddressCheck.matches(address)) {
            val parts = address.split('.')
            for (part in parts) {
                if (part.toInt() > 255) {
                    return false
                }
            }
            return true
        }
        if (ipv6AddressCheck.matches(address)) {
            val parts = address.split(':')
            for (part in parts) {
                if (part.length > 4) {
                    return false
                }
            }
            return true
        }
        return false
    }
}