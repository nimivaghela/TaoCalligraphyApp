package com.app.taocalligraphy.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityMainBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.challenges.ChallengesListingActivity
import com.app.taocalligraphy.ui.field_healing.AllFieldHealingActivity
import com.app.taocalligraphy.ui.home.viewmodel.HomeViewModel
import com.app.taocalligraphy.ui.how_to_meditate.HowToMeditateActivity
import com.app.taocalligraphy.ui.how_to_meditate.ReadMeditateActivity
import com.app.taocalligraphy.ui.meditation.MeditationDetailActivity
import com.app.taocalligraphy.ui.meditation_rooms_list.MeditationRoomsFragment
import com.app.taocalligraphy.ui.meditation_timer.PlayMeditationTimerActivity
import com.app.taocalligraphy.ui.notification.NotificationFragment
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.profile_view.PublicProfileViewActivity
import com.app.taocalligraphy.ui.program.ProgramDetailsActivity
import com.app.taocalligraphy.ui.program.ProgramsListActivity
import com.app.taocalligraphy.ui.search.SearchFragment
import com.app.taocalligraphy.ui.wellness.BookConsultationActivity
import com.app.taocalligraphy.ui.wellness.WellnessFragment
import com.app.taocalligraphy.ui.wellness.adapter.CategoryListAdapter
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_wellness.view.*

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(), CategoryListAdapter.OnCategoryClicked {
    private var notificationCount: String = "0"
    private val mViewModel: HomeViewModel by viewModels()
    private val mProfileViewModel: ProfileViewModel by viewModels()
    private var categoryDataList: ArrayList<CategoryDataModel> = arrayListOf()

    companion object {
        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
            redirectToSearch: Boolean = false,
            errorMsg: String? = null
        ) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra(Constants.Param.redirectToSearch, redirectToSearch)
            intent.putExtra(Constants.Param.subscribeErrorMsg, errorMsg)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int = R.layout.activity_main

    private val redirectToSearch by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.redirectToSearch, false) ?: false
    }

    private val errorMsg by lazy {
        return@lazy intent.extras?.getString(Constants.Param.subscribeErrorMsg, null)
    }

    val MY_REQUEST_CODE = 1241
    private val appUpdateManager by lazy {
        return@lazy AppUpdateManagerFactory.create(this)
    }

    private val isFromNotification by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromNotification, false) ?: false
    }

    private val categoryListAdapter by lazy {
        return@lazy CategoryListAdapter(categoryDataList, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TaoCalligraphy.instance.simpleExoPlayer?.release()
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.bg_watermark_screen_drawable
            )
        )
    }

    override fun initView() {
        errorMsg?.let {
            if (!mViewModel.isSubscriptionErrorMessage)
                longToast(it, ERROR)
            mViewModel.isSubscriptionErrorMessage = true
        }

        mBinding.bottomNav.itemIconTintList = null
        checkDynamicLinkData()
        if (redirectToSearch) {
            mBinding.bottomNav.selectedItemId = R.id.search
            if (isNetwork()) {
                mBinding.llWellnessDialog.gone()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, searchFragment).commit()
                mViewModel.selectedFragment = Constants.search
            } else {
                showDialogNoInternet()
            }
        } else {
            if (isFromNotification) {
                HomeFragment.isFromNotification = true
            }

            when(mViewModel.selectedFragment) {
                Constants.home -> supportFragmentManager.beginTransaction().replace(R.id.container, homeFragment).commit()
                Constants.wellnessDialog -> mBinding.llWellnessDialog.visible()
                Constants.search -> supportFragmentManager.beginTransaction().replace(R.id.container, searchFragment).commit()
                Constants.notification -> supportFragmentManager.beginTransaction().replace(R.id.container, notificationFragment).commit()
                Constants.wellness -> onCategoryClicked(mViewModel.selectedWellnessCategory)
            }
        }
        mBinding.llWellnessDialog.rvCategory.adapter = categoryListAdapter
        callFetchDailyWisdomDataAPI()
        callCategoryDataApi()
        setSubscriptionBadge()
        checkForUpdateAvailability()

//        mProfileViewModel.userProfile(this, mDisposable)
        LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(
            mSubscriptionReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
        )
        LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )
    }


    override fun onResume() {
        super.onResume()
        noInternetConnectionDialog?.dismiss()
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        MY_REQUEST_CODE
                    )
                }
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                }
            }
    }

    private fun checkForUpdateAvailability() {
        // Before starting an update, register a listener for updates.
        appUpdateManager.registerListener(listener)
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    MY_REQUEST_CODE
                )
            }
        }
    }

    // Create a listener to track request state updates.
    val listener = InstallStateUpdatedListener { state ->
        // (Optional) Provide a download progress bar.
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            val bytesDownloaded = state.bytesDownloaded()
            val totalBytesToDownload = state.totalBytesToDownload()
            // Show update progress bar.
        } else if (state.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackbarForCompleteUpdate()
        }
        // Log state or install the update.
    }

    // Displays the snackbar notification and call to action.
    private fun popupSnackbarForCompleteUpdate() {
        // When status updates are no longer needed, unregister the listener.
        appUpdateManager.unregisterListener(listener)
        appUpdateManager.completeUpdate()
//        Snackbar.make(
//            mBinding.root,
//            "An update has just been downloaded.",
//            Snackbar.LENGTH_INDEFINITE
//        ).apply {
//            setAction("RESTART") { appUpdateManager.completeUpdate() }
////            setActionTextColor(resources.getColor(R.color.snackbar_action_text_color))
//            show()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
            } else {

            }
        }
    }

    private fun checkDynamicLinkData() {
        if (!userHolder.mDynamicLinkUserID.isNullOrEmpty()) {
            PublicProfileViewActivity.startActivity(
                this,
                userHolder.mDynamicLinkUserID.toString()
            )
        } else if (!userHolder.mDynamicLinkMeditationContentID.isNullOrEmpty()) {
            MeditationDetailActivity.startActivity(
                this@MainActivity, userHolder.mDynamicLinkMeditationContentID.toString()
            )
        } else if (!userHolder.mDynamicLinkWatchMeditationContentID.isNullOrEmpty()) {
            MeditationDetailActivity.startActivity(
                this@MainActivity, userHolder.mDynamicLinkWatchMeditationContentID.toString(),
                isFromMeditate = true
            )
        } else if (!userHolder.mDynamicLinkReadMeditationContentID.isNullOrEmpty()) {
            ReadMeditateActivity.startActivity(
                this@MainActivity, userHolder.mDynamicLinkReadMeditationContentID.toString()
            )
        } else if (!userHolder.mDynamicLinkProgramID.isNullOrEmpty()) {
            ProgramDetailsActivity.startActivity(
                this@MainActivity,
                userHolder.mDynamicLinkProgramID.toString(),
                false
            )
        } else if (!userHolder.mDynamicLinkMediationTimerID.isNullOrEmpty()) {
            PlayMeditationTimerActivity.startActivity(
                this@MainActivity,
                meditationId = userHolder.mDynamicLinkMediationTimerID.toString(),
                isFromNotification = true,
                meditationDetail = null
            )
        } else if (!userHolder.mDynamicLinkDailyWisdomID.isNullOrEmpty()) {
            HomeFragment.isFromNotification = true
        }
        userHolder.clearDynamicLinkData()
    }

    private fun deleteDownloads(downloadsList: List<MeditationContentResponse>) {
        if (isNetwork() && downloadsList.isNotEmpty()) {
            val jsonArray = JsonArray()
            downloadsList.forEach { content ->
                jsonArray.add(content.id.toInt())
                mViewModel.deleteUserDownload(content.id)
            }

            val jsonObject = JsonObject()
            jsonObject.add("contentIds", jsonArray)

            mViewModel.deleteDownloadsFromServer(jsonObject)
        }
    }

    private val mSubscriptionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
//            mProfileViewModel.getUserModulePermissionApi(this@MainActivity, mDisposable)
        }
    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            setSubscriptionBadge()
        }
    }

    private fun callCategoryDataApi() {
        mViewModel.fetchAllCategoryData(this, mDisposable)
    }

    private fun callFetchDailyWisdomDataAPI() {
        val currentDate: String = getCurrentDateWithFormatyyyyMMddFormat()
        mViewModel.fetchDailyWisdomDataAPI(currentDate, this, mDisposable)
    }

    override fun observeApiCallbacks() {
        TaoCalligraphy.instance.meditationDbRepo.getAllMeditationContentsDesc(true).observe(this) {
            it?.let { downloadsList ->
                deleteDownloads(downloadsList)
            }
        }

        mViewModel.fetchDailyWisdomDataResponse.observe(this@MainActivity) { response ->
            response?.let { requestState ->
//                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { response ->
                    response.data?.let {
                        mBinding.apply {
                            response.data?.totalUnReadNotificationCount?.let {
                                notificationCount = it
                                setNotificationCount(it)
                            } ?: kotlin.run {
                                mBinding.bottomNav.removeBadge(R.id.notifications)
                            }

                            response.data?.totalUnReadChatCount?.let {
                                userHolder.setChatCount(it)
                            } ?: kotlin.run {
                                userHolder.setChatCount("0")
                            }
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let {
                                longToast(
                                    it, ERROR
                                )
                            }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let {
                                longToast(
                                    it, ERROR
                                )
                            }
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

        mViewModel.fetchCategoryDataResponse.observe(this) { response ->
            response?.let { requestState ->
//                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.list?.let { categoryList ->
                    categoryDataList.clear()

                    if (categoryList.size > 0) {
                        categoryDataList.addAll(categoryList)
                        categoryListAdapter.notifyDataSetChanged()

                        mBinding.llWellnessDialog.rvCategory.visible()
                        mBinding.llWellnessDialog.lblNoCategory.gone()
                    } else {
                        mBinding.llWellnessDialog.rvCategory.gone()
                        mBinding.llWellnessDialog.lblNoCategory.visible()
                    }
                }
            }
        }

        mProfileViewModel.userModulePermissionData.observe(this) { response ->
            response?.let { requestState ->
//                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { it ->
                    var enumSet = UserHolder.EnumUserModulePermission.values().toHashSet()
                    it.data?.let {
                        it.forEach { item ->
                            if (enumSet.any { it.name.equals(item.moduleName ?: "") }) {
                                UserHolder.EnumUserModulePermission.valueOf(
                                    item.moduleName ?: ""
                                ).permission = item
                            }
                        }
                    }
                    val intent = Intent(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                }

                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR -> errorObj.customMessage?.let {
                            longToast(
                                it,
                                errorObj.type
                            )
                        }
                        Constants.CUSTOM_ERROR -> errorObj.customMessage?.let {
                            longToast(
                                it,
                                errorObj.type
                            )
                        }
                    }
                }

            }
        }

    }

    fun setNotificationCount(it: String) {
        notificationCount = it
        if (!(UserHolder.EnumUserModulePermission.NOTIFICATION.permission?.canAccess
                ?: false)
        ) return

        if (it.toInt() > 9) {
            mBinding.bottomNav.setBadge(
                R.id.notifications,
                "9+"
            )
        } else {
            if (it.toInt() > 0) {
                mBinding.bottomNav.setBadge(
                    R.id.notifications,
                    it
                )
            } else {
                mBinding.bottomNav.hideBadge(R.id.notifications)
            }
        }
    }

    fun setSubscriptionBadge() {
        if (!(UserHolder.EnumUserModulePermission.SEE_PROGRAMS.permission?.canAccess ?: true)) {
            mBinding.llWellnessDialog.ivProgramLock.visible()
        } else {
            mBinding.llWellnessDialog.ivProgramLock.gone()
        }

        if (UserHolder.EnumUserModulePermission.SEARCH.permission == null) return

        if (!(UserHolder.EnumUserModulePermission.SEARCH.permission?.canAccess ?: false)) {
            mBinding.bottomNav.setSubscripitionBadge(
                R.id.search,
                UserHolder.EnumUserModulePermission.SEARCH.permission?.badge ?: " "
            )
        } else {
            mBinding.bottomNav.hideSubscribeBadge(R.id.search)
        }

        if (!(UserHolder.EnumUserModulePermission.NOTIFICATION.permission?.canAccess ?: false)) {
            mBinding.bottomNav.hideBadge(R.id.notifications)
            mBinding.bottomNav.setSubscripitionBadge(
                R.id.notifications,
                UserHolder.EnumUserModulePermission.NOTIFICATION.permission?.badge ?: " "
            )
        } else {
            mBinding.bottomNav.hideSubscribeBadge(R.id.notifications)
            setNotificationCount(notificationCount)
        }
        categoryListAdapter.notifyDataSetChanged()
    }

    var homeFragment = HomeFragment()
    var notificationFragment = NotificationFragment()
    var meditationRoomsFragment = MeditationRoomsFragment()
    var searchFragment = SearchFragment()

    override fun handleListener() {
        mBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.wellness -> {
                    if (isNetwork()) {
                        categoryDialogVisibilityManageListener()
                    } else {
                        showDialogNoInternet()
                    }
                    return@setOnItemSelectedListener true
                }
                R.id.notifications -> {
                    if (isNetwork()) {
                        mBinding.llWellnessDialog.gone()
                        if (UserHolder.EnumUserModulePermission.NOTIFICATION.permission?.canAccess == null)
                            return@setOnItemSelectedListener false

                        if (!(UserHolder.EnumUserModulePermission.NOTIFICATION.permission?.canAccess
                                ?: false)
                        ) {
                            SubscriptionActivity.startActivityForResult(
                                this@MainActivity
                            )
                            return@setOnItemSelectedListener false
                        } else {
                            mViewModel.selectedFragment = Constants.notification
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.container, notificationFragment).commit()

                            val currentFragment =
                                supportFragmentManager.findFragmentById(R.id.container)
                            if (currentFragment is SearchFragment) {
                                searchFragment.clearSearch()
                            }
                        }
                    } else {
                        showDialogNoInternet()
                    }
                    return@setOnItemSelectedListener true
                }
                R.id.home -> {
                    mBinding.llWellnessDialog.gone()
                    mViewModel.selectedFragment = Constants.home
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, homeFragment).commit()
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
                    if (currentFragment is SearchFragment) {
                        searchFragment.clearSearch()
                    }
                    callFetchDailyWisdomDataAPI()
                    return@setOnItemSelectedListener true
                }
//                R.id.rooms -> {
//                    if (isNetwork()) {
//                        mBinding.llWellnessDialog.gone()
//                        supportFragmentManager.beginTransaction()
//                            .replace(R.id.container, meditationRoomsFragment).commit()
//                    } else {
//                        showDialogNoInternet()
//                    }
//                    return@setOnItemSelectedListener true
//                }
                R.id.search -> {
                    if (isNetwork()) {
                        mBinding.llWellnessDialog.gone()

                        if (UserHolder.EnumUserModulePermission.SEARCH.permission?.canAccess == null)
                            return@setOnItemSelectedListener false

                        if (!(UserHolder.EnumUserModulePermission.SEARCH.permission?.canAccess
                                ?: true)
                        ) {
                            SubscriptionActivity.startActivityForResult(
                                this@MainActivity
                            )
                            return@setOnItemSelectedListener false
                        } else {
                            mViewModel.selectedFragment = Constants.search
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.container, searchFragment).commit()
                        }
                    } else {
                        showDialogNoInternet()
                    }
                    return@setOnItemSelectedListener true
                }
                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
        mBinding.llWellnessDialog.llWellnessData.setOnClickListener {
            //Using this listener wellness dialog will not hide when user click on this
        }

        mBinding.llWellnessDialog.llPrograms.setOnClickListener {
            if (UserHolder.EnumUserModulePermission.SEE_PROGRAMS.permission?.canAccess == null)
                return@setOnClickListener

            if (!(UserHolder.EnumUserModulePermission.SEE_PROGRAMS.permission?.canAccess ?: true)) {
                SubscriptionActivity.startActivityForResult(
                    this@MainActivity
                )
                return@setOnClickListener
            }
            mBinding.llWellnessDialog.gone()
            ProgramsListActivity.startActivity(this)
            mBinding.bottomNav.selectedItemId = R.id.home
        }

        mBinding.llWellnessDialog.tvWellnessChallenges.setOnClickListener {
            mBinding.llWellnessDialog.gone()
            ChallengesListingActivity.startActivity(this)
        }

        mBinding.llWellnessDialog.llBookAConsultation.setOnClickListener {
            mBinding.llWellnessDialog.gone()
            BookConsultationActivity.startActivity(this)
        }

        mBinding.llWellnessDialog.llFieldHealing.setOnClickListener {
            mBinding.llWellnessDialog.gone()
            AllFieldHealingActivity.startActivity(this)
        }

        mBinding.llWellnessDialog.tvHowToMeditate.setOnClickListener {
            mBinding.llWellnessDialog.gone()
            HowToMeditateActivity.startActivity(this)
            mBinding.bottomNav.selectedItemId = R.id.home
        }

    }

    private fun showDialogNoInternet() {
        /* mBinding.bottomNav.menu[0].isChecked = true
         showNoInternetBottomSheet(
             true,
             getString(R.string.oops_it_seems_you_re_offline), true
         )*/
    }

    private fun categoryDialogVisibilityManageListener() {
        if (!mBinding.llWellnessDialog.isVisible()) {
            if (isNetwork()) {
                mViewModel.selectedFragment = Constants.wellnessDialog
                mBinding.llWellnessDialog.visible()
            }
        } else {
            mViewModel.selectedFragment = ""
            mBinding.llWellnessDialog.gone()
        }
    }

    fun hideWellnessDialog() {
        mBinding.llWellnessDialog.gone()
    }

    override fun onCategoryClicked(position: Int) {
        if (UserHolder.EnumUserModulePermission.CONTENT_LIBRARY.permission?.canAccess == null)
            return

        mBinding.llWellnessDialog.gone()
        if (!(UserHolder.EnumUserModulePermission.CONTENT_LIBRARY.permission?.canAccess ?: false)) {
            SubscriptionActivity.startActivityForResult(
                this@MainActivity
            )
            return
        }
        setWellNessFragment(position)
    }

    fun setWellNessFragment(position: Int) {
        mViewModel.selectedFragment = Constants.wellness
        mViewModel.selectedWellnessCategory = position
        val wellnessFragment = WellnessFragment()
        val args = Bundle()
        args.putInt("POSITION", position)
        wellnessFragment.arguments = args
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, wellnessFragment).commit()
    }

    var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (isTaskRoot) {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
            if (currentFragment is HomeFragment) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed()
                    return
                } else {
                    this.doubleBackToExitPressedOnce = true
                    showToast(this, getString(R.string.press_again_to_exit))
                    addDelay({ doubleBackToExitPressedOnce = false }, 2000)
                }
            } else {
                if (currentFragment is SearchFragment) {
                    searchFragment.clearSearch()
                }
                mViewModel.selectedFragment = Constants.home
                mBinding.bottomNav.selectedItemId = R.id.home
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, HomeFragment()).commit()
                mBinding.llWellnessDialog.rvCategory.adapter = categoryListAdapter
                callFetchDailyWisdomDataAPI()
                callCategoryDataApi()
                return
            }
        } else {
            super.onBackPressed()
        }
    }

    fun setSearchTabSelected() {
        mBinding.bottomNav.selectedItemId = R.id.search
    }

    override fun noInternetListener(model: IsNetworkAvailableListener) {
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this@MainActivity)
            .unregisterReceiver(mSubscriptionReceiver)
        LocalBroadcastManager.getInstance(this@MainActivity)
            .unregisterReceiver(mAccessLevelReceiver)
        super.onDestroy()
    }
}