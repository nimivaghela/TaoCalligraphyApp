package com.app.taocalligraphy.ui.history

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityHistoryBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.history.FetchMeditationHistoryData
import com.app.taocalligraphy.models.response.history.FetchProgramHistoryData
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.history.adapter.*
import com.app.taocalligraphy.ui.history.dialog.LightTransmissionOrderDetailDialog
import com.app.taocalligraphy.ui.history.viewModel.HistoryViewModel
import com.app.taocalligraphy.ui.meditation.MeditationDetailActivity
import com.app.taocalligraphy.ui.program.ProgramDetailsActivity
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@AndroidEntryPoint
class HistoryActivity : BaseActivity<ActivityHistoryBinding>(),
    CompletedProgramsHistoryAdapter.OnCompletedProgramItemClickListener,
    InProgressProgramsHistoryAdapter.OnInProgressProgramItemClickListener,
    MeditationsHistoryAdapter.OnMeditationItemClickListener,
    LightTransmissionsHistoryAdapter.LightTransmissionsItemClickListener {

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, HistoryActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    // Note : selectedTabType types like Programs/Challenges/LiveSessions/Meditations/LightTransmissions

    private val mViewModel: HistoryViewModel by viewModels()
    override fun getResource() = R.layout.activity_history

    private val inProgressProgramsHistoryAdapter by lazy {
        return@lazy InProgressProgramsHistoryAdapter(this)
    }

    private val completedProgramsHistoryAdapter by lazy {
        return@lazy CompletedProgramsHistoryAdapter(this@HistoryActivity)
    }

    private val challengesHistoryAdapter by lazy {
        return@lazy ChallengesHistoryAdapter()
    }

    private val liveSessionsHistoryAdapter by lazy {
        return@lazy LiveSessionsHistoryAdapter(this)
    }

    private val meditationsHistoryAdapter by lazy {
        return@lazy MeditationsHistoryAdapter(this)
    }

    private val lightTransmissionsHistoryAdapter by lazy {
        return@lazy LightTransmissionsHistoryAdapter(this)
    }

    override fun initView() {
        setupToolbar()
        updateProfile()

        when (mViewModel.selectedTabType) {
            Constants.programs -> {
                mBinding.llMeditations.gone()
                mBinding.rlPrograms.visible()
                mBinding.tvCountMeditation.gone()
                mBinding.nestedScroll.visible()

                mViewModel.selectedTabType = Constants.programs
                changeTabBackground(mBinding.tvPrograms)
                setProgramsHistoryAdapter()
                setShowPastVisibility(false)

                hideShowUIAsPerType(true)
                setProgramsHistoryAdapter()

                if (mViewModel.inProgressProgram.isEmpty()) {
                    callInProgressProgramHistoryAPI()
                } else {
                    setInProgressProgramData()
                }

                if (mViewModel.completedProgram.isEmpty()) {
                    callCompletedProgramHistoryAPI()
                } else {
                    setCompletedProgramData()
                }
            }
            Constants.challenges -> {

            }
            Constants.live_sessions -> {

            }
            Constants.meditations -> {
                mBinding.rlPrograms.gone()
                mBinding.llMeditations.visible()
                mBinding.tvCountMeditation.visible()
                mBinding.nestedScroll.gone()
                hideShowUIAsPerType(false)
                changeTabBackground(mBinding.tvMeditation)
                setMeditationsHistoryAdapter()
                setShowPastVisibility(true)
                mBinding.tvNoProgressFoundMeditation.gone()
                setMeditationProgressImage(mViewModel.currentProgress)

                if (mViewModel.meditationList.isEmpty()) {
                    callMeditationHistoryAPI()
                } else {
                    setMediationData()
                }
            }
            Constants.light_transmissions -> {

            }
        }

        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            mBinding.swipeToRefreshLayout.isRefreshing = false
            mViewModel.currentPageCompletedProgram = -1
            mViewModel.currentPageInProgressProgram = -1
            when (mViewModel.selectedTabType) {
                Constants.programs -> {
                    callInProgressProgramHistoryAPI()
                    callCompletedProgramHistoryAPI()
                }
                Constants.challenges -> {

                }
                Constants.live_sessions -> {

                }
                Constants.meditations -> {
                    mViewModel.currentPageMeditation = -1
                    callMeditationHistoryAPI()
                }
                Constants.light_transmissions -> {

                }
            }
        }
    }

    private fun callMeditationHistoryAPI() {
        mViewModel.currentPageMeditation += 1

        mViewModel.fetchMeditationHistoryAPI(
            this@HistoryActivity,
            mDisposable
        )
    }

    private fun callInProgressProgramHistoryAPI() {
        mViewModel.currentPageInProgressProgram += 1
        mViewModel.programType = Constants.inProgress
        mViewModel.fetchInProgressProgramHistoryAPI(
            this@HistoryActivity,
            mDisposable
        )
    }

    private fun callCompletedProgramHistoryAPI() {
        mViewModel.currentPageCompletedProgram += 1
        mViewModel.programType = Constants.completed

        mViewModel.fetchForYouProgramHistoryAPI(
            this@HistoryActivity,
            mDisposable
        )
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

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    @SuppressLint("SetTextI18n")
    override fun observeApiCallbacks() {
        mViewModel.fetchCompletedProgramHistoryData.observe(this) { requestState ->
            requestState?.let {
                showProgressIndicator(mBinding.llProgress, requestState.progress)

                if (mViewModel.currentPageCompletedProgram == 0)
                    mViewModel.completedProgram = arrayListOf()

                requestState.apiResponse?.data?.forYouProgramList?.list?.let { dataList ->
                    mViewModel.completedProgram.addAll(dataList)
                    mViewModel.completedProgramTotalCount =
                        requestState.apiResponse?.data?.forYouProgramList?.totalRecords ?: 0
                    setCompletedProgramData()
                }

                longToastState(requestState.error)
            }
            mViewModel.fetchCompletedProgramHistoryData.postValue(null)
        }

        mViewModel.fetchInProgressProgramHistoryData.observe(this) { requestState ->
            requestState?.let {
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                mBinding.tvCountMeditation.gone()
                requestState.apiResponse?.data?.inProgressProgramsList?.list?.let { dataList ->
                    if (mViewModel.currentPageInProgressProgram == 0)
                        mViewModel.inProgressProgram = arrayListOf()

                    mViewModel.inProgressProgramTotalCount =
                        requestState.apiResponse?.data?.inProgressProgramsList?.totalRecords ?: 0
                    mViewModel.inProgressProgram.addAll(dataList)
                    setInProgressProgramData()
                }

                longToastState(requestState.error)
            }
            mViewModel.fetchInProgressProgramHistoryData.postValue(null)
        }

        mViewModel.fetchMeditationHistoryData.observe(this) { requestState ->
            requestState?.let {
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.list?.let { dataList ->

                    if (mViewModel.currentPageMeditation == 0)
                        mViewModel.meditationList = arrayListOf()

                    mViewModel.meditationList.addAll(dataList)
                    mViewModel.meditationTotalCount =
                        requestState.apiResponse?.data?.totalRecords ?: 0
                    setMediationData()
                } ?: kotlin.run {
                    mBinding.rvMeditations.visible()
                    mBinding.tvNoProgressFoundMeditation.gone()
                }

                longToastState(requestState.error)
            }
            mViewModel.fetchMeditationHistoryData.postValue(null)
        }
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(
                this@HistoryActivity,
                HistoryActivity::class.java.simpleName
            )
        }
        mBinding.tvPrograms.setOnClickListener {
            mBinding.llMeditations.gone()
            mBinding.rlPrograms.visible()
            mBinding.tvCountMeditation.gone()
            mBinding.nestedScroll.visible()

            mViewModel.selectedTabType = Constants.programs
            changeTabBackground(mBinding.tvPrograms)
            setProgramsHistoryAdapter()
            setShowPastVisibility(false)

            hideShowUIAsPerType(true)
            mViewModel.currentPageCompletedProgram = -1
            mViewModel.currentPageInProgressProgram = -1

            callCompletedProgramHistoryAPI()
            callInProgressProgramHistoryAPI()
        }

        mBinding.tvChallenges.setOnClickListener {
            mViewModel.selectedTabType = Constants.challenges
            hideShowUIAsPerType(false)
            mBinding.tvNoProgressFound.gone()
            mBinding.tvNoCompletedFound.gone()
            changeTabBackground(mBinding.tvChallenges)
            setChallengesHistoryAdapter()
            setShowPastVisibility(false)
        }

        mBinding.tvLiveSessions.setOnClickListener {
            mViewModel.selectedTabType = Constants.live_sessions
            mBinding.tvNoProgressFound.gone()
            mBinding.tvNoCompletedFound.gone()
            hideShowUIAsPerType(false)
            changeTabBackground(mBinding.tvLiveSessions)
            setLiveSessionsHistoryAdapter()
            setShowPastVisibility(true)
        }

        mBinding.tvMeditation.setOnClickListener {
            mBinding.rlPrograms.gone()
            mBinding.llMeditations.visible()
            mBinding.tvCountMeditation.visible()
            mBinding.nestedScroll.gone()
            mViewModel.selectedTabType = Constants.meditations
            hideShowUIAsPerType(false)
            changeTabBackground(mBinding.tvMeditation)
            setMeditationsHistoryAdapter()
            setShowPastVisibility(true)
            mBinding.tvNoProgressFoundMeditation.gone()
            mViewModel.currentPageMeditation = -1
            callMeditationHistoryAPI()
        }

        mBinding.tvLightTransmissions.setOnClickListener {
            mViewModel.selectedTabType = Constants.light_transmissions
            mBinding.tvNoProgressFound.gone()
            mBinding.tvNoCompletedFound.gone()
            hideShowUIAsPerType(false)
            changeTabBackground(mBinding.tvLightTransmissions)
            setLightTransmissionsHistoryAdapter()
            setShowPastVisibility(false)
        }

        /*mBinding.sliderTimer.addOnChangeListener { slider, value, fromUser ->
            if (value > 15) {
                mBinding.ivCenterTime.setImageResource(R.drawable.bg_gold_round)
            } else {
                mBinding.ivCenterTime.setImageResource(R.drawable.bg_shimmer_light_round)
            }
        }*/

        mBinding.llStartTime.setOnClickListener {
            setProgressImage(0)
        }

        mBinding.llCenterTime.setOnClickListener {
            setProgressImage(50)
        }

        mBinding.llEndTime.setOnClickListener {
            setProgressImage(100)
        }

        mBinding.llShowPast.setOnClickListener {
            // Do Nothing just to prevent background List click
        }

        mBinding.rvInProgress.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d(TAG, "onScrolled: ")
                val layoutManager: LinearLayoutManager =
                    mBinding.rvInProgress.layoutManager as LinearLayoutManager
                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()

                if (!mViewModel.isLoadingInProgressProgramData && !mViewModel.isInProgressProgramLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                        callInProgressProgramHistoryAPI()
                    }
                }
            }
        })

        mBinding.rvMeditations.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d(TAG, "onScrolled: ")
                val layoutManager: LinearLayoutManager =
                    mBinding.rvMeditations.layoutManager as LinearLayoutManager
                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()

                if (!mViewModel.isLoadingMeditationData && !mViewModel.isMeditationLastPage) {

                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= mViewModel.perPage) {
                        when (mViewModel.selectedTabType) {
                            Constants.meditations -> {
                                mViewModel.isLoadingMeditationData = true
                                callMeditationHistoryAPI()
                            }
                        }
                    }
                }

            }
        })

        mBinding.rvHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                Log.d(TAG, "onScrolled: ")
                val layoutManager: LinearLayoutManager =
                    mBinding.rvHistory.layoutManager as LinearLayoutManager
                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()

                if (!mViewModel.isLoadingCompletedProgramData && !mViewModel.isCompletedProgramLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE) {
                        when (mViewModel.selectedTabType) {
                            Constants.programs -> {
                                callCompletedProgramHistoryAPI()
                            }
                        }
                    }
                }
            }
        })

    }


    private fun hideShowUIAsPerType(isFromProgram: Boolean) {
        if (isFromProgram) {
            mBinding.apply {
                rvHistory.visible()
                rvInProgress.visible()
                tvCompleted.visible()
                tvInProgress.visible()
            }
        } else {
            mBinding.apply {
                rvHistory.visible()
                rvInProgress.gone()
                tvCompleted.gone()
                tvInProgress.gone()
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setProgressImage(progress: Int) {
        mViewModel.currentProgress = progress
        mBinding.linearProgressHistory.setProgressCompat(progress, true)
        when (progress) {
            100 -> {
                mBinding.llStartTime.setImageResource(R.drawable.bg_white_round)
                mBinding.llCenterTime.setImageResource(R.drawable.bg_white_round)
                mBinding.llEndTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivStartTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivCenterTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivEndTime.setImageResource(R.drawable.bg_gold_round)
                mViewModel.days = 30
                mViewModel.currentPageMeditation = -1
                callMeditationHistoryAPI()
            }
            50 -> {
                mBinding.llStartTime.setImageResource(R.drawable.bg_white_round)
                mBinding.llCenterTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.llEndTime.setImageResource(R.drawable.bg_white_round)
                mBinding.ivStartTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivCenterTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivEndTime.setImageResource(R.drawable.bg_shimmer_light_round)
                mViewModel.days = 7
                mViewModel.currentPageMeditation = -1
                callMeditationHistoryAPI()
            }
            else -> {
                mBinding.llStartTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.llCenterTime.setImageResource(R.drawable.bg_white_round)
                mBinding.llEndTime.setImageResource(R.drawable.bg_white_round)
                mBinding.ivStartTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivCenterTime.setImageResource(R.drawable.bg_shimmer_light_round)
                mBinding.ivEndTime.setImageResource(R.drawable.bg_shimmer_light_round)
                mViewModel.days = 1
                mViewModel.currentPageMeditation = -1
                callMeditationHistoryAPI()
            }
        }
    }

    private fun setMeditationProgressImage(progress: Int) {
        mBinding.linearProgressHistory.setProgressCompat(progress, true)
        when (progress) {
            100 -> {
                mBinding.llStartTime.setImageResource(R.drawable.bg_white_round)
                mBinding.llCenterTime.setImageResource(R.drawable.bg_white_round)
                mBinding.llEndTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivStartTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivCenterTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivEndTime.setImageResource(R.drawable.bg_gold_round)
            }
            50 -> {
                mBinding.llStartTime.setImageResource(R.drawable.bg_white_round)
                mBinding.llCenterTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.llEndTime.setImageResource(R.drawable.bg_white_round)
                mBinding.ivStartTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivCenterTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivEndTime.setImageResource(R.drawable.bg_shimmer_light_round)
            }
            else -> {
                mBinding.llStartTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.llCenterTime.setImageResource(R.drawable.bg_white_round)
                mBinding.llEndTime.setImageResource(R.drawable.bg_white_round)
                mBinding.ivStartTime.setImageResource(R.drawable.bg_gold_round)
                mBinding.ivCenterTime.setImageResource(R.drawable.bg_shimmer_light_round)
                mBinding.ivEndTime.setImageResource(R.drawable.bg_shimmer_light_round)
            }
        }
    }

    private fun setShowPastVisibility(shouldVisible: Boolean) {
        if (shouldVisible) {
            mBinding.llShowPast.isVisible = true
        } else {
            mBinding.llShowPast.isGone = true
        }
    }

    private fun changeTabBackground(activeTextView: AppCompatTextView) {
        makeAllTabInActive(
            mBinding.tvPrograms,
            mBinding.tvChallenges,
            mBinding.tvLiveSessions,
            mBinding.tvMeditation,
            mBinding.tvLightTransmissions
        )
        activeTextView.setBackgroundResource(R.drawable.bg_white_gold_border_22dp)
        activeTextView.setTextColor(ContextCompat.getColor(this, R.color.gold))
    }

    private fun makeAllTabInActive(
        vararg otherTextView: AppCompatTextView,
    ) {
        for (textview in otherTextView) {
            textview.setBackgroundResource(R.drawable.bg_gray_95_radius_22dp)
            textview.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
        }
    }

    private fun setProgramsHistoryAdapter() {
        mBinding.rvInProgress.adapter = inProgressProgramsHistoryAdapter
        mBinding.rvHistory.adapter = completedProgramsHistoryAdapter
    }

    private fun setChallengesHistoryAdapter() {
        mBinding.rvHistory.adapter = challengesHistoryAdapter
    }

    private fun setLiveSessionsHistoryAdapter() {
        mBinding.rvHistory.adapter = liveSessionsHistoryAdapter
    }

    private fun setMeditationsHistoryAdapter() {

/*
        val linearLayoutManager = object : LinearLayoutManager(this) { override fun canScrollVertically() = true }
*/
        mBinding.rvMeditations.adapter = meditationsHistoryAdapter


    }

    private fun setLightTransmissionsHistoryAdapter() {
        mBinding.rvHistory.adapter = lightTransmissionsHistoryAdapter
    }

    override fun onLightTransmissionItemClick() {
        val dialog =
            LightTransmissionOrderDetailDialog(this)
        dialog.behavior.isDraggable = false
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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

    override fun onCompletedItemClicked(data: FetchProgramHistoryData.ForYouProgramData?) {
        data?.let { navigateToProgramDetailScreen(it, true) }
    }

    override fun onInProgressItemClicked(data: FetchProgramHistoryData.ForYouProgramData?) {
        data?.let { navigateToProgramDetailScreen(it, false) }
    }

    private fun navigateToProgramDetailScreen(
        data: FetchProgramHistoryData.ForYouProgramData,
        isFromCompletedProgram: Boolean,
    ) {
        ProgramDetailsActivity.startActivity(this, data.id.toString(), isFromCompletedProgram)
    }

    override fun onMeditationItemClicked(data: FetchMeditationHistoryData.MeditationData?) {
        data?.let { navigateToMeditationDetailScreen(it) }
    }

    private fun navigateToMeditationDetailScreen(data: FetchMeditationHistoryData.MeditationData) {
        MeditationDetailActivity.startActivity(this, data.id.toString())
    }

    private fun setInProgressProgramData() {
        inProgressProgramsHistoryAdapter.setHistoryInProgressPrograms(mViewModel.inProgressProgram)
        mViewModel.isLoadingInProgressProgramData =
            (mViewModel.inProgressProgram.size) == mViewModel.inProgressProgramTotalCount

        if (mViewModel.inProgressProgram.size > 0) {
            mBinding.rvInProgress.visible()
            mBinding.tvNoProgressFound.gone()
        } else {
            mBinding.rvInProgress.gone()
            mBinding.tvNoProgressFound.visible()
        }

        mViewModel.isInProgressProgramLastPage =
            mViewModel.inProgressProgram.size == mViewModel.inProgressProgramTotalCount
    }

    private fun setCompletedProgramData() {
        completedProgramsHistoryAdapter.setCompletedPrograms(mViewModel.completedProgram)
        mViewModel.isLoadingCompletedProgramData =
            (mViewModel.completedProgram.size + 1) == mViewModel.completedProgramTotalCount
        if (mViewModel.completedProgram.size > 0) {
            mBinding.rvHistory.visible()
            mBinding.tvNoCompletedFound.gone()
        } else {
            mBinding.rvHistory.gone()
            mBinding.tvNoCompletedFound.visible()
        }

        mViewModel.isCompletedProgramLastPage =
            mViewModel.completedProgram.size == mViewModel.completedProgramTotalCount
    }

    private fun setMediationData() {
        meditationsHistoryAdapter.setHistoryMeditationData(mViewModel.meditationList)
        mBinding.tvCountMeditation.text =
            "${mViewModel.completedProgramTotalCount} ${getString(R.string.meditations)}"
        mViewModel.isLoadingMeditationData =
            (mViewModel.meditationList.size) == mViewModel.meditationTotalCount
        mBinding.tvCountMeditation.visible()

        if (mViewModel.meditationList.size > 0) {
            mBinding.rvMeditations.visible()
            mBinding.tvNoProgressFoundMeditation.gone()
        } else {
            mBinding.rvMeditations.gone()
            mBinding.tvNoProgressFoundMeditation.visible()
        }
    }

}