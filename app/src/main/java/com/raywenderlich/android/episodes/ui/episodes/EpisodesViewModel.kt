package com.raywenderlich.android.episodes.ui.episodes

import androidx.lifecycle.*
import com.raywenderlich.android.episodes.model.Episode
import com.raywenderlich.android.episodes.model.EpisodeRepository
import com.raywenderlich.android.episodes.model.NoTrilogy
import com.raywenderlich.android.episodes.model.Trilogy
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class EpisodesViewModel @Inject constructor(
  private val episodeRepository: EpisodeRepository
) : ViewModel() {

  val snackbar: LiveData<String?>
    get() = _snackbar

  private val _snackbar = MutableLiveData<String?>()

  private val _spinner = MutableLiveData<Boolean>(false)

  val spinner: LiveData<Boolean>
    get() = _spinner

  private val trilogyChannel = ConflatedBroadcastChannel<Trilogy>()

  val episodesUsingFlow: Flow<List<Episode>> = trilogyChannel.asFlow()
    .flatMapLatest { trilogy ->
      _spinner.value = true
      if (trilogy == NoTrilogy) {
        episodeRepository.episodesFlow
      } else {
        episodeRepository.getEpisodesForTrilogyFlow(trilogy)
      }
    }
    .onEach {
      _spinner.value = false
    }
    .catch {  throwable ->
      _snackbar.value = throwable.message
    }

  init {
    clearTrilogyNumber()

    loadData { episodeRepository.tryUpdateRecentEpisodesCache() }
  }

  fun setTrilogyNumber(num: Int) {
    trilogyChannel.offer(Trilogy(num))

    loadData { episodeRepository.tryUpdateRecentEpisodesForTrilogyCache(Trilogy(num)) }
  }

  private fun clearTrilogyNumber() {
    trilogyChannel.offer(NoTrilogy)

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
