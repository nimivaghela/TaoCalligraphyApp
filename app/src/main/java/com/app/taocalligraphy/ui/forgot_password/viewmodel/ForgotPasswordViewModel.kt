package com.app.taocalligraphy.ui.forgot_password.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    var isSuccessMessageVisible = false

    val forgetPasswordLiveData = MutableLiveData<RequestState<LoginResponse>>()

    fun forgetPasswordApiRequest(
        email: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.emailId] = email
        mainRepository.forgotPasswordApi(params, baseView, disposable, forgetPasswordLiveData)
    }

}