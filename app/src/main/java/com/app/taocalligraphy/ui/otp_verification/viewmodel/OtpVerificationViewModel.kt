package com.app.taocalligraphy.ui.otp_verification.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.VerifyEmailTokenRequest
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.MainRepository
import com.app.taocalligraphy.userHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    val verifyEmailTokenLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val resendOtpLiveData = MutableLiveData<RequestState<LoginResponse>>()

    fun verifyEmailTokenRequest(
        otp: String,
        email: String,
        password: String,
        deviceModel: String,
        deviceVersion: String,
        location: String,
        deviceId: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.verifyEmailTokenApi(
            VerifyEmailTokenRequest().apply {
                this.otp = "" + otp
                this.email = "" + email
                this.password = "" + password
                this.deviceType = "" + Constants.DEVICE_TYPE
                this.deviceToken = "" + userHolder.mAuthToken!!
                this.browserType = "" + Constants.BROWSER_TYPE
                this.deviceModel = "" + deviceModel
                this.deviceVersion = "" + deviceVersion
                this.location = "" + location
                this.osType = "" + Constants.OS_TYPE
                this.deviceId = "" + deviceId

            }, baseView, disposable, verifyEmailTokenLiveData
        )
    }

    fun resendOtpApiRequest(
        email: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.emailId] = email
        mainRepository.resendOtpApi(params, baseView, disposable, resendOtpLiveData)
    }

}