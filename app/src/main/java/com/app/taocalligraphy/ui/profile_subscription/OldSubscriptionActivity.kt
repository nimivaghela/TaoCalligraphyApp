package com.app.taocalligraphy.ui.profile_subscription

import android.app.Activity
import android.content.Intent
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivitySubscriptionBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.profile_subscription.viewmodel.SubscriptionViewModel
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.GoogleBillingHelper
import com.app.taocalligraphy.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImageProfile
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class OldSubscriptionActivity : BaseActivity<ActivitySubscriptionBinding>() {

    private val mViewModel : SubscriptionViewModel by viewModels()

    private lateinit var productDetails : ProductDetails
    private var currentPlanType : String = Constants.Subscription.SUBSCRIBE_YEARLY
    private  var currentProduct : ProductDetails.SubscriptionOfferDetails? = null


    private val isFromNotification by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromNotification, false) ?: false
    }

    override fun getResource() = R.layout.activity_subscription

    companion object {

//        fun startActivityForResult(activity: AppCompatActivity,result : ActivityResultLauncher<Intent>) {
//            val intent = Intent(activity, SubscriptionActivity::class.java)
//            result.launch(intent)
//        }
    }

    override fun initView() {
        setupToolbar()
        mBinding.shimmerPrice.visible()
        mBinding.layoutPriceDetails.gone()
        GoogleBillingHelper.getInstance().createBillingConnection(this)
        updateProfile()
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()
    }

    private fun updateProfile() {
        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }
    override fun handleListener() {
        mBinding.apply {
            btnProceedToPayment.setOnClickListener {
                GoogleBillingHelper.getInstance().launchSubscribeFlow(currentPlanType,this@OldSubscriptionActivity)
            }

            switchSilenceDuringMeditation.setOnCheckedChangeListener { compoundButton, isYearly->
                if(!(::productDetails.isInitialized))
                    return@setOnCheckedChangeListener

                currentPlanType = if(isYearly) Constants.Subscription.SUBSCRIBE_YEARLY else Constants.Subscription.SUBSCRIBE_MONTHLY
                currentProduct = productDetails.subscriptionOfferDetails?.find { it.basePlanId == currentPlanType }
                setPriceUi()
            }

            mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
                onBackPressed()
            }
            mBinding.mToolbar.cardProfile.clickWithDebounce {
                UserMenuActivity.startActivity(this@OldSubscriptionActivity)
            }
//            btnProceedToPayment.setOnClickListener {
//                CancelSubscriptionActivity.startActivity(this@SubscriptionActivity)
//            }
        }
    }

    override fun observeApiCallbacks() {
        GoogleBillingHelper.getInstance().productDetailsLiveData.observe(this@OldSubscriptionActivity){
            productDetails = it
            currentProduct = it.subscriptionOfferDetails?.find { it.basePlanId == currentPlanType }
            setPriceUi()
            if(mBinding.shimmerPrice.isVisible){
                mBinding.shimmerPrice.gone()
                mBinding.layoutPriceDetails.visible()
            }
        }

//        GoogleBillingHelper.getInstance().setBillingListener(object : GoogleBillingHelper.OnBillingListener{
//            override fun onError() {
//                subscriptionErrorDialog()
//            }
//
//            override fun onSuccess(purchase: Purchase) {
//                val pricePhase = currentProduct?.pricingPhases?.pricingPhaseList?.first()
//                val price: Double = (pricePhase?.priceAmountMicros)?.toDouble()?.div(1000000) ?: 0.0
//
//                mViewModel.callSubscribeUserApi(
//                    purchase,currentProduct!!,productDetails.productId,
//                    if(currentPlanType == Constants.Subscription.SUBSCRIBE_YEARLY) "Annual" else "Monthly",
//                    price,
//                    this@OldSubscriptionActivity, mDisposable
//                )
//            }
//        })

        mViewModel.subscribeUserLiveData.observe(this){ response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { it ->
                    userHolder.isSubscribed = true
                    TaoCalligraphy.instance.isSubscriptionStatusApiCalled = false
                    longToast("Subscribed successfully.",SUCCESS)
                    var resultIntent = Intent()
                    resultIntent.putExtra("isSubscribed",true)
                    setResult(Activity.RESULT_OK,resultIntent)
                    finish()
                }
                requestState.error.let {
                    if((requestState.error?.errorCode ?: 200) >= 400){
                        longToast(it?.customMessage ?: "", ERROR)
                    }
                }
                longToastState(requestState.error)
                mViewModel.subscribeUserLiveData.value = null
            }
        }
    }

    fun setPriceUi(){
        val pricePhase = currentProduct?.pricingPhases?.pricingPhaseList?.first()
        val price: Double = (pricePhase?.priceAmountMicros)?.toDouble()?.div(1000000) ?: 0.0
        val ind = pricePhase?.formattedPrice?.indexOf(price.toString()[0]) ?: 1
        val currencySymbol = pricePhase?.formattedPrice?.substring(0,ind)

        var animation = AnimationUtils.loadAnimation(this, R.anim.text_change)

        if(currentPlanType == Constants.Subscription.SUBSCRIBE_YEARLY){
            mBinding.apply {
                tvCurrency.text = currencySymbol ?: ""
                tvAmount.text = String.format("%.2f",price.div(12))
                tvYear.text = currencySymbol + price.toString() + "/year"
                tvBilledMonthly.setTextColor(getColor(R.color.medium_grey))
                tvBilledYearly.setTextColor(getColor(R.color.gold))
            }
        }else{
            mBinding.apply {
                tvCurrency.text = currencySymbol ?: ""
                tvAmount.text = price.toString()
                tvYear.text = currencySymbol + String.format("%.2f",price * 12) + "/year"
                tvBilledMonthly.setTextColor(getColor(R.color.gold))
                tvBilledYearly.setTextColor(getColor(R.color.medium_grey))
            }
        }
        mBinding.tvAmount.startAnimation(animation)
        mBinding.tvYear.startAnimation(animation)
    }

    private fun subscriptionErrorDialog() {
        val title = getString(R.string.failed_to_purchase_product)
        val desctiption = getString(R.string.payment_cancelled)

        val builder = this?.let { AlertDialog.Builder(it, R.style.DialogTheme) }

        builder?.setTitle("" + title)?.setMessage("" + desctiption)?.setCancelable(true)
            ?.setPositiveButton("ok")
            { dialog, which ->
                dialog!!.dismiss()
            }
        val alert = builder?.create()
        alert?.show()

        }


    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
        if (model.isAvailable) {
            initView()
            noInternetConnectionDialog?.dismiss()
        } else {
            if (!isFinishing) {
                showNoInternetDialog()
            }
        }
    }

    override fun onBackPressed() {
        if (isFromNotification && isTaskRoot) {
            MainActivity.startActivity(this)
            finish()
        } else {
            super.onBackPressed()
        }
    }
}