package com.multigp.racesync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

import android.app.Application
import com.multigp.racesync.services.RaceSynceMessagingService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RaceSyncApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = RaceSynceMessagingService.CHANNEL_NAME
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}