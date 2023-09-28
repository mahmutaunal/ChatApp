package com.mahmutalperenunal.chatapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.mahmutalperenunal.chatapp.R
import com.mahmutalperenunal.chatapp.model.Chat
import com.mahmutalperenunal.chatapp.ui.ViewFullImageActivity

class ChatsAdapter(
    private var mContext: Context,
    private var mChatList: List<Chat>
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder?>() {

    private var firebaseUser: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message: TextView? = null
        var seen: TextView? = null
        var imageMessageRight: ImageView? = null
        var imageMessageLeft: ImageView? = null

        init {
            message = itemView.findViewById(R.id.messageItem_message_textView)
            seen = itemView.findViewById(R.id.messageItem_seen_textView)
            imageMessageRight = itemView.findViewById(R.id.messageItemRight_imageView)
            imageMessageLeft = itemView.findViewById(R.id.messageItemLeft_imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == 1) {
            val view: View =
                LayoutInflater.from(mContext)
                    .inflate(R.layout.message_item_right_layout, parent, false)
            ViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(mContext)
                    .inflate(R.layout.message_item_left_layout, parent, false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat: Chat = mChatList[position]

        if (chat.message.equals("Sent you an image.") && !chat.url.equals("")) {

            if (chat.sender.equals(firebaseUser.uid)) {

                holder.message!!.visibility = View.GONE
                holder.imageMessageRight!!.visibility = View.VISIBLE
                Glide.with(mContext).load(chat.url).centerCrop()
                    .placeholder(R.drawable.profile_image).into(holder.imageMessageRight!!)

                holder.imageMessageRight!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Delete Image",
                        "Cancel"
                    )

                    val builder: AlertDialog.Builder = AlertDialog.Builder(mContext, R.style.CustomAlertDialog)
                    builder.setTitle("What do you want?")

                    builder.setItems(options) { _, which ->
                        if (which == 0) {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.url)
                            mContext.startActivity(intent)
                        } else if (which == 1) {
                            deleteMessage(position, holder)
                        }
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }


            } else if (!chat.sender.equals(firebaseUser.uid)) {

                holder.message!!.visibility = View.GONE
                holder.imageMessageLeft!!.visibility = View.VISIBLE
                Glide.with(mContext).load(chat.url).centerCrop()
                    .placeholder(R.drawable.profile_image).into(holder.imageMessageLeft!!)

                holder.imageMessageLeft!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "View Full Image",
                        "Cancel"
                    )

                    val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context, R.style.CustomAlertDialog)
                    builder.setTitle("What do you want?")

                    builder.setItems(options) { _, which ->
                        if (which == 0) {
                            val intent = Intent(mContext, ViewFullImageActivity::class.java)
                            intent.putExtra("url", chat.url)
                            mContext.startActivity(intent)
                        }
                    }
                    builder.show()
                }

            }

        } else {

            holder.message!!.text = chat.message

            if (firebaseUser.uid == chat.sender) {
                holder.message!!.setOnClickListener {
                    AlertDialog.Builder(mContext, R.style.CustomAlertDialog)
                        .setTitle("What do you want?")
                        .setMessage("")
                        .setPositiveButton("Delete Message") { dialog, _ ->
                            deleteMessage(position, holder)
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                    .create()
                    .show()
                }
            }

        }

        if (position == mChatList.size - 1) {

            if (chat.isSeen) {

                holder.seen!!.text = "Seen"

                if (chat.message.equals("Sent you an image.") && !chat.url.equals("")) {
                    val lp: ConstraintLayout.LayoutParams? =
                        holder.seen!!.layoutParams as ConstraintLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.seen!!.layoutParams = lp
                }

            } else {

                holder.seen!!.text = "Sent"

                if (chat.message.equals("Sent you an image.") && !chat.url.equals("")) {
                    val lp: ConstraintLayout.LayoutParams? =
                        holder.seen!!.layoutParams as ConstraintLayout.LayoutParams?
                    lp!!.setMargins(0, 245, 10, 0)
                    holder.seen!!.layoutParams = lp
                }

            }

        } else {
            holder.seen!!.visibility = View.GONE
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mChatList[position].sender.equals(firebaseUser.uid)) {
            1
        } else {
            0
        }
    }

    private fun deleteMessage(position: Int, holder: ChatsAdapter.ViewHolder) {
        FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList[position].messageId!!)
            .removeValue()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(holder.itemView.context, "Message Deleted.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        holder.itemView.context,
                        "Failed, Message Not Deleted!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}