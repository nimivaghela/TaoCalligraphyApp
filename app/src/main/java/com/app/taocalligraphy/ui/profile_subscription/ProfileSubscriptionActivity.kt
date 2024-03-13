package com.app.taocalligraphy.ui.profile_subscription

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityProfileSubscriptionBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.SubscriptionDetails
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.profile_subscription.viewmodel.SubscriptionViewModel
import com.app.taocalligraphy.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImageProfile
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ProfileSubscriptionActivity : BaseActivity<ActivityProfileSubscriptionBinding>() {

    private val mViewModel: SubscriptionViewModel by viewModels()

    override fun getResource() = R.layout.activity_profile_subscription

    companion object {
        fun startActivity(activity: AppCompatActivity,subscriptionDetails: SubscriptionDetails?) {
            val intent = Intent(activity, ProfileSubscriptionActivity::class.java)
            intent.putExtra(Constants.subscription,subscriptionDetails)
            activity.startActivityWithAnimation(intent)
        }
    }

    val startCancelSubscriptionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.d("Subscription", "cancel Subscription return")
            mBinding.llMain.gone()
            mBinding.shimmerDetails.visible()
            if(!mViewModel.isApiLoading) {
                mViewModel.getSubscriptionDetails(this, mDisposable)
                mViewModel.isApiLoading = true
            }
        }

    val startCancelSubscriptionDetailsActivityForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.d("Subscription", "cancel Subscription return")
            mBinding.llMain.gone()
            mBinding.shimmerDetails.visible()
            mViewModel.isRedirectToCancel = true
            if(!mViewModel.isApiLoading) {
                mViewModel.getSubscriptionDetails(this, mDisposable)
                mViewModel.isApiLoading = true
            }
        }

//    val subscriptionDetails by lazy{
//        return@lazy intent?.extras?.getParcelable<SubscriptionDetails>(Constants.subscription)
//    }

    override fun initView() {
        mViewModel.subscriptionDetails = intent?.extras?.getParcelable<SubscriptionDetails>(Constants.subscription)!!
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(0, 0, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        setupToolbar()
//        if(!isApiLoading) {
//            mViewModel.getSubscriptionDetails(this, mDisposable)
//            isApiLoading = true
//        }
        mBinding.llMain.gone()
        mBinding.shimmerDetails.visible()
        setData()
        updateProfile()

    }

    private fun setData() {
        mBinding.shimmerDetails.gone()
        mBinding.llMain.visible()
        mBinding.lottieLines.visible()

        mBinding.apply {
            txtSubscriptionLevel.text =
                (mViewModel.subscriptionDetails?.subscriptionPlan + if(!(mViewModel.subscriptionDetails?.subscriptionType.isNullOrEmpty())) ", " + (mViewModel.subscriptionDetails?.subscriptionType ?: "") else "")
            mViewModel.subscriptionMethod = (mViewModel.subscriptionDetails?.subscriptionMethod ?: "")
            txtSubscriptionMethod.text =
                (mViewModel.subscriptionDetails?.subscriptionMethod ?: "").replace("_", " ")
            txtSubscriptionDate.text = getddMMMYYYYFromDate(mViewModel.subscriptionDetails?.subscriptionDate ?: "")
            if(!mViewModel.subscriptionDetails?.nextRenewalDate.isNullOrEmpty())
                txtNextRenewalDate.text = getddMMMYYYYFromDate(mViewModel.subscriptionDetails?.nextRenewalDate ?: "")
            mViewModel.subscriptionType = mViewModel.subscriptionDetails?.subscriptionType ?: ""
            mViewModel.subscriptionType =
                if (mViewModel.subscriptionType == "Monthly") Constants.Subscription.SUBSCRIBE_MONTHLY else Constants.Subscription.SUBSCRIBE_MONTHLY
            if(mViewModel.subscriptionDetails?.subscriptionPlan.equals(Constants.Subscription.LEVEL_1)){
                tvTitleSubscriptionMethod.gone()
                txtSubscriptionMethod.gone()
                btnCancelSubscription.gone()
                btnManageSubscription.gone()
                tvTitleRenewal.text = getString(R.string.expires_on)
            }else{
                tvTitleSubscriptionMethod.visible()
                txtSubscriptionMethod.visible()
                btnCancelSubscription.visible()
                btnManageSubscription.visible()
            }
        }
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    private fun updateProfile() {
        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }


    override fun observeApiCallbacks() {
        mViewModel.subscriptionDetailsLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let {
                    mBinding.shimmerDetails.gone()
                    mBinding.llMain.visible()
                    mBinding.lottieLines.visible()
                    mViewModel.isApiLoading = false
                    mBinding.apply {
                        txtSubscriptionLevel.text = (it.data?.subscriptionPlan + if(!(it.data?.subscriptionType.isNullOrEmpty())) ", " + (it.data?.subscriptionType ?: "") else "")
                        mViewModel.subscriptionMethod = (it.data?.subscriptionMethod ?: "")
                        txtSubscriptionMethod.text = (it.data?.subscriptionMethod ?: "").replace("_", " ")
                        if(!it.data?.subscriptionDate.isNullOrEmpty())
                        txtSubscriptionDate.text = getddMMMYYYYFromDate(it.data?.subscriptionDate ?: "")
                        if(!it.data?.nextRenewalDate.isNullOrEmpty())
                        txtNextRenewalDate.text = getddMMMYYYYFromDate(it.data?.nextRenewalDate ?: "")
                        mViewModel.subscriptionType = response.apiResponse?.data?.subscriptionType ?: ""
                        mViewModel.subscriptionType = if (mViewModel.subscriptionType == "Monthly") Constants.Subscription.SUBSCRIBE_MONTHLY else Constants.Subscription.SUBSCRIBE_MONTHLY
                        if(it.data?.subscriptionPlan.equals(Constants.Subscription.LEVEL_1)){
                            tvTitleSubscriptionMethod?.gone()
                            txtSubscriptionMethod.gone()
                            btnCancelSubscription.gone()
                            btnManageSubscription.gone()
                            tvTitleRenewal?.text = getString(R.string.expires_on)
                        }else{
                            tvTitleSubscriptionMethod?.visible()
                            txtSubscriptionMethod.visible()
                            btnCancelSubscription.visible()
                            btnManageSubscription.visible()
                        }
                    }
                    if (!response.apiResponse?.data?.cancelDate.isNullOrEmpty() && !mViewModel.isRedirectToCancel) {
                        TaoCalligraphy.instance.isSubscriptionStatusApiCalled = false
                        mViewModel.isRedirectToCancel = true
                        CancelSubscriptionActivity.startActivityForResult(this@ProfileSubscriptionActivity,
                            response.apiResponse?.data?.cancelDate ?: "",
                            startCancelSubscriptionDetailsActivityForResult)
                        finish()
                    }
                }
                longToastState(requestState.error)
            }
        }
    }

    override fun handleListener() {
        mBinding.apply {

            mToolbar.cardProfile.clickWithDebounce {
                UserMenuActivity.startActivity(this@ProfileSubscriptionActivity)
            }
            mToolbar.ivBackToolbar.clickWithDebounce {
                onBackPressed()
            }


            btnManageSubscription.setOnClickListener {
                if (mViewModel.subscriptionMethod == Constants.PLAYSTORE) {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/account/subscriptions")
                    )
                    startCancelSubscriptionActivityForResult.launch(browserIntent)
                } else {
                    var method = mViewModel.subscriptionMethod.replace("_", " ")
                    longToast(getString(R.string.you_have_subscribed_from) + "$method," +
                            getString(R.string.please_manage_the_subscription_from_the)
                            + method, Constants.ERROR)
                }
            }

            btnCancelSubscription.setOnClickListener {
                if (mViewModel.subscriptionMethod == Constants.PLAYSTORE) {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/account/subscriptions?sku=subscribe&package=${application.packageName}")
                    )
                    startCancelSubscriptionActivityForResult.launch(browserIntent)
                } else {
                    var method = mViewModel.subscriptionMethod.replace("_", " ")
                    longToast(getString(R.string.you_have_subscribed_from) + "$method," + getString(
                                            R.string.please_cancel_the_subscription_from_the) +  method,Constants.ERROR)
                }
            }

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