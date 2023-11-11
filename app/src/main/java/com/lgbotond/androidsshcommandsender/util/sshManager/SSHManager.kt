package com.lgbotond.androidsshcommandsender.util.sshManager

import android.util.Log
import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.coroutines.CoroutineContext

class SSHManager : CoroutineScope {

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
            return "SUCCESS"
        } else {
            return "FAILURE"
        }
    }

    private fun initialize(address: String, port: Int, username: String, password: String): Boolean {
        try {
            session = jSch.getSession(username, address, port).also {
                it.setPassword(password)
                it.setConfig("StrictHostKeyChecking", "no")
            }

            // Connect the session
            session.connect(MAX_CONNECTION_TIMEOUT)

            // Clear output stream
            outputBuffer.reset()

            // Initialize the shell channel
            channel = (session.openChannel("shell") as ChannelShell).apply {
                outputStream = outputBuffer
            }

            // Connect the shell channel
            channel.connect(MAX_CONNECTION_TIMEOUT)

            //ready.postValue(true)

            return true
        } catch (e: JSchException) {
            e.printStackTrace()
            Log.d("SSHConn", e.message!!)
            //error.postValue(e.message!!)

            return false
        }
    }

    private fun send(command: String) = launch (Dispatchers.IO) {
        if (!session.isConnected) {
            //error.postValue(context.getString("R.string.error_not_connected"))
            //return
            Log.d("SSHConn","not connected")
        }
        else
        {
            Log.d("SSHConn","connected")
        }
        PrintStream(channel.outputStream).apply {
            println(command)
            flush()
        }
    }

    private fun disconnect() = launch {
        session.disconnect()
    }

}