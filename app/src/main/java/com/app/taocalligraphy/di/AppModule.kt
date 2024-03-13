package com.app.taocalligraphy.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.app.taocalligraphy.BuildConfig
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.ApiHelperImpl
import com.app.taocalligraphy.api.ApiService
import com.app.taocalligraphy.database.TaoCalligraphyDatabase
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.SaveUserDetail
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.userHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideBaseUrl() = BuildConfig.API_URL

    @Singleton
    @Provides
    fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            // Create a Custom Interceptor to apply Headers application wide
            val headerInterceptor = Interceptor { chain ->
                var request = chain.request()
                request = request.newBuilder()
                    .addHeader(
                        "X-API-KEY",
                        "U2FsdGVkX19T6bllTz2bJnE9EAJU5BPHQaXGp6TugLYbwR4d12/JurAq28FqDR+bx0Udj851+xbpbUfNsBVklAZ7SHnuC5Q/Nuol2yaOgXwtWJs+H9ZLn3w6B2PFOikJ"
                    )
                    .addHeader("AccessToken", "" + userHolder.accessToken)
                    .addHeader("DeviceLanguage", "" + Locale.getDefault().language.lowercase())
                    .addHeader("timezone", "" + TimeZone.getDefault().id)
                    .build()
                val response = chain.proceed(request)
                response
            }
            OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // connect timeout
                .readTimeout(60, TimeUnit.SECONDS) // Socked Timeout
                .addInterceptor(headerInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
        } else {
            val headerInterceptor = Interceptor { chain ->
                var request = chain.request()
                request = request.newBuilder().addHeader(
                    "X-API-KEY",
                    "U2FsdGVkX19T6bllTz2bJnE9EAJU5BPHQaXGp6TugLYbwR4d12/JurAq28FqDR+bx0Udj851+xbpbUfNsBVklAZ7SHnuC5Q/Nuol2yaOgXwtWJs+H9ZLn3w6B2PFOikJ"
                )
                    .addHeader("AccessToken", "" + userHolder.accessToken)
                    .addHeader("DeviceLanguage", "" + Locale.getDefault().language.lowercase())
                    .addHeader("timezone", "" + TimeZone.getDefault().id)
                    .build()
                val response = chain.proceed(request)
                response
            }
            OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // connect timeout
                .readTimeout(60, TimeUnit.SECONDS) // Socked Timeout
                .addInterceptor(headerInterceptor)
                .build()
        }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BASE_URL: String): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

    @Provides
    @Singleton
    fun provideViewHolder(@ApplicationContext context: Context): UserHolder =
        UserHolder(context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE))

    @Provides
    @Singleton
    fun provideUserViewHolder(@ApplicationContext context: Context): SaveUserDetail =
        SaveUserDetail(context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE))

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaoCalligraphyDatabase =
        Room.databaseBuilder(
            context,
            TaoCalligraphyDatabase::class.java,
            "tao_calligraphy_database"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

    @Provides
    @Singleton
    fun provideDao(db: TaoCalligraphyDatabase) = db.meditationContentDao()
}