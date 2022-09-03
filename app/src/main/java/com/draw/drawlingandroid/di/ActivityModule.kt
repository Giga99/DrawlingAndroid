package com.draw.drawlingandroid.di

import android.app.Application
import android.content.Context
import com.draw.drawlingandroid.data.remote.api.SetupApi
import com.draw.drawlingandroid.data.remote.ws.CustomGsonMessageAdapter
import com.draw.drawlingandroid.data.remote.ws.DrawingApi
import com.draw.drawlingandroid.data.remote.ws.FlowStreamAdapter
import com.draw.drawlingandroid.data.datasource.DefaultDrawingRepository
import com.draw.drawlingandroid.data.datasource.DefaultSetupRepository
import com.draw.drawlingandroid.domain.repositories.DrawingRepository
import com.draw.drawlingandroid.domain.repositories.SetupRepository
import com.draw.drawlingandroid.util.Constants
import com.google.gson.Gson
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.retry.LinearBackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityModule {

    @ActivityRetainedScoped
    @Provides
    fun provideSetupApi(okHttpClient: OkHttpClient): SetupApi =
        Retrofit.Builder()
            .baseUrl(if (Constants.USE_LOCALHOST) Constants.HTTP_BASE_URL_LOCALHOST else Constants.HTTP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(SetupApi::class.java)

    @ActivityRetainedScoped
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

    @ActivityRetainedScoped
    @Provides
    fun provideSetupRepository(
        setupApi: SetupApi,
        @ApplicationContext context: Context
    ): SetupRepository = DefaultSetupRepository(setupApi, context)

    @ActivityRetainedScoped
    @Provides
    fun provideDrawingRepository(
        drawingApi: DrawingApi
    ): DrawingRepository = DefaultDrawingRepository(drawingApi)
}
