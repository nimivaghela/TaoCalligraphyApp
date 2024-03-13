package com.app.taocalligraphy.repository

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.SearchByAllDataRequest
import com.app.taocalligraphy.models.response.search_responses.fetch_searched_keyword_data_model.FetchSearchedKeywordResponse
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.MeditationContentListDataModel
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.ProgramListDataModel
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.SearchDataByAllTypeResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class SearchRepository @Inject constructor(private var apiHelper: ApiHelper) {
    fun fetchSearchedKeywordAPI(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchSearchedKeywordResponse>>
    ) {
        apiHelper.fetchSearchedKeywordApi().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchSearchedKeywordResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchSearchedKeywordResponse>) {
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

    fun searchAllTypeDataAPI(
        param: SearchByAllDataRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<SearchDataByAllTypeResponse>>
    ) {
        apiHelper.searchAllTypeDataApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<SearchDataByAllTypeResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<SearchDataByAllTypeResponse>) {
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

    fun contentSortingAPI(
        param: SearchByAllDataRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<MeditationContentListDataModel>>
    ) {
        apiHelper.contentSortingAPI(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<MeditationContentListDataModel>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<MeditationContentListDataModel>) {
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

    fun programSortingAPI(
        param: SearchByAllDataRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<ProgramListDataModel>>
    ) {
        apiHelper.programSortingAPI(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<ProgramListDataModel>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<ProgramListDataModel>) {
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