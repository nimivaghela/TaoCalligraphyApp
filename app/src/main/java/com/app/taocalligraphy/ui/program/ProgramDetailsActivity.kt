package com.app.taocalligraphy.ui.program

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityProgramDetailsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.program.UserProgramApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.meditation.dialog.PaidSessionTwoFieldDialog
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.TabsSelectSingleAdapter
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.program.fragment.ProgramsAboutFragment
import com.app.taocalligraphy.ui.program.fragment.ProgramsProgramFragment
import com.app.taocalligraphy.ui.program.fragment.ProgramsProgressFragment
import com.app.taocalligraphy.ui.program.viewmodel.ProgramViewModel
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageWithAnimate
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


@AndroidEntryPoint
class ProgramDetailsActivity : BaseActivity<ActivityProgramDetailsBinding>(),
    TabsSelectSingleAdapter.SingleSelectItemClickListener,
    PaidSessionTwoFieldDialog.OnPayHeartListener,
    PaidSessionTwoFieldDialog.OnPayAmountListener {

    override fun getResource() = R.layout.activity_program_details

    private val programId by lazy {
        return@lazy intent.extras?.getString(Constants.Param.programId, "") ?: ""
    }

    private val isFromHistoryCompletedProgram by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromHistoryCompletedProgram, false)
            ?: false
    }

    private val isFromNotification by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromNotification, false) ?: false
    }

    private val isFromQuestionnaires by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromQuestionnaires, false) ?: false
    }

    private val mViewModel: ProgramViewModel by viewModels()
    private var mTabsList = mutableListOf<String>()
    private var mTabsSelectSingleAdapter: TabsSelectSingleAdapter? = null
    private lateinit var mProgramsProgramFragment: ProgramsProgramFragment
    private lateinit var mProgramsAboutFragment: ProgramsAboutFragment
    private lateinit var mProgramsProgressFragment: ProgramsProgressFragment
    private var selectedTabPos = 0

    companion object {
        var daysList: UserProgramApiResponse.Days? = null
        fun startActivity(
            activity: Activity,
            programId: String,
            isFromHistoryCompletedProgram: Boolean,
            isFromQuestionnaires: Boolean = false
        ) {
            val intent = Intent(activity, ProgramDetailsActivity::class.java)
            intent.putExtra(Constants.Param.programId, programId)
            intent.putExtra(
                Constants.Param.isFromHistoryCompletedProgram,
                isFromHistoryCompletedProgram
            )
            intent.putExtra(Constants.Param.isFromQuestionnaires, isFromQuestionnaires)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun initView() {

        mBinding.tvProgramName.isSelected = true

        if (isNetwork() && mViewModel.userProgramList == null) {
            mViewModel.getUserProgramApi(
                programId,
                isFromHistoryCompletedProgram,
                this,
                mDisposable
            )
        } else {
            setFavouriteData(false)
            mViewModel.userProgramList?.let {
                setTabs(it)
                daysList = it.daysList?.get(mViewModel.selectedDayPos)
                setUi(it)
            }
        }

        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            onRefresh()

            mBinding.swipeToRefreshLayout.isRefreshing = false
        }

        if (isTablet(this)) {
            ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(0, 0, 0, insets.bottom)
                WindowInsetsCompat.CONSUMED
            }
        }

        LocalBroadcastManager.getInstance(this@ProgramDetailsActivity).registerReceiver(
            mSubscriptionReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
        )

        LocalBroadcastManager.getInstance(this@ProgramDetailsActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )

        if (mViewModel.isFromConfigChanges)
            setBannerData()
    }

    private fun onRefresh() {
        mViewModel.selectedDayPos = 0
        mViewModel.userProgramList = null
        mViewModel.programContentList.clear()
        mViewModel.programProgressList.clear()
        if (isNetwork())
            mViewModel.getUserProgramApi(
                programId,
                isFromHistoryCompletedProgram,
                this,
                mDisposable
            )
    }

    private val mSubscriptionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            onRefresh()
        }
    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            setTabBar()
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this@ProgramDetailsActivity)
            .unregisterReceiver(mSubscriptionReceiver)
        LocalBroadcastManager.getInstance(this@ProgramDetailsActivity)
            .unregisterReceiver(mAccessLevelReceiver)

        mViewModel.isFromConfigChanges = isChangingConfigurations
        super.onDestroy()
    }

    fun setTabBar() {
        if (((mViewModel.userProgramList?.subscription?.isAccessible
                ?: true) == true && mViewModel.userProgramList?.isPaidContent == true && mViewModel.userProgramList?.isPurchased == false) || (mViewModel.userProgramList?.subscription?.isAccessible
                ?: true) == false
        ) {
            mBinding.cardFav.gone()
        } else {
            if (isNetwork()) {
                if (UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess
                        ?: true
                ) {
                    mBinding.cardFav.visible()
                    mBinding.cardFav.isEnabled = true
                    mBinding.cardFav.isClickable = true
                    mBinding.cardFav.alpha = 0.8f
                } else {
                    mBinding.cardFav.visible()
                    mBinding.cardFav.isEnabled = false
                    mBinding.cardFav.isClickable = false
                    mBinding.cardFav.alpha = 0.5f
                }
            }
        }

    }

    override fun onPause() {
        handler.removeCallbacks(runnableImage)
        super.onPause()
    }

    fun showProgramFeedbackScreen() {
        mViewModel.userProgramList?.let {
            if (it.isProgramJoined == true && !mViewModel.isFeedbackScreenVisible &&
                !it.isPostAssessmentCompleted && !it.isRatingSkipped
            ) {
                if (getCurrentDate().after(
                        dateFormatter_yyyy_mm_dd.parse(
                            it.daysList?.last()?.programDate ?: ""
                        )
                    )
                ) {
                    mViewModel.isFeedbackScreenVisible = true
                    ProgramFeedbackActivity.startActivity(
                        this,
                        it.programId ?: 0,
                        it.title ?: "",
                        isFromHistoryCompletedProgram
                    )
                } else if (mViewModel.isLastContentPlayed && it.daysList?.last()?.programDate == dateFormatter_yyyy_mm_dd.format(
                        Calendar.getInstance().time
                    ) && !it.isRatingSkipped
                ) {
                    mViewModel.isFeedbackScreenVisible = true
                    ProgramFeedbackActivity.startActivity(
                        this,
                        it.programId ?: 0,
                        it.title ?: "",
                        isFromHistoryCompletedProgram
                    )
                }
            }
        }
    }

    private var bannerList = ArrayList<String>()

    fun setBannerData() {
        if (mViewModel.bannerList.isNotEmpty()) {
            imageIndex = 0
            bannerList = mViewModel.bannerList
            handler.removeCallbacks(runnableImage)
            handler.postDelayed(runnableImage, 1000)
        }
    }

    private fun setTabs(data: UserProgramApiResponse) {
        mProgramsProgramFragment = ProgramsProgramFragment.newInstance(
            data,
            mViewModel.isProgramJoined,
            isFromHistoryCompletedProgram,
            isFromQuestionnaires
        )

        mProgramsProgressFragment = ProgramsProgressFragment.newInstance(
            programId,
            isFromHistoryCompletedProgram
        )

        mProgramsAboutFragment = ProgramsAboutFragment.newInstance(data)

        when (mViewModel.selectedTab) {
            getString(R.string.program_capital) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mProgramsProgramFragment)
                    .commitAllowingStateLoss()
                selectedTabPos = 0
            }
            getString(R.string.progress) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mProgramsProgressFragment)
                    .commitAllowingStateLoss()
                selectedTabPos = 1
            }
            getString(R.string.about_capital) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mProgramsAboutFragment)
                    .commitAllowingStateLoss()
                selectedTabPos = 2
            }
        }

        if (mViewModel.isProgramJoined) {
            mTabsList.clear()
            mTabsList.add(getString(R.string.program_capital))
            mTabsList.add(getString(R.string.progress))
            mTabsList.add(getString(R.string.about_capital))
        } else {
            mTabsList.clear()
            mTabsList.add(getString(R.string.program_capital))
            mTabsList.add(getString(R.string.about_capital))

            selectedTabPos = if (selectedTabPos == 2) 1 else selectedTabPos
        }
        mTabsSelectSingleAdapter =
            TabsSelectSingleAdapter(this, mTabsList, this, selectedTabPos)
        mBinding.rvProgramsTab.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvProgramsTab.adapter = mTabsSelectSingleAdapter
    }

    override fun observeApiCallbacks() {
        mViewModel.getUserProgramListLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { data ->
                    mViewModel.userProgramList = data
                    mViewModel.isProgramJoined = data.isProgramJoined == true
                    setFavouriteData(false)
                    setTabs(data)
                    daysList = data.daysList?.get(mViewModel.selectedDayPos)

                    setUi(data)
                }
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
            mViewModel.getUserProgramListLiveData.postValue(null)
        }

        mViewModel.userProgressListLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    longToast(it.message.toString(), it.type ?: SUCCESS)
                    if (isNetwork() && !mViewModel.isProgramJoined)
                        mViewModel.getUserProgramApi(
                            programId,
                            isFromHistoryCompletedProgram,
                            this,
                            mDisposable
                        )
                }
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
                                Constants.StatusCode.STATUS_409 -> {
                                    initView()
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
            }

            mViewModel.userProgressListLiveData.postValue(null)
        }

        mViewModel.favouriteMeditationContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { it ->
                    mViewModel.userProgramList?.isFavorite =
                        mViewModel.userProgramList?.isFavorite != true
                    setFavouriteData(true)
                }
                longToastState(requestState.error)
            }
        }
    }

    private fun setUi(data: UserProgramApiResponse) {
        mBinding.tvProgramName.text = data.title

        data.averageRatingCount?.let { rating ->
            mBinding.rbRating.rating = rating.toFloat()
        } ?: kotlin.run {
            mBinding.rbRating.rating = 0f
        }

        mBinding.tvRateCount.text = data.totalRatingCount.toString()
        mBinding.tvTotalUserJoinedCount.text = data.totalUserJoinedCount.toString()
        data.completedDay?.let {
            if (data.completedDay!! <= 0) {
                mBinding.circularProgressDailyActivityChart.progress = 0f
                mBinding.tvNotStarted.text = getString(R.string.program_not_started)
            } else {
                mBinding.tvNotStarted.text = getString(
                    R.string.program_completed_days,
                    data.completedDay.toString(),
                    data.totalDays.toString()
                )
                val progressDays =
                    ((100 * data.completedDay!!) / data.totalDays!!).toFloat()
                mBinding.circularProgressDailyActivityChart.progress = progressDays
            }
        } ?: kotlin.run {
            mBinding.circularProgressDailyActivityChart.progress = 0f
            mBinding.tvNotStarted.text = getString(R.string.program_not_started)
        }

        if (data.canRejoin && !isFromHistoryCompletedProgram) {
            mViewModel.isProgramJoined = false
            mBinding.cvJoinPrograms.visible()
//            mBinding.btnJoinPrograms.visible()
            mBinding.btnJoinPrograms.text = getString(R.string.program_rejoin)
            mBinding.btnGetMeditation.gone()
        } else if (data.isProgramJoined == true) {
//            mBinding.btnJoinPrograms.gone()
            mBinding.cvJoinPrograms.gone()
            mBinding.btnGetMeditation.gone()
        } else if (data.isProgramJoined == false) {

            if ((data?.subscription?.isAccessible
                    ?: true) == true && data?.isPaidContent == true && data?.isPurchased == false
            ) {
//                    GET
//                mBinding.btnJoinPrograms.gone()
                mBinding.cvJoinPrograms.gone()

                mBinding.btnGetMeditation.visible()
                mBinding.btnGetMeditation.text = getString(R.string.get)
                mBinding.btnGetMeditation.icon = getDrawable(R.drawable.vd_arrow_up_white)
                mBinding.btnGetMeditation.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END
                mBinding.btnGetMeditation.iconTint = ColorStateList.valueOf(getColor(R.color.white))

            } else if ((data?.subscription?.isAccessible ?: true) == false) {
//                        Subscribe
//                mBinding.btnJoinPrograms.gone()
                mBinding.cvJoinPrograms.gone()
                mBinding.btnGetMeditation.visible()
                mBinding.btnGetMeditation.text = getString(R.string.subscribe)
                mBinding.btnGetMeditation.icon = null
                mBinding.btnGetMeditation.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END
                mBinding.btnGetMeditation.iconTint = ColorStateList.valueOf(getColor(R.color.white))
            } else {
//                    can access
//                mBinding.btnJoinPrograms.visible()
                mBinding.cvJoinPrograms.visible()
                mBinding.btnGetMeditation.gone()
            }

        }
        setTabBar()
    }

    private fun setFavouriteData(shouldPlayAnimation: Boolean) {
        mViewModel.userProgramList?.let {
            setMeditationFavouriteStatus(
                mBinding.lottieLike,
                it.isFavorite,
                shouldPlayAnimation
            )
        }
    }

    override fun handleListener() {
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.btnJoinPrograms.setOnClickListener {
            if (isNetwork())
                mViewModel.userProgramApi(programId, this, mDisposable)
            else
                longToast(
                    getString(
                        R.string.no_internet,
                        getString(R.string.to_get_program_details)
                    ), ERROR
                )
        }

        mBinding.btnGetMeditation.setOnClickListener {
            mViewModel.userProgramList?.let { programList ->
                if (programList.subscription?.isAccessible == true && programList.isPaidContent == true && programList.isPurchased == false) {
                    val dialog =
                        PaidSessionTwoFieldDialog(
                            this,
                            programList.isUnlockWithHearts!!,
                            if (programList.currencies!!.size > 0) programList.currencies?.get(0) else null,
                            programList.heartDetails?.requiredDiamondHearts ?: 0,
                            this@ProgramDetailsActivity,
                            this
                        )
                    dialog.show()
                } else if (programList.subscription?.isAccessible == false) {
                    SubscriptionActivity.startActivityForResult(
                        this@ProgramDetailsActivity
                    )
                }
            }
        }

        mBinding.cardFav.setOnClickListener {
            mViewModel.setProgramFavorite(
                programId,
                isFromHistoryCompletedProgram,
                this,
                mDisposable
            )
        }
    }

    override fun onSingleItemClick(tabText: String, selectedPos: Int) {
        when (tabText) {
            getString(R.string.program_capital) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mProgramsProgramFragment)
                    .commitAllowingStateLoss()
                selectedTabPos = 0
                mViewModel.selectedTab = getString(R.string.program_capital)
            }
            getString(R.string.progress) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mProgramsProgressFragment)
                    .commitAllowingStateLoss()
                selectedTabPos = 1
                mViewModel.selectedTab = getString(R.string.progress)
            }
            getString(R.string.about_capital) -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.root_container, mProgramsAboutFragment)
                    .commitAllowingStateLoss()
                selectedTabPos = 2
                mViewModel.selectedTab = getString(R.string.about_capital)
            }
        }
    }

    private var imageIndex = 0
    val handler = Handler(Looper.getMainLooper())
    private val runnableImage = object : Runnable {
        override fun run() {
            if (bannerList.isNotEmpty())
                mBinding.ivBannerImages.loadImageWithAnimate(
                    bannerList[imageIndex],
                    placeHolder = R.drawable.img_default_for_content,
                    true
                )
            if (bannerList.size > 1) {
                if (bannerList.size - 1 > imageIndex)
                    imageIndex++
                else
                    imageIndex = 0
                handler.postDelayed(this, 3000)
            }
        }
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null && !error.lowercase()
                .contains("Program is already joined".lowercase(), true)
        ) {
            longToast(error, ERROR)
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

    override fun onPayAmountClicked() {
        mViewModel.userProgramList?.isPurchased = true
        setUi(mViewModel.userProgramList!!)
//        mBinding.btnJoinPrograms.visible()
//        mBinding.btnGetMeditation.gone()
    }

    override fun onPayHeartClicked() {
        mViewModel.userProgramList?.isPurchased = true
        setUi(mViewModel.userProgramList!!)
//        mBinding.btnJoinPrograms.visible()
//        mBinding.btnGetMeditation.gone()
    }
}