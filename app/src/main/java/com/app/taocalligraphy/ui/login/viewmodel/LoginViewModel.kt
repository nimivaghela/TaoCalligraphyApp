package com.app.taocalligraphy.ui.login.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.LoginRequest
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.MainRepository
import com.app.taocalligraphy.userHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val mainRepository: MainRepository) :
    BaseViewModel() {

    val userLoginLiveData = MutableLiveData<RequestState<LoginResponse>>()

    fun userLogin(
        email: String, password: String, loginType: String,
        socialId: String,
        deviceModel: String,
        deviceVersion: String,
        location: String,
        deviceId: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.userLogin(
            LoginRequest().apply {
                this.email = "" + email
                this.password = "" + password
                this.loginType = "" + loginType
                this.socialId = "" + socialId
                this.deviceType = "" + Constants.DEVICE_TYPE
                this.deviceToken = "" + userHolder.mAuthToken!!
                this.browserType = "" + Constants.BROWSER_TYPE
                this.deviceModel = "" + deviceModel
                this.deviceVersion = "" + deviceVersion
                this.location = "" + location
                this.osType = "" + Constants.OS_TYPE
                this.deviceId = "" + deviceId
            }, baseView, disposable, userLoginLiveData
        )
    }
}