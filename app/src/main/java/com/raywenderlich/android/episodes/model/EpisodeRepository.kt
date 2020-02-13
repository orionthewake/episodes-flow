package com.raywenderlich.android.episodes.model

import com.raywenderlich.android.episodes.model.local.EpisodeDao
import com.raywenderlich.android.episodes.model.network.EpisodeRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class EpisodeRepository @Inject constructor(
  private val episodeDao: EpisodeDao,
  private val episodeRDS: EpisodeRemoteDataSource,
  private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

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
}