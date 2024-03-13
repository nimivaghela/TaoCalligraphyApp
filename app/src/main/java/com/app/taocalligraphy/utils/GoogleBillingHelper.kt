package com.app.taocalligraphy.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.app.taocalligraphy.other.Constants
import com.google.firebase.crashlytics.internal.model.ImmutableList


class GoogleBillingHelper {

    companion object{
        private lateinit var instance : GoogleBillingHelper

        @JvmName("getInstance1")
        fun getInstance():GoogleBillingHelper{
            if(!this::instance.isInitialized)
                instance =  GoogleBillingHelper()

            return instance
        }
    }

    private lateinit var billingClient: BillingClient
    private lateinit var  productDetails: ProductDetails
    private var isConnectionEstablished = false

    private val _productDetailsLiveData = MutableLiveData<ProductDetails>()
    val productDetailsLiveData : LiveData<ProductDetails> = _productDetailsLiveData

//    private val _onPurchaseLiveData = MutableLiveData<Purchase>()
//    val onPurchaseLiveData : LiveData<Purchase> = _onPurchaseLiveData

    private var billingListener : OnBillingListener? = null

    fun setBillingListener(listener: OnBillingListener) {
        billingListener = null
        this.billingListener = listener
    }

    fun createBillingConnection(activityContext: Context){
        if(isConnectionEstablished) return

        // Set up the billing client
        billingClient = BillingClient
            .newBuilder(activityContext)
            .enablePendingPurchases()
            .setListener(purchasesUpdatedListener)
            .build()

        startConnection()

    }

    fun startConnection(){
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    isConnectionEstablished = true
                    getProductsAvailableToBuy()
                }
            }

            override fun onBillingServiceDisconnected() {
            }

        })
    }

    fun getProductsAvailableToBuy(){
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    ImmutableList.from(
                        QueryProductDetailsParams.Product.newBuilder().setProductId(Constants.Subscription.SUBSCRIBE).setProductType(BillingClient.ProductType.SUBS).build()
                    )
                ).build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult, productDetailsList ->
            if(productDetailsList.isNotEmpty()) {
                productDetails = productDetailsList.first()
                Log.d("Subscripition",productDetails.toString())
                _productDetailsLiveData.postValue(productDetailsList.first())
            }
        }
    }

    fun launchSubscribeFlow(type : String ,context: Context){
        // restarting the connection is not established
        if (!billingClient!!.isReady) {
            startConnection()
        }

        if(!this::productDetails.isInitialized)
            return

        var selectedOfferToken : String = productDetails.subscriptionOfferDetails?.find { it.basePlanId == type && it.offerId == Constants.Subscription.FREE_TRIAL}?.offerToken ?: ""

        if(selectedOfferToken.isNullOrEmpty())
        selectedOfferToken = productDetails.subscriptionOfferDetails?.find { it.basePlanId == type}?.offerToken ?: ""

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                .setProductDetails(productDetails)
                // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                // for a list of offers that are available to the user
                .setOfferToken(selectedOfferToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        // Launch the billing flow
        val billingResult = billingClient.launchBillingFlow(context as Activity, billingFlowParams)
    }

    fun getPurchasedItems(){
        var queryPurchaseParams : QueryPurchasesParams = QueryPurchasesParams.newBuilder().setProductType(BillingClient.SkuType.SUBS).build()
        billingClient.queryPurchasesAsync(queryPurchaseParams) { p0, p1 ->
            Log.d("Subscription",p0.toString())
            if(p1.size > 0){
                for(item in p1){
                    Log.d("Subscription",item.toString())
                    billingListener?.onSuccess(item)
                    break;
                }
            }
            else{
                billingListener?.onRestoreSubscriptionFail()
            }
        }

//        var queryPurchaseHistoryParams : QueryPurchaseHistoryParams = QueryPurchaseHistoryParams.newBuilder().setProductType(BillingClient.SkuType.SUBS).build()
//        billingClient.queryPurchaseHistoryAsync(queryPurchaseHistoryParams) { p0, p1 ->
//            Log.d("Subscription",p0.toString())
//            if(!p1.isNullOrEmpty() && p1?.size > 0){
//                for(item in p1){
//                    Log.d("Subscription",item.toString())
////                    billingListener?.onSuccess(item)
//                    break;
//                }
//            }
//            else{
//                billingListener?.onRestoreSubscriptionFail()
//            }
//        }

    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                billingListener?.onError()
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                billingListener?.onError()
            } else {
                // Handle any other error codes.
                billingListener?.onError()
            }
        }

    fun handlePurchase(purchase: Purchase) {

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.

        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()){
                    billingListener?.onSuccess(purchase)
                }

        }

    }

    interface OnBillingListener {
        fun onError()
        fun onRestoreSubscriptionFail()
        fun onSuccess(purchase: Purchase)
    }

}