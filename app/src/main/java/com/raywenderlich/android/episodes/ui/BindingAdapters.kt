package com.raywenderlich.android.episodes.ui

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
  if (!imageUrl.isNullOrEmpty()) {
    Glide.with(view.context)
      .load(imageUrl)
      .transition(DrawableTransitionOptions.withCrossFade())
      .into(view)
  }
}
