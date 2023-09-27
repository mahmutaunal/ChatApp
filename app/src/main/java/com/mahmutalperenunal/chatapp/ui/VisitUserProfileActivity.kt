package com.mahmutalperenunal.chatapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.databinding.ActivityVisitUserProfileBinding
import com.mahmutalperenunal.chatapp.model.User

class VisitUserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisitUserProfileBinding
    private var userVisitId: String = ""
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.visitUserProfileToolbar)

        //set action bar title
        supportActionBar!!.title = "Profile"

        userVisitId = intent.getStringExtra("visit_id").toString()

        retrieveUserData()
    }

    private fun retrieveUserData() {
        val ref = FirebaseDatabase.getInstance().reference.child("Users").child(userVisitId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    user = p0.getValue(User::class.java)

                    binding.visitUserProfileUsernameTextView.text = user!!.username
                    Glide.with(applicationContext).load(user!!.profile).centerCrop()
                        .placeholder(R.drawable.profile_image)
                        .into(binding.visitUserProfilePpImageView)
                    Glide.with(applicationContext).load(user!!.cover).centerCrop()
                        .placeholder(R.drawable.cover_image)
                        .into(binding.visitUserProfileCoverImageView)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}