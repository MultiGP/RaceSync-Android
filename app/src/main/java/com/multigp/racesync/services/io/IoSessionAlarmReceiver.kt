package com.multigp.racesync.services.io

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.google.gson.Gson
import com.multigp.racesync.OnboardingActivity
import com.multigp.racesync.R
import com.multigp.racesync.domain.model.io.EventSession
import com.multigp.racesync.domain.model.io.MGP_EVENT_TIMEZONE_ID
import com.multigp.racesync.domain.model.io.startInstant
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class IoSessionAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val json = intent.getStringExtra(EXTRA_SESSION_JSON) ?: return
        val session = runCatching { Gson().fromJson(json, EventSession::class.java) }
            .getOrNull() ?: return

        IoNotificationChannel.ensure(context)

        val title = context.getString(R.string.io_notification_title)
        val body = bodyFor(context, session)
        val notifId = session.id.hashCode()

        // If the bucketed session links to a MultiGP race, jump straight to the
        // existing race-notification deep link (same UX as FCM race pushes). If
        // not, just launch the app.
        val raceId = session.raceId
        val tapIntent = if (!raceId.isNullOrBlank()) {
            Intent(
                Intent.ACTION_VIEW,
                "racesync://notification_race_details/$raceId".toUri(),
                context,
                OnboardingActivity::class.java
            )
        } else {
            Intent(context, OnboardingActivity::class.java)
        }.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP }
        val contentIntent = PendingIntent.getActivity(
            context,
            notifId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, IoNotificationChannel.ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        context.getSystemService<NotificationManager>()?.notify(notifId, notification)
    }

    private fun bodyFor(context: Context, session: EventSession): String {
        val activity = session.activity.orEmpty()
        val start = session.startInstant()
        return if (start != null) {
            val fmt = SimpleDateFormat("h:mm a", Locale.US).apply {
                timeZone = TimeZone.getTimeZone(MGP_EVENT_TIMEZONE_ID)
            }
            context.getString(R.string.io_notification_body_with_time, activity, fmt.format(start))
        } else {
            context.getString(R.string.io_notification_body_no_time, activity)
        }
    }

    companion object {
        const val ACTION = "com.multigp.racesync.IO_SESSION_ALARM"
        const val EXTRA_SESSION_JSON = "session_json"
    }
}
