package com.app.taocalligraphy.ui.profile_subscription

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityNewSubscriptionBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.post_signup_questionnaire.WelcomeQuestionnaireActivity
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.profile_subscription.viewmodel.SubscriptionViewModel
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.GoogleBillingHelper
import com.app.taocalligraphy.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_downloads.*
import kotlinx.android.synthetic.main.activity_new_subscription.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.joda.time.DateTime

@AndroidEntryPoint
class SubscriptionActivity : BaseActivity<ActivityNewSubscriptionBinding>(){

    companion object {

        fun startActivityForResult(activity: AppCompatActivity,isFromSignup : Boolean = false) {
            val intent = Intent(activity, SubscriptionActivity::class.java)
            intent.putExtra(Constants.Param.isFromSignup,isFromSignup)
            activity.startActivityWithAnimation(intent)
        }
    }

    private var isDailogOpen: Boolean = false
    private val mViewModel : SubscriptionViewModel by viewModels()
    private val mProfileViewModel: ProfileViewModel by viewModels()
    override fun getResource() = R.layout.activity_new_subscription


    private lateinit var productDetails : ProductDetails
    private  var currentProduct : ProductDetails.SubscriptionOfferDetails? = null
    private var currentPlanType : String = Constants.Subscription.SUBSCRIBE_YEARLY

    private val isFromNotification by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromNotification, false) ?: false
    }

    private val isFromSignup by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromSignup, false) ?: false
    }

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(0, 0, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        if(!this::productDetails.isInitialized){
            mBinding.shimmerPrice.visible()
            mBinding.cardAnnually.gone()
            mBinding.cardMonthly.gone()
            mBinding.llRestorePurchase.gone()
            GoogleBillingHelper.getInstance().createBillingConnection(this)
        }
        setUi()
    }

    fun setUi(){
        if(userHolder.isFreeTrialCompleted) {
            tvFreeTrial.gone()
        }
    }

    override fun handleListener() {
        mBinding.apply {
            btnClose.setOnClickListener {
                onBackPressed()
            }

            btnMonthly.clickWithDebounce {
                currentProduct = productDetails.subscriptionOfferDetails?.find { it.basePlanId == Constants.Subscription.SUBSCRIBE_MONTHLY }
                currentPlanType = Constants.Subscription.SUBSCRIBE_MONTHLY
                GoogleBillingHelper.getInstance().launchSubscribeFlow(currentPlanType,this@SubscriptionActivity)
            }

            btnAnnual.clickWithDebounce {
                currentProduct = productDetails.subscriptionOfferDetails?.find { it.basePlanId == Constants.Subscription.SUBSCRIBE_YEARLY }
                currentPlanType = Constants.Subscription.SUBSCRIBE_YEARLY
                GoogleBillingHelper.getInstance().launchSubscribeFlow(currentPlanType,this@SubscriptionActivity)
            }

            txtRestorePurchase.clickWithDebounce {
                GoogleBillingHelper.getInstance().getPurchasedItems()
            }

        }
    }

    private fun openHomeScreen() {
        MainActivity.startActivity(this)
        finishAffinity()
    }

    override fun observeApiCallbacks() {
        GoogleBillingHelper.getInstance().productDetailsLiveData.observe(this@SubscriptionActivity){
            productDetails = it
            setPriceUi()
            if(mBinding.shimmerPrice.isVisible){
                mBinding.shimmerPrice.gone()
                mBinding.cardAnnually.visible()
                mBinding.cardMonthly.visible()
                mBinding.llRestorePurchase.visible()
            }
        }

        GoogleBillingHelper.getInstance().setBillingListener(object : GoogleBillingHelper.OnBillingListener{
            override fun onError() {
                subscriptionErrorDialog()
            }

            override fun onRestoreSubscriptionFail() {
                longToast(getString(R.string.no_subscription_found), ERROR)
            }

            override fun onSuccess(purchase: Purchase) {
                currentProduct = currentProduct ?: productDetails.subscriptionOfferDetails?.first()
                val pricePhase = currentProduct?.pricingPhases?.pricingPhaseList?.last()
                val price: Double = (pricePhase?.priceAmountMicros)?.toDouble()?.div(1000000) ?: 0.0

                    mViewModel.callSubscribeUserApi(
                        purchase,
                        currentProduct?.pricingPhases?.pricingPhaseList?.first()?.priceCurrencyCode ?: "USD",
                        productDetails.productId,
                        if (currentPlanType == Constants.Subscription.SUBSCRIBE_YEARLY) "Annual" else "Monthly",
                        price,
                        this@SubscriptionActivity, mDisposable
                    )
            }
        })

        mViewModel.subscribeUserLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { it ->
                    userHolder.isSubscribed = true
                    TaoCalligraphy.instance.isSubscriptionStatusApiCalled = false
                    longToast(getString(R.string.subscribed_successfully), SUCCESS)
//                    var resultIntent = Intent()
//                    resultIntent.putExtra("isSubscribed",true)
//                    setResult(Activity.RESULT_OK,resultIntent)
                    mProfileViewModel.getUserModulePermissionApi(
                        this@SubscriptionActivity,
                        mDisposable
                    )
                }
                requestState.error.let {
                    if ((requestState.error?.errorCode ?: 200) >= 400) {
                        longToast(it?.customMessage ?: "", ERROR)
                    }
                }
                longToastState(requestState.error)
                mViewModel.subscribeUserLiveData.value = null
            }
        }

            mProfileViewModel.userModulePermissionData.observe(this) { response ->
                response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                    requestState.apiResponse?.let { it ->
                        var enumSet = UserHolder.EnumUserModulePermission.values().toHashSet()
                        it.data?.let {
                            it.forEach { item ->
                                if(enumSet.any { it.name.equals(item.moduleName ?: "") }) {
                                    UserHolder.EnumUserModulePermission.valueOf(item.moduleName ?: "").permission = item
                                }
                            }
                        }
                        val secondIntent = Intent(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
                        LocalBroadcastManager.getInstance(this).sendBroadcast(secondIntent)
                        finish()
                        if(isFromSignup){
                            WelcomeQuestionnaireActivity.startActivity(this@SubscriptionActivity)
                            finish()
                        }else {
                            val intent = Intent(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
                            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                        }
                    }

                    requestState.error?.let { errorObj ->
                        when (errorObj.errorState) {
                            Constants.NETWORK_ERROR -> errorObj.customMessage?.let { longToast(it, errorObj.type) }
                            Constants.CUSTOM_ERROR -> errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        }
                    }

                }
            }

        mViewModel.startFreeTrialLiveData.observe(this){ response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { it ->
                    userHolder.isFreeTrialCompleted = true
                    TaoCalligraphy.instance.isSubscriptionStatusApiCalled = false
                    longToast(getString(R.string.subscribed_successfully),SUCCESS)
                    userHolder.freeTrialCompletionDate = formatDateYYYYMMMDD(DateTime.now().plusDays(userHolder.freeTrialDays-1).toDate())
//                    if(isFromQuestionnaries){
//                        openHomeScreen()
//                    }else {
//                        val intent = Intent(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
//                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//                    }
//                    val intent = Intent(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
//                    finish()
                    mProfileViewModel.getUserModulePermissionApi(this@SubscriptionActivity, mDisposable)
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
        var currentProduct = productDetails.subscriptionOfferDetails?.find { it.basePlanId == Constants.Subscription.SUBSCRIBE_YEARLY }
        var pricePhase = currentProduct?.pricingPhases?.pricingPhaseList?.last()
        var price: Double = (pricePhase?.priceAmountMicros)?.toDouble()?.div(1000000) ?: 0.0
        var ind = pricePhase?.formattedPrice?.indexOf(price.toString()[0]) ?: 1
        var currencySymbol = pricePhase?.formattedPrice?.substring(0,ind)

        mBinding.apply {
            btnAnnual.text = getString(R.string.annual).uppercase()+" (${currencySymbol}${String.format("%.2f",price.div(12))}/${getString(R.string.month)})"
        }

        currentProduct = productDetails.subscriptionOfferDetails?.find { it.basePlanId == Constants.Subscription.SUBSCRIBE_MONTHLY }
        pricePhase = currentProduct?.pricingPhases?.pricingPhaseList?.last()
        price = (pricePhase?.priceAmountMicros)?.toDouble()?.div(1000000) ?: 0.0
        ind = pricePhase?.formattedPrice?.indexOf(price.toString()[0]) ?: 1
        currencySymbol = pricePhase?.formattedPrice?.substring(0,ind)

        mBinding.apply {
            btnMonthly.text = getString(R.string.monthly).uppercase()+" (${currencySymbol}${price}/${getString(R.string.month)})"
        }
    }

    private fun subscriptionErrorDialog() {
        if(isDailogOpen) return

        isDailogOpen = true
        val title = getString(R.string.failed_to_purchase_product)
        val description = getString(R.string.payment_cancelled)

        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.setTitle("" + title)
            .setMessage("" + description)
            .setCancelable(false)
            .setPositiveButton(
                "" + getString(R.string.btn_ok)
            ) { dialog, _ ->
                isDailogOpen = false
                dialog!!.dismiss()
            }

        val alert = builder.create()
        alert.show()

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
        }else if(isFromSignup){
            WelcomeQuestionnaireActivity.startActivity(this@SubscriptionActivity)
            finish()
        } else {
            super.onBackPressed()
        }
    }

}