package com.app.taocalligraphy.ui.home

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.api.UpdateAclPermissionsWorkManager
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentHomeBinding
import com.app.taocalligraphy.models.WeekDatesDayListModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataModel
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.NavigationHandler
import com.app.taocalligraphy.ui.downloads.DownloadsActivity
import com.app.taocalligraphy.ui.home.adapter.*
import com.app.taocalligraphy.ui.home.viewmodel.HomeViewModel
import com.app.taocalligraphy.ui.how_to_meditate.HowToMeditateActivity
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.profile_subscription.viewmodel.SubscriptionViewModel
import com.app.taocalligraphy.ui.statistics.StatisticsActivity
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.ui.welcome.WelcomeActivity
import com.app.taocalligraphy.ui.welcome_login.WelcomeLoginActivity
import com.app.taocalligraphy.ui.wellness.adapter.WeekCalendarAdapter
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
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(),
    WellnessCategoryAdapter.OnItemClickListener,
    UpcomingSessionAdapter.SessionClickListener,
    WeekCalendarAdapter.OnWeekAdapterItemClickListener,
    HowToMeditateAdapter.OnHowToMeditateClickListener,
    NewReleaseMeditationAdapter.OnMultiSessionListener,
    ForYouMeditationAdapter.OnMultiSessionListener {

    private val mViewModel: HomeViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )
    private val subscriptionViewModel: SubscriptionViewModel by viewModels()
    private val mProfileViewModel: ProfileViewModel by viewModels()

    private var isLoadingForYou = true
    private var isLoadingNewRelease = true
    private var isLoadingHowToMeditate = true

    companion object {
        var isFromNotification: Boolean = false
    }

    override fun getLayoutId(): Int = R.layout.fragment_home

    private val forYouDataAdapter by lazy {
        return@lazy ForYouMeditationAdapter(this)
    }

    private val newReleaseDataAdapter by lazy {
        return@lazy NewReleaseMeditationAdapter(this)
    }

    private val howToMeditateDataAdapter by lazy {
        return@lazy HowToMeditateAdapter(this)
    }

    private val wellnessCategoryAdapter by lazy {
        return@lazy WellnessCategoryAdapter(this)
    }

    private val featureSessionAdapter by lazy {
        return@lazy FeatureSessionAdapter(requireContext())
    }

    private val upcomingSessionAdapter by lazy {
        return@lazy UpcomingSessionAdapter(this)
    }

    private var mWeekCalendarAdapter: WeekCalendarAdapter? = null
    private var mWeekDatesDayListModelDummy = mutableListOf<WeekDatesDayListModel>()

    override fun displayMessage(message: String) {
    }

    val startSubscriptionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}


    @SuppressLint("NotifyDataSetChanged")
    override fun initView() {
        setupToolbar()
        setUserData()
        setAdapter()
        find7DatesFromToday()

        if (mViewModel.isDataLoaded()) {
            getSubscriptionStatus()
        } else {
            setNewReleasesData()
            setForYouData()
            setHowToMeditateData()
            wellnessCategoryAdapter.setCategoryData(mViewModel.categoryDataList)
        }

        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            onRefresh()
        }

        if (!requireActivity().isNetwork()) {
            setVisibilityBasedOnConnection(false)
        } else {
            setVisibilityBasedOnConnection(true)
        }

        registerReceivers()
    }

    private fun registerReceivers() {
        unregisterReceivers()

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mMessageReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_FAVOURITES_CHANGED)
        )

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mSubscriptionReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
        )

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )
    }

    private fun unregisterReceivers() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mMessageReceiver)
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(mSubscriptionReceiver)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mAccessLevelReceiver)
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            intent.extras?.let {
                if (intent.hasExtra(Constants.BroadcastIntentFilter.CONTENT_ID)) {
                    val contentId =
                        intent.getStringExtra(Constants.BroadcastIntentFilter.CONTENT_ID)

                    mViewModel.forYouDataList.filter { it?.id == contentId }.let {
                        if (it.isNotEmpty()) {
                            it.first()?.isFavorites = false
                        }
                    }.also { forYouDataAdapter.setMeditationData(mViewModel.forYouDataList) }

                    mViewModel.newReleasesDataList.filter { it?.id == contentId }.let {
                        if (it.isNotEmpty()) {
                            it.first()?.isFavorites = false
                        }
                    }
                        .also { newReleaseDataAdapter.setMeditationData(mViewModel.newReleasesDataList) }
                }
            }
        }
    }

    private val mSubscriptionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            onRefresh()
        }
    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            setSubscriptionUI()
        }
    }

    override fun onDestroy() {
        unregisterReceivers()
        super.onDestroy()
    }

    private fun onRefresh() {
        mBinding.rvForYou.gone()
        mBinding.rvNewRelease.gone()
        mBinding.rvHowToMeditate.gone()
        mBinding.rvSelectCategoryExperienceMore.gone()
        mBinding.shimmerforYou.showShimmer(true)
        mBinding.shimmerNewRelease.showShimmer(true)
        mBinding.shimmerHowToMeditate.showShimmer(true)
        mBinding.shimmerForYouWellnessFirstRow.showShimmer(true)
        mBinding.shimmerforYou.visible()
        mBinding.shimmerNewRelease.visible()
        mBinding.shimmerHowToMeditate.visible()
        mBinding.shimmerForYouWellnessFirstRow.visible()
        mBinding.lblNoForYouData.gone()
        mBinding.lblNoNewRelease.gone()

        if (!requireActivity().isNetwork()) {
            setVisibilityBasedOnConnection(false)
        } else {
            setVisibilityBasedOnConnection(true)
        }

        mBinding.swipeToRefreshLayout.isRefreshing = false
        getSubscriptionStatus()
    }

    private fun getAllData() {
        mViewModel.currentPageForYouCount = -1
        mViewModel.currentPageNewReleaseCount = -1
        mViewModel.currentPageHowToMeditateCount = -1

        WorkManager.getInstance(TaoCalligraphy.instance)
        UpdateAclPermissionsWorkManager.startWorkManager()
        callForYouContentAPI()
        callNewReleaseContentAPI()
        callHowToMeditateContentAPI()
        callFetchDailyWisdomDataAPI()
        callFetchWellnessCategoryAPI()
        callActiveAlarmAPI()
    }

    private fun getSubscriptionStatus() {
        mViewModel.currentPageForYouCount = -1
        mViewModel.currentPageNewReleaseCount = -1
        mViewModel.currentPageHowToMeditateCount = -1
        mViewModel.howToMeditateDataList.clear()
        mViewModel.forYouDataList.clear()
        mViewModel.newReleasesDataList.clear()
        mViewModel.categoryDataList.clear()
        mViewModel.isSubscriptionStatusApiLoading = true

        subscriptionViewModel.getSubscriptionStatus(this, mDisposable)
    }

    private fun callFetchDailyWisdomDataAPI() {
        val currentDate: String = getCurrentDateWithFormatyyyyMMddFormat()
        mViewModel.fetchDailyWisdomDataAPI(currentDate, this, mDisposable)
    }

    private fun callActiveAlarmAPI() {
        mViewModel.getActiveAlarm(this, mDisposable)
    }

    @SuppressLint("SetTextI18n")
    private fun setUserData() {
        mBinding.apply {
            tvForYou.text =
                getString(R.string.for_you_with_comma) + " " + userHolder.firstName
            mToolbar.ivProfileImageToolbar.loadImageProfile(
                userHolder.originalProfilePicUrl,
                R.drawable.ic_profile_default
            )
        }
    }

    private fun setAdapter() {
        mBinding.rvForYou.adapter = forYouDataAdapter
        mBinding.rvNewRelease.adapter = newReleaseDataAdapter
        mBinding.rvHowToMeditate.adapter = howToMeditateDataAdapter

        if (!isTablet(requireActivity())) {
            mBinding.rvSelectCategoryExperienceMore.layoutManager =
                GridLayoutManager(requireContext(), 3)
        } else {
            mBinding.rvSelectCategoryExperienceMore.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        mBinding.rvSelectCategoryExperienceMore.adapter = wellnessCategoryAdapter
        mBinding.rvFeatureSession.adapter = featureSessionAdapter
        mBinding.rvUpcomingSession.adapter = upcomingSessionAdapter

        mBinding.rvWeeks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mWeekCalendarAdapter = WeekCalendarAdapter(mWeekDatesDayListModelDummy, this)
        mBinding.rvWeeks.adapter = mWeekCalendarAdapter
    }

    private fun callForYouContentAPI() {
        mViewModel.fetchForYouDataAPI(
            this,
            mDisposable
        )
    }

    private fun callNewReleaseContentAPI() {
        mViewModel.fetchNewReleaseDataAPI(
            this,
            mDisposable
        )
    }

    private fun callHowToMeditateContentAPI() {
        mViewModel.fetchHowToMeditateDataAPI(
            this,
            mDisposable
        )
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun observeApiCallbacks() {
        mViewModel.userLogoutLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    activity?.let {
                        cancelAllPreviousAlarm()
                        userHolder.clearUserHolder()
                        LoginManager.getInstance().logOut()
                        (it as MainActivity).notificationManager?.cancelAll()
                        val gso =
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .build()
                        val googleSignInClient =
                            GoogleSignIn.getClient(requireContext(), gso)
                        googleSignInClient.signOut()
                        mViewModel.deleteAllDownloads()
                        TaoCalligraphy.instance.getDownloadTracker()?.removeAllDownload()
                        cancelAllPreviousAlarm()
                        val intent = Intent(it, WelcomeActivity::class.java)
                        intent.addFlags(
                            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                    Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                        startActivity(intent)
                        it.finishAffinity()
                    }
                }
                longToastState(requestState.error)
            }
        }

        mViewModel.fetchCategoryDataResponse.observe(this@HomeFragment) { response ->
            response?.let { requestState ->
                requestState.apiResponse.let {
                    it?.let { data ->
                        userHolder.setWellnessCategoryData(data.data)
                        data.data?.list?.let { dataList ->
                            mViewModel.categoryDataList.clear()
                            mViewModel.categoryDataList.addAll(dataList)
                            wellnessCategoryAdapter.setCategoryData(mViewModel.categoryDataList)
                        }
                        mBinding.shimmerForYouWellnessFirstRow.gone()
                        mBinding.rvSelectCategoryExperienceMore.visible()
                    }
                }
                longToastState(requestState.error)

            }
        }


        mProfileViewModel.userModulePermissionData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { it ->
                    val enumSet = UserHolder.EnumUserModulePermission.values().toHashSet()
                    it.data?.let {
                        it.forEach { item ->
                            if (enumSet.any { enum -> enum.name == (item.moduleName ?: "") }) {
                                UserHolder.EnumUserModulePermission.valueOf(
                                    item.moduleName ?: ""
                                ).permission = item
                            }
                        }
                    }
                    val intent = Intent(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
                    LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
                }

                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR -> errorObj.customMessage?.let {
                            requireActivity().longToast(
                                it,
                                errorObj.type
                            )
                        }
                        Constants.CUSTOM_ERROR -> errorObj.customMessage?.let {
                            requireActivity().longToast(
                                it,
                                errorObj.type
                            )
                        }
                    }
                }

            }
        }
        mViewModel.fetchNewReleaseDataResponse.observe(this@HomeFragment) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { response ->

                    if (mViewModel.currentPageNewReleaseCount == 0) {
                        mViewModel.newReleasesDataList.clear()
                    }

                    response.data?.newRelease?.list?.let { list ->
                        mViewModel.totalNewReleaseCount =
                            response.data?.newRelease?.totalRecords?.toInt() ?: 0

                        mViewModel.newReleasesDataList.addAll(list)
                        isLoadingNewRelease = list.isEmpty()
                        setNewReleasesData()
                    } ?: kotlin.run {
                        if (mViewModel.currentPageNewReleaseCount == 0) {
                            mBinding.apply {
                                rvNewRelease.gone()
                                lblNoNewRelease.visible()
                            }
                        }
                    }
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchNewReleaseDataResponse.postValue(null)
        }

        mViewModel.fetchForYouDataResponse.observe(this@HomeFragment) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { response ->

                    if (mViewModel.currentPageForYouCount == 0) {
                        mViewModel.forYouDataList.clear()
                    }

                    response.data?.forYou?.list?.let { list ->
                        mBinding.forYouShadow?.visible()
                        mViewModel.totalForYouCount =
                            response.data?.forYou?.totalRecords?.toInt() ?: 0

                        mViewModel.forYouDataList.addAll(list)
                        isLoadingForYou = list.isEmpty()

                        setForYouData()

                    } ?: kotlin.run {
                        if (mViewModel.currentPageForYouCount == 0) {
                            mBinding.apply {
                                rvForYou.gone()
                                lblNoForYouData.visible()
                            }
                        }
                    }
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchForYouDataResponse.postValue(null)
        }

        mViewModel.fetchHowToMeditateDataResponse.observe(this@HomeFragment) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { response ->

                    if (mViewModel.currentPageHowToMeditateCount == 0) {
                        mViewModel.howToMeditateDataList.clear()
                    }

                    response.data?.hotToMeditate?.list?.let { list ->
                        mViewModel.totalHowToMeditateCount =
                            response.data?.forYou?.totalRecords?.toInt() ?: 0

                        mViewModel.howToMeditateDataList.addAll(list)
                        isLoadingHowToMeditate = list.isEmpty()

                        setHowToMeditateData()
                    }
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchHowToMeditateDataResponse.postValue(null)
        }

        mViewModel.fetchDailyWisdomDataResponse.observe(this@HomeFragment) { response ->
            response?.let { requestState ->
//                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { response ->
                    response.data?.let {
                        mBinding.apply {
                            tvAuthorName.text = it.personName
                            tvQuoteOfDay.text = it.text
                            tvLiveSessionCount.text = it.liveSessionCount
                            tvMeditationCount.text = it.numberOfMeditations
                            tvGoalCount.text =
                                it.goalPercentage + "% " + getString(R.string.of_goal)
                            circularProgressDailyActivityChart.progress =
                                it.goalPercentage!!.toFloat()

                            if (formatTimeInHHmmss(it.meditationTime).isEmpty()) {
                                tvTime.text = getString(R.string.zero_h_zero_m)
                            } else {
                                tvTime.text =
                                    formatTimeInHHmmss(it.meditationTime/*"13:40"*/)
                            }

                            tvDay.text = mWeekDatesDayListModelDummy[0].days
                        }

                        if (it.totalUnReadNotificationCount != null)
                            (requireActivity() as MainActivity).setNotificationCount(
                                it.totalUnReadNotificationCount ?: ""
                            )

                        if (isFromNotification) {
                            isFromNotification = false
                            scrollToView(mBinding.scrollView, mBinding.llDailyWisdom)
                        }
                    }
                }
                longToastState(requestState.error)
            }
        }

        mViewModel.activeAlarmResponse.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.data?.let { it ->
                    if (it.isWakeUpWithMeditation == false) {
                        cancelAllPreviousAlarm()
                    } else {
                        if ((userHolder.getAlarmData() != null) and (it.contentId != null)) {
                            val alarmData = userHolder.getAlarmData()
                            if (alarmData?.contentId != it.contentId) {
                                (requireActivity() as MainActivity).setAlarmInManager(it)
                            } else if (alarmData?.time != it.time) {
                                (requireActivity() as MainActivity).setAlarmInManager(it)
                            } else if (alarmData?.repeatDays?.size != it.repeatDays?.size) {
                                (requireActivity() as MainActivity).setAlarmInManager(it)
                            } else if (alarmData?.repeatDays?.size == it.repeatDays?.size) {
                                alarmData?.repeatDays?.let { alarmList ->
                                    it.repeatDays?.let { repeatDays ->
                                        if ((!alarmList.containsAll(repeatDays)) and (!repeatDays.containsAll(
                                                alarmList
                                            ))
                                        ) {
                                            (requireActivity() as MainActivity).setAlarmInManager(it)
                                        }
                                    }
                                }
                            }
                        } else if ((userHolder.getAlarmData() == null) and (it.contentId != null)) {
                            (requireActivity() as MainActivity).setAlarmInManager(it)
                        }
                    }
                    userHolder.setAlarmData(it)
                }
                longToastState(requestState.error)
            }
        }

        subscriptionViewModel.subscriptionStatusLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.data?.let { data ->
                    TaoCalligraphy.instance.isSubscriptionStatusApiCalled = true
                    userHolder.isSubscribed = data.isSubscribed ?: false
                    getAllData()
                    mViewModel.isSubscriptionStatusApiLoading = false
                    subscriptionViewModel.subscriptionStatusLiveData.postValue(null)
                }
//                longToastState(requestState.error)
            }
        }
    }


    private fun callFetchWellnessCategoryAPI() {
        mViewModel.fetchAllCategoryData(this@HomeFragment, mDisposable)
    }

    @SuppressLint("SimpleDateFormat")
    private fun find7DatesFromToday() {
        mWeekDatesDayListModelDummy.clear()
        val today = DateTime().withTimeAtStartOfDay()
        val formatterDate: DateTimeFormatter =
            DateTimeFormat.forPattern("dd") //"dd/MM/yyyy"
        val formatterDay: DateTimeFormatter = DateTimeFormat.forPattern("EEE")
        for (i in 0..6) {
            val mDate = formatterDate.print(today.plusDays(i))
            val mDay = formatterDay.print(today.plusDays(i))
            mWeekDatesDayListModelDummy.add(
                WeekDatesDayListModel(
                    mDate,
                    mDay,
                    (i % 2 == 0)
                )
            )
        }
    }

    private fun setVisibilityBasedOnConnection(isConnected: Boolean) {
        if (isConnected) {
            mBinding.llOnlineContent.visible()
            mBinding.llOfflineContent.gone()
        } else {
            mBinding.llOnlineContent.gone()
            mBinding.llOfflineContent.visible()
            if (!userHolder.canAccessDownload) {
                mBinding.ivLock.visible()
                mBinding.ivThumbImage.gone()
            } else {
                mBinding.ivLock.gone()
                mBinding.ivThumbImage.visible()
            }
        }
    }

    private fun setupToolbar() {
        mBinding.mToolbar.cardProfile.visible()
        mBinding.mToolbar.ivBackToolbar.gone()
    }

    private fun setSubscriptionUI() {
        if (UserHolder.EnumUserModulePermission.SEE_PERSONAL_STATS.permission?.canAccess ?: false) {
            mBinding.ivStatsLock.gone()
            mBinding.ivStatsArrow.visible()
            mBinding.tvDailyActivitySeeAll.alpha = 1f
        } else {
            mBinding.ivStatsLock.visible()
            mBinding.ivStatsArrow.gone()
            mBinding.tvDailyActivitySeeAll.alpha = 0.5f
        }

        if (UserHolder.EnumUserModulePermission.CONTENT_LIBRARY.permission?.canAccess ?: false) {
            mBinding.rvSelectCategoryExperienceMore.isEnabled = true
            mBinding.rvSelectCategoryExperienceMore.alpha = 1f
            mBinding.ivWellnessTitleLock.gone()
        } else {
            mBinding.rvSelectCategoryExperienceMore.isEnabled = false
            mBinding.rvSelectCategoryExperienceMore.alpha = 0.5f
            mBinding.ivWellnessTitleLock.visible()
        }

        if (UserHolder.EnumUserModulePermission.SEARCH.permission?.canAccess ?: false) {
            mBinding.tvSeeAllWellness.isEnabled = true
            mBinding.tvSeeAllWellness.alpha = 1f
            mBinding.ivWellnessSeeAllArrow.visible()
            mBinding.ivWellnessLock.gone()
        } else {
            mBinding.tvSeeAllWellness.isEnabled = false
            mBinding.tvSeeAllWellness.alpha = 0.5f
            mBinding.ivWellnessSeeAllArrow.gone()
            mBinding.ivWellnessLock.visible()
        }

        if (mViewModel.forYouDataList.isNotEmpty()) {
            forYouDataAdapter.setMeditationData(mViewModel.forYouDataList)
        }

        if (mViewModel.newReleasesDataList.isNotEmpty()) {
            newReleaseDataAdapter.setMeditationData(mViewModel.newReleasesDataList)
        }
    }

    override fun postInit() {
    }

    override fun initObserver() {
    }

    override fun handleListener() {
        mBinding.rvNewRelease.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvNewRelease.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvNewRelease.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if (totalItem < mViewModel.totalNewReleaseCount && (!isLoadingNewRelease) &&
                    ((totalItem == (lastVisibleItem + 2)) || (totalItem == (lastVisibleItem + 3)))
                ) {
                    isLoadingNewRelease = true
                    callNewReleaseContentAPI()
                }
            }
        })

        mBinding.rvForYou.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvForYou.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvForYou.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if (totalItem < mViewModel.totalForYouCount && (!isLoadingForYou) &&
                    ((totalItem == (lastVisibleItem + 2)) || (totalItem == (lastVisibleItem + 3)))
                ) {
                    isLoadingForYou = true
                    callForYouContentAPI()
                }
            }

            // uncomment this code when need to hide shadow when list is scroll to end
            // do the same for other list too

            /*override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollHorizontally(0)) {
                    mBinding.forYouShadow?.gone()
                }
            }*/
        })

        mBinding.rvHowToMeditate.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvHowToMeditate.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvHowToMeditate.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if (totalItem < mViewModel.totalHowToMeditateCount && (!isLoadingHowToMeditate) &&
                    ((totalItem == (lastVisibleItem + 2)) || (totalItem == (lastVisibleItem + 3)))
                ) {
                    isLoadingHowToMeditate = true
                    callHowToMeditateContentAPI()
                }
            }
        })

        mBinding.tvSeeAllHowToMeditate.setOnClickListener {
            (requireActivity() as MainActivity).hideWellnessDialog()
            HowToMeditateActivity.startActivity(requireActivity() as AppCompatActivity)
        }

        mBinding.btnViewDownloads.clickWithDebounce {
            if (!userHolder.canAccessDownload) {
                return@clickWithDebounce
            }

            (requireActivity() as MainActivity).hideWellnessDialog()
            DownloadsActivity.startActivity(requireActivity() as AppCompatActivity)
        }

        mBinding.btnReconnectNow.clickWithDebounce {
            //TaoCalligraphy.instance.checkNetworkConnection()
        }

        mBinding.mToolbar.cardProfile.clickWithDebounce {
            (requireActivity() as MainActivity).hideWellnessDialog()
            UserMenuActivity.startActivity(activity as MainActivity)
        }

        mBinding.tvSeeAllForYou.clickWithDebounce {
            (requireActivity() as MainActivity).hideWellnessDialog()
            SeeAllActivity.startActivity(
                activity as MainActivity,
                getString(R.string.for_you),
                Constants.forYou
            )
        }

        mBinding.tvSeeAllNewReleases.clickWithDebounce {
            (requireActivity() as MainActivity).hideWellnessDialog()
            SeeAllActivity.startActivity(
                activity as MainActivity,
                getString(R.string.new_release),
                Constants.newRelease
            )
        }
        mBinding.llWellnessSeeAll.clickWithDebounce {
            if (UserHolder.EnumUserModulePermission.SEARCH.permission == null) {
                return@clickWithDebounce
            }

            if (UserHolder.EnumUserModulePermission.SEARCH.permission?.canAccess ?: false) {
                (requireActivity() as MainActivity).hideWellnessDialog()
                (requireActivity() as MainActivity).setSearchTabSelected()
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.container, (requireActivity() as MainActivity).searchFragment)
                    ?.commit()
            } else {
                SubscriptionActivity.startActivityForResult(requireActivity() as AppCompatActivity)
            }
        }
        mBinding.tvStats.clickWithDebounce {
            if (UserHolder.EnumUserModulePermission.SEE_PERSONAL_STATS.permission == null) {
                return@clickWithDebounce
            }

            if (UserHolder.EnumUserModulePermission.SEE_PERSONAL_STATS.permission?.canAccess
                    ?: false
            ) {
                (requireActivity() as MainActivity).hideWellnessDialog()
                StatisticsActivity.startActivity(activity as MainActivity)
            } else {
                SubscriptionActivity.startActivityForResult(requireActivity() as AppCompatActivity)
            }

        }

    }

    override fun onItemCategoryClick(position: Int) {
        if (!(UserHolder.EnumUserModulePermission.CONTENT_LIBRARY.permission?.canAccess ?: false)) {
            return
        }
        (activity as MainActivity).setWellNessFragment(position)
    }

    override fun onSessionClick() {
    }

    override fun onWeekAdapterClick(weekDatesDayListModel: WeekDatesDayListModel) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        setVisibilityBasedOnConnection(requireActivity().isNetwork())
        wellnessCategoryAdapter.selectedPosition = -1
        wellnessCategoryAdapter.notifyDataSetChanged()
        setUserData()
        callFetchDailyWisdomDataAPI()

        if (!TaoCalligraphy.instance.isSubscriptionStatusApiCalled && !mViewModel.isSubscriptionStatusApiLoading)
            onRefresh()

        if (UserHolder.EnumUserModulePermission.CONTENT_LIBRARY.permission?.canAccess != null)
            setSubscriptionUI()
    }


    override fun onStop() {
        super.onStop()
        TaoCalligraphy.instance.isHomeFragmentVisible = false
    }

    override fun unauthorized(error: String?) {
        com.app.taocalligraphy.utils.extensions.logInfo(msg = "unauthorized == ")
        WelcomeLoginActivity.startActivity(requireActivity() as AppCompatActivity)
        WelcomeLoginActivity.isFromLogin = true
        requireActivity().finishAffinity()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
        if (model.isAvailable) {
            setVisibilityBasedOnConnection(true)
            if (mViewModel.isDataLoaded()) {
                onRefresh()
            } else {
                setNewReleasesData()
                setForYouData()
                setHowToMeditateData()
                wellnessCategoryAdapter.setCategoryData(mViewModel.categoryDataList)
            }
        } else {
            (requireActivity() as MainActivity).hideWellnessDialog()
            if (isAdded)
                setVisibilityBasedOnConnection(false)
        }
    }

    private fun scrollToView(scrollView: NestedScrollView, childView: View) {
        val delay: Long = 1500 //delay to let finish with possible modifications to ScrollView
        scrollView.postDelayed({ scrollView.smoothScrollTo(0, childView.top - 250) }, delay)
    }


    private fun setForYouData() {
        mBinding.shimmerforYou.hideShimmer()
        mBinding.shimmerforYou.gone()
        mBinding.rvForYou.visible()

        if (mViewModel.forYouDataList.size > 0) {
            mBinding.apply {
                lblNoForYouData.gone()
            }
        } else {
            mBinding.apply {
                rvForYou.gone()
                lblNoForYouData.visible()
            }
        }

        forYouDataAdapter.setMeditationData(mViewModel.forYouDataList)
    }

    private fun setNewReleasesData() {
        mBinding.shimmerNewRelease.hideShimmer()
        mBinding.shimmerNewRelease.gone()
        mBinding.rvNewRelease.visible()

        if (mViewModel.newReleasesDataList.size > 0) {
            mBinding.apply {
                lblNoNewRelease.gone()
            }
        } else {
            mBinding.apply {
                rvNewRelease.gone()
                lblNoNewRelease.visible()
            }
        }

        newReleaseDataAdapter.setMeditationData(mViewModel.newReleasesDataList)
    }

    private fun setHowToMeditateData() {
        mBinding.shimmerHowToMeditate.hideShimmer()
        mBinding.shimmerHowToMeditate.gone()
        mBinding.rvHowToMeditate.visible()

        if (mViewModel.howToMeditateDataList.size > 0) {
            mBinding.apply {
                rvHowToMeditate.visible()
                tvSeeAllHowToMeditate.visible()
                lblNoHowToMeditateData.gone()
            }
        } else {
            mBinding.apply {
                rvHowToMeditate.gone()
                tvSeeAllHowToMeditate.gone()
                lblNoHowToMeditateData.visible()
            }
        }

        howToMeditateDataAdapter.setMeditationData(mViewModel.howToMeditateDataList)
    }

    override fun onHowToMeditateClicked(dataModel: HowToMeditateDataModel) {
        (requireActivity() as MainActivity).hideWellnessDialog()
        NavigationHandler.handleHowToMeditateNavigation(
            dataModel,
            requireActivity() as AppCompatActivity,
            startSubscriptionActivityForResult
        )
    }

    override fun onMultiSessionClicked(dataModel: ProgramDataModel) {
        (requireActivity() as MainActivity).hideWellnessDialog()
        NavigationHandler.handleNavigation(
            dataModel,
            requireActivity() as AppCompatActivity,
            startSubscriptionActivityForResult
        )
    }

    override fun onFavouriteClicked(dataModel: ProgramDataModel, isForYou: Boolean) {
        (requireActivity() as MainActivity).hideWellnessDialog()
        if (dataModel.type == Constants.content) {
            // call fav content api
            mViewModel.favoriteMeditationContent(
                dataModel.id ?: "",
                this@HomeFragment,
                mDisposable
            )
        } else {
            // call fav program api
            mViewModel.setProgramFavorite(
                dataModel.id!!,
                false,
                this@HomeFragment,
                mDisposable
            )
        }

        if (isForYou) {
            mViewModel.newReleasesDataList.filter { it?.id == dataModel.id }.let {
                if (it.isNotEmpty()) {
                    it.first()?.isFavorites = !it.first()?.isFavorites!!
                    newReleaseDataAdapter.notifyItemChanged(
                        mViewModel.newReleasesDataList.indexOf(it.first())
                    )
                }
            }
        } else {
            mViewModel.forYouDataList.filter { it?.id == dataModel.id }.let {
                if (it.isNotEmpty()) {
                    it.first()?.isFavorites = !it.first()?.isFavorites!!
                    forYouDataAdapter.notifyItemChanged(
                        mViewModel.forYouDataList.indexOf(it.first())
                    )
                }
            }
        }
    }
}