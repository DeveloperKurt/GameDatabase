package com.developerkurt.gamedatabase.di

import com.developerkurt.gamedatabase.data.api.GameAPIService
import com.developerkurt.gamedatabase.data.api.GameAPIServiceGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule
{
    @Singleton
    @Provides
    fun provideGameAPIService(): GameAPIService
    {
        return GameAPIServiceGenerator().create()
    }
}