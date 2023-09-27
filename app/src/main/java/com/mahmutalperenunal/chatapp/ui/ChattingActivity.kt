package com.mahmutalperenunal.chatapp.ui

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.adapter.ChatsAdapter
import com.mahmutalperenunal.chatapp.databinding.ActivityChattingBinding
import com.mahmutalperenunal.chatapp.model.Chat
import com.mahmutalperenunal.chatapp.model.User

class ChattingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChattingBinding
    private var firebaseUser: FirebaseUser? = null
    private var visitId: String? = null
    private var chatsAdapter: ChatsAdapter? = null
    private var chatsList: List<Chat>? = null
    private var seenListener: ValueEventListener? = null
    private var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        visitId = intent.getStringExtra("visit_id")

        firebaseUser = FirebaseAuth.getInstance().currentUser

        binding.recyclerViewChats.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        binding.recyclerViewChats.layoutManager = LinearLayoutManager(applicationContext)

        getUsernameAndProfilePhotoAndMessages()

        seenMessage(visitId!!)

        binding.chattingSendButton.setOnClickListener {
            controlMessage()
            binding.chattingMessageTextInputEditText.setText("")
        }

        binding.chattingAttachButton.setOnClickListener { openActivity() }
    }

    private fun openActivity() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Pick Image"), 438)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.data != null) {
            val loadingBar = ProgressDialog(this)
            loadingBar.setMessage("Image sending...")
            loadingBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            val uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }

                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "Sent you an image."
                    messageHashMap["receiver"] = visitId
                    messageHashMap["isSeen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                }
            }
            loadingBar.dismiss()
        }
    }

    private fun controlMessage() {
        val message = binding.chattingMessageTextInputEditText.text.toString()

        if (message.isEmpty()) {
            Toast.makeText(applicationContext, "Please write a message...", Toast.LENGTH_SHORT)
                .show()
        } else {
            sendMessageToUser(firebaseUser!!.uid, visitId, message)
        }
    }

    private fun sendMessageToUser(senderId: String, receiverId: String?, message: String) {
        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isSeen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        reference.child("Chats").child(messageKey!!).setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chatListReference =
                        FirebaseDatabase.getInstance().reference.child("ChatLists")
                            .child(firebaseUser!!.uid).child(visitId!!)

                    chatListReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (!snapshot.exists()) {
                                chatListReference.child("id").setValue(visitId)
                            } else {
                                val chatListReceiverReference =
                                    FirebaseDatabase.getInstance().reference.child("ChatLists")
                                        .child(visitId!!).child(firebaseUser!!.uid)
                                chatListReceiverReference.child("id").setValue(firebaseUser!!.uid)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })

                    val reference = FirebaseDatabase.getInstance().reference.child("Users")
                        .child(firebaseUser!!.uid)
                }
            }
    }

    private fun getUsernameAndProfilePhotoAndMessages() {
        reference = FirebaseDatabase.getInstance().reference.child("Users").child(visitId!!)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User? = snapshot.getValue(User::class.java)

                binding.chattingUsernameTextView.text = user!!.username
                Glide.with(applicationContext).load(user.profile).centerCrop()
                    .placeholder(R.drawable.profile_image).into(binding.chattingProfileImageView)

                retrieveMessages(firebaseUser!!.uid, visitId)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun retrieveMessages(senderId: String, receiverId: String?) {
        chatsList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                (chatsList as ArrayList<Chat>).clear()

                for (p0 in snapshot.children) {
                    val chat = p0.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(senderId) && chat.sender.equals(receiverId)
                        || chat.receiver.equals(receiverId) && chat.sender.equals(senderId)
                    ) {
                        (chatsList as ArrayList<Chat>).add(chat)
                    }

                    chatsAdapter = ChatsAdapter(applicationContext, (chatsList as ArrayList<Chat>))
                    binding.recyclerViewChats.adapter = chatsAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun seenMessage(userId: String) {
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val chat = data.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(firebaseUser!!.uid) && chat.sender.equals(userId)) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isSeen"] = true
                        data.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onPause() {
        super.onPause()

        reference!!.removeEventListener(seenListener!!)
    }
}