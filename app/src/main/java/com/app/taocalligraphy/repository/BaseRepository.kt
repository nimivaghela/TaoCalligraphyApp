package com.app.taocalligraphy.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.FetchCategoryTagsByIdRequest
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.models.response.alarm.AlarmResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.FetchCategoryDataResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_tags_by_id.FetchCategoryTagsByIdResponse
import com.app.taocalligraphy.models.response.home_screen.FetchDailyWisdomDataResponse
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.models.response.user_downloads.UserDownloads
import com.app.taocalligraphy.other.Constants
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import retrofit2.HttpException
import javax.inject.Inject

open class BaseRepository @Inject constructor() {

    @Inject
    lateinit var apiHelperBase: ApiHelper

    fun fetchCategoryData(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchCategoryDataResponse>>
    ) {
        apiHelperBase.fetchCategoryData().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                callback.value = RequestState(progress = true)
            }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchCategoryDataResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchCategoryDataResponse>) {
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

    fun favoriteMeditationContent(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<Any>>
    ) {
        apiHelperBase.favouriteMeditationContent(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<Any>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<Any>) {
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

    fun setProgramFavourite(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<Any>>
    ) {
        apiHelperBase.setProgramFavourite(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<Any>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<Any>) {
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

    fun userLogout(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelperBase.userLogout(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<LoginResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<LoginResponse>) {
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

    fun fetchCategoryTagsById(
        params: FetchCategoryTagsByIdRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchCategoryTagsByIdResponse>>
    ) {
        apiHelperBase.fetchCategoryTagsById(params).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchCategoryTagsByIdResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchCategoryTagsByIdResponse>) {
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

    fun getActiveAlarm(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<AlarmResponse>>
    ) {
        apiHelperBase.getActiveAlarm().observeOn(AndroidSchedulers.mainThread())
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
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: ""))
                }
            }).addTo(disposable)
    }

    fun fetchDailyWisdomDataAPI(
        map: Map<String, String>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchDailyWisdomDataResponse>>
    ) {
        apiHelperBase.fetchDailyWisdomDataAPI(map).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchDailyWisdomDataResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchDailyWisdomDataResponse>) {
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