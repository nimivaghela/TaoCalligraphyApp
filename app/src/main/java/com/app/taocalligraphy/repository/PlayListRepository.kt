package com.app.taocalligraphy.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.*
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.FetchCategoryDataResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_tags_by_id.FetchCategoryTagsByIdResponse
import com.app.taocalligraphy.models.response.playList.PlaylistApiResponse
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import retrofit2.HttpException
import javax.inject.Inject

class PlayListRepository @Inject constructor(private val apiHelper: ApiHelper) : ViewModel() {

    fun fetchCategoryData(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchCategoryDataResponse>>
    ) {
        apiHelper.fetchCategoryData().observeOn(AndroidSchedulers.mainThread())
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

    fun playlistContentFilterApi(
        param: PlaylistContentFilterRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<PlaylistContentFilterApiResponse>>
    ) {
        apiHelper.playlistContentFilterApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<PlaylistContentFilterApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<PlaylistContentFilterApiResponse>) {
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

    fun addPlaylistApi(
        param: AddPlaylistRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<PlaylistContentFilterApiResponse>>
    ) {
        apiHelper.addPlaylistApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<PlaylistContentFilterApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<PlaylistContentFilterApiResponse>) {
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

    fun editPlaylistApi(
        param: EditPlaylistRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.editPlaylistApi(param).observeOn(AndroidSchedulers.mainThread())
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

    fun playlistContentApi(
        param: Map<String, Any?>,
        body: PagingRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<PlaylistContentFilterApiResponse>>
    ) {
        apiHelper.playlistContentApi(param, body).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<PlaylistContentFilterApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<PlaylistContentFilterApiResponse>) {
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

    fun playListDeleteApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.playListDeleteApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<LoginResponse>>(baseView) {
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

    fun playListApi(
        param: PlaylistRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<PlaylistApiResponse>>
    ) {
        apiHelper.playListApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<PlaylistApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<PlaylistApiResponse>) {
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
        apiHelper.fetchCategoryTagsById(params).observeOn(AndroidSchedulers.mainThread())
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

    fun fetchCategoryTagsByIds(
        params: FetchCategoryTagsByIdsRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchCategoryTagsByIdResponse>>
    ) {
        apiHelper.fetchCategoryTagsByIds(params).observeOn(AndroidSchedulers.mainThread())
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

}