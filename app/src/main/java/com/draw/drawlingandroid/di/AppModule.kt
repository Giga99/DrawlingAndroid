package com.draw.drawlingandroid.di

import android.content.Context
import com.draw.drawlingandroid.data.remote.api.SetupApi
import com.draw.drawlingandroid.repository.DefaultSetupRepository
import com.draw.drawlingandroid.repository.SetupRepository
import com.draw.drawlingandroid.util.Constants.HTTP_BASE_URL
import com.draw.drawlingandroid.util.Constants.HTTP_BASE_URL_LOCALHOST
import com.draw.drawlingandroid.util.Constants.USE_LOCALHOST
import com.draw.drawlingandroid.util.DispatcherProvider
import com.draw.drawlingandroid.util.clientId
import com.draw.drawlingandroid.util.dataStore
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
}
