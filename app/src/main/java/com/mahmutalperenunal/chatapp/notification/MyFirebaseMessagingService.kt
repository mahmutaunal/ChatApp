package com.mahmutalperenunal.chatapp.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mahmutalperenunal.chatapp.ui.ChattingActivity

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @SuppressLint("ObsoleteSdkInt")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val send = message.data["send"]
        val user = message.data["user"]

        val sharedPreferences = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currentOnlineUser = sharedPreferences.getString("currentUser", "none")

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null && send == firebaseUser.uid) {
            if (currentOnlineUser != user) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendNewDevicesNotification(message)
                } else {
                    sendOldDevicesNotification(message)
                }
            }
        }
    }

    private fun sendOldDevicesNotification(message: RemoteMessage) {
        val user = message.data["user"]
        val icon = message.data["icon"]
        val title = message.data["title"]
        val body = message.data["body"]

        message.notification
        val j = user!!.replace("\\D".toRegex(), "").toInt()
        val intent = Intent(this, ChattingActivity::class.java)

        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, j, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setSmallIcon(icon!!.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i = 0
        if (j > 0) {
            i = j
        }

        notificationManager.notify(i, builder.build())
    }

    private fun sendNewDevicesNotification(message: RemoteMessage) {
        val user = message.data["user"]
        val icon = message.data["icon"]
        val title = message.data["title"]
        val body = message.data["body"]

        message.notification
        val j = user!!.replace("\\D".toRegex(), "").toInt()
        val intent = Intent(this, ChattingActivity::class.java)

        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, j, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val oreoNotification = NewDeviceNotification(this)

        val builder: Notification.Builder =
            oreoNotification.getNewDeviceNotification(
                title,
                body,
                pendingIntent,
                defaultSound,
                icon
            )

        var i = 0
        if (j > 0) {
            i = j
        }

        oreoNotification.getManager!!.notify(i, builder.build())
    }

}