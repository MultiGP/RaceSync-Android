package com.multigp.racesync.services.io

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.multigp.racesync.domain.repositories.EventSessionBucketlist
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * AlarmManager alarms are lost across reboots. This receiver re-arms every bucketed session
 * whose start time is still in the future.
 *
 * Uses Hilt's [EntryPointAccessors] instead of `@AndroidEntryPoint` to sidestep a known
 * Hilt 2.48 + AGP ASM-instrumentation bug on annotated BroadcastReceivers.
 */
class IoBootReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface IoBootDeps {
        fun bucketlist(): EventSessionBucketlist
        fun notifier(): IoSessionNotifier
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED) return

        val deps = EntryPointAccessors.fromApplication(
            context.applicationContext,
            IoBootDeps::class.java
        )
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                IoNotificationChannel.ensure(context)
                val sessions = deps.bucketlist().allSessions()
                Log.d(TAG, "Re-scheduling ${sessions.size} IO session alarms after boot")
                sessions.forEach { deps.notifier().schedule(it) }
            } catch (t: Throwable) {
                Log.w(TAG, "Failed to re-schedule IO alarms after boot", t)
            } finally {
                pending.finish()
            }
        }
    }

    companion object {
        private const val TAG = "IoBootReceiver"
    }
}
