package com.raywenderlich.android.episodes.model

import androidx.annotation.AnyThread
import com.raywenderlich.android.episodes.model.local.EpisodeDao
import com.raywenderlich.android.episodes.model.network.EpisodeRemoteDataSource
import com.raywenderlich.android.episodes.utils.ComparablePair
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRepository @Inject constructor(
  private val episodeDao: EpisodeDao,
  private val episodeRDS: EpisodeRemoteDataSource,
  private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

  val episodesFlow: Flow<List<Episode>>
    get() = episodeDao.loadAllEpisodesFlow()
        .flowOn(defaultDispatcher)
        .conflate()

  fun getEpisodesForTrilogyFlow(trilogy: Trilogy): Flow<List<Episode>> {
    return episodeDao.getEpisodesForTrilogyNumberFlow(trilogy.number)
  }

  private fun shouldUpdateEpisodesCache(): Boolean {
    return true
  }

  suspend fun tryUpdateRecentEpisodesCache() {
    if (shouldUpdateEpisodesCache()) fetchRecentEpisodes()
  }

  suspend fun tryUpdateRecentEpisodesForTrilogyCache(trilogy: Trilogy) {
    if (shouldUpdateEpisodesCache()) fetchRecentEpisodesForTrilogy(trilogy)
  }

  private suspend fun fetchRecentEpisodes() {
    val episodes = episodeRDS.fetchAllEpisodes()
    episodeDao.saveAll(episodes)
  }

  private suspend fun fetchRecentEpisodesForTrilogy(trilogy: Trilogy) {
    val episodesForTrilogy = episodeRDS.episodesForTrilogy(trilogy)
    episodeDao.saveAll(episodesForTrilogy)
  }

  private fun List<Episode>.applySort(favoritesSortOrder: List<String>): List<Episode> {
    return sortedBy { episode ->
      val positionForItem = favoritesSortOrder.indexOf(episode.episodeId).let { order ->
        if (order > -1) order else Int.MAX_VALUE
      }
      ComparablePair(positionForItem, episode.number)
    }
  }

  @AnyThread
  suspend fun List<Episode>.applyMainSafeSort(favoritesSortOrder: List<String>) =
    withContext(defaultDispatcher) {
      this@applyMainSafeSort.applySort(favoritesSortOrder)
    }
}