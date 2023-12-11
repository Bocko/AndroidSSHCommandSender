package com.lgbotond.androidsshcommandsender.util.sshManager

import android.content.Context
import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import com.lgbotond.androidsshcommandsender.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.coroutines.CoroutineContext

class SSHManager(private val context: Context) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    companion object {
        const val MAX_CONNECTION_TIMEOUT = 60 * 1000
        const val OUTPUT_BUFFER_DELAY_MS = 100L
    }

    private val jSch = JSch()

    private lateinit var session: Session
    private lateinit var channel: ChannelShell

    private val outputBuffer = ByteArrayOutputStream()

    fun getOutputText() : String { return outputBuffer.toString() }

    fun sendCommand(address: String, port: Int, username: String, password: String, command: String) : String {
        if(initialize(address, port, username, password)) {
            send(command)
            Thread.sleep(OUTPUT_BUFFER_DELAY_MS)
            disconnect()
            return getString(R.string.send_success)
        } else {
            return getString(R.string.send_failed)
        }
    }

    private fun initialize(address: String, port: Int, username: String, password: String): Boolean {
        try {
            // Clear output stream
            outputBuffer.reset()

            // Setting Credentials
            session = jSch.getSession(username, address, port).also {
                it.setPassword(password)
                it.setConfig("StrictHostKeyChecking", "no")
            }

            // Connect the session
            session.connect(MAX_CONNECTION_TIMEOUT)

            // Initialize the shell channel
            channel = (session.openChannel("shell") as ChannelShell).apply {
                outputStream = outputBuffer
            }

            // Connect the shell channel
            channel.connect(MAX_CONNECTION_TIMEOUT)

            return true
        } catch (e: JSchException) {
            e.printStackTrace()
            logToOutput(e.message!!, getString(R.string.type_error))

            return false
        }
    }

    private fun send(command: String) = launch (Dispatchers.IO) {
        if (!session.isConnected) {
            logToOutput(getString(R.string.not_connected), getString(R.string.type_error))
        }
        else
        {
            logToOutput(getString(R.string.connected), getString(R.string.type_info))
        }
        PrintStream(channel.outputStream).apply {
            println(command)
            flush()
        }
    }

    private fun disconnect() = launch {
        session.disconnect()
    }

    private fun logToOutput(log: String, type: String = "") {
        val text = if(type.isEmpty()) {
            context.getString(R.string.log_pattern,log)
        } else {
            context.getString(R.string.log_type_pattern, type, log)
        }
        outputBuffer.write(text.encodeToByteArray())
    }

    private fun getString(id: Int) : String {
        return context.getString(id)
    }

}