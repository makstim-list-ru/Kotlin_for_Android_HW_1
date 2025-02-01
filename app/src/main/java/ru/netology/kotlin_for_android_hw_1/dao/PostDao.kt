package ru.netology.kotlin_for_android_hw_1.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.kotlin_for_android_hw_1.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getPostsAll(): LiveData<List<PostEntity>>

    @Insert
    fun insert(post: PostEntity)

    @Query(
        """
           UPDATE PostEntity SET
               likesNum = likesNum + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = :id;
        """
    )
    fun likeByID(id: Long)

    @Query(
        """
           UPDATE PostEntity SET
               sharesNum = sharesNum + 1
           WHERE id = :id;
        """
    )
    fun shareByID(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeByID(id: Long)

    fun save(post: PostEntity) {
        insert(post)
    }

    @Query("UPDATE PostEntity SET content = :content WHERE id = :id")
    fun edit(id: Long, content: String)

    fun edit(post: PostEntity) {
        edit(post.id, post.content)
    }
}