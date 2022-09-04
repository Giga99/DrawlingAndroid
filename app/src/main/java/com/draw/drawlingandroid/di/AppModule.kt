package com.draw.drawlingandroid.di

import android.content.Context
import com.draw.drawlingandroid.R
import com.draw.drawlingandroid.util.*
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @ApiUrl
    @Provides
    fun provideApiUrl(
        @ApplicationContext context: Context
    ): String = context.getString(R.string.api_url)

    @WebSocketUrl
    @Provides
    fun provideWebSocketUrl(
        @ApplicationContext context: Context
    ): String = context.getString(R.string.web_socket_url)

    @Singleton
    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun provideClientId(@ApplicationContext context: Context): String = runBlocking {
        context.dataStore.clientId()
    }

    @Singleton
    @Provides
    fun provideDispatcherProvider(): DispatcherProvider {
        return object : DispatcherProvider {
            override val main: CoroutineDispatcher
                get() = Dispatchers.Main
            override val io: CoroutineDispatcher
                get() = Dispatchers.IO
            override val default: CoroutineDispatcher
                get() = Dispatchers.Default

        }
    }

    @Singleton
    @Provides
    fun provideGsonInstance(): Gson = Gson()

    @Singleton
    @Provides
    fun provideOkHttpClient(clientId: String): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val url = chain.request().url.newBuilder()
                    .addQueryParameter("client_id", clientId)
                    .build()
                val request = chain.request().newBuilder()
                    .url(url)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
            .build()
}
