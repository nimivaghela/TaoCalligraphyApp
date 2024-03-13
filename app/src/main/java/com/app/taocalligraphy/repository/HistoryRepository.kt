package com.app.taocalligraphy.repository


import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.MeditationHistoryDataRequest
import com.app.taocalligraphy.models.request.ProgramHistoryDataRequest
import com.app.taocalligraphy.models.response.history.FetchMeditationHistoryData
import com.app.taocalligraphy.models.response.history.FetchProgramHistoryData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class HistoryRepository @Inject constructor(private val apiHelper: ApiHelper) {

    fun fetchProgramHistoryAPI(
        param: Map<String, Any?>,
        historyDataRequest: ProgramHistoryDataRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchProgramHistoryData>>
    ) {
        apiHelper.fetchProgramHistoryAPI(param, historyDataRequest)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchProgramHistoryData>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchProgramHistoryData>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                        )
                }
            }).addTo(disposable)
    }

    fun fetchMeditationHistoryAPI(
        param: MeditationHistoryDataRequest,
        map: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchMeditationHistoryData>>
    ) {
        apiHelper.fetchMeditationHistoryAPI(map, param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchMeditationHistoryData>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchMeditationHistoryData>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                        )
                }
            }).addTo(disposable)
    }

}