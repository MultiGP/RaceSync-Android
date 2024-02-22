package com.multigp.racesync.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.multigp.racesync.domain.model.Chapter
import com.multigp.racesync.domain.model.Race
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addChapters(chapters: List<Chapter>)
    @Query("SELECT * FROM chapters")
    fun getAllChapters(): PagingSource<Int, Chapter>

    @Query("SELECT * FROM chapters WHERE id = :chapterId")
    fun getChapter(chapterId: String): Flow<Chapter>

    @Query("DELETE FROM chapters")
    suspend fun deleteAllChapters()
}