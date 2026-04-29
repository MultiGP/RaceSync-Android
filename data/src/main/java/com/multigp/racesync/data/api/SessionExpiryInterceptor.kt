package com.multigp.racesync.data.api

import com.multigp.racesync.data.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class SessionExpiryInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code() == 401) {
            sessionManager.notifySessionExpired()
        }
        return response
    }
}
