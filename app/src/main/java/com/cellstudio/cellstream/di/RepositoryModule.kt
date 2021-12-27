package com.cellstudio.cellstream.di

import com.cellstudio.cellstream.data.base.repositories.config.ConfigRepository
import com.cellstudio.cellstream.data.base.repositories.config.DefaultConfigRepository
import com.cellstudio.cellstream.data.base.repositories.source.SourceRepository
import com.cellstudio.cellstream.data.commons.environments.Environment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideSourceRepository(environment: Environment): SourceRepository {
        return environment.selectedDataSource
    }

    @Provides
    @Singleton
    fun provideConfigRepository(environment: Environment): ConfigRepository {
        return DefaultConfigRepository(environment)
    }
}
