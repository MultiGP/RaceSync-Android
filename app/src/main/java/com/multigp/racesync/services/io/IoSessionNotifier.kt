package com.multigp.racesync.services.io

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import com.google.gson.Gson
import com.multigp.racesync.domain.model.io.EventSession
import com.multigp.racesync.domain.model.io.startInstant
import java.util.concurrent.TimeUnit

interface IoSessionNotifier {
    /** Schedules a one-shot notification [hoursBefore] hours before the session's start. */
    fun schedule(session: EventSession, hoursBefore: Long = 1)

    /** Cancels any pending notification for [sessionId]. */
    fun cancel(sessionId: String)
}

class IoSessionNotifierImpl(
    private val context: Context,
    private val gson: Gson = Gson(),
) : IoSessionNotifier {

    private val alarmManager: AlarmManager? = context.getSystemService()

    override fun schedule(session: EventSession, hoursBefore: Long) {
        val am = alarmManager ?: return
        val start = session.startInstant() ?: run {
            Log.w(TAG, "Session ${session.id} has no startInstant, skipping")
            return
        }
        val triggerAt = start.time - TimeUnit.HOURS.toMillis(hoursBefore)
        if (triggerAt <= System.currentTimeMillis()) {
            Log.d(TAG, "Session ${session.id} trigger in the past, skipping")
            return
        }
        val pi = pendingIntent(session.id, sessionJson = gson.toJson(session), create = true)
            ?: return

        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()
        try {
            if (canExact) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
            }
        } catch (se: SecurityException) {
            // Permission was revoked between the check and the call.
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi)
        }
    }

    override fun cancel(sessionId: String) {
        val am = alarmManager ?: return
        val pi = pendingIntent(sessionId, sessionJson = null, create = false) ?: return
        am.cancel(pi)
        pi.cancel()
    }

    private fun pendingIntent(sessionId: String, sessionJson: String?, create: Boolean): PendingIntent? {
        val intent = Intent(context, IoSessionAlarmReceiver::class.java).apply {
            action = IoSessionAlarmReceiver.ACTION
            sessionJson?.let { putExtra(IoSessionAlarmReceiver.EXTRA_SESSION_JSON, it) }
        }
        val flags = (if (create) PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_NO_CREATE) or
            PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, sessionId.hashCode(), intent, flags)
    }

    companion object {
        private const val TAG = "IoSessionNotifier"
    }
}
