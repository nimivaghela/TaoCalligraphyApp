package com.app.taocalligraphy.ui.wellness

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
import com.app.taocalligraphy.databinding.ActivityAllMeditationsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.NavigationHandler
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.ui.wellness.adapter.AllMeditationsAdapter
import com.app.taocalligraphy.ui.wellness.adapter.WellnessSubCategoryAdapter
import com.app.taocalligraphy.ui.wellness.viewmodel.CategoryViewModel
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.loadImageProfile
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class AllMeditationsActivity : BaseActivity<ActivityAllMeditationsBinding>(),
    AllMeditationsAdapter.OnMeditationItemClickListener,
    WellnessSubCategoryAdapter.CategorySelectionListener {

    override fun getResource(): Int = R.layout.activity_all_meditations

    companion object {
        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
            title: String,
            selectedType: String,
            categoryData: CategoryDataModel,
            subcategoryId: Int
        ) {
            val intent = Intent(activity, AllMeditationsActivity::class.java)
            intent.putExtra(Constants.Param.title, title)
            intent.putExtra(Constants.Param.selectedType, selectedType)
            intent.putExtra(Constants.Param.subCategory, categoryData)
            intent.putExtra(Constants.Param.categoryId, subcategoryId)
            activity.startActivityWithAnimation(intent)
        }
    }

    val startSubscriptionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data?.getBooleanExtra("isSubscribed", false) == true) {
                    onRefresh()
                }
            }
        }

    private val viewModel: CategoryViewModel by viewModels()

    private val title by lazy {
        return@lazy intent?.extras?.getString(Constants.Param.title, "") ?: ""
    }

    private val categoryData: CategoryDataModel? by lazy {
        return@lazy intent?.extras?.getParcelable(Constants.Param.subCategory) as CategoryDataModel?
    }

    private val videoAdapter by lazy {
        return@lazy AllMeditationsAdapter(this)
    }

    private val subCategoryAdapter: WellnessSubCategoryAdapter by lazy {
        return@lazy WellnessSubCategoryAdapter(this)
    }

    override fun initView() {
        viewModel.dataList.removeAll {
            it == null
        }
        if (viewModel.selectedTabType == "") {
            viewModel.selectedTabType =
                intent?.extras?.getString(Constants.Param.selectedType, "") ?: Constants.VIDEO
        }

        if (viewModel.selectedSubCategoryId == -1) {
            viewModel.selectedSubCategoryId =
                intent?.extras?.getInt(Constants.Param.categoryId, -1) ?: -1
        }

        mBinding.tvTitleToolbar.text = getString(R.string.all_meditation_title, title)
        setupToolbar()
        mBinding.shimmerVideos.visible()
        mBinding.rvVideos.gone()
        mBinding.rvVideos.layoutManager = LinearLayoutManager(this)
        mBinding.rvVideos.adapter = videoAdapter

        mBinding.rvCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvCategory.adapter = subCategoryAdapter

        if (viewModel.selectedSubCategoryId == -1) {
            categoryData?.let {
                if (it.subCategoryDetails.isNotEmpty()) {
                    viewModel.selectedSubCategoryId = it.subCategoryDetails[0].id
                    viewModel.selectedSybCategoryName = it.subCategoryDetails[0].name
                } else {
                    viewModel.selectedSubCategoryId = it.categoryId?.toInt() ?: 0
                    mBinding.rvCategory.gone()
                    mBinding.tvCategoryTitle.gone()
                }
            }
        } else {
            categoryData?.let {
                if (it.subCategoryDetails.isNotEmpty()) {
                    viewModel.selectedPos =
                        it.subCategoryDetails.indexOf(it.subCategoryDetails.first { subCategory -> subCategory.id == viewModel.selectedSubCategoryId })

                    viewModel.selectedSybCategoryName =
                        it.subCategoryDetails[viewModel.selectedPos].name
                } else {
                    viewModel.selectedSubCategoryId = it.categoryId?.toInt() ?: 0
                    mBinding.rvCategory.gone()
                    mBinding.tvCategoryTitle.gone()
                }
            }
        }

        mBinding.tvCategoryTitle.text = viewModel.selectedSybCategoryName

        subCategoryAdapter.setSubCategory(
            categoryData?.subCategoryDetails ?: emptyList(),
            viewModel.selectedPos
        )

        when (viewModel.selectedTabType) {
            Constants.VIDEO -> {
                changeTabBackground(mBinding.tvVideo)
            }
            Constants.MUSIC -> {
                changeTabBackground(mBinding.tvMusic)
            }
            Constants.Guided_Meditation_Audio -> {
                changeTabBackground(mBinding.tvGuidedAudio)
            }
        }

        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            mBinding.swipeToRefreshLayout.isRefreshing = false
            onRefresh()
        }

        LocalBroadcastManager.getInstance(this@AllMeditationsActivity).registerReceiver(
            mSubscriptionReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
        )
    }

    fun onRefresh() {
        mBinding.shimmerVideos.visible()
        mBinding.rvVideos.gone()
        viewModel.currentPage = -1
        getCategoriesData()
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
            UserMenuActivity.startActivity(this@AllMeditationsActivity)
        }

        mBinding.tvVideo.setOnClickListener {
            viewModel.currentPage = -1
            viewModel.selectedTabType = Constants.VIDEO
            changeTabBackground(mBinding.tvVideo)
        }

        mBinding.tvGuidedAudio.setOnClickListener {
            viewModel.currentPage = -1
            viewModel.selectedTabType = Constants.Guided_Meditation_Audio
            changeTabBackground(mBinding.tvGuidedAudio)
        }

        mBinding.tvMusic.setOnClickListener {
            viewModel.currentPage = -1
            viewModel.selectedTabType = Constants.MUSIC
            changeTabBackground(mBinding.tvMusic)
        }

        mBinding.rvVideos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvVideos.layoutManager?.itemCount ?: 0
                if (totalItem < viewModel.totalData && !viewModel.isLastData && viewModel.isResponseReceived) {
                    viewModel.isResponseReceived = false
                    viewModel.dataList.add(null)
                    videoAdapter.setMeditationData(viewModel.dataList)

                    getCategoriesData()
                }
            }
        })
    }

    private fun changeTabBackground(activeTextView: AppCompatTextView) {
        makeAllTabInActive(
            mBinding.tvVideo,
            mBinding.tvGuidedAudio,
            mBinding.tvMusic,
        )
        activeTextView.setBackgroundResource(R.drawable.bg_white_gold_border_22dp)
        activeTextView.setTextColor(ContextCompat.getColor(this, R.color.gold))

        mBinding.shimmerVideos.visible()
        mBinding.rvVideos.gone()

        if (viewModel.currentPage == -1 || viewModel.dataList.isEmpty())
            getCategoriesData()
        else
            setMeditationData()
    }

    private fun makeAllTabInActive(
        vararg otherTextView: AppCompatTextView,
    ) {
        for (textview in otherTextView) {
            textview.setBackgroundResource(R.drawable.bg_gray_95_radius_22dp)
            textview.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
        }
    }

    override fun observeApiCallbacks() {
        viewModel.getAllCategoryDataRes.observe(this) { response ->
            response?.let { requestState ->
                if (viewModel.currentPage == 0) {
                    viewModel.dataList.clear()
                }

                requestState.apiResponse?.data?.let { data ->
                    data.list?.let { list ->
                        viewModel.dataList.removeAll {
                            it == null
                        }
                        viewModel.dataList.addAll(list)
                        viewModel.isLastData = list.isEmpty()
                        viewModel.totalData = data.totalRecords?.toInt() ?: 0
                    }
                    setMeditationData()
                }
            }

            viewModel.getAllCategoryDataRes.postValue(null)
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
            data!!,
            this@AllMeditationsActivity,
            startSubscriptionActivityForResult
        )
    }

    override fun onFavouriteClicked(dataModel: ProgramDataModel) {
        if (dataModel.type == Constants.content) {
            // call fav content api
            viewModel.favoriteMeditationContent(
                dataModel.id ?: "",
                this@AllMeditationsActivity,
                mDisposable
            )
        } else {
            // call fav program api
            viewModel.setProgramFavorite(
                dataModel.id!!,
                false,
                this@AllMeditationsActivity,
                mDisposable
            )
        }
    }

    override fun onCategoryChange(categoryId: Int, categoryName: String, selectedPos: Int) {
        mBinding.shimmerVideos.visible()
        mBinding.rvVideos.gone()
        viewModel.currentPage = -1
        viewModel.selectedSubCategoryId = categoryId
        viewModel.selectedPos = selectedPos
        viewModel.selectedSybCategoryName = categoryName
        mBinding.tvCategoryTitle.text = categoryName
        getCategoriesData()
    }

    private fun getCategoriesData() {
        mBinding.tvNoVideoFound.gone()
        viewModel.currentPage++

        val jsonObject = JsonObject()
        jsonObject.addProperty("per_page", 5)
        jsonObject.addProperty("current_page", viewModel.currentPage)
        jsonObject.addProperty("search", "")

        viewModel.getAllCategoryData(
            viewModel.selectedTabType,
            viewModel.selectedSubCategoryId,
            jsonObject,
            this,
            mDisposable
        )
    }

    private val mSubscriptionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            onRefresh()
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this@AllMeditationsActivity)
            .unregisterReceiver(mSubscriptionReceiver)
        super.onDestroy()
    }

    private fun setMeditationData() {
        mBinding.shimmerVideos.gone()
        mBinding.rvVideos.visible()
        viewModel.isResponseReceived = true

        videoAdapter.setMeditationData(viewModel.dataList)

        if (viewModel.dataList.isEmpty()) {
            mBinding.rvVideos.gone()
            mBinding.tvNoVideoFound.visible()
        } else {
            mBinding.rvVideos.visible()
            mBinding.tvNoVideoFound.gone()
        }
    }

}