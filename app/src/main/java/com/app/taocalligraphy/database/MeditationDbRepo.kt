package com.app.taocalligraphy.database

import androidx.lifecycle.LiveData
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import javax.inject.Inject

class MeditationDbRepo @Inject constructor(private val meditationContentDao: MeditationContentDao) {

    fun getAllMeditationContent(isDeleted: Boolean): LiveData<List<MeditationContentResponse>> =
        meditationContentDao.getAllMeditationContents(isDeleted)

    fun getAllMeditationContentsDesc(isDeleted: Boolean): LiveData<List<MeditationContentResponse>> =
        meditationContentDao.getAllMeditationContentsDesc(isDeleted)

    fun addMeditationContent(meditationContentResponse: MeditationContentResponse) =
        meditationContentDao.insert(meditationContentResponse)

    fun deleteAllMeditationContent() = meditationContentDao.deleteAll()

    fun deleteMeditationContent(id: String) =
        meditationContentDao.deleteMeditationContent(id)

    fun getMeditationData(id: String): MeditationContentResponse? =
        meditationContentDao.getMeditationContent(id)

    fun updateMeditationData(isDeleted: Boolean, id: String) =
        meditationContentDao.updateMeditationContent(isDeleted, id)

    fun getTotalDownloads() = meditationContentDao.getCount()
}