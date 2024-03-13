package com.app.taocalligraphy.repository

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.MeditationTimerEditRequest
import com.app.taocalligraphy.models.request.MeditationTimerRequest
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.models.response.meditation_timer.MeditationListApiResponse
import com.app.taocalligraphy.models.response.meditation_timer.SoundApiResponse
import com.app.taocalligraphy.models.response.meditation_timer.SoundBackImageApiResponse
import com.app.taocalligraphy.models.response.program.UserProgramContentDetailApiRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MeditationRepository @Inject constructor(private val apiHelper: ApiHelper) {

    fun soundApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<SoundApiResponse>>
    ) {
        apiHelper.soundApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<SoundApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<SoundApiResponse>) {
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

    fun backgroundImageListApi(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<SoundBackImageApiResponse>>
    ) {
        apiHelper.backgroundImageListApi().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<SoundBackImageApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<SoundBackImageApiResponse>) {
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

    fun meditationTimerListApi(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<MeditationListApiResponse>>
    ) {
        apiHelper.meditationTimerListApi().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<MeditationListApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<MeditationListApiResponse>) {
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

    fun meditationTimerAddApi(
        param: MeditationTimerRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.meditationTimerAddApi(param).observeOn(AndroidSchedulers.mainThread())
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

    fun meditationTimerEditApi(
        param: MeditationTimerEditRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.meditationTimerEditApi(param).observeOn(AndroidSchedulers.mainThread())
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

    fun meditationTimerDeleteApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.meditationTimerDeleteApi(param).observeOn(AndroidSchedulers.mainThread())
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

    fun meditationTimerCloneApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.meditationTimerCloneApi(param).observeOn(AndroidSchedulers.mainThread())
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

    fun fetchMeditationTimerDetailByIDApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<MeditationListApiResponse.MeditationDetail>>
    ) {
        apiHelper.fetchMeditationTimerDetailByIDApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<MeditationListApiResponse.MeditationDetail>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<MeditationListApiResponse.MeditationDetail>) {
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

    fun userMeditationTimerPlayDetailsApi(
        param: Map<String, Any?>
    ) {
        val call = apiHelper.userMeditationTimerPlayDetailsApi(param)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }
        })
    }

}