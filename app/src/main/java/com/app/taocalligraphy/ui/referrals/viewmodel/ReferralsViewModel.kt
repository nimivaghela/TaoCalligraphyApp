package com.app.taocalligraphy.ui.referrals.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.*
import com.app.taocalligraphy.models.response.profile.*
import com.app.taocalligraphy.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class ReferralsViewModel @Inject constructor(private val mainRepository: ProfileRepository) :
    BaseViewModel() {

    var currPage = -1
    var shouldLoadMore: Boolean = true
    var isLoading: Boolean = true
    var referralLink: String = ""

    lateinit var referralUsersList: ReferralsResponse.ReferralUsersList

    val userReferralsLiveData = MutableLiveData<RequestState<ReferralsResponse>>()

    fun getReferralData(
        request :ReferralsRequest,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.userReferralsApi(
            request,
            baseView,
            disposable,
            userReferralsLiveData
        )
    }

}