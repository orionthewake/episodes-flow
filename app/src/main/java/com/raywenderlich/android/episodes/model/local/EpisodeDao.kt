package com.raywenderlich.android.episodes.model.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.raywenderlich.android.episodes.model.Episode
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun save(episode: Episode)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveAll(episodes: List<Episode>)

  @Query("SELECT * FROM episode WHERE episodeId = :episodeId")
  fun loadEpisode(episodeId: String): LiveData<Episode>

  @Query("SELECT * FROM episode ORDER BY number")
  fun loadAllEpisodes(): LiveData<List<Episode>>

  @Query("SELECT * FROM episode WHERE trilogy = :trilogyNumber ORDER BY number")
  fun getEpisodesForTrilogyNumber(trilogyNumber: Int): LiveData<List<Episode>>

  @Query("SELECT * FROM episode ORDER BY number")
  fun loadAllEpisodesFlow(): Flow<List<Episode>>

  @Query("SELECT * FROM episode WHERE trilogy = :trilogyNumber ORDER BY number")
  fun getEpisodesForTrilogyNumberFlow(trilogyNumber: Int): Flow<List<Episode>>
}