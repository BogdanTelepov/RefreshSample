package com.example.registrationapp.retrofit

import com.example.tokenapp.retrofit.ValidateInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Client {

    const val ANDROID_VERSION = "Android-Version"

    const val READ_TIMEOUT = 100L
    const val CONNECT_TIMEOUT = 120L

    /**HEADERS*/
    const val AUTHORIZATION = "Authorization"

    @Singleton
    @Provides
    fun provideAuthInterceptor(): Interceptor {
        return ValidateInterceptor()


    }

    @Singleton
    @Provides
    fun getInterceptor(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(provideAuthInterceptor())
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Singleton
    @Provides
    fun getRegistrationClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://neocafe1.herokuapp.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun getApi(retrofit: Retrofit): RestApi {
        return retrofit.create(RestApi::class.java)
    }
}