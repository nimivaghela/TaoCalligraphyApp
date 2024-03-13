package com.app.taocalligraphy.repository

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.AddAlarmRequest
import com.app.taocalligraphy.models.response.alarm.AlarmResponse
import com.app.taocalligraphy.other.Constants.ERROR
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import retrofit2.HttpException
import javax.inject.Inject

class AlarmRepository @Inject constructor(private val apiHelper: ApiHelper) : BaseRepository() {

    fun addAlarmAPI(
        param: AddAlarmRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<AlarmResponse>?>
    ) {
        apiHelper.addAlarm(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<AlarmResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<AlarmResponse>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: ERROR))

                }
            }).addTo(disposable)
    }

    fun alarmOnOff(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<AlarmResponse>?>
    ) {
        apiHelper.alarmOnOff().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<AlarmResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<AlarmResponse>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: ERROR))

                }
            }).addTo(disposable)
    }

}