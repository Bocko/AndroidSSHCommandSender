package com.lgbotond.androidsshcommandsender

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lgbotond.androidsshcommandsender.databinding.ActivityMainBinding
import com.lgbotond.androidsshcommandsender.util.Utilities.validateIpAddress
import com.lgbotond.androidsshcommandsender.util.saveManager.SaveManager
import com.lgbotond.androidsshcommandsender.util.sshManager.SSHManager
import com.lgbotond.androidsshcommandsender.util.xtensions.fadeOut
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var binding: ActivityMainBinding

    private lateinit var saveManager : SaveManager
    private val sshManager = SSHManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveManager = SaveManager(binding.root.context)
        binding.btnSend.setOnClickListener { sendEvent() }

        saveManager.loadSettings(
            binding.etAddress,
            binding.etPort,
            binding.etUsername,
            binding.etPassword,
            binding.etCommand)
    }

    private fun sendEvent() {
        if(!checkInputFields()) {
            saveManager.saveSettings(
                binding.etAddress.text.toString(),
                binding.etPort.text.toString().toInt(),
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString(),
                binding.etCommand.text.toString())
            connectClientAndSendCommand()
        }
    }

    private fun connectClientAndSendCommand() = launch {
        withContext(Dispatchers.IO) {
            sshManager.sendCommand(
                binding.etAddress.text.toString(),
                binding.etPort.text.toString().toInt(),
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString(),
                binding.etCommand.text.toString())
        }
        binding.tvStatus.text = sshManager.getOutputText()
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