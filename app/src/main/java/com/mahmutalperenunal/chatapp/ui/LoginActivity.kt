package com.mahmutalperenunal.chatapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mahmutalperenunal.chatapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.loginToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //initialize firebase auth
        mAuth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        val email: String = binding.loginEmailTextInputEditText.text.toString()
        val password: String = binding.loginPasswordTextInputEditText.text.toString()

        if (email.isEmpty()) {
            binding.loginEmailTextInputLayout.error = "Email field cannot be blank!"
            Toast.makeText(
                applicationContext,
                "Please fill in the blank fields!",
                Toast.LENGTH_SHORT
            ).show()
        } else if (password.isEmpty()) {
            binding.loginPasswordTextInputLayout.error = "Password field cannot be blank!"
            Toast.makeText(
                applicationContext,
                "Please fill in the blank fields!",
                Toast.LENGTH_SHORT
            ).show()
        } else {

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val intent = Intent(
                        applicationContext,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error Message: " + task.exception!!.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }
}