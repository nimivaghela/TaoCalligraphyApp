package com.app.taocalligraphy.ui.alarm

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.AlarmContent
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.AddAlarmRequest
import com.app.taocalligraphy.models.response.alarm.AlarmResponse
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val repository: AlarmRepository) :
    BaseViewModel() {
    var contentDetailList = ArrayList<PlaylistContentFilterApiResponse.ContentList>()

    var repeatSunday = false
    var repeatMonday = false
    var repeatTuesday = false
    var repeatWednesday = false
    var repeatThursday = false
    var repeatFriday = false
    var repeatSaturday = false
    var isAlarmChanged = false
    var isCancelWithoutSaving = false

    var timePickerHours = 0
    var timePickerMinute = 0

    var alarmContent: AlarmContent? = null
    var isDataSelected: Boolean = false
    var isConfigChange = false
    var content: PlaylistContentFilterApiResponse.ContentList? = null

    val addAlarmLiveData =
        MutableLiveData<RequestState<AlarmResponse>?>()

    val alarmOnOffLiveData =
        MutableLiveData<RequestState<AlarmResponse>?>()

    fun setAlarm(
        body: AddAlarmRequest, baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        repository.addAlarmAPI(
            body,
            baseView,
            disposable,
            addAlarmLiveData
        )
    }

    fun alarmOnOff(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        repository.alarmOnOff(
            baseView,
            disposable,
            alarmOnOffLiveData
        )
    }
}