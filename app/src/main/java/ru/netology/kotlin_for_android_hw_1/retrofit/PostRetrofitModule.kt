package ru.netology.kotlin_for_android_hw_1.retrofit

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.kotlin_for_android_hw_1.auth.AppAuthorization
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PostRetrofitModule {

    companion object {
        //        private const val BASE_URL = "http://10.0.2.2:9999/api/"
        private const val BASE_URL = "http://10.0.2.2:9999/api/slow/"
    }

    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val newRequest =
                AppAuthorization.getInstance().authStateFlow.value.token?.let { token ->
                    chain.request().newBuilder()
                        .addHeader("Authorization", token)
                        .build()
                } ?: chain.request()
            chain.proceed(newRequest)
        }
        .build()

    @Singleton
    @Provides
    fun provideRetrofitService(
        okHttpClient: OkHttpClient
    ): PostsRetrofitSuspendInterface =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
            .create(PostsRetrofitSuspendInterface::class.java)


}