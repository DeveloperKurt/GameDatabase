package com.developerkurt.gamedatabase.di

import com.developerkurt.gamedatabase.data.BaseRepository
import com.developerkurt.gamedatabase.data.GameRepository
import com.developerkurt.gamedatabase.data.api.GameAPIService
import com.developerkurt.gamedatabase.data.persistence.RoomAppDatabase
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
    fun provideGameRepository(
            apiService: GameAPIService,
            roomAppDatabase: RoomAppDatabase): GameRepository
    {
        return GameRepository.GameRepositoryBuilder()
            .setApiService(apiService)
            .setRoomDatabase(roomAppDatabase)
            .setConfig(BaseRepository.RepositoryConfig.LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH)
            .setShouldRetryWhenFailed(true)
            .create()
    }
}