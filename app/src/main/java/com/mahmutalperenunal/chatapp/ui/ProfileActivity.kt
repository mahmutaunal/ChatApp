package com.mahmutalperenunal.chatapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.databinding.ActivityProfileBinding
import com.mahmutalperenunal.chatapp.model.User

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var refUsers: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.settingsToolbar)

        //set action bar title
        supportActionBar!!.title = "Profile"

        //initialize
        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        setUserProfilePicture()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.app_bar_logout -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(
                    applicationContext,
                    WelcomeActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUserProfilePicture() {
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user: User? = snapshot.getValue(User::class.java)

                    //set data
                    binding.profileUsernameTextView.text = user!!.username
                    Glide.with(applicationContext).load(user.profile).centerCrop()
                        .placeholder(R.drawable.profile_image).into(binding.profilePpImageView)
                    Glide.with(applicationContext).load(user.cover).centerCrop()
                        .placeholder(R.drawable.cover_image).into(binding.profileCoverImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    "Error Message: " + error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}