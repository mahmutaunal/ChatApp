package com.mahmutalperenunal.chatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.model.User
import com.mahmutalperenunal.chatapp.ui.ChattingActivity
import com.mahmutalperenunal.chatapp.ui.VisitUserProfileActivity
import de.hdodenhof.circleimageview.CircleImageView

class AllUsersAdapter(
    private val mContext: Context,
    private var mUsers: List<User>
) : RecyclerView.Adapter<AllUsersAdapter.ViewHolder?>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

        holder.itemView.setOnClickListener {
            val intent = Intent(
                mContext,
                ChattingActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("visit_id", user.uid)
            mContext.startActivity(intent)
        }

        holder.profileImage.setOnClickListener {
            val intent = Intent(mContext, VisitUserProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("visit_id", user.uid)
            mContext.startActivity(intent)
        }
    }

    fun filterList(filterList: ArrayList<User>) {
        mUsers = filterList
        notifyDataSetChanged()
    }

}