package com.example.educationassistant

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelName = "Assignment and Exam Reminders"
            val channelDescription = "Channel for assignment and exam reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription


            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
