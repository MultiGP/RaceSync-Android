package com.multigp.racesync.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapter_remote_keys")
class ChapterRemoteKeys(
    @PrimaryKey
    val chapterId: String,
    val prevKey: Int?,
    val nextKey: Int?
)