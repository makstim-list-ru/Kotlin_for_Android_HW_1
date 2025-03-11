package ru.netology.kotlin_for_android_hw_1.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.kotlin_for_android_hw_1.entity.PostEntity

@Dao
interface PostDaoSuspend {
    @Query("SELECT * FROM PostEntity ORDER BY id ASC")
    fun getPostsAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getPostById(id: Long): PostEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query(
        """
           UPDATE PostEntity SET
               likesNum = likesNum + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = :id;
        """
    )
    suspend fun likeByID(id: Long)

    @Query(
        """
           UPDATE PostEntity SET
               sharesNum = sharesNum + 1
           WHERE id = :id;
        """
    )
    suspend fun shareByID(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeByID(id: Long)

    suspend fun save(post: PostEntity) {
        insert(post)
    }

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    suspend fun edit(id: Long, content: String)

    suspend fun edit(post: PostEntity) {
        edit(post.id, post.content)
    }

    @Query("SELECT EXISTS(SELECT 1 FROM PostEntity)")
    suspend fun hasTable(): Boolean

}