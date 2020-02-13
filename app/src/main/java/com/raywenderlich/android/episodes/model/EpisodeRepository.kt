package com.raywenderlich.android.episodes.model

import androidx.annotation.AnyThread
import com.raywenderlich.android.episodes.model.local.EpisodeDao
import com.raywenderlich.android.episodes.model.network.EpisodeRemoteDataSource
import com.raywenderlich.android.episodes.utils.ComparablePair
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class EpisodeRepository @Inject constructor(
  private val episodeDao: EpisodeDao,
  private val episodeRDS: EpisodeRemoteDataSource,
  private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

  private lateinit var favoritesSortOrder: List<String>

  private val favoritesFlow = flow {
    favoritesSortOrder = episodeRDS.favoritesSortOrder()
    emit(favoritesSortOrder)
  }

  val episodesFlow: Flow<List<Episode>>
      get() = episodeDao.loadAllEpisodesFlow()
        .combine(favoritesFlow) { episodes, favoritesOrder ->
          episodes.applySort(favoritesOrder)
        }
        .flowOn(defaultDispatcher)
        .conflate()

  fun getEpisodesForTrilogyFlow(trilogy: Trilogy): Flow<List<Episode>> {
    return episodeDao.getEpisodesForTrilogyNumberFlow(trilogy.number)
      .map { episodeList ->
        episodeList.applyMainSafeSort(favoritesSortOrder)
      }
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