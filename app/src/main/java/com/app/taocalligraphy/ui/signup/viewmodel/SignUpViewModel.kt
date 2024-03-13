package com.app.taocalligraphy.ui.signup.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.StaticContentResponseModel
import com.app.taocalligraphy.models.request.SignUpRequest
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.MainRepository
import com.app.taocalligraphy.userHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val mainRepository: MainRepository) :
    BaseViewModel() {

    val userSignUpnLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val staticContentResponseModel = MutableLiveData<RequestState<StaticContentResponseModel>>()

    fun userSignUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        signupType: String,
        socialId: String,
        isEmailIdEnteredManually: Boolean,
        deviceModel: String,
        deviceVersion: String,
        location: String,
        deviceId: String,
        referralCode: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.userSignUp(SignUpRequest().apply {
            this.firstName = "" + firstName
            this.lastName = "" + lastName
            this.email = "" + email
            this.password = "" + password
            this.signupType = "" + signupType
            this.socialId = "" + socialId
            this.deviceType = "" + Constants.DEVICE_TYPE
            this.deviceToken = "" + userHolder.mAuthToken!!
            this.isEmailIdEnteredManually = isEmailIdEnteredManually
            this.browserType = "" + Constants.BROWSER_TYPE
            this.deviceModel = "" + deviceModel
            this.deviceVersion = "" + deviceVersion
            this.location = "" + location
            this.osType = "" + Constants.OS_TYPE
            this.deviceId = "" + deviceId
            this.referralCode = referralCode
        }, baseView, disposable, userSignUpnLiveData)
    }

    fun getStaticData(
        type: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.staticContentApi(type, baseView = baseView, disposable, staticContentResponseModel)
    }


}