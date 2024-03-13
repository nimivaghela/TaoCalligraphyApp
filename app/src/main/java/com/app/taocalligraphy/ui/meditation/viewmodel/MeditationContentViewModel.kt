package com.app.taocalligraphy.ui.meditation.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.R
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.response.alarm.AlarmResponse
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.AlarmRepository
import com.app.taocalligraphy.repository.MeditationContentRepository
import com.app.taocalligraphy.utils.extensions.getTimeStampFromTime
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class MeditationContentViewModel @Inject constructor(
    private val repository: MeditationContentRepository,
    private val alarmRepo: AlarmRepository,
    private val app: Application
) :
    BaseViewModel() {

    var selectedTab = Constants.about

    var isConfigChange = false
    var isZenModeDontShow = false
    var currentPainRating = 0
    var postAssessmentPainLevel = 0
    var isSelectedPain = false
    var isPause = false
    var isMoveToFullScreen = false
    var isFromFullScreen = false
    var isPlaying = true
    val selectedExperience = ArrayList<MeditationContentResponse.FeedbackTag>()
    val playlistContentList = mutableListOf<PlaylistContentFilterApiResponse.ContentList>()

    var contentHandlerTimer = Handler(Looper.getMainLooper())
    val contentPlayTimeRunner = object : Runnable {
        override fun run() {
            totalContentTime += (interval * playerPlaybackSpeed).toLong()
            contentHandlerTimer.postDelayed(this, 1000)
        }
    }

    val meditationContentLiveData = MutableLiveData<RequestState<MeditationContentResponse>>()
    val preAssessmentFeedbackLiveData = MutableLiveData<RequestState<Any>>()
    val likeDisLikeMeditationContentLiveData = MutableLiveData<RequestState<Any>>()

    var meditationContent: MeditationContentResponse? = null

    var totalContentTime = 1000L
    var interval = 1000L
    var playerPlaybackSpeed = 1f

    fun getChapterList(): List<MeditationContentResponse.Chapter?> {
        meditationContent?.let {
            it.chapters?.let { chapters ->
                /* val sortedChapters =
                     chapters.sortedBy { chapter -> chapter?.chapterNumber?.toInt() }*/
                chapters.forEachIndexed { index, chapter ->
                    chapter?.duration = getTimeStampFromTime(chapter?.chapterTimeStamp)
                    val nextChapter = chapters.getOrNull(index + 1)
                    if (nextChapter != null) {
                        if (index == 0) chapter?.startTimeStamp = 0
                        else chapter?.startTimeStamp = chapter?.duration ?: 0
                        chapter?.endTimeStamp =
                            getTimeStampFromTime(nextChapter.chapterTimeStamp) - 500
                    } else {
                        chapter?.startTimeStamp = chapter?.duration ?: 0
                        chapter?.endTimeStamp = getTimeStampFromTime(it.contentDuration) - 500
                    }
                }
                return chapters
            }
        }
        return emptyList()
    }

    fun getIntroChapterTime(): Long {
        val introChapter =
            getChapterList().find {
                it?.chapterType?.lowercase() == app.getString(R.string.intro).lowercase()
            }
        introChapter?.let {
            return introChapter.endTimeStamp
        } ?: kotlin.run {
            return 0L
        }
    }

    fun getCategoryTitleFromList(categoryDetailsList: List<MeditationContentResponse.CategoryDetails>?): ArrayList<String> {
        val categoryListData = ArrayList<String>()
        categoryDetailsList?.forEach { categoryList ->
            categoryList.subCategoryList?.let {
                categoryListData.addAll(it)
            }
        }
        return categoryListData
    }

    fun getMeditationContent(
        contentId: String, baseView: BaseView, disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.contentId] = contentId
        params[Constants.Param.languageId] = 1
        repository.getMeditationContent(params, baseView, disposable, meditationContentLiveData)
    }

    fun preAssessmentFeedback(
        contentId: String,
        currentPainRating: Int,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.id] = contentId
        params[Constants.Param.ratingNumber] = currentPainRating
        repository.preAssessmentFeedback(
            params, baseView, disposable, preAssessmentFeedbackLiveData
        )
    }

    fun postAssessmentFeedback(
        contentId: String,
        currentPainRating: Int,
        tagsId: JsonArray,
        feedback: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val json = JsonObject()
        json.addProperty(Constants.Param.id, contentId)
        json.addProperty(Constants.Param.ratingNumber, currentPainRating)
        json.add("tagIds", tagsId)
        json.addProperty("feedbackTag", feedback)

        repository.postAssessmentFeedback(
            json, baseView, disposable, preAssessmentFeedbackLiveData
        )
    }

    fun contentPlayTime(
        contentId: String, playTime: String
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.contentId] = contentId
        params[Constants.Param.playTime] = playTime
        repository.contentPlayTime(
            params
        )
    }


    fun programContentPlayTime(
        contentId: String,
        playTime: String
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.programContentId] = contentId
        params[Constants.Param.playTime] = playTime
        repository.programContentPlayTime(
            params
        )
    }


    fun likeDisLikeMeditationContent(
        contentId: String, type: String, baseView: BaseView, disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.contentId] = contentId
        params[Constants.Param.type] = type
        repository.likeDisLikeMeditationContent(
            params, baseView, disposable, likeDisLikeMeditationContentLiveData
        )
    }

    private val alarmOnOffLiveData =
        MutableLiveData<RequestState<AlarmResponse>?>()

    fun alarmOnOff(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        alarmRepo.alarmOnOff(
            baseView,
            disposable,
            alarmOnOffLiveData
        )
    }
}