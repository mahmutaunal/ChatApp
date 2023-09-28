package com.mahmutalperenunal.chatapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.model.Chat
import com.mahmutalperenunal.chatapp.model.User
import com.mahmutalperenunal.chatapp.ui.ChattingActivity
import com.mahmutalperenunal.chatapp.ui.VisitUserProfileActivity
import de.hdodenhof.circleimageview.CircleImageView

class LastChatsAdapter(
    private val mContext: Context,
    private var mUsers: List<User>,
    private val isChatCheck: Boolean
) : RecyclerView.Adapter<LastChatsAdapter.ViewHolder?>() {

    private var lastMsg: String = ""
    private var mUserFiltered = emptyList<User>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var lastMessage: TextView
        var profileImage: CircleImageView
        var onlineImage: CircleImageView
        var offlineImage: CircleImageView

        init {
            username = itemView.findViewById(R.id.lastChatsItem_username_textView)
            lastMessage = itemView.findViewById(R.id.lastChatsItem_lastMessage_textView)
            profileImage = itemView.findViewById(R.id.lastChatsItem_profile_imageView)
            onlineImage = itemView.findViewById(R.id.lastChatsItem_online_imageView)
            offlineImage = itemView.findViewById(R.id.lastChatsItem_offline_imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.last_chats_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = mUsers[position]
        holder.username.text = user.username
        Glide.with(mContext).load(user.profile).placeholder(R.drawable.profile)
            .into(holder.profileImage)

        if (isChatCheck) {
            retrieveLastMessage(user.uid, holder.lastMessage)
        } else {
            holder.lastMessage.visibility = View.GONE
        }

        if (isChatCheck) {
            if (user.status == "online") {
                holder.onlineImage.visibility = View.VISIBLE
                holder.offlineImage.visibility = View.GONE
            } else {
                holder.onlineImage.visibility = View.GONE
                holder.offlineImage.visibility = View.VISIBLE
            }
        } else {
            holder.onlineImage.visibility = View.GONE
            holder.offlineImage.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(
                mContext,
                ChattingActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("visit_id", user.uid)
            mContext.startActivity(intent)
        }

        holder.profileImage.setOnClickListener {
            val intent = Intent(
                mContext,
                VisitUserProfileActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("visit_id", user.uid)
            mContext.startActivity(intent)
        }
    }

    private fun retrieveLastMessage(chatUserId: String?, lastMessageTxt: TextView) {
        lastMsg = "defaultMsg"

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                for (dataSnapshot in p0.children) {
                    val chat: Chat? = dataSnapshot.getValue(Chat::class.java)

                    if (firebaseUser != null && chat != null) {
                        if (chat.receiver == firebaseUser.uid &&
                            chat.sender == chatUserId ||
                            chat.receiver == chatUserId &&
                            chat.sender == firebaseUser.uid
                        ) {
                            lastMsg = chat.message!!
                        }
                    }
                }
                when (lastMsg) {
                    "defaultMsg" -> lastMessageTxt.text = "No Message"
                    "sent you an image." -> lastMessageTxt.text = "Image sent."
                    else -> lastMessageTxt.text = lastMsg
                }
                lastMsg = "defaultMsg"
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}