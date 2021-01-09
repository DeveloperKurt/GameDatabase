package com.developerkurt.gamedatabase.di

import android.content.Context
import androidx.hilt.lifecycle.ViewModelAssistedFactory
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.developerkurt.gamedatabase.data.BaseRepository
import com.developerkurt.gamedatabase.data.GameRepository
import com.developerkurt.gamedatabase.data.api.GameAPIService
import com.developerkurt.gamedatabase.data.api.GameAPIServiceGenerator
import com.developerkurt.gamedatabase.data.persistence.RoomAppDatabase
import com.developerkurt.gamedatabase.viewmodels.GameListViewModel
import com.developerkurt.gamedatabase.viewmodels.GameListViewModel_AssistedFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import okhttp3.mockwebserver.MockWebServer
import org.mockito.Mockito
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class NetworkTestingModule
{

    @Singleton
    @Provides
    fun provideMockWebServer(): MockWebServer = MockWebServer()


    @Singleton
    @Provides
    fun provideGameAPIService(mockServer: MockWebServer): GameAPIService
    {
        return GameAPIServiceGenerator(baseUrl = mockServer.url("/").toString()).create()
    }

}

@InstallIn(SingletonComponent::class)
@Module
class PersistenceTestingModule
{

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext applicationContext: Context): RoomAppDatabase
    {
        return Room.inMemoryDatabaseBuilder(
                applicationContext, RoomAppDatabase::class.java).build()

    }
}

//Disable retry when failed to save from execution time.
@InstallIn(SingletonComponent::class)
@Module
class GameRepositoryTestingModule
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
            .setShouldRetryWhenFailed(false)
            .setRefreshIntervalInMs(0L)
            .create()
    }
}

val gameListViewModel: GameListViewModel by lazy { Mockito.mock(GameListViewModel::class.java) }

@InstallIn(ActivityRetainedComponent::class)
@Module
interface TestActivityModule
{
    @Binds
    @IntoMap
    @StringKey("com.developerkurt.gamedatabase.viewmodels.GameListViewModel")
    fun bindGameListViewModel(factory: GameListViewModel_AssistedFactory): ViewModelAssistedFactory<out ViewModel>
}

