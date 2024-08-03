package com.example.educationassistant

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val notificationId = 1
const val channelId = "channel_for_exams_and_assignments"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.main_logo)
            .setContentTitle(intent.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId,notification)
    }
}
