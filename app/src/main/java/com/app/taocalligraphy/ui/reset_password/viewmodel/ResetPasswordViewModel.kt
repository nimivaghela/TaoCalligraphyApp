package com.app.taocalligraphy.ui.reset_password.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
class ResetPasswordViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    val resetPasswordLiveData = MutableLiveData<RequestState<LoginResponse>>()

    fun resetPasswordApiRequest(
        password: String,
        token: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.password] = password
        params[Constants.Param.verificationToken] = token
        mainRepository.resetPasswordApi(params, baseView, disposable, resetPasswordLiveData)
    }

}