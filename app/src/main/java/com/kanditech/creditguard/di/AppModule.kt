package com.kanditech.creditguard.di

import android.content.Context
import androidx.room.Room
import com.kanditech.creditguard.data.local.CreditGuardDatabase
import com.kanditech.creditguard.data.local.SessionManager
import com.kanditech.creditguard.data.local.dao.CreditGuardDao
import com.kanditech.creditguard.data.remote.AuthInterceptor
import com.kanditech.creditguard.data.remote.CreditGuardApi
import com.kanditech.creditguard.data.repository.CreditGuardRepositoryImpl
import com.kanditech.creditguard.domain.repository.CreditGuardRepository
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
object AppModule {

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionManager: SessionManager): AuthInterceptor {
        return AuthInterceptor(sessionManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://creditguard-backend-bpfecqbeaudwajcj.southindia-01.azurewebsites.net/api/") // Placeholder, change as needed
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCreditGuardApi(retrofit: Retrofit): CreditGuardApi {
        return retrofit.create(CreditGuardApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CreditGuardDatabase {
        return Room.databaseBuilder(
            context,
            CreditGuardDatabase::class.java,
            "creditguard_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCreditGuardDao(db: CreditGuardDatabase): CreditGuardDao {
        return db.creditGuardDao()
    }

    @Provides
    @Singleton
    fun provideCreditGuardRepository(
        api: CreditGuardApi,
        dao: CreditGuardDao
    ): CreditGuardRepository {
        return CreditGuardRepositoryImpl(api, dao)
    }
}
