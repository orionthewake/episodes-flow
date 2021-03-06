package com.raywenderlich.android.episodes.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.raywenderlich.android.episodes.model.Episode

@Database(entities = [Episode::class], version = 1, exportSchema = false)
abstract class EpisodeDatabase : RoomDatabase() {
  abstract fun episodeDao(): EpisodeDao

  companion object {

    @Volatile
    private var instance: EpisodeDatabase? = null

    fun getInstance(context: Context): EpisodeDatabase {
      return instance ?: synchronized(this) {
        instance ?: buildDatabase(context).also { instance = it }
      }
    }

    private fun buildDatabase(context: Context): EpisodeDatabase {
      return Room.databaseBuilder(context, EpisodeDatabase::class.java, "episodedb")
        .build()
    }
  }
}