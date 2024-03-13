package com.app.taocalligraphy.ui.program

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityProgramsListBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.program.InProgressProgramListResponse
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.NavigationHandler
import com.app.taocalligraphy.ui.history.HistoryActivity
import com.app.taocalligraphy.ui.meditation_rooms_detail.adapter.ProgressListAdapter
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.MeditationRoomCatSmallAdapter
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.program.adapter.ForYouProgramsAdapter
import com.app.taocalligraphy.ui.program.adapter.InProgressProgramsAdapter
import com.app.taocalligraphy.ui.program.viewmodel.ProgramViewModel
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ProgramsListActivity : BaseActivity<ActivityProgramsListBinding>(),
    InProgressProgramsAdapter.ProgressProgramsSelectListener,
    ForYouProgramsAdapter.ForYouProgramsSelectListener,
    MeditationRoomCatSmallAdapter.CategorySelectionListener,
    ProgressListAdapter.CategoryBaseProgramsSelectListener {

    private val mViewModel: ProgramViewModel by viewModels()

    private val mInProgressProgramsAdapter by lazy {
        return@lazy InProgressProgramsAdapter(this)
    }
    private val mForYouProgramsAdapter by lazy {
        return@lazy ForYouProgramsAdapter(this)
    }
    private val mWellnessCategorySmallAdapter by lazy {
        return@lazy MeditationRoomCatSmallAdapter(this, mViewModel)
    }
    private val mProgressListAdapter by lazy {
        return@lazy ProgressListAdapter(this)
    }

    private var isCollapsed = true

    var totalInProgressProgramCount = 0
    var isLoadingInProgressPrograms = true
    private var currentPageInProgressProgram = -1
    var shouldLoadMore = false

    override fun getResource() = R.layout.activity_programs_list

    companion object {
        fun startActivity(activity: AppCompatActivity, categoryId: String? = "0") {
            val intent = Intent(activity, ProgramsListActivity::class.java)
            intent.putExtra(Constants.Param.categoryId, categoryId)
            activity.startActivityWithAnimation(intent)
        }
    }

    val startSubscriptionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data?.getBooleanExtra("isSubscribed", false) == true)
                    onRefresh()
            }
        }

    override fun initView() {
        mViewModel.forYouProgramsList.removeAll {
            it == null
        }

        if (mViewModel.selectedPosition == -1) {
            mViewModel.categoryId = intent.extras?.getString(Constants.Param.categoryId, "0") ?: ""
        }

        setupToolbar()
        updateProfile()
        mBinding.shimmerForYou.showShimmer(true)
        mBinding.shimmerForFilterByTopic.showShimmer(true)
        mBinding.shimmerForOthersTopic.showShimmer(true)
        mBinding.shimmerForInProgressPrograms.showShimmer(true)

        if (mViewModel.categoryId.isNotEmpty()) {
            mBinding.nestedScroll.apply {
                fullScroll(View.FOCUS_UP)
                smoothScrollTo(0, 0)
            }
        }

        setUpData()

        if (mViewModel.categoryList.isEmpty()) {
            mViewModel.currentPageInCategoryBaseProgram = -1
            callFetchWellnessCategoryAPI()
        } else {
            setCategoriesData()
        }

        if (mViewModel.forYouProgramsList.isEmpty()) {
            mViewModel.currentPageForYouProgram = -1
            callGetForYouProgramsListAPI()
        } else {
            setForYouProgramData()
        }

        setAccessControlView()
        LocalBroadcastManager.getInstance(this@ProgramsListActivity).registerReceiver(
            mSubscriptionReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
        )
        LocalBroadcastManager.getInstance(this@ProgramsListActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )
    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            setAccessControlView()
        }
    }

    private fun setAccessControlView() {
        if (UserHolder.EnumUserModulePermission.ACCESS_PERSONAL_HISTORY.permission?.canAccess == false) {
            mBinding.ivLock.visible()
        } else {
            mBinding.ivLock.gone()
        }
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
        currentPageInProgressProgram = -1
        callGetInProgressProgramsListAPI()
    }

    fun onRefresh() {
        mViewModel.currentPageForYouProgram = -1
        mViewModel.currentPageInCategoryBaseProgram = -1

        mBinding.shimmerForYou.showShimmer(true)
        mBinding.shimmerForFilterByTopic.showShimmer(true)
        mBinding.shimmerForOthersTopic.showShimmer(true)
        mBinding.shimmerForInProgressPrograms.showShimmer(true)

        mBinding.rvForYouPrograms.gone()
        mBinding.rvInProgressPrograms.gone()
        mBinding.rvOthersPrograms.gone()
        mBinding.rvSelectCategory.gone()

        mBinding.shimmerForYou.visible()
        mBinding.shimmerForFilterByTopic.visible()
        mBinding.shimmerForOthersTopic.visible()
        mBinding.shimmerForInProgressPrograms.visible()

        mViewModel.forYouProgramsList.clear()
        mViewModel.categoryProgramList.clear()
        mViewModel.inProgressProgramsList.clear()
        mViewModel.categoryList.clear()

        callFetchWellnessCategoryAPI()
        callGetForYouProgramsListAPI()
    }

    private fun callFetchWellnessCategoryAPI() {
        mViewModel.fetchAllCategoryData(this, mDisposable)
    }

    private fun callGetForYouProgramsListAPI() {
        mViewModel.getForYouProgramList(this, mDisposable)
    }

    private fun callGetInProgressProgramsListAPI() {
        currentPageInProgressProgram += 1
        mViewModel.getInProgressProgramList(
            Constants.inProgress, currentPageInProgressProgram, this, mDisposable
        )
    }

    private fun callCategoryBaseProgramList(categoryId: String) {
        mViewModel.getCategoryBaseProgramList(categoryId, this, mDisposable)
    }

    private fun callProgramFavouriteAPI(programId: String) {
        mViewModel.setProgramFavorite(programId, false, this, mDisposable)
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


    private fun setUpData() {
        mBinding.rvSelectCategory.adapter = mWellnessCategorySmallAdapter
        mBinding.rvInProgressPrograms.adapter = mInProgressProgramsAdapter
        mBinding.rvForYouPrograms.adapter = mForYouProgramsAdapter
        mBinding.rvOthersPrograms.adapter = mProgressListAdapter
    }

    override fun observeApiCallbacks() {
        mViewModel.inProgressProgramListLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.data?.let { it ->
                    totalInProgressProgramCount = it.inProgressProgramsList?.totalRecords ?: 0
                    mViewModel.inProgressProgramsList.removeAll {
                        it == null
                    }
                    it.inProgressProgramsList?.programList?.forEach { program ->
                        mViewModel.inProgressProgramsList.filter { oldProgram -> oldProgram?.id == program.id }
                            .let {
                                if (it.isNotEmpty()) {
                                    mViewModel.inProgressProgramsList.remove(it.first())
                                }
                            }

                        mViewModel.inProgressProgramsList.add(program)
                    }

                    isLoadingInProgressPrograms = it.inProgressProgramsList?.programList!!.isEmpty()

                    if (!isLoadingInProgressPrograms && totalInProgressProgramCount > mViewModel.inProgressProgramsList.size)
                        mViewModel.inProgressProgramsList.add(null)

                    mInProgressProgramsAdapter.setInProgressPrograms(mViewModel.inProgressProgramsList)

                    checkInProgressData()

                    mBinding.rvInProgressPrograms.visible()
                    mBinding.shimmerForInProgressPrograms.hideShimmer()
                    mBinding.shimmerForInProgressPrograms.gone()
                }
                longToastState(requestState.error)
            }
            mViewModel.inProgressProgramListLiveData.postValue(null)
        }

        mViewModel.forYouProgramListLiveData.observe(this) { response ->
            response?.let { requestState ->
                if (mViewModel.currentPageForYouProgram == 0) {
                    mViewModel.forYouProgramsList.clear()
                }
                requestState.apiResponse?.data?.let { it ->
                    mViewModel.totalForYouProgramCount = it.forYouProgramList?.totalRecords ?: 0
                    mViewModel.forYouProgramsList.removeAll {
                        it == null
                    }

                    it.forYouProgramList?.programList?.let { it1 ->
                        mViewModel.forYouProgramsList.addAll(it1)
                    }

                    setForYouProgramData()
                }
                longToastState(requestState.error)
            }
            mViewModel.forYouProgramListLiveData.postValue(null)
        }

        mViewModel.fetchCategoryDataResponse.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse.let { fetchCategoryData ->
                    fetchCategoryData?.let { data ->
                        data.data?.list?.let { it ->
                            mViewModel.categoryList = it
                            setCategoriesData()
                        }
                    }
                }
            }
            mViewModel.fetchCategoryDataResponse.postValue(null)
        }

        mViewModel.categoryBaseProgramListLiveData.observe(this) { response ->
            response?.let { requestState ->
                if (mViewModel.currentPageInCategoryBaseProgram == 0) {
                    showProgressIndicator(
                        mBinding.progressBarCategoryBasePrograms,
                        requestState.progress
                    )
                    mViewModel.categoryProgramList.clear()
                }
                requestState.apiResponse?.data?.let { it ->
                    mViewModel.totalCategoryBaseProgramCount = it.totalRecords ?: 0
                    if (mViewModel.currentPageInCategoryBaseProgram == 0 || mViewModel.totalCategoryBaseProgramCount == 0) {
                        mViewModel.categoryProgramList.clear()
                    }
                    it.programList?.let {
                        mViewModel.categoryProgramList.addAll(it)
                    }
                    mViewModel.isLoadingCategoryBasePrograms = it.programList!!.isEmpty()

                    setCategoriesProgramData()
                }
                longToastState(requestState.error)
            }
            mViewModel.categoryBaseProgramListLiveData.postValue(null)
        }
    }

    private fun checkInProgressData() {
        if (mViewModel.inProgressProgramsList.isNotEmpty()) {
            mBinding.inProgressTitle.visible()
            mBinding.btnProgramHistory.visible()
        } else {
            mBinding.inProgressTitle.gone()
            mBinding.btnProgramHistory.gone()
        }
        setAccessControlView()
    }

    private fun checkForYouData() {
        if (mViewModel.forYouProgramsList.isNotEmpty()) {
            mBinding.tvNoForYou.gone()
        } else {
            mBinding.tvNoForYou.visible()
        }
    }

    private fun checkCategoryBaseProgramData() {
        if (mViewModel.categoryProgramList.isNotEmpty()) {
            mBinding.tvNoProgram.gone()
        } else {
            mBinding.tvNoProgram.visible()
        }
    }

    private fun showHideRating(type: String, title: String) {
        if (isCollapsed) {
            mBinding.ivDownUpArrow.setImageResource(R.drawable.vd_medium_grey_up_arrow)
            mBinding.llFilterRatingInfo.isVisible = true
            isCollapsed = false
            mBinding.tvRating.text = getString(R.string.rating)
        } else {
            mBinding.ivDownUpArrow.setImageResource(R.drawable.vd_medium_grey_down_arrow)
            mBinding.llFilterRatingInfo.isGone = true
            isCollapsed = true
            mBinding.tvRating.text = title

            mViewModel.sortOrder = if (title == "Rating") Constants.desc else Constants.asc

            if (type != mViewModel.sortType) {
                mViewModel.sortType = type
                onCategoryChange(
                    mViewModel.categoryList[mViewModel.selectedPosition].categoryId
                        ?: ""
                )
            }
        }
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(
                this@ProgramsListActivity,
                ProgramsListActivity::class.java.simpleName
            )
        }
        mBinding.llRating.setOnClickListener {
            showHideRating(Constants.ratings, getString(R.string.rating))

        }

        mBinding.llFilterRatingInfo.setOnClickListener {
            showHideRating(Constants.A_Z, getString(R.string.a_to_z))
            mViewModel.sortOrder = Constants.asc
        }

        mBinding.rvForYouPrograms.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvForYouPrograms.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvForYouPrograms.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if (mViewModel.isLoadingForYouPrograms and ((totalItem == (lastVisibleItem + 2)) or (totalItem == (lastVisibleItem + 3)))) {
                    callGetForYouProgramsListAPI()
                }
            }
        })

        mBinding.rvInProgressPrograms.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvInProgressPrograms.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvInProgressPrograms.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if ((!isLoadingInProgressPrograms) &&
                    ((totalItem == (lastVisibleItem + 2)) or (totalItem == (lastVisibleItem + 3)))
                ) {
                    isLoadingInProgressPrograms = true
                    callGetInProgressProgramsListAPI()
                }
            }
        })

        mBinding.nestedScroll.viewTreeObserver.addOnScrollChangedListener {
            val view: View =
                mBinding.nestedScroll.getChildAt(mBinding.nestedScroll.childCount - 1) as View
            val diff: Int =
                view.bottom - (mBinding.nestedScroll.height + mBinding.nestedScroll.scrollY)

            if (diff == 0) {
                if ((!mViewModel.isLoadingCategoryBasePrograms) and (!shouldLoadMore)) {
                    shouldLoadMore = true
                    mBinding.progressBarLoadMorePrograms.visibility = View.VISIBLE
                    if (mViewModel.selectedPosition == -1) {
                        callCategoryBaseProgramList(
                            mViewModel.categoryList[0].categoryId ?: ""
                        )
                    } else if (mViewModel.categoryList.isNotEmpty()) {
                        callCategoryBaseProgramList(
                            mViewModel.categoryList[mViewModel.selectedPosition].categoryId
                                ?: ""
                        )
                    }
                } else {
                    mBinding.progressBarLoadMorePrograms.visibility = View.INVISIBLE
                }
            } else {
                mBinding.progressBarLoadMorePrograms.visibility = View.INVISIBLE
            }
        }
        mBinding.btnProgramHistory.clickWithDebounce {
            if (UserHolder.EnumUserModulePermission.ACCESS_PERSONAL_HISTORY.permission?.canAccess == false) {
                SubscriptionActivity.startActivityForResult(this@ProgramsListActivity)
                return@clickWithDebounce
            }

            HistoryActivity.startActivity(this)
        }
    }


    override fun onProgressProgramsItemClick(data: InProgressProgramListResponse.InProgressProgramsList.Program?) {
        NavigationHandler.handleProgramInProgressNavigation(
            data!!,
            this@ProgramsListActivity,
            startSubscriptionActivityForResult
        )
    }

    override fun onForYouProgramsItemClick(data: ProgramDataModel?) {
        NavigationHandler.handleNavigation(
            data!!,
            this@ProgramsListActivity,
            startSubscriptionActivityForResult
        )
    }

    override fun onCategoryProgramsItemClick(data: ProgramDataModel?) {
        NavigationHandler.handleNavigation(
            data!!,
            this@ProgramsListActivity,
            startSubscriptionActivityForResult
        )
    }


    override fun onProgressProgramsFavouriteClick(id: String, adapterPosition: Int) {
        callProgramFavouriteAPI(id)
        updateForYouFavorites(id)
        updateProgramListFavorites(id)
    }

    override fun onForYouProgramsFavouriteClick(id: String, adapterPosition: Int) {
        callProgramFavouriteAPI(id)
        updateInProgressFavorites(id)
        updateProgramListFavorites(id)
    }

    override fun onCategoryChange(categoryId: String) {
        if (mBinding.progressBarCategoryBasePrograms.visibility == View.INVISIBLE) {
            mViewModel.categoryProgramList = arrayListOf()
            mBinding.rvOthersPrograms.inVisible()
            mViewModel.currentPageInCategoryBaseProgram = -1
            callCategoryBaseProgramList(categoryId)
        }
    }

    override fun onCategoryProgramsFavouriteClick(id: String, adapterPosition: Int) {
        callProgramFavouriteAPI(id)
        updateForYouFavorites(id)
        updateInProgressFavorites(id)
    }

    private fun updateForYouFavorites(programId: String) {
        mViewModel.forYouProgramsList.filter { it?.id == programId }.let {
            if (it.isNotEmpty()) {
                it.first()?.isFavorites = !it.first()?.isFavorites!!
                mForYouProgramsAdapter.notifyItemChanged(
                    mViewModel.forYouProgramsList.indexOf(it.first())
                )
            }
        }
    }

    private fun updateProgramListFavorites(programId: String) {
        mViewModel.categoryProgramList.filter { it.id == programId }.let {
            if (it.isNotEmpty()) {
                it.first().isFavorites = !it.first().isFavorites
                mProgressListAdapter.notifyItemChanged(
                    mViewModel.categoryProgramList.indexOf(
                        it.first()
                    )
                )
            }
        }
    }

    private fun updateInProgressFavorites(programId: String) {
        mViewModel.inProgressProgramsList.filter { it?.id == programId }.let {
            if (it.isNotEmpty()) {
                it.first()?.isFavorite = !it.first()?.isFavorite!!
                mInProgressProgramsAdapter.notifyItemChanged(
                    mViewModel.inProgressProgramsList.indexOf(
                        it.first()
                    )
                )
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

    private val mSubscriptionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            onRefresh()
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this@ProgramsListActivity)
            .unregisterReceiver(mSubscriptionReceiver)
        LocalBroadcastManager.getInstance(this@ProgramsListActivity)
            .unregisterReceiver(mAccessLevelReceiver)
        super.onDestroy()
    }

    private fun setForYouProgramData() {
        mViewModel.isLoadingForYouPrograms =
            mViewModel.totalForYouProgramCount > mViewModel.forYouProgramsList.size

        if (mViewModel.isLoadingForYouPrograms)
            mViewModel.forYouProgramsList.add(null)

        mForYouProgramsAdapter.setForYouPrograms(mViewModel.forYouProgramsList)

        checkForYouData()

        mBinding.shimmerForYou.hideShimmer()
        mBinding.shimmerForYou.gone()
        mBinding.rvForYouPrograms.visible()
    }

    private fun setCategoriesData() {
        if (mViewModel.selectedPosition == -1) {
            val indexOfCategory = mViewModel.categoryList.indexOfFirst {
                it.categoryId == mViewModel.categoryId
            }
            if (indexOfCategory == -1) {
                mViewModel.selectedPosition = 0
            } else {
                mViewModel.selectedPosition = indexOfCategory
            }
        }
        mWellnessCategorySmallAdapter.setCategoryData(mViewModel.categoryList)
        mViewModel.categoryList.firstOrNull()?.let {
            if (mViewModel.categoryProgramList.isEmpty()) {
                if (mViewModel.categoryId == "0") callCategoryBaseProgramList(
                    it.categoryId ?: ""
                )
                else callCategoryBaseProgramList(mViewModel.categoryId)
            } else {
                setCategoriesProgramData()
            }
        }

        when (mViewModel.sortType) {
            Constants.ratings -> {
                mBinding.tvRating.text = getString(R.string.rating)
            }
            Constants.A_Z -> {
                mBinding.tvRating.text = getString(R.string.a_to_z)
            }
        }

        mBinding.rvSelectCategory.visible()
        mBinding.shimmerForFilterByTopic.hideShimmer()
        mBinding.shimmerForFilterByTopic.gone()
    }

    private fun setCategoriesProgramData() {
        mBinding.progressBarCategoryBasePrograms.visibility = View.INVISIBLE
        mBinding.progressBarLoadMorePrograms.visibility = View.INVISIBLE
        mBinding.lblVideoCount.text = (mViewModel.totalCategoryBaseProgramCount).toString()
        mBinding.lblIn.visible()

        if (mViewModel.selectedPosition == -1) {
            mBinding.lblViewCat.text =
                mViewModel.categoryList[0].categoryName
        } else if (mViewModel.categoryList.isNotEmpty()) {
            mBinding.lblViewCat.text =
                mViewModel.categoryList[mViewModel.selectedPosition].categoryName
        }

        mProgressListAdapter.setProgramsData(mViewModel.categoryProgramList)

        if (mViewModel.totalCategoryBaseProgramCount > 0) {
            mBinding.llFilterRating.visible()
        } else {
            mBinding.llFilterRating.gone()
        }

        shouldLoadMore = false
        checkCategoryBaseProgramData()

        mBinding.rvOthersPrograms.visible()
        mBinding.shimmerForOthersTopic.hideShimmer()
        mBinding.shimmerForOthersTopic.gone()
    }

}