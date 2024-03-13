package com.app.taocalligraphy.repository


import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.response.FetchDailyStatDataReponse
import com.app.taocalligraphy.models.response.stats_response.FetchMonthStatDataReponse
import com.app.taocalligraphy.models.response.stats_response.FetchWeekStatDataReponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class StatsRepository @Inject constructor(private val apiHelper: ApiHelper) {
    fun fetchDailyStatsData(
        map: Map<String, String>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchDailyStatDataReponse>>
    ) {
        apiHelper.fetchDailyStatsData(map).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchDailyStatDataReponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchDailyStatDataReponse>) {
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

    fun fetchWeekStatsData(
        map: Map<String, String>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchWeekStatDataReponse>>
    ) {
        apiHelper.fetchWeekStatsData(map).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchWeekStatDataReponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchWeekStatDataReponse>) {
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

    fun fetchMonthStatsData(
        map: Map<String, String>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchMonthStatDataReponse>>
    ) {
        apiHelper.fetchMonthStatsData(map).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchMonthStatDataReponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchMonthStatDataReponse>) {
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