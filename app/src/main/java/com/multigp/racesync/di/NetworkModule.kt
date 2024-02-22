package com.multigp.racesync.di

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
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
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateDeserializer())
            .create()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideRaceSyncApi(retrofit: Retrofit): RaceSyncApi {
        return retrofit.create(RaceSyncApi::class.java)
    }

    @Provides
    fun provideAPiKey(): String{
        return BuildConfig.API_KEY
    }

}

class DateDeserializer : JsonDeserializer<Date> {
    private val dateFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    )

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date {
        json?.asString?.let { dateString ->
            dateFormats.forEach { dateFormat ->
                try {
                    return dateFormat.parse(dateString)
                } catch (e: ParseException) {
                    // Ignore and try the next format
                }
            }
        }
        throw JsonParseException("Unparseable date: $json")
    }
}