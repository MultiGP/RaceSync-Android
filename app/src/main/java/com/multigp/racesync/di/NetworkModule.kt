package com.multigp.racesync.di

import com.multigp.racesync.BuildConfig
import com.multigp.racesync.data.api.RaceSyncApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun httpInterceptor(): HttpLoggingInterceptor {
        return (HttpLoggingInterceptor())
            .apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    @Singleton
    fun provideHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofitInstance(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRaceSyncApi(retrofit: Retrofit): RaceSyncApi {
        return retrofit.create(RaceSyncApi::class.java)
    }

}