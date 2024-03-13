package com.app.taocalligraphy.repository


import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.*
import com.app.taocalligraphy.models.request.FetchHomeDataRequest
import com.app.taocalligraphy.models.request.LoginRequest
import com.app.taocalligraphy.models.request.SignUpRequest
import com.app.taocalligraphy.models.request.VerifyEmailTokenRequest
import com.app.taocalligraphy.models.response.InitialUserExperienceDetail
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.models.response.SubscriptionDetails
import com.app.taocalligraphy.models.response.home_screen.FetchHomeContentDataResponse
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiHelper: ApiHelper) {

    suspend fun getTermsAndConditions() = apiHelper.getTermsAndConditions()

    fun staticContentApi(
        type: String,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<StaticContentResponseModel>>
    ) {
        apiHelper.getStaticContent(type).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<StaticContentResponseModel>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<StaticContentResponseModel>) {
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

    fun forgotPasswordApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.forgotPasswordApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<LoginResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<LoginResponse>) {
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

    fun resetPasswordApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.resetPasswordApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<LoginResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<LoginResponse>) {
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

    fun fetchHomeContentDataAPI(
        param: FetchHomeDataRequest,
        map: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchHomeContentDataResponse>>
    ) {
        apiHelper.fetchHomeContentDataAPI(map, param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchHomeContentDataResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchHomeContentDataResponse>) {
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

    fun fetchViewAllContentDataAPI(
        param: FetchHomeDataRequest,
        map: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchHomeContentDataResponse>>
    ) {
        apiHelper.fetchViewAllContentDataAPI(map, param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchHomeContentDataResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchHomeContentDataResponse>) {
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


    fun resendOtpApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.resendOtpApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<LoginResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<LoginResponse>) {
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

    fun verifyEmailTokenApi(
        param: VerifyEmailTokenRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.verifyEmailTokenApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<LoginResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<LoginResponse>) {
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

    fun userLogin(
        param: LoginRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.userLogin(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<LoginResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<LoginResponse>) {
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

    fun userSignUp(
        param: SignUpRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.userSignUp(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<LoginResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<LoginResponse>) {
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

    fun getInitialUserExperience(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<MeditationContentResponse>>
    ) {
        apiHelper.initialUserExperience().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<MeditationContentResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<MeditationContentResponse>) {
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

    fun getInitialUserExperienceDetail(
        categoryId:Int,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<InitialUserExperienceDetail>>
    ) {
        apiHelper.initialUserExperienceDetail(categoryId).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<InitialUserExperienceDetail>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<InitialUserExperienceDetail>) {
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

    fun subscribeUser(
        params:JsonObject,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<Any>>
    ) {
        apiHelper.subscribeUser(params).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.postValue(RequestState(progress = true)) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<Any>>(baseView) {

                override fun onApiSuccess(response: CommonResponseModel<Any>) {
                    callback.postValue(  RequestState(progress = false, apiResponse = response))
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.postValue(RequestState(
                        progress = false,
                        error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                    ))

                }
            }).addTo(disposable)
    }

    fun getSubscriptionDetails(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<SubscriptionDetails>>
    ) {
        apiHelper.getSubscriptionDetails().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<SubscriptionDetails>>(baseView) {

                override fun onApiSuccess(response: CommonResponseModel<SubscriptionDetails>) {
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

    fun getSubscriptionStatus(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<SubscriptionDetails>>
    ) {
        apiHelper.getSubscriptionStatus().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<SubscriptionDetails>>(baseView) {

                override fun onApiSuccess(response: CommonResponseModel<SubscriptionDetails>) {
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

    fun startFreeTrial(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<Unit>>
    ) {
        apiHelper.startFreeTrial().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<Unit>>(baseView) {

                override fun onApiSuccess(response: CommonResponseModel<Unit>) {
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