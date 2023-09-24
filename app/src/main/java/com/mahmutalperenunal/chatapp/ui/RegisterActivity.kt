package com.mahmutalperenunal.chatapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}