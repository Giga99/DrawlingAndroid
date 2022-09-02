package com.draw.drawlingandroid.di

import android.app.Application
import android.content.Context
import com.draw.drawlingandroid.data.remote.api.SetupApi
import com.draw.drawlingandroid.data.remote.ws.CustomGsonMessageAdapter
import com.draw.drawlingandroid.data.remote.ws.DrawingApi
import com.draw.drawlingandroid.data.remote.ws.FlowStreamAdapter
import com.draw.drawlingandroid.repository.DefaultSetupRepository
import com.draw.drawlingandroid.repository.SetupRepository
import com.draw.drawlingandroid.util.Constants
import com.google.gson.Gson
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.retry.LinearBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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

    @Singleton
    @Provides
    fun provideSetupApi(okHttpClient: OkHttpClient): SetupApi =
        Retrofit.Builder()
            .baseUrl(if (Constants.USE_LOCALHOST) Constants.HTTP_BASE_URL_LOCALHOST else Constants.HTTP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(SetupApi::class.java)

    @Singleton
    @Provides
    fun provideDrawingApi(
        app: Application,
        okHttpClient: OkHttpClient,
        gson: Gson
    ): DrawingApi = Scarlet.Builder()
        .backoffStrategy(LinearBackoffStrategy(Constants.RECONNECT_INTERVAL))
        .lifecycle(AndroidLifecycle.ofApplicationForeground(app))
        .webSocketFactory(
            okHttpClient.newWebSocketFactory(if (Constants.USE_LOCALHOST) Constants.WS_BASE_URL_LOCALHOST else Constants.WS_BASE_URL)
        )
        .addStreamAdapterFactory(FlowStreamAdapter.Factory)
        .addMessageAdapterFactory(CustomGsonMessageAdapter.Factory(gson))
        .build()
        .create()

    @Singleton
    @Provides
    fun provideSetupRepository(
        setupApi: SetupApi,
        @ApplicationContext context: Context
    ): SetupRepository = DefaultSetupRepository(setupApi, context)
}
