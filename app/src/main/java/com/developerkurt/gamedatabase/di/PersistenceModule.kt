package com.developerkurt.gamedatabase.di

import android.content.Context
import androidx.room.Room
import com.developerkurt.gamedatabase.data.persistence.RoomAppDatabase
import com.developerkurt.gamedatabase.data.persistence.databaseName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PersistenceModule
{

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
}