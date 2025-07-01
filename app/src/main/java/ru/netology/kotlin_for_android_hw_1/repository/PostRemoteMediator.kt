package ru.netology.kotlin_for_android_hw_1.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.netology.kotlin_for_android_hw_1.dao.PostDaoSuspend
import ru.netology.kotlin_for_android_hw_1.dao.PostRemoteKeyDao
import ru.netology.kotlin_for_android_hw_1.entity.PostEntity
import ru.netology.kotlin_for_android_hw_1.entity.PostRemoteKeyEntity
import ru.netology.kotlin_for_android_hw_1.retrofit.PostsRetrofitSuspendInterface
import ru.netology.kotlin_for_android_hw_1.roomdb.RoomDBSuspend
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val db: RoomDBSuspend,
    private val dao: PostDaoSuspend,
    private val postsRetrofitSuspendInterface: PostsRetrofitSuspendInterface,
    private val postRemoteKeyDao: PostRemoteKeyDao,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {

                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    postsRetrofitSuspendInterface.getBefore(id, state.config.pageSize)
                }

                LoadType.PREPEND -> {
//                    return MediatorResult.Success(true)
                    val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    postsRetrofitSuspendInterface.getAfter(id, state.config.pageSize)
                }

                LoadType.REFRESH -> {
                    //return MediatorResult.Success(false)
                    postsRetrofitSuspendInterface.getLatest(state.config.initialLoadSize)
//                    if (dao.isEmpty()) postsRetrofitSuspendInterface.getLatest(
//                        state.config.initialLoadSize
//                    ) else {
//                        val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(true)
//                        postsRetrofitSuspendInterface.getAfter(
//                            id,
//                            state.config.pageSize
//                        )
//                    }
                }
            }
            if (!response.isSuccessful) {
                throw HttpException(response)
            }

            if (response.body().isNullOrEmpty())
                if (response.body() == null)
                    println("ERR --------------- postList = response.body() == NULL")
                else
                    println("ERR --------------- postList = response.body() == EMPTY")

            val postList = response.body().orEmpty()

            if(postList.isEmpty()) return MediatorResult.Success(postList.isEmpty())

            db.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        postRemoteKeyDao.removeAll()
                        postRemoteKeyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.AFTER,
                                    id = postList.first().id,
                                ),
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.BEFORE,
                                    id = postList.last().id,
                                ),
                            )
                        )
                        dao.removeAllComplete()
                    }

                    LoadType.PREPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.AFTER,
                                id = postList.first().id,
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                id = postList.last().id,
                            )
                        )
                    }
                }

                dao.insert(postList.map { PostEntity.fromPostToEntity(it) })
            }

            return MediatorResult.Success(postList.isEmpty())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            println("ERR --------------- MediatorResult.Error(e): $e")
            return MediatorResult.Error(e)
        }
    }
}