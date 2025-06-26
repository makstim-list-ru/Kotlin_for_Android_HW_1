package ru.netology.kotlin_for_android_hw_1.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import okio.IOException
import retrofit2.HttpException
import ru.netology.kotlin_for_android_hw_1.dao.PostDaoSuspend
import ru.netology.kotlin_for_android_hw_1.entity.PostEntity
import ru.netology.kotlin_for_android_hw_1.retrofit.PostsRetrofitSuspendInterface

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val dao: PostDaoSuspend,
    private val postsRetrofitSuspendInterface: PostsRetrofitSuspendInterface
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {
        try {
            val result = when (loadType) {

                LoadType.APPEND -> {
                    val id = state.lastItemOrNull()?.id ?: return MediatorResult.Success(false)
                    postsRetrofitSuspendInterface.getBefore(id, state.config.pageSize)
                }

                LoadType.PREPEND -> {
                    val id = state.lastItemOrNull()?.id ?: return MediatorResult.Success(false)
                    postsRetrofitSuspendInterface.getAfter(id, state.config.pageSize)
                }

                LoadType.REFRESH -> postsRetrofitSuspendInterface.getLatest(state.config.pageSize)
            }
            if (!result.isSuccessful) {
                throw HttpException(result)
            }
            val post = result.body().orEmpty()

            dao.insert(post.map { PostEntity.fromPostToEntity(it) })

            return MediatorResult.Success(post.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}