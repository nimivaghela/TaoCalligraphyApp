package com.app.taocalligraphy.repository

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.FavoriteContentRequest
import com.app.taocalligraphy.models.request.FavoriteProgramRequest
import com.app.taocalligraphy.models.response.favorite_models.FavoriteContentResponse
import com.app.taocalligraphy.models.response.favorite_models.FavoriteProgramResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import retrofit2.HttpException
import javax.inject.Inject

class FavoriteRepository @Inject constructor(private val apiHelper: ApiHelper) {
    fun fetchFavoriteProgramDataAPI(
        param: FavoriteProgramRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FavoriteProgramResponse>>
    ) {
        apiHelper.fetchFavoriteProgramDataAPI(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FavoriteProgramResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FavoriteProgramResponse>) {
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

    fun fetchFavoriteContentDataAPI(
        param: FavoriteContentRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FavoriteContentResponse>>
    ) {
        apiHelper.fetchFavoriteContentDataAPI(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FavoriteContentResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FavoriteContentResponse>) {
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