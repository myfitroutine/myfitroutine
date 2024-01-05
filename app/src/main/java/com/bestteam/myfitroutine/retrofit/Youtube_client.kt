package com.bestteam.myfitroutine.retrofit

import com.bestteam.myfitroutine.BuildConfig
import com.bestteam.myfitroutine.Contain.YOUTUBE_BASE_URL
import com.bestteam.myfitroutine.Repository.YoutubeRepository
import com.bestteam.myfitroutine.Repository.YoutubeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.annotations.ApiStatus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Youtube_client {

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    } else {
        OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(YOUTUBE_BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    fun provideYoutubeApiService(retrofit: Retrofit): Youtube_interface {
        return retrofit.create(Youtube_interface::class.java)
    }

    @Singleton
    @Provides
    fun provideYoutubeRepository(apiService: Youtube_interface): YoutubeRepository {
        return YoutubeRepositoryImpl(apiService)
    }
}