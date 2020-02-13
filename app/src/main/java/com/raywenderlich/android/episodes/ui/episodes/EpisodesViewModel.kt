package com.raywenderlich.android.episodes.ui.episodes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.episodes.model.Episode
import com.raywenderlich.android.episodes.model.EpisodeRepository
import com.raywenderlich.android.episodes.model.Trilogy
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


class EpisodesViewModel @Inject constructor(
  private val episodeRepository: EpisodeRepository
) : ViewModel() {

  val snackbar: LiveData<String?>
    get() = _snackbar

  private val _snackbar = MutableLiveData<String?>()

  private val _spinner = MutableLiveData<Boolean>(false)

  val spinner: LiveData<Boolean>
    get() = _spinner

  val episodesUsingFlow: Flow<List<Episode>> =
      episodeRepository.episodesFlow

  init {
    clearTrilogyNumber()

    loadData { episodeRepository.tryUpdateRecentEpisodesCache() }
  }

  fun setTrilogyNumber(num: Int) {
    loadData { episodeRepository.tryUpdateRecentEpisodesForTrilogyCache(Trilogy(num)) }
  }

  private fun clearTrilogyNumber() {
    loadData { episodeRepository.tryUpdateRecentEpisodesCache() }
  }

  fun onSnackbarShown() {
    _snackbar.value = null
  }

  private fun loadData(block: suspend () -> Unit): Job {
    return viewModelScope.launch {
      try {
        _spinner.value = true
        block()
      } catch (error: Throwable) {
        _snackbar.value = error.message
      } finally {
        _spinner.value = false
      }
    }
  }
}
