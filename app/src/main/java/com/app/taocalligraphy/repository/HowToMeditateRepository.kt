package com.app.taocalligraphy.repository

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.FetchHowToMeditateRequest
import com.app.taocalligraphy.models.response.how_to_meditate_response.FetchHowToMeditateDataListResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import retrofit2.HttpException
import javax.inject.Inject

class HowToMeditateRepository @Inject constructor(var apiHelper: ApiHelper) {

    fun fetchHowToMeditateDataListAPI(
        params: FetchHowToMeditateRequest,
        map: HashMap<String, Any>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchHowToMeditateDataListResponse>>
    ) {
        apiHelper.fetchHowToMeditateDataList(params, map).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                callback.value = RequestState(progress = true)
            }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchHowToMeditateDataListResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchHowToMeditateDataListResponse>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }
                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: ""))
                }
            }).addTo(disposable)
    }
}