package com.app.taocalligraphy.repository

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.*
import com.app.taocalligraphy.models.request.*
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.models.response.profile.*
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import okhttp3.MultipartBody
import retrofit2.HttpException
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apiHelper: ApiHelper) {

    fun userProfile(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.userProfile().observeOn(AndroidSchedulers.mainThread())
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

    fun callRemoveProfilePictureAPI(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<JsonObject>>
    ) {
        apiHelper.callRemoveProfilePictureAPI().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<JsonObject>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<JsonObject>) {
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

    fun getLanguages(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LanguageListApiResponse>>
    ) {
        apiHelper.getLanguages().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<LanguageListApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<LanguageListApiResponse>) {
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

    fun getSettingsLanguages(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LanguageListApiResponse>>
    ) {
        apiHelper.getSettingsLanguages().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<LanguageListApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<LanguageListApiResponse>) {
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

    fun regionsApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<RegionListApiResponse>>
    ) {
        apiHelper.regionsApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<RegionListApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<RegionListApiResponse>) {
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

    fun countryApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<RegionListApiResponse>>
    ) {
        apiHelper.countryApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<RegionListApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<RegionListApiResponse>) {
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

    fun userEditProfile(
        param: UserProfileRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.userEditProfile(param).observeOn(AndroidSchedulers.mainThread())
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

    fun userProfilePublic(
        param: UserProfilePublicRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.userProfilePublic(param).observeOn(AndroidSchedulers.mainThread())
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

    fun userSettingsApi(
        param: UserSettingsRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.userSettingsApi(param).observeOn(AndroidSchedulers.mainThread())
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

    fun userFeedbackApi(
        param: UserFeedbackRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.userFeedbackApi(param).observeOn(AndroidSchedulers.mainThread())
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

    fun userGoalsApi(
        param: UserGoalsRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.userGoalsApi(param).observeOn(AndroidSchedulers.mainThread())
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

    fun userProfilePictureEdit(
        param: MultipartBody.Part,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<UserProfileEditApiResponse>>
    ) {
        apiHelper.userProfilePictureEdit(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<UserProfileEditApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<UserProfileEditApiResponse>) {
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

    fun changePasswordApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.changePasswordApi(param).observeOn(AndroidSchedulers.mainThread())
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

    fun getUserProfilePublicApi(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<UserProfilePublicApiResponse>>
    ) {
        apiHelper.getUserProfilePublicApi(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<UserProfilePublicApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<UserProfilePublicApiResponse>) {
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

    fun getUserInterestsApi(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<UserInterestApiResponse>>
    ) {
        apiHelper.getUserInterestsApi().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<UserInterestApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<UserInterestApiResponse>) {
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

    fun getUserSettingsApi(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<UserSettingsApiResponse>>
    ) {
        apiHelper.getUserSettingsApi().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<UserSettingsApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<UserSettingsApiResponse>) {
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

    fun getUserModulePermissionApi(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<List<UserModulePermission>>>
    ) {
        apiHelper.getUserModulePermissionApi().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<List<UserModulePermission>>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<List<UserModulePermission>>) {
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

    fun userInterestApi(
        param: UserInterestRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.userInterestApi(param).observeOn(AndroidSchedulers.mainThread())
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

    fun getUserGoalsScreen2Api(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<UserGoalsScreenApiResponse>>
    ) {
        apiHelper.getUserGoalsScreen2Api().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<UserGoalsScreenApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<UserGoalsScreenApiResponse>) {
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

    fun getLeftMenuDataApi(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LeftMenuResponse>>
    ) {
        apiHelper.getLeftMenuDataApi().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<LeftMenuResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<LeftMenuResponse>) {
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

    fun getUserGoalsScreen1Api(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<UserGoalsScreenApiResponse>>
    ) {
        apiHelper.getUserGoalsScreen1Api().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<UserGoalsScreenApiResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<UserGoalsScreenApiResponse>) {
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

    fun deleteAccountApi(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<LoginResponse>>
    ) {
        apiHelper.deleteAccountApi().observeOn(AndroidSchedulers.mainThread())
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

    fun userReferralsApi(
        param: ReferralsRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<ReferralsResponse>>
    ) {
        apiHelper.userReferrals(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object : CallbackWrapper<CommonResponseModel<ReferralsResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<ReferralsResponse>) {
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