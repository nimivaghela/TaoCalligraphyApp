package com.app.taocalligraphy.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse

@Dao
interface MeditationContentDao {

    @Query("SELECT * FROM MeditationContent where isDeleted = :isDeleted")
    fun getAllMeditationContents(isDeleted: Boolean): LiveData<List<MeditationContentResponse>>

    @Query("SELECT * FROM MeditationContent where isDeleted = :isDeleted ORDER BY createdTime DESC")
    fun getAllMeditationContentsDesc(isDeleted: Boolean): LiveData<List<MeditationContentResponse>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(meditationContentResponse: MeditationContentResponse)

    @Query("DELETE FROM MeditationContent")
    fun deleteAll()

    @Query("DELETE FROM MeditationContent where meditation_id = :id")
    fun deleteMeditationContent(id: String)

    @Query("SELECT * FROM MeditationContent where meditation_id = :id")
    fun getMeditationContent(id: String) : MeditationContentResponse

    @Query("UPDATE MeditationContent set isDeleted = :isDeleted where meditation_id = :id")
    fun updateMeditationContent(isDeleted: Boolean, id: String)

    @Query("SELECT COUNT() FROM MeditationContent")
    fun getCount(): Int
}