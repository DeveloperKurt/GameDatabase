package com.developerkurt.gamedatabase.di

import com.developerkurt.gamedatabase.data.GameRepository
import com.developerkurt.gamedatabase.data.api.GameAPIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class GameRepositoryModule
{

    @Singleton
    @Provides
    fun provideGameRepository(apiService: GameAPIService): GameRepository
    {
        return GameRepository(apiService)
    }
}