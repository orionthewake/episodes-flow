package com.raywenderlich.android.episodes.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.raywenderlich.android.episodes.model.Episode

@Dao
interface EpisodeDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(episode: Episode)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveAll(episodes: List<Episode>)
}