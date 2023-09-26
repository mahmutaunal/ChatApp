package com.mahmutalperenunal.chatapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mahmutalperenunal.chatapp.adapter.AllUsersAdapter
import com.mahmutalperenunal.chatapp.databinding.ActivityAllUsersBinding
import com.mahmutalperenunal.chatapp.model.User

class AllUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllUsersBinding
    private var allUsersAdapter: AllUsersAdapter? = null
    private var usersList: List<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.allUsersToolbar)

        //initialize
        usersList = ArrayList()

        binding.allUsersRecyclerView.setHasFixedSize(true)
        binding.allUsersRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        retrieveAllUsersData()
    }

    private fun retrieveAllUsersData() {
        val firebaseUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val refUser = FirebaseDatabase.getInstance().reference.child("Users")

        refUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (usersList as ArrayList<User>).clear()

                for (data in snapshot.children) {
                    val users: User? = data.getValue(User::class.java)

                    if (!(users!!.uid).equals(firebaseUserId)) {
                        (usersList as ArrayList<User>).add(users)
                    } else {

                    }

                    allUsersAdapter = AllUsersAdapter(applicationContext, usersList!!, false)
                    binding.allUsersRecyclerView.adapter = allUsersAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}