package com.mahmutalperenunal.chatapp.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class LastChatsAdapter(
    private val mContext: Context,
    private val mUsers: List<User>,
    val isChatCheck: Boolean
) : RecyclerView.Adapter<LastChatsAdapter.ViewHolder?>() {

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
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}