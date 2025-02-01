package ru.netology.kotlin_for_android_hw_1.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.kotlin_for_android_hw_1.dao.PostDao
import ru.netology.kotlin_for_android_hw_1.entity.PostEntity

@Database (entities = [PostEntity::class], version = 1)
abstract class RoomDB : RoomDatabase () {
    abstract fun getPostDao() : PostDao
}