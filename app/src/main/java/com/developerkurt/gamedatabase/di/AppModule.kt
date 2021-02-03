package com.developerkurt.gamedatabase.di.app_modules

import android.content.Context
import androidx.room.Room
import com.developerkurt.gamedatabase.data.LocalGameDataSource
import com.developerkurt.gamedatabase.data.RemoteGameDataSource
import com.developerkurt.gamedatabase.data.source.DefaultGameRepository
import com.developerkurt.gamedatabase.data.source.GameRepository
import com.developerkurt.gamedatabase.data.source.local.RoomLocalGameDataSource
import com.developerkurt.gamedatabase.data.source.local.room.RoomAppDatabase
import com.developerkurt.gamedatabase.data.source.local.room.databaseName
import com.developerkurt.gamedatabase.data.source.remote.DefaultRemoteGameDataSource
import com.developerkurt.gamedatabase.data.source.remote.api.DefaultGameAPIService
import com.developerkurt.gamedatabase.data.source.remote.api.DefaultGameAPIServiceGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule
{


    @Singleton
    @Provides
    fun provideDefaultRemoteGameDataSource(gameAPIService: DefaultGameAPIService, ioDispatcher: CoroutineDispatcher): RemoteGameDataSource
    {
        return DefaultRemoteGameDataSource(gameAPIService, ioDispatcher, true, 25000L)
    }

    @Singleton
    @Provides
    fun provideRoomLocalDataSource(roomDatabase: RoomAppDatabase, ioDispatcher: CoroutineDispatcher): LocalGameDataSource
    {
        return RoomLocalGameDataSource(roomDatabase.gameDataDao(), ioDispatcher)
    }

    @Singleton
    @Provides
    fun provideGameAPIService(): DefaultGameAPIService
    {
        return DefaultGameAPIServiceGenerator().create()
    }

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext applicationContext: Context): RoomAppDatabase
    {
        return Room.databaseBuilder(
                applicationContext,
                RoomAppDatabase::class.java,
                databaseName)
            .build()
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

/**
 * The binding for [GameRepository] is on its own module so that we can replace it easily in tests.
 */
@InstallIn(SingletonComponent::class)
@Module
object GameRepositoryModule
{

    /**
     * Modify this field in order the change the repository's config
     */
    private val repositoryConfig = DefaultGameRepository.RepositoryConfig.LOCAL_FIRST_CONTINUOUS_NETWORK_REFRESH

    @Singleton
    @Provides
    fun provideGameRepository(
            remoteDataSource: RemoteGameDataSource,
            localGameDataSource: LocalGameDataSource): GameRepository
    {
        return DefaultGameRepository(remoteDataSource, localGameDataSource, repositoryConfig)
    }
}