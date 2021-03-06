package com.raywenderlich.android.episodes.ui.episodes

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.raywenderlich.android.episodes.R
import com.raywenderlich.android.episodes.databinding.EpisodesFragmentBinding
import com.raywenderlich.android.episodes.di.Injectable
import com.raywenderlich.android.episodes.ui.injectViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


@ExperimentalCoroutinesApi
@FlowPreview
class EpisodesFragment : Fragment(), Injectable, CoroutineScope {

  private var job: Job = Job()

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private lateinit var viewModel: EpisodesViewModel

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main + job


  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    viewModel = injectViewModel(viewModelFactory)

    val binding = EpisodesFragmentBinding.inflate(inflater, container, false)
    context ?: return binding.root

    viewModel.spinner.observe(viewLifecycleOwner) { show ->
      binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
    }

    viewModel.snackbar.observe(viewLifecycleOwner) { text ->
      text?.let {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
        viewModel.onSnackbarShown()
      }
    }

    val adapter = EpisodeAdapter()
    binding.episodeList.adapter = adapter
    launch {
      subscribeUi(adapter)
    }

    setHasOptionsMenu(true)
    return binding.root
  }

  override fun onDestroy() {
    super.onDestroy()
    job.cancel()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.filter_prequels -> {
        filterData(1)
        true
      }
      R.id.filter_original -> {
        filterData(2)
        true
      }
      R.id.filter_sequels -> {
        filterData(3)
        true
      }
      R.id.filter_all -> {
        filterData(-1)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private suspend fun subscribeUi(adapter: EpisodeAdapter) {
    viewModel.episodesUsingFlow.collect { episodes ->
      adapter.submitList(episodes)
    }
  }

  private fun filterData(num: Int) {
    with(viewModel) {
      setTrilogyNumber(num)
    }
  }

}
