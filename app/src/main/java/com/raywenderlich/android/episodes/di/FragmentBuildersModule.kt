package com.raywenderlich.android.episodes.di

import com.raywenderlich.android.episodes.ui.episodes.EpisodesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
  @ContributesAndroidInjector
  abstract fun contributeThemeFragment(): EpisodesFragment
}