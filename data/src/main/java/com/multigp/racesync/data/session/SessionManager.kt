package com.multigp.racesync.data.session

import com.multigp.racesync.data.prefs.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SessionManager(
    private val dataStore: DataStoreManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _expiredEvents = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val expiredEvents: SharedFlow<Unit> = _expiredEvents.asSharedFlow()

    fun notifySessionExpired() {
        scope.launch {
            // Skip if there's no active session — guards against a 401 on /user/login
            // (bad credentials) and against duplicate 401s from concurrent in-flight requests.
            if (dataStore.getSessionId() != null) {
                dataStore.clearSession()
                _expiredEvents.tryEmit(Unit)
            }
        }
    }
}
