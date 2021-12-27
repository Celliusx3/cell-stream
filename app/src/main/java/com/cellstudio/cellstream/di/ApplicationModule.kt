package com.cellstudio.cellstream.di

import android.content.Context
import android.content.SharedPreferences
import com.cellstudio.cellstream.data.commons.environments.DefaultEnvironment
import com.cellstudio.cellstream.data.commons.environments.Environment
import com.cellstudio.cellstream.data.services.network.DefaultNetworkService
import com.cellstudio.cellstream.data.services.network.NetworkService
import com.cellstudio.cellstream.data.services.storage.DefaultStorageService
import com.cellstudio.cellstream.data.services.storage.StorageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    private var prefs: SharedPreferences? = null

    @Provides
    @Singleton
    fun provideNetworkService(): NetworkService {
        return DefaultNetworkService()
    }

    @Provides
    @Singleton
    fun provideStorageService(prefs: SharedPreferences): StorageService {
        return DefaultStorageService(prefs)
    }

    @Provides
    @Singleton
    fun provideEnvironment(networkService: NetworkService, storageService: StorageService): Environment {
        return DefaultEnvironment(networkService, storageService)
    }


    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        if (prefs == null) {
            val key = context.packageName ?: throw NullPointerException("Prefs key may not be null")
            prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        }
        return prefs!!
    }
}