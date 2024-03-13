package com.app.taocalligraphy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse

@Database(entities = [MeditationContentResponse::class], version = 1, exportSchema = false)
@TypeConverters(DataConvertors::class)
abstract class TaoCalligraphyDatabase : RoomDatabase() {
    abstract fun meditationContentDao(): MeditationContentDao
}