package com.mahmutalperenunal.chatapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mahmutalperenunal.chatapp.MainActivity
import com.mahmutalperenunal.chatapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    private var firebaseUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize firebase auth
        mAuth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val username: String = binding.registerUsernameTextInputEditText.text.toString()
        val email: String = binding.registerEmailTextInputEditText.text.toString()
        val password: String = binding.registerPasswordTextInputEditText.text.toString()

        if (username.isEmpty()) {
            binding.registerUsernameTextInputLayout.error = "Username field cannot be blank!"
            Toast.makeText(
                applicationContext,
                "Please fill in the blank fields!",
                Toast.LENGTH_SHORT
            ).show()
        } else if (email.isEmpty()) {
            binding.registerEmailTextInputLayout.error = "Email field cannot be blank!"
            Toast.makeText(
                applicationContext,
                "Please fill in the blank fields!",
                Toast.LENGTH_SHORT
            ).show()
        } else if (password.isEmpty()) {
            binding.registerPasswordTextInputLayout.error = "Password field cannot be blank!"
            Toast.makeText(
                applicationContext,
                "Please fill in the blank fields!",
                Toast.LENGTH_SHORT
            ).show()
        } else {

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseUserId = mAuth.currentUser!!.uid
                    refUsers = FirebaseDatabase.getInstance().reference.child("Users")
                        .child(firebaseUserId!!)

                    val userHashMap = HashMap<String, Any>()
                    userHashMap["uid"] = firebaseUserId!!
                    userHashMap["username"] = username
                    userHashMap["profile"] = "gs://chatapp-623f2.appspot.com/profile_image.png"
                    userHashMap["cover"] = "gs://chatapp-623f2.appspot.com/cover_image.jpg"
                    userHashMap["status"] = "offline"
                    userHashMap["search"] = username.toLowerCase()

                    refUsers.updateChildren(userHashMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(
                                applicationContext,
                                MainActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
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