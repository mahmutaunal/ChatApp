package com.mahmutalperenunal.chatapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.adapter.AllUsersAdapter
import com.mahmutalperenunal.chatapp.adapter.LastChatsAdapter
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
        supportActionBar!!.title = "All Users"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

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

                    allUsersAdapter = AllUsersAdapter(applicationContext, usersList!!)
                    binding.allUsersRecyclerView.adapter = allUsersAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.app_bar_profile -> {
                val intent = Intent(
                    applicationContext,
                    ProfileActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.all_users_toolbar_menu, menu)

        val searchViewItem = menu.findItem(R.id.all_users_search)
        val searchView = MenuItemCompat.getActionView(searchViewItem) as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchForUsers(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchForUsers(newText)
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    private fun searchForUsers(str: String) {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid

        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (usersList as ArrayList<User>).clear()

                for (snapshot in p0.children) {
                    val user: User? = snapshot.getValue(User::class.java)
                    if (!(user!!.uid).equals(firebaseUserID)) {
                        (usersList as ArrayList<User>).add(user)
                    }
                }
                allUsersAdapter = AllUsersAdapter(applicationContext, usersList!!)
                binding.allUsersRecyclerView.adapter = allUsersAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}