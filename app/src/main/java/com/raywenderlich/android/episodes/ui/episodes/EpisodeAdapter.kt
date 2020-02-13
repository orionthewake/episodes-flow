package com.raywenderlich.android.episodes.ui.episodes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.episodes.databinding.ListItemEpisodeBinding
import com.raywenderlich.android.episodes.model.Episode

class EpisodeAdapter : ListAdapter<Episode, RecyclerView.ViewHolder>(EpisodeDiffCallback()) {

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val episode = getItem(position)
    (holder as EpisodeViewHolder).bind(episode)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return EpisodeViewHolder(
      ListItemEpisodeBinding.inflate(
        LayoutInflater.from(parent.context), parent, false
      )
    )
  }

  class EpisodeViewHolder(
    private val binding: ListItemEpisodeBinding
  ) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Episode) {
      binding.apply {
        episode = item
        executePendingBindings()
      }
    }
  }
}

private class EpisodeDiffCallback : DiffUtil.ItemCallback<Episode>() {

  override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean {
    return oldItem.episodeId == newItem.episodeId
  }

  override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean {
    return oldItem == newItem
  }
}