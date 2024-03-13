package com.app.taocalligraphy.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivitySeeAllBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.NavigationHandler
import com.app.taocalligraphy.ui.home.adapter.MeditationSeeAllAdapter
import com.app.taocalligraphy.ui.home.adapter.ProgramsSeeAllAdapter
import com.app.taocalligraphy.ui.home.viewmodel.SeeAllViewModel
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class SeeAllActivity : BaseActivity<ActivitySeeAllBinding>(),
    MeditationSeeAllAdapter.OnMeditationItemClickListener,
    ProgramsSeeAllAdapter.OnProgramItemClickListener {

    private val mViewModel: SeeAllViewModel by viewModels()

    override fun getResource(): Int = R.layout.activity_see_all

    companion object {
        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
            title: String,
            type: String
        ) {
            val intent = Intent(activity, SeeAllActivity::class.java)
            intent.putExtra(Constants.Param.title, title)
            intent.putExtra(Constants.Param.type, type)
            activity.startActivityWithAnimation(intent)
        }
    }


    val startSubscriptionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data?.getBooleanExtra("isSubscribed", false) == true) {
                    mViewModel.currentPageMeditationCount = -1
                    mViewModel.currentPageProgramsCount = -1
                    mViewModel.meditationList.clear()
                    mViewModel.programList.clear()
                    initView()
                }
            }
        }

    private val title by lazy {
        return@lazy intent.extras?.getString(Constants.Param.title, "") ?: ""
    }

    private val type by lazy {
        return@lazy intent.extras?.getString(Constants.Param.type, "") ?: ""
    }

    private val meditationsAdapter by lazy {
        return@lazy MeditationSeeAllAdapter(this)
    }

    private val programsAdapter by lazy {
        return@lazy ProgramsSeeAllAdapter(this)
    }

    override fun initView() {
        mViewModel.meditationList.removeAll {
            it == null
        }
        mViewModel.programList.removeAll {
            it == null
        }

        mBinding.tvTitleToolbar.text = title
        if (title == getString(R.string.new_release)) {
            mBinding.tvNoProgressFoundMeditation.text = getString(R.string.no_new_release)
            mBinding.tvNoProgressFoundPrograms.text = getString(R.string.no_new_release)
        }

        mBinding.shimmerPrograms.visible()
        mBinding.shimmerMeditation.visible()
        mBinding.rvMeditations.gone()
        mBinding.rvPrograms.gone()
        setProgramsAdapter()
        setMeditationsAdapter()

        setupToolbar()
        if (mViewModel.selectedTabType == Constants.meditations) {
            mBinding.llMeditations.visible()
            mBinding.llPrograms.gone()
            changeTabBackground(mBinding.tvMeditation)
            if (mViewModel.meditationList.isEmpty())
                callMeditationContentAPI()
            else
                setMeditationData()
        } else {
            mBinding.llMeditations.gone()
            mBinding.llPrograms.visible()
            changeTabBackground(mBinding.tvPrograms)
            if (mViewModel.programList.isEmpty())
                callProgramsContentAPI()
            else
                setProgramData()
        }
        mViewModel.isLoadingMeditation = false
        mViewModel.isLoadingPrograms = false

        LocalBroadcastManager.getInstance(this@SeeAllActivity).registerReceiver(
            mSubscriptionReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
        )
    }

    private fun setMeditationsAdapter() {
        val linearLayoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically() = true
        }
        mBinding.rvMeditations.adapter = meditationsAdapter
        mBinding.rvMeditations.layoutManager = linearLayoutManager
    }

    private fun setProgramsAdapter() {
        val linearLayoutManager = object : LinearLayoutManager(this) {
            override fun canScrollVertically() = true
        }
        mBinding.rvPrograms.adapter = programsAdapter
        mBinding.rvPrograms.layoutManager = linearLayoutManager
    }


    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()

        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this@SeeAllActivity)
        }

        mBinding.tvMeditation.setOnClickListener {
            mBinding.llMeditations.visible()
            mBinding.llPrograms.gone()

            mViewModel.selectedTabType = Constants.meditations
            changeTabBackground(mBinding.tvMeditation)
            if (mViewModel.meditationList.isEmpty())
                callMeditationContentAPI()
            else
                setMeditationData()
        }

        mBinding.tvPrograms.setOnClickListener {
            mBinding.llMeditations.gone()
            mBinding.llPrograms.visible()

            mViewModel.selectedTabType = Constants.programs
            changeTabBackground(mBinding.tvPrograms)
            if (mViewModel.programList.isEmpty())
                callProgramsContentAPI()
            else
                setProgramData()
        }
        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            mBinding.swipeToRefreshLayout.isRefreshing = false
            reload()
        }

        mBinding.rvMeditations.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvMeditations.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvMeditations.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if ((!mViewModel.isLoadingMeditation && mViewModel.isMeditationsLoadMoreData) and ((totalItem == (lastVisibleItem + 2)) or (totalItem == (lastVisibleItem + 3)))) {
                    mViewModel.isLoadingMeditation = true
                    callMeditationContentAPI()
                }
            }
        })

        mBinding.rvPrograms.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvPrograms.layoutManager?.itemCount ?: 0
                val lastVisibleItem = ((mBinding.rvPrograms.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if ((!mViewModel.isLoadingPrograms && mViewModel.isProgramsLoadMoreData) and ((totalItem == (lastVisibleItem + 2)) or (totalItem == (lastVisibleItem + 3)))) {
                    mViewModel.isLoadingPrograms = true
                    callProgramsContentAPI()
                }
            }
        })

    }

    override fun observeApiCallbacks() {
        mViewModel.fetchMeditationDataResponse.observe(this@SeeAllActivity) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { response ->
                    mViewModel.isLoadingMeditation = false

                    if (mViewModel.currentPageMeditationCount == 0) {
                        mViewModel.meditationList.clear()
                    }

                    response.data?.meditations?.list?.let { list ->
                        mViewModel.totalMeditationsCount =
                            response.data?.meditations?.totalRecords?.toInt() ?: 0

                        mViewModel.meditationList.removeAll {
                            it == null
                        }

                        mViewModel.meditationList.addAll(list)
                        setMeditationData()
                        mViewModel.currentPageMeditationCount += 1
                    }
                }
                longToastState(requestState.error)
            }

            mViewModel.fetchMeditationDataResponse.postValue(null)
        }

        mViewModel.fetchProgramsDataResponse.observe(this@SeeAllActivity) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { response ->
                    mViewModel.isLoadingPrograms = false
                    if (mViewModel.currentPageProgramsCount == 0) {
                        mViewModel.programList.clear()
                    }

                    response.data?.programs?.list?.let { list ->
                        mViewModel.totalProgramsCount =
                            response.data?.programs?.totalRecords?.toInt() ?: 0

                        mViewModel.programList.removeAll {
                            it == null
                        }

                        mViewModel.programList.addAll(list)
                        setProgramData()
                        mViewModel.currentPageProgramsCount += 1
                    }
                }
                longToastState(requestState.error)
            }
            mViewModel.fetchProgramsDataResponse.postValue(null)
        }
    }

    private fun reload() {
        if (mViewModel.selectedTabType == Constants.meditations) {
            mViewModel.currentPageMeditationCount = -1
            mBinding.tvNoProgressFoundMeditation.gone()
            mBinding.rvMeditations.gone()
            mBinding.shimmerMeditation.visible()
            mViewModel.meditationList.clear()
            callMeditationContentAPI()
        } else {
            mViewModel.currentPageProgramsCount = -1
            mBinding.tvNoProgressFoundPrograms.gone()
            mBinding.rvPrograms.gone()
            mBinding.shimmerPrograms.visible()
            mViewModel.programList.clear()
            callProgramsContentAPI()
        }
    }

    private fun callMeditationContentAPI() {
        val listType = type

        mViewModel.fetchViewAllMeditationsDataAPI(
            listType,
            this,
            mDisposable
        )
    }

    private fun callProgramsContentAPI() {
        val listType = type

        mViewModel.fetchViewAllProgramsDataAPI(
            listType,
            this,
            mDisposable
        )
    }

    private fun changeTabBackground(activeTextView: AppCompatTextView) {
        makeAllTabInActive(
            mBinding.tvPrograms,
            mBinding.tvLiveSessions,
            mBinding.tvMeditation,
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

    override fun onMeditationItemClicked(data: ProgramDataModel) {
        NavigationHandler.handleNavigation(
            data,
            this@SeeAllActivity as AppCompatActivity,
            startSubscriptionActivityForResult
        )
    }

    override fun onProgramItemClicked(data: ProgramDataModel) {
        NavigationHandler.handleNavigation(
            data,
            this@SeeAllActivity as AppCompatActivity,
            startSubscriptionActivityForResult
        )
    }

    override fun onFavouriteClicked(dataModel: ProgramDataModel) {
        if (dataModel.type == Constants.content) {
            // call fav content api
            mViewModel.favoriteMeditationContent(
                dataModel.id ?: "",
                this@SeeAllActivity,
                mDisposable
            )
        } else {
            // call fav program api
            mViewModel.setProgramFavorite(
                dataModel.id!!,
                false,
                this@SeeAllActivity,
                mDisposable
            )
        }
    }

    private val mSubscriptionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            mViewModel.currentPageMeditationCount = -1
            mViewModel.currentPageProgramsCount = -1
            mBinding.shimmerPrograms.visible()
            mBinding.shimmerMeditation.visible()
            mBinding.rvMeditations.gone()
            mBinding.rvPrograms.gone()

            callMeditationContentAPI()
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this@SeeAllActivity)
            .unregisterReceiver(mSubscriptionReceiver)
        super.onDestroy()
    }

    private fun setMeditationData() {
        mBinding.shimmerMeditation.gone()
        mViewModel.isMeditationsLoadMoreData =
            mViewModel.meditationList.size < mViewModel.totalMeditationsCount

        if (mViewModel.isMeditationsLoadMoreData)
            mViewModel.meditationList.add(null)

        if (mViewModel.meditationList.isEmpty()) {
            mBinding.tvNoProgressFoundMeditation.visible()
            mBinding.rvMeditations.gone()
        } else {
            mBinding.tvNoProgressFoundMeditation.gone()
            mBinding.rvMeditations.visible()
            meditationsAdapter.setMeditationData(mViewModel.meditationList)
        }
    }

    private fun setProgramData() {
        mBinding.shimmerPrograms.gone()
        mViewModel.isProgramsLoadMoreData =
            mViewModel.programList.size < mViewModel.totalProgramsCount

        if (mViewModel.isProgramsLoadMoreData)
            mViewModel.programList.add(null)

        if (mViewModel.programList.isEmpty()) {
            mBinding.rvPrograms.gone()
            mBinding.tvNoProgressFoundPrograms.visible()
        } else {
            programsAdapter.setProgramData(mViewModel.programList)
            mBinding.rvPrograms.visible()
            mBinding.tvNoProgressFoundPrograms.gone()
        }
    }
}