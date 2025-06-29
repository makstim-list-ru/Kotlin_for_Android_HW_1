package ru.netology.kotlin_for_android_hw_1.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.kotlin_for_android_hw_1.dao.PostDaoSuspend
import ru.netology.kotlin_for_android_hw_1.dao.PostRemoteKeyDao
import ru.netology.kotlin_for_android_hw_1.entity.PostEntity
import ru.netology.kotlin_for_android_hw_1.entity.PostRemoteKeyEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class], version = 1, exportSchema = false)
abstract class RoomDBSuspend : RoomDatabase() {
    abstract fun getPostDao(): PostDaoSuspend
    abstract fun getPostRemoteKeyDao(): PostRemoteKeyDao
}