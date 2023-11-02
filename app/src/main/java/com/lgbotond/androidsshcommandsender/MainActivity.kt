package com.lgbotond.androidsshcommandsender

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lgbotond.androidsshcommandsender.Xtensions.fadeOut
import com.lgbotond.androidsshcommandsender.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var test = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSend.setOnClickListener { sendEvent() }
    }

    private fun sendEvent() {
        if(test) {
            binding.tvStatus.setTextColor(getColor(R.color.error))
            binding.tvStatus.setText("FAILED")
        } else {
            binding.tvStatus.setTextColor(getColor(R.color.success))
            binding.tvStatus.setText("SUCCESS")
        }

        binding.tvStatus.fadeOut()

        test = !test
    }
}