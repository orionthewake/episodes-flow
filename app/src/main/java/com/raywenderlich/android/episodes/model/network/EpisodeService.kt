package com.raywenderlich.android.episodes.model.network

import com.raywenderlich.android.episodes.model.Episode
import retrofit2.http.GET

interface EpisodeService {

  companion object {
    const val BASE_URL = "https://raw.githubusercontent.com/"
  }

  @GET("orionthewake/episodes/master/data/episodes.json")
  suspend fun getAllEpisodes(): List<Episode>

  @GET("orionthewake/episodes/master/data/favorites.json")
  suspend fun getFavoritesSortOrder() : List<Episode>
}