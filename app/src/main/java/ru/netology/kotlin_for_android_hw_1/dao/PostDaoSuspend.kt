package ru.netology.kotlin_for_android_hw_1.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import ru.netology.kotlin_for_android_hw_1.entity.PostEntity

@Dao
interface PostDaoSuspend {
    //@Query("SELECT * FROM PostEntity ORDER BY id ASC")
    @Query("SELECT * FROM PostEntity ORDER BY CASE WHEN id >= 0 THEN 0 ELSE 1 END ASC, ABS(id) ASC")
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

    @Query("DELETE FROM PostEntity WHERE id > 0")
    suspend fun removeAll()

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

    @Transaction
    suspend fun deleteAndInsert(posts: List<PostEntity>) {
        removeAll()
        insert(posts)
    }

    @Query("SELECT MIN(id) FROM PostEntity")
    suspend fun getMinId(): Long?

    @Query("SELECT MAX(id) FROM PostEntity")
    suspend fun getMaxId(): Long?

    @Query("SELECT * FROM PostEntity WHERE id < 0 ORDER BY id ASC")
    suspend fun getUnsaved(): List<PostEntity>

}