package com.app.taocalligraphy.repository

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.eventbus.PlaylistContentChangeListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.ui.meditation.FullScreenVideoPlayerActivity
import com.app.taocalligraphy.ui.meditation.FullScreenVideoPlayerTabletActivity
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class MeditationContentRepository @Inject constructor(private val apiHelper: ApiHelper) {


    fun getMeditationContent(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<MeditationContentResponse>>
    ) {
        apiHelper.getMeditationContent(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value =
                RequestState(progress = !FullScreenVideoPlayerActivity.isScreenVisible || !FullScreenVideoPlayerTabletActivity.isScreenVisible)  }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<MeditationContentResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<MeditationContentResponse>) {
                    if (FullScreenVideoPlayerActivity.isScreenVisible || FullScreenVideoPlayerTabletActivity.isScreenVisible) {
                        EventBus.getDefault().post(PlaylistContentChangeListener(response.data))
                    } else {
                        if (TaoCalligraphy.instance.isAppInBackground) {
                            EventBus.getDefault().post(PlaylistContentChangeListener(response.data))
                        } else {
                            callback.value = RequestState(progress = false, apiResponse = response)
                        }
                    }
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

    fun preAssessmentFeedback(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<Any>>
    ) {
        apiHelper.preAssessmentFeedback(param).observeOn(AndroidSchedulers.mainThread())
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
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                        )
                }
            }).addTo(disposable)
    }

    fun postAssessmentFeedback(
        json: JsonObject,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<Any>>
    ) {
        apiHelper.postAssessmentFeedback(json).observeOn(AndroidSchedulers.mainThread())
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
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                        )
                }
            }).addTo(disposable)
    }

    fun contentPlayTime(
        param: Map<String, Any?>
    ) {
        val call = apiHelper.contentPlayTime(param)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }
        })
    }

    fun programContentPlayTime(
        param: Map<String, Any?>
    ) {
        val call = apiHelper.programContentPlayTime(param)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }
        })
    }


    fun likeDisLikeMeditationContent(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<Any>>
    ) {
        apiHelper.likeDisLikeMeditationContent(param).observeOn(AndroidSchedulers.mainThread())
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
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                        )
                }
            }).addTo(disposable)
    }

}