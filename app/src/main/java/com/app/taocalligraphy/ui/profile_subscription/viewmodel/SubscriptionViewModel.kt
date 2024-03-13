package com.app.taocalligraphy.ui.profile_subscription.viewmodel

import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.response.SubscriptionDetails
import com.app.taocalligraphy.repository.MainRepository
import com.app.taocalligraphy.utils.extensions.getDateTimeFromTimeStamp
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(private val repository: MainRepository) : BaseViewModel() {

    var subscriptionType = ""
    var subscriptionMethod = ""
    var isRedirectToCancel = false
    var isApiLoading = false
    lateinit var subscriptionDetails : SubscriptionDetails

    val subscribeUserLiveData = MutableLiveData<RequestState<Any>>()
    val subscriptionDetailsLiveData = MutableLiveData<RequestState<SubscriptionDetails>>()
    val subscriptionStatusLiveData = MutableLiveData<RequestState<SubscriptionDetails>>()
    val startFreeTrialLiveData = MutableLiveData<RequestState<Unit>>()

    fun callSubscribeUserApi(
        purchase: Purchase,
        currencyType: String,
        productId : String,
        type : String,
        price : Double,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val date: String = getDateTimeFromTimeStamp(purchase.purchaseTime)

        val androidData = JsonObject()

        androidData.addProperty("productId",productId)
        androidData.addProperty("packageName",purchase.packageName)
        androidData.addProperty("purchaseToken",purchase.purchaseToken)
        androidData.addProperty("purchaseState",purchase.purchaseState)
        androidData.addProperty("orderId",purchase.orderId)

        val json = JsonObject()
        json.addProperty("subscriptionType", type)
        json.addProperty("price", price.toString())
        json.addProperty("currencyType", currencyType)
        json.addProperty("subscriptionMethod", "PLAY_STORE")
        json.addProperty("subscriptionPlan", "Subscribe")
//        json.addProperty("endDate", "")
        json.add("androidData",androidData)


        repository.subscribeUser(
            json, baseView, disposable, subscribeUserLiveData
        )
    }

    fun getSubscriptionDetails(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        repository.getSubscriptionDetails(
            baseView, disposable, subscriptionDetailsLiveData
        )
    }

    fun getSubscriptionStatus(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        repository.getSubscriptionStatus(
            baseView, disposable, subscriptionStatusLiveData
        )
    }

    fun startFreeTrial(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        repository.startFreeTrial(
            baseView, disposable, startFreeTrialLiveData
        )
    }

}