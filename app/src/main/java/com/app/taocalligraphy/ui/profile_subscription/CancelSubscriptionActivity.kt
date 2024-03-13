package com.app.taocalligraphy.ui.profile_subscription

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
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
import com.app.taocalligraphy.databinding.ActivityCancelSubscriptionBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.post_signup_questionnaire.WelcomeQuestionnaireActivity
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.profile_subscription.viewmodel.SubscriptionViewModel
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.GoogleBillingHelper
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class CancelSubscriptionActivity : BaseActivity<ActivityCancelSubscriptionBinding>() {

    override fun getResource() = R.layout.activity_cancel_subscription

    companion object {
        fun startActivityForResult(activity: AppCompatActivity,cancelDate:String,result : ActivityResultLauncher<Intent>? = null,isCancelsubscription : Boolean = true) {
            val intent = Intent(activity, CancelSubscriptionActivity::class.java)
            intent.putExtra(Constants.cancelDate,cancelDate)
            intent.putExtra(Constants.isCancel,isCancelsubscription)
            activity.startActivityWithAnimation(intent)
        }
    }

    private val mViewModel : SubscriptionViewModel by viewModels()
    private val mProfileViewModel: ProfileViewModel by viewModels()

    private lateinit var productDetails : ProductDetails
    private  var currentProduct : ProductDetails.SubscriptionOfferDetails? = null
    private var currentPlanType : String = Constants.Subscription.SUBSCRIBE_YEARLY

    private val cancelDate by lazy{
        return@lazy intent?.extras?.getString(Constants.cancelDate, "")
    }

    private val isCancel by lazy{
        return@lazy intent?.extras?.getBoolean(Constants.isCancel, true) ?: true
    }

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(0, 0, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        if(!this::productDetails.isInitialized){
            GoogleBillingHelper.getInstance().createBillingConnection(this)
        }
        setupToolbar()
        updateProfile()
        mBinding.apply {
            tvSubscriptionExpired.text = userHolder.firstName + ", " + getString(if(isCancel) R.string.your_subscription_was_canceled_on else R.string.your_subscription_was_Expired_on) + " " + formatDate(cancelDate)
        }
    }

    override fun observeApiCallbacks() {
        GoogleBillingHelper.getInstance().productDetailsLiveData.observe(this@CancelSubscriptionActivity){
            productDetails = it
        }

        GoogleBillingHelper.getInstance().setBillingListener(object : GoogleBillingHelper.OnBillingListener{
            override fun onError() {
            }

            override fun onRestoreSubscriptionFail() {
                longToast(getString(R.string.no_subscription_found), Constants.ERROR)
            }

            override fun onSuccess(purchase: Purchase) {
                currentProduct = currentProduct ?: productDetails.subscriptionOfferDetails?.first()
                val pricePhase = currentProduct?.pricingPhases?.pricingPhaseList?.first()
                val price: Double = (pricePhase?.priceAmountMicros)?.toDouble()?.div(1000000) ?: 0.0

                    mViewModel.callSubscribeUserApi(
                        purchase,
                        currentProduct?.pricingPhases?.pricingPhaseList?.first()?.priceCurrencyCode ?: "USD",
                        productDetails.productId,
                        if (currentPlanType == Constants.Subscription.SUBSCRIBE_YEARLY) "Annual" else "Monthly",
                        price,
                        this@CancelSubscriptionActivity, mDisposable
                    )
            }
        })

        mViewModel.subscribeUserLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { it ->
                    userHolder.isSubscribed = true
                    TaoCalligraphy.instance.isSubscriptionStatusApiCalled = false
                    longToast(getString(R.string.subscribed_successfully), Constants.SUCCESS)
//                    var resultIntent = Intent()
//                    resultIntent.putExtra("isSubscribed",true)
//                    setResult(Activity.RESULT_OK,resultIntent)
                    mProfileViewModel.getUserModulePermissionApi(
                        this@CancelSubscriptionActivity,
                        mDisposable
                    )
                }
                requestState.error.let {
                    if ((requestState.error?.errorCode ?: 200) >= 400) {
                        longToast(it?.customMessage ?: "", Constants.ERROR)
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

                    val intent = Intent(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

                    finish()
                }

                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR -> errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR -> errorObj.customMessage?.let { longToast(it, errorObj.type) }
                    }
                }

            }
        }

    }

    private fun setupToolbar() {

    }

    private fun updateProfile() {

    }

    override fun handleListener() {
        mBinding.btnResubscribe.setOnClickListener {
            SubscriptionActivity.startActivityForResult(this@CancelSubscriptionActivity)
            finish()
        }
        mBinding.btnClose.clickWithDebounce {
            onBackPressed()
        }

        mBinding.btnRestorePurchase.clickWithDebounce {
            GoogleBillingHelper.getInstance().getPurchasedItems()
        }

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
}