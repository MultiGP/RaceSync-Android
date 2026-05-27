package com.multigp.racesync.services.io

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import com.multigp.racesync.R

object IoNotificationChannel {
    const val ID = "io_event_reminders"

    fun ensure(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val mgr = context.getSystemService<NotificationManager>() ?: return
        if (mgr.getNotificationChannel(ID) != null) return
        val channel = NotificationChannel(
            ID,
            context.getString(R.string.io_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.io_notification_channel_description)
        }
        mgr.createNotificationChannel(channel)
    }
}
