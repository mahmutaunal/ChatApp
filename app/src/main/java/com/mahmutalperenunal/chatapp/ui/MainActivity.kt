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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.adapter.AllUsersAdapter
import com.mahmutalperenunal.chatapp.databinding.ActivityMainBinding
import com.mahmutalperenunal.chatapp.model.Chat
import com.mahmutalperenunal.chatapp.model.ChatList
import com.mahmutalperenunal.chatapp.model.User
import com.mahmutalperenunal.chatapp.notification.Token

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var usersAdapter: AllUsersAdapter? = null
    private var usersList: List<User>? = null
    private var chatList: List<ChatList>? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        setAUnreadMessagesNumberAndGetChatLists()

        updateToken(FirebaseMessaging.getInstance().token.toString())

        binding.mainAllUsersButton.setOnClickListener { allUsersActivity() }
    }

    private fun updateToken(token: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val tokenNew = Token(token)
        ref.child(firebaseUser!!.uid).setValue(tokenNew)
    }

    private fun setAUnreadMessagesNumberAndGetChatLists() {
        val ref =
            FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var countUnreadMessages = 0

                for (data in snapshot.children) {
                    val chat = data.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(firebaseUser!!.uid) && !chat.isSeen) {
                        countUnreadMessages += 1
                    }
                }

                retrieveChatsList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun retrieveChatsList() {
        chatList = ArrayList()

        val ref =
            FirebaseDatabase.getInstance().reference.child("ChatLists").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (chatList as ArrayList).clear()

                for (data in snapshot.children) {
                    val chats = data.getValue(ChatList::class.java)
                    (chatList as ArrayList).add(chats!!)
                }

                retrieveUsersList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun retrieveUsersList() {
        usersList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (usersList as ArrayList).clear()

                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)

                    for (eachChatList in chatList!!) {
                        if (user!!.uid.equals(eachChatList.id)) {
                            (usersList as ArrayList).add(user)
                        }
                    }
                }

                usersAdapter =
                    AllUsersAdapter(applicationContext, (usersList as ArrayList<User>), true)
                binding.mainRecyclerView.adapter = usersAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun allUsersActivity() {
        val intent = Intent(applicationContext, AllUsersActivity::class.java)
        startActivity(intent)
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
        menuInflater.inflate(R.menu.main_toolbar_menu, menu)

        val searchViewItem = menu.findItem(R.id.app_bar_search)
        val searchView = MenuItemCompat.getActionView(searchViewItem) as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                /*if (mylist.contains(query)) {
                    adapter.filter.filter(query)
                } else {
                    // Search query not found in List View
                    Toast.makeText(this@MainActivity, "Not found", Toast.LENGTH_LONG).show()
                }*/
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //adapter.filter.filter(newText)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}