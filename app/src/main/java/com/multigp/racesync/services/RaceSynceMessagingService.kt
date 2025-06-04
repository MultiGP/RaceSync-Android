package com.multigp.racesync.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.multigp.racesync.R
import kotlin.random.Random

class RaceSynceMessagingService : FirebaseMessagingService() {

    private val random = Random
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM Token", "Notification Received")
        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            val title = data["title"] ?: "RaceSync"
            val body = data["body"] ?: ""
            val raceID = data["raceId"] ?: return
            sendNotification(title, body, raceID)
        }
    }

    private fun sendNotification(title: String, body: String, raceID: String) {
        val deepLinkUri = "racesync://notification_race_details/$raceID".toUri()
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = androidx.core.app.TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        }

        val channelId = this.getString(R.string.default_notification_channel_id)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(random.nextInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        Log.d("FCM Token","New token: $token")
    }

    companion object {
        const val CHANNEL_NAME = "FCM notification channel"
    }
}