package com.multigp.racesync.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.multigp.racesync.domain.model.ChapterRemoteKeys
import com.multigp.racesync.domain.model.RaceRemoteKeys

@Dao
interface ChapterRemoteKeysDao {
    @Query("SELECT * FROM chapter_remote_keys WHERE chapterId = :chapterId")
    suspend fun getChapterRemoteKeys(chapterId: String): ChapterRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllChapterRemoteKeys(chapterRemoteKeys: List<ChapterRemoteKeys>)

    @Query("DELETE FROM chapter_remote_keys")
    suspend fun deleteAllChapterRemoteKeys()
}