package com.mahmutalperenunal.chatapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class AllUsersAdapter(
    val mContext: Context,
    val mUsers: List<User>,
    val isChatCheck: Boolean
) : RecyclerView.Adapter<AllUsersAdapter.ViewHolder?>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var profileImage: CircleImageView
        var onlineImage: CircleImageView
        var offlineImage: CircleImageView

        init {
            username = itemView.findViewById(R.id.userItem_username_textView)
            profileImage = itemView.findViewById(R.id.userItem_profile_imageView)
            onlineImage = itemView.findViewById(R.id.userItem_online_imageView)
            offlineImage = itemView.findViewById(R.id.userItem_offline_imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: User = mUsers[position]
        holder.username.text = user.username
        Glide.with(mContext).load(user.profile).centerCrop()
            .placeholder(R.drawable.profile_image).into(holder.profileImage)
    }

}