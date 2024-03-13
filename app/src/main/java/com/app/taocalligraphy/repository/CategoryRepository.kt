package com.app.taocalligraphy.repository

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.FetchCategoryDetailContentByFilterRequest
import com.app.taocalligraphy.models.request.FetchCategoryProgramByIDRequest
import com.app.taocalligraphy.models.request.FetchCategoryTagsByIdRequest
import com.app.taocalligraphy.models.request.FetchHomeDataRequest
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_by_id.FetchCategoryDetailByIdResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_content_by_filter.FetchCategoryDetailContentByFilterResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_content_by_filter.VideoDetail
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_program_list_by_id.FetchCategoryProgramByIDResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_tags_by_id.FetchCategoryTagsByIdResponse
import com.app.taocalligraphy.models.response.home_screen.FetchHomeContentDataResponse
import com.app.taocalligraphy.models.response.home_screen.ForYouDataModel
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import retrofit2.HttpException
import javax.inject.Inject

class CategoryRepository @Inject constructor(var apiHelper: ApiHelper) {
    fun fetchCategoryDetailByID(
        id: Int,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchCategoryDetailByIdResponse>>
    ) {
        apiHelper.fetchCategoryDetailByID(id).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchCategoryDetailByIdResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchCategoryDetailByIdResponse>) {
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

    fun fetchCategoryProgramListByID(
        map: HashMap<String, Any>,
        params: FetchCategoryProgramByIDRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchCategoryProgramByIDResponse>>
    ) {
        apiHelper.fetchCategoryProgramByID(map, params).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchCategoryProgramByIDResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchCategoryProgramByIDResponse>) {
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



    fun fetchCategoryDetailContentByFilter(
        params: FetchCategoryDetailContentByFilterRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchCategoryDetailContentByFilterResponse>>
    ) {
        apiHelper.fetchCategoryDetailContentByFilter(params)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchCategoryDetailContentByFilterResponse>>(
                    baseView
                ) {
                override fun onApiSuccess(response: CommonResponseModel<FetchCategoryDetailContentByFilterResponse>) {
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

    fun getAllCategoryData(
        contentType: String,
        subCategoryId: Int,
        json: JsonObject,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<ForYouDataModel>>
    ) {
        apiHelper.getAllCategoryData(contentType, subCategoryId, json).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<ForYouDataModel>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<ForYouDataModel>) {
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