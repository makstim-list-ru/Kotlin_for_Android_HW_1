package ru.netology.kotlin_for_android_hw_1.roomdb

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.kotlin_for_android_hw_1.dao.PostDaoSuspend
import ru.netology.kotlin_for_android_hw_1.dao.PostRemoteKeyDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Provides
    @Singleton
    fun provideDb(
        @ApplicationContext
        context: Context
    ): RoomDBSuspend =
        Room.databaseBuilder(context, RoomDBSuspend::class.java, "database.db").build()

    @Provides
    fun providePostDao(roomDBSuspend: RoomDBSuspend) : PostDaoSuspend = roomDBSuspend.getPostDao()

    @Provides
    fun provideRemoteKeyDao(roomDBSuspend: RoomDBSuspend) : PostRemoteKeyDao = roomDBSuspend.getPostRemoteKeyDao()
}