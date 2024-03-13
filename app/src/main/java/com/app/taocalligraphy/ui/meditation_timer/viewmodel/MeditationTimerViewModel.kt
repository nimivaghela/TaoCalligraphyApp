package com.app.taocalligraphy.ui.meditation_timer.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.MeditationTimerModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.MeditationTimerEditRequest
import com.app.taocalligraphy.models.request.MeditationTimerRequest
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.models.response.meditation_timer.MeditationListApiResponse
import com.app.taocalligraphy.models.response.meditation_timer.SoundApiResponse
import com.app.taocalligraphy.models.response.meditation_timer.SoundBackImageApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.MeditationRepository
import com.app.taocalligraphy.ui.meditation_timer.PlayMeditationTimerActivity
import com.app.taocalligraphy.utils.CountDownTimerExt
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class MeditationTimerViewModel @Inject constructor(private val mainRepository: MeditationRepository) :
    BaseViewModel() {

    var startSongDownloaded = false
    var midSongDownloaded = false
    var endSongDownloaded = false

    var mediaPosition = 0
    var totalSongDuration = 0
    var currentDuration = 0
    var firstSongDuration: Long = 0
    var secondSongDuration: Long = 0
    var thirdSongDuration: Long = 0
    var playNext = false
    var arrayMusic: ArrayList<String>? = null
    var midSongDuration: Long = 0
    var midSongRepeat: Int = 0
    var startSongDownloadRequestId: Long = 0
    var midSongDownloadRequestId: Long = 0
    var endSongDownloadRequestId: Long = 0
    var isZenModeDontShow = false
    var mLastClickTime: Long = 0
    var isPlayingMusic = false
    var isPlayingDuration = "00:00:00"
    var totalMeditationTime = ""
    var meditationRemainingTime = 0L
    var isConfigChange = false
    var isMeditationCompleted = false

    val onTimerFinished = MutableLiveData<Boolean>()
    val onTimerUpdated = MutableLiveData<Double>()

    val mediaPlayer = MediaPlayer()

    val meditationTimerList: ArrayList<MeditationListApiResponse.MeditationDetail?> = ArrayList()
    var hasMeditationData = true

    var meditationDetail: MeditationListApiResponse.MeditationDetail? = null
    var selectedStartSound = -1
    var selectedEndSound = -1
    var selectedAmbientSound = -1
    var soundFrom = ""
    var selectedSoundTimer = 0
    var selectedTimer = ""
    var selectedSoundId = -1
    var backgroundId = -1
    var mTimerArrayList =ArrayList<MeditationTimerModel>()
    var startSoundList = ArrayList<SoundApiResponse.SoundList?>()
    var endSoundList = ArrayList<SoundApiResponse.SoundList?>()
    var ambientSoundList = ArrayList<SoundApiResponse.SoundList?>()
    var remindDailyTime: String = ""
    var backgroundImageData: SoundBackImageApiResponse? = null
    var soundsList: ArrayList<SoundApiResponse.SoundList?> = ArrayList()

    val timerExt by lazy {
        object : CountDownTimerExt(meditationRemainingTime, 100) {
            override fun onTimerTick(millisUntilFinished: Long) {
                //Log.e("MyTag","Timer change :: ${mViewModel.currentDuration}")
                val totDuration = totalSongDuration.toLong()
                val currDuration = totDuration - millisUntilFinished
                meditationRemainingTime = totDuration - currDuration

                currentDuration = mediaPlayer.currentPosition

                val currentSeconds = (currDuration / 1000.0)
                val totalSeconds = (totDuration / 1000.0)

                // calculating percentage
                val percentage = currentSeconds / totalSeconds * 100.0
                onTimerUpdated.postValue(percentage)
            }

            override fun onTimerFinish() {
                onTimerFinished.postValue(true)
            }
        }
    }

    val soundLiveData = MutableLiveData<RequestState<SoundApiResponse>>()
    val soundLiveDataAmbient = MutableLiveData<RequestState<SoundApiResponse>>()
    val backgroundImageLiveData = MutableLiveData<RequestState<SoundBackImageApiResponse>>()
    val meditationTimerListLiveData = MutableLiveData<RequestState<MeditationListApiResponse>>()
    val meditationTimerLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val meditationTimerEditLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val meditationTimerDeleteLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val meditationTimerCloneLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val fetchMeditationTimerDetailByIDResponse =
        MutableLiveData<RequestState<MeditationListApiResponse.MeditationDetail>>()

    fun soundApi(
        soundType: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.soundType] = soundType
        mainRepository.soundApi(params, baseView, disposable, soundLiveData)
    }

    fun soundAmbientApi(
        soundType: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.soundType] = soundType
        mainRepository.soundApi(params, baseView, disposable, soundLiveDataAmbient)
    }

    fun backgroundImageListApi(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.backgroundImageListApi(baseView, disposable, backgroundImageLiveData)
    }

    fun meditationTimerListApi(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.meditationTimerListApi(baseView, disposable, meditationTimerListLiveData)
    }

    fun meditationTimerAddApi(
        meditationTime: Int,
        name: String,
        startSound: Int,
        endSound: Int,
        ambientSound: Int,
        remindTime: String,
        background: Int,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.meditationTimerAddApi(
            MeditationTimerRequest().apply {
                this.meditationTime = meditationTime
                this.name = name
                this.startSound = startSound
                this.endSound = endSound
                this.ambientSound = ambientSound
                this.remindTime = remindTime
                this.background = background
            }, baseView, disposable, meditationTimerLiveData
        )
    }

    fun meditationTimerEditApi(
        meditationId: Int,
        meditationTime: Int,
        name: String,
        startSound: Int,
        endSound: Int,
        ambientSound: Int,
        remindTime: String,
        background: Int,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.meditationTimerEditApi(
            MeditationTimerEditRequest().apply {
                this.meditationId = meditationId
                this.meditationTime = meditationTime
                this.name = name
                this.startSound = startSound
                this.endSound = endSound
                this.ambientSound = ambientSound
                this.reminderTime = remindTime
                this.background = background
            }, baseView, disposable, meditationTimerEditLiveData
        )
    }

    fun meditationTimerDeleteApi(
        meditationId: Int,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.id] = meditationId
        mainRepository.meditationTimerDeleteApi(
            params,
            baseView,
            disposable,
            meditationTimerDeleteLiveData
        )
    }

    fun meditationTimerCloneApi(
        meditationId: Int,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.meditationTimerId] = meditationId
        mainRepository.meditationTimerCloneApi(
            params,
            baseView,
            disposable,
            meditationTimerCloneLiveData
        )
    }

    fun fetchMeditationTimerDetailByIDApi(
        meditationId: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any?>()
        params[Constants.Param.meditationTimerId] = meditationId
        mainRepository.fetchMeditationTimerDetailByIDApi(
            params,
            baseView,
            disposable,
            fetchMeditationTimerDetailByIDResponse
        )
    }

    fun userMeditationTimerPlayDetailsApi(
        meditationTime: String,
        totalMeditationTime: String
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.meditationTime] = meditationTime
        params[Constants.Param.totalMeditationTime] = totalMeditationTime
        mainRepository.userMeditationTimerPlayDetailsApi(
            params
        )
    }

}