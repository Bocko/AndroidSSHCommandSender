package com.lgbotond.androidsshcommandsender.util
object Utilities {

    private val ipv4AddressCheck = "^(\\d{1,3}\\.){3}\\d{1,3}\$".toRegex()
    private val ipv6AddressCheck = "^([\\da-f]{1,4}:){7}[\\da-f]{1,4}\$/i".toRegex()

    @JvmStatic
    fun validateIpAddress(address: String) : Boolean {
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