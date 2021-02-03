package com.developerkurt.gamedatabase.di

import com.developerkurt.gamedatabase.data.source.DefaultGameRepository
import com.developerkurt.gamedatabase.data.source.GameRepository
import com.developerkurt.gamedatabase.data.source.fake.FakeGameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt will inject a [FakeGameRepository] instead of a [DefaultGameRepository].
 */
@Module
@InstallIn(SingletonComponent::class)
object TestGameRepositoryModule
{
    @Singleton
    @Provides
    fun provideFakeRepository(): GameRepository = FakeGameRepository()
}