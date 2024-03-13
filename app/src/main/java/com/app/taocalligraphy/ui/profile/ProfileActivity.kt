package com.app.taocalligraphy.ui.profile

import android.content.*
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityProfileBinding
import com.app.taocalligraphy.models.ProfileMenuModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.profile.UserGoalsScreenApiResponse
import com.app.taocalligraphy.models.response.profile.UserInterestApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.community.CommunityActivity
import com.app.taocalligraphy.ui.goals.GoalsActivity
import com.app.taocalligraphy.ui.home.viewmodel.HomeViewModel
import com.app.taocalligraphy.ui.interests.InterestsActivity
import com.app.taocalligraphy.ui.profile.adapter.ProfileMenuAdapter
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.profile_account_info.ProfileAccountInfoActivity
import com.app.taocalligraphy.ui.profile_rewards.ProfileRewardsActivity
import com.app.taocalligraphy.ui.profile_subscription.CancelSubscriptionActivity
import com.app.taocalligraphy.ui.profile_subscription.ProfileSubscriptionActivity
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.profile_subscription.viewmodel.SubscriptionViewModel
import com.app.taocalligraphy.ui.referrals.ReferralsActivity
import com.app.taocalligraphy.ui.settings.SettingsActivity
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.ui.welcome.WelcomeActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding>(),
    ProfileMenuAdapter.OnAdapterItemClickListener {

    private val mViewModel: ProfileViewModel by viewModels()
    private val mHomeViewModel: HomeViewModel by viewModels()
    private val mSubscriptionViewModel: SubscriptionViewModel by viewModels()
    private var mProfileMenuAdapter: ProfileMenuAdapter? = null
    var userGoalsScreenList = java.util.ArrayList<UserGoalsScreenApiResponse.UserGoalsScreenList>()
    var userInterestList = java.util.ArrayList<UserInterestApiResponse.InterestList?>()


    companion object {
        // Used to Call Api on Finishing other activity
        private var isFromNavigation = false

        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, ProfileActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_profile

    override fun initView() {
        setupToolbar()
        updateProfile()
        initRecyclerView()

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(0, 0, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        setDataFromPreference()

        LocalBroadcastManager.getInstance(this@ProfileActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )
    }

    private fun setDataFromPreference() {
        mBinding.ivUserProfile.loadImageProfile(
            userHolder.thumbProfilePicUrl,
            R.drawable.ic_profile_default
        )
        mBinding.tvName.text = "${userHolder.firstName} ${userHolder.lastName}"
        mBinding.tvEmailId.text = userHolder.emailAddress
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@ProfileActivity)
            .unregisterReceiver(mAccessLevelReceiver)
    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            mProfileMenuAdapter?.notifyDataSetChanged()
        }
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()
    }


    private fun initRecyclerView() {
        setUpProfileMenuData(mViewModel.showGoals)
        mProfileMenuAdapter = ProfileMenuAdapter(mViewModel.mProfileMenuList, this)
        mBinding.rvProfileMenu.adapter = mProfileMenuAdapter
    }

    private fun setUpProfileMenuData(isUpdateGoals: Boolean) {
        mViewModel.mProfileMenuList.clear()
        mViewModel.mProfileMenuList.add(
            ProfileMenuModel(
                getString(R.string.account_info),
                R.drawable.vd_user_circle,
                getString(R.string.personal_info_password_public_profile),
                (UserHolder.EnumUserModulePermission.ACCOUNT_INFO.permission?.canAccess ?: true),
                (UserHolder.EnumUserModulePermission.ACCOUNT_INFO.permission?.badge ?: "")
            )
        )
        if (isUpdateGoals) {
            mViewModel.mProfileMenuList.add(
                ProfileMenuModel(
                    getString(R.string.goals),
                    R.drawable.vd_goals_icon,
                    mViewModel.goalsData,
                    (UserHolder.EnumUserModulePermission.GOALS.permission?.canAccess ?: true),
                    (UserHolder.EnumUserModulePermission.GOALS.permission?.badge ?: "")
                )
            )
            mViewModel.mProfileMenuList.add(
                ProfileMenuModel(
                    getString(R.string.interests),
                    R.drawable.vd_interests_icon,
                    mViewModel.interestData,
                    (UserHolder.EnumUserModulePermission.INTERESTS.permission?.canAccess ?: true),
                    (UserHolder.EnumUserModulePermission.INTERESTS.permission?.badge ?: "")
                )
            )
        }
//        mProfileMenuList.add(
//            ProfileMenuModel(
//                getString(R.string.community),
//                R.drawable.vd_community_icon,
//                getString(R.string.friend_list_pending_requests_blocked_users)
//            )
//        )
        mViewModel.mProfileMenuList.add(
            ProfileMenuModel(
                getString(R.string.referrals),
                R.drawable.vd_referral_icon,
                getString(R.string.referral_link_referral_rewards),
                (UserHolder.EnumUserModulePermission.REFERRALS.permission?.canAccess ?: true),
                (UserHolder.EnumUserModulePermission.REFERRALS.permission?.badge ?: "")
            )
        )
//        mProfileMenuList.add(
//            ProfileMenuModel(
//                getString(R.string.rewards),
//                R.drawable.vd_rewards_icon,
//                getString(R.string.earn_or_redeem_reward_points)
//            )
//        )

        mViewModel.mProfileMenuList.add(
            ProfileMenuModel(
                getString(R.string.subscription),
                R.drawable.vd_subscription_icon,
                mViewModel.subscription,
                (UserHolder.EnumUserModulePermission.SUBSCRIPTION.permission?.canAccess ?: true),
                (UserHolder.EnumUserModulePermission.SUBSCRIPTION.permission?.badge ?: "")
            )
        )
        mViewModel.mProfileMenuList.add(
            ProfileMenuModel(
                getString(R.string.settings),
                R.drawable.vd_settings_icon,
                getString(R.string.notifications_language),
                (UserHolder.EnumUserModulePermission.SETTINGS.permission?.canAccess ?: true),
                (UserHolder.EnumUserModulePermission.SETTINGS.permission?.badge ?: "")
            )
        )
    }

    override fun observeApiCallbacks() {
        mViewModel.userProfileLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    if (it.data != null) {
                        userHolder.isLogin = true
                        it.data?.apply {
                            mBinding.ivUserProfile.loadImageProfile(
                                originalProfilePicUrl,
                                R.drawable.ic_profile_default
                            )
                            mBinding.tvName.text = "$firstName $lastName"
                            mBinding.tvEmailId.text = email
                        }
                    }
                }
//                showErrorState(requestState.error)
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        else -> {
                            when (errorObj.errorCode) {
                                Constants.StatusCode.STATUS_401 -> {
                                }
                                Constants.StatusCode.STATUS_404 -> {
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
            }
        }

        mHomeViewModel.userLogoutLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    cancelAllPreviousAlarm()
                    userHolder.clearUserHolder()
                    LoginManager.getInstance().logOut()
                    notificationManager?.cancelAll()
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    val googleSignInClient = GoogleSignIn.getClient(this, gso)
                    googleSignInClient.signOut()
                    mViewModel.deleteAllDownloads()
                    TaoCalligraphy.instance.getDownloadTracker()?.removeAllDownload()
                    cancelAllPreviousAlarm()
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                    startActivity(intent)
                    finishAffinity()
                }
//                showErrorState(requestState.error)
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            longToast("" + errorObj.customMessage, Constants.ERROR)
                        else -> {
                        }
                    }
                }
            }
        }
        mViewModel.leftMenuResponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { dataResponse ->
                    dataResponse.goals?.let {
                        mViewModel.goalsData = TextUtils.join(", ", it)
                    } ?: kotlin.run {
                        mViewModel.goalsData = ""
                    }

                    dataResponse.interests?.let {
                        mViewModel.interestData = TextUtils.join(", ", it)
                    } ?: kotlin.run {
                        mViewModel.interestData = ""
                    }
                    mViewModel.subscriptionPlan = dataResponse.subscriptionPlan ?: ""
                    mViewModel.subscription = dataResponse.subscription ?: ""
                    if (mViewModel.subscription != "")
                        mViewModel.subscription =
                            mViewModel.subscription + " | " + getString(R.string.manage)
                    mViewModel.showGoals = true
                    mViewModel.isProfileApiCalled = true

                    setUpProfileMenuData(mViewModel.showGoals)
                    mProfileMenuAdapter?.notifyDataSetChanged()
                    isFromNavigation = false
                }
                longToastState(requestState.error)
                mViewModel.leftMenuResponse.postValue(null)
            }
        }

        mSubscriptionViewModel.subscriptionDetailsLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    if (response.apiResponse?.data?.isExpired() ?: false) {
                        CancelSubscriptionActivity.startActivityForResult(
                            this@ProfileActivity,
                            response.apiResponse?.data?.endDate ?: "",
                            isCancelsubscription = false
                        )
                    } else if (!response.apiResponse?.data?.cancelDate.isNullOrEmpty()) {
                        CancelSubscriptionActivity.startActivityForResult(
                            this@ProfileActivity,
                            response.apiResponse?.data?.cancelDate ?: "",
                            isCancelsubscription = true
                        )
                    } else if (mViewModel.subscriptionPlan.equals(Constants.Subscription.LEVEL_0)) {
                        SubscriptionActivity.startActivityForResult(this@ProfileActivity)
                    } else {
                        ProfileSubscriptionActivity.startActivity(this, response.apiResponse?.data)
                    }
                    mSubscriptionViewModel.subscriptionDetailsLiveData.postValue(null)
                }
                longToastState(requestState.error)
            }
        }

    }

    override fun handleListener() {
        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(
                this@ProfileActivity,
                ProfileActivity::class.java.simpleName
            )
        }
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.tvLogoutProfile.setOnClickListener {
            logoutConfirmationDialog()
        }
    }

    override fun onAdapterClick(data: ProfileMenuModel) {
        when (data.title) {
            getString(R.string.account_info) -> {
                if (!(UserHolder.EnumUserModulePermission.ACCOUNT_INFO.permission?.canAccess
                        ?: true)
                ) {
                    SubscriptionActivity.startActivityForResult(
                        this@ProfileActivity
                    )
                    return
                }
                ProfileAccountInfoActivity.startActivity(this)
                isFromNavigation = true
            }
            getString(R.string.rewards) -> {
                if (!(UserHolder.EnumUserModulePermission.COLLECT_REFERRAL_REWARD.permission?.canAccess
                        ?: true)
                ) {
                    SubscriptionActivity.startActivityForResult(
                        this@ProfileActivity
                    )
                    return
                }
                ProfileRewardsActivity.startActivity(this)
            }
            getString(R.string.subscription) -> {
                if (UserHolder.EnumUserModulePermission.SUBSCRIPTION.permission?.canAccess
                        ?: false
                ) {
                    mSubscriptionViewModel.getSubscriptionDetails(this, mDisposable)
//                    ProfileSubscriptionActivity.startActivity(this@ProfileActivity)
                } else {
                    SubscriptionActivity.startActivityForResult(this)
                }
            }
            getString(R.string.goals) -> {
                if (!(UserHolder.EnumUserModulePermission.GOALS.permission?.canAccess ?: true)) {
                    SubscriptionActivity.startActivityForResult(
                        this@ProfileActivity
                    )
                    return
                }
                GoalsActivity.startActivity(this)
                isFromNavigation = true
            }
            getString(R.string.interests) -> {
                if (!(UserHolder.EnumUserModulePermission.INTERESTS.permission?.canAccess
                        ?: true)
                ) {
                    SubscriptionActivity.startActivityForResult(
                        this@ProfileActivity
                    )
                    return
                }
                InterestsActivity.startActivity(this)
                isFromNavigation = true
            }
            getString(R.string.settings) -> {
                if (!(UserHolder.EnumUserModulePermission.SETTINGS.permission?.canAccess ?: true)) {
                    SubscriptionActivity.startActivityForResult(
                        this@ProfileActivity
                    )
                    return
                }
                SettingsActivity.startActivity(this)
                isFromNavigation = true
            }
            getString(R.string.community) -> {
                CommunityActivity.startActivity(this)
                //PublicProfileViewActivity.startActivity(this)
                isFromNavigation = true
            }
            getString(R.string.referrals) -> {
                if (!(UserHolder.EnumUserModulePermission.REFERRALS.permission?.canAccess
                        ?: true)
                ) {
                    SubscriptionActivity.startActivityForResult(
                        this@ProfileActivity
                    )
                    return
                }
                ReferralsActivity.startActivity(this, "")
            }
        }
    }

    private fun logoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.setTitle("")
            .setMessage("" + getString(R.string.logout_msg))
            .setCancelable(true)
            .setPositiveButton(
                "" + getString(R.string.yes)
            ) { dialog, which ->
                dialog!!.dismiss()
                if (isNetwork())
                    mHomeViewModel.userLogout("", this@ProfileActivity, mDisposable)
                else
                    longToast(
                        getString(
                            R.string.no_internet,
                            getString(R.string.no_internet_connection_to_logout)
                        ),
                        Constants.ERROR
                    )
            }
            .setNegativeButton(
                "" + getString(R.string.no)
            ) { dialog, which -> dialog!!.dismiss() }
        builder.create().show()
    }

    override fun onResume() {

        super.onResume()
        setDataFromPreference()
        updateProfile()
        if (isNetwork()) {
            if (!mViewModel.isProfileApiCalled || isFromNavigation)
                mViewModel.getLeftMenuDataApi(this, mDisposable)
            else
                setUpProfileMenuData(mViewModel.showGoals)
//            mViewModel.userProfile(this, mDisposable)
//            mViewModel.getUserGoalsScreen2Api(this, mDisposable)
//            mViewModel.getUserInterestsApi(this, mDisposable)
        }

    }

    private fun updateProfile() {
        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
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