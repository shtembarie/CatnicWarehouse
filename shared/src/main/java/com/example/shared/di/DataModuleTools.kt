package com.example.shared.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.data.BuildConfig
import com.example.shared.local.dataStore.DataStoreManager
import com.example.shared.local.dataStore.DataStoreManagerImp
import com.example.shared.local.room.DBManager.AppDatabase
import com.example.shared.networking.interceptor.RetrofitInterceptor
import com.example.shared.networking.services.login.LoginApiService
import com.example.shared.networking.services.main.ApiServices
import com.example.shared.networking.services.warehouse.WarehouseApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DomaineTools {


    @Singleton
    @Provides
    fun provideBlogDb(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }


    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Singleton
    @Provides
    @MainAppOkHTTPInstance
    fun providesMainOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        preferences: SharedPreferences,
        @ApplicationContext appContext: Context
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(RetrofitInterceptor(provideDataStoreManager(context = appContext )))
            .addInterceptor { chain ->
                val token = preferences.getString("token", "token_")!!
                val request = chain.request().newBuilder().addHeader(
                    "Authorization", "Bearer $token"
                ).build()
                chain.proceed(request)
            }
            .build()

    @Singleton
    @Provides
    @WarehouseAppOkHTTPInstance
    fun providesWarehouseOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        preferences: SharedPreferences,
        @ApplicationContext appContext: Context
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(RetrofitInterceptor(provideDataStoreManager(context = appContext )))
            .addInterceptor { chain ->
                val token = preferences.getString("token", "token_")!!
                val request = chain.request().newBuilder().addHeader(
                    "Authorization", "Bearer $token"
                ).build()
                chain.proceed(request)
            }
            .build()

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(httpLoggingInterceptor)

            .build()


    @Singleton
    @Provides
    @LoginRetrofitInstance
    fun provideLoginRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.loginbaseUrl)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    @LoginAppApiService
    fun provideLoginApiService(@LoginRetrofitInstance retrofit: Retrofit): LoginApiService =
        retrofit.create(LoginApiService::class.java)

    @Singleton
    @Provides
    @MainAppRetrofitInstance
    fun provideMainRetrofit(
        @MainAppOkHTTPInstance okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.mainbaseUrl)
        .client(okHttpClient)
        .build()


    @Singleton
    @Provides
    @WarehouseAppApiService
    fun provideWarehouseApiService(@WarehouseAppRetrofitInstance retrofit: Retrofit): WarehouseApiServices = retrofit.create(WarehouseApiServices::class.java)
    @Singleton
    @Provides
    @WarehouseAppRetrofitInstance
    fun provideWarehouseRetrofit(
        @WarehouseAppOkHTTPInstance okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.warehouseBaseUrl)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    @MainAppApiService
    fun provideApiService(@MainAppRetrofitInstance retrofit: Retrofit): ApiServices =
        retrofit.create(ApiServices::class.java)

    @Singleton
    @Provides
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManagerImp(context)
    }

}

@Qualifier
annotation class LoginRetrofitInstance

@Qualifier
annotation class MainAppRetrofitInstance

@Qualifier
annotation class MainAppOkHTTPInstance

@Qualifier
annotation class LoginAppApiService

@Qualifier
annotation class MainAppApiService

@Qualifier
annotation class WarehouseAppRetrofitInstance

@Qualifier
annotation class WarehouseAppOkHTTPInstance

@Qualifier
annotation class WarehouseAppApiService


