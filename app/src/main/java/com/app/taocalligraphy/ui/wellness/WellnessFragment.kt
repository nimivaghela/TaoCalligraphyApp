package com.app.taocalligraphy.ui.wellness

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.TextAppearanceSpan
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentWellnessBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_content_by_filter.ContentData
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.NavigationHandler
import com.app.taocalligraphy.ui.experience.adapter.AudioDataAdapter
import com.app.taocalligraphy.ui.experience.adapter.MusicDataAdapter
import com.app.taocalligraphy.ui.experience.adapter.VideoDataAdapter
import com.app.taocalligraphy.ui.field_healing.adapter.HealingNamesAdapter
import com.app.taocalligraphy.ui.field_healing.adapter.PageAdapter
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.home.adapter.FeatureSessionAdapter
import com.app.taocalligraphy.ui.program.ProgramsListActivity
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.ui.wellness.adapter.WellnessProgramAdapter
import com.app.taocalligraphy.ui.wellness.adapter.WellnessSubCategoryFilterAdapter
import com.app.taocalligraphy.ui.wellness.adapter.WellnessUpcomingSessionAdapter
import com.app.taocalligraphy.ui.wellness.dialog.WellnessFilterDialog
import com.app.taocalligraphy.ui.wellness.viewmodel.CategoryViewModel
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.collections.ArrayList


@AndroidEntryPoint
class WellnessFragment : BaseFragment<FragmentWellnessBinding>(),
    AudioDataAdapter.OnAudioItemClickListener,
    VideoDataAdapter.OnVideoItemClickListener,
    MusicDataAdapter.OnMusicItemClickListener,
    WellnessUpcomingSessionAdapter.SessionClickListener,
    WellnessProgramAdapter.OnMultiSessionListener {

    private val mViewModel: CategoryViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    private var isFieldHealingShow = true

    private val programDataAdapter by lazy {
        return@lazy WellnessProgramAdapter(this)
    }

    private val featureSessionAdapter by lazy {
        return@lazy FeatureSessionAdapter(requireContext())
    }

    private val upcomingSessionAdapter by lazy {
        return@lazy WellnessUpcomingSessionAdapter(this)
    }

    private val mAudioAdapter by lazy {
        return@lazy AudioDataAdapter(this, true)
    }
    private val mMusicAdapter by lazy {
        return@lazy MusicDataAdapter(this, true)
    }
    private val mVideoAdapter by lazy {
        return@lazy VideoDataAdapter(this, true)
    }

    private val wellnessSubCategoryAdapter = WellnessSubCategoryFilterAdapter(object :
        WellnessSubCategoryFilterAdapter.OnAdapterItemClickListener {
        override fun onAdapterClick(mClickedData: CategoryDataModel.SubCategoryDetail) {
            val list: ArrayList<Int> = arrayListOf()

            if (mClickedData.isSelected) {
                mViewModel.subCatId = mClickedData.id
                list.add(mClickedData.id)
                list.add(mViewModel.categoryID)
                mViewModel.isSubCategoryClicked = true
            } else {
                mViewModel.subCatId = -1
                list.add(mViewModel.categoryID)
                mViewModel.isSubCategoryClicked = false
            }
            callCategoryTagsByIdAPI(list)
            callContentApiForTopicFilter()
            mViewModel.tagsID.clear()
            mViewModel.isFilterSelected = false
        }
    })

    private fun callContentApiForTopicFilter() {
        val tagList = mViewModel.tagsDataList.filter {
            it?.isSelected == true
        }.map {
            it?.id
        } as ArrayList<Int?>

        mViewModel.tagsID.clear()
        mViewModel.tagsID.addAll(tagList)

        mViewModel.idNeedToPassInCategory = mViewModel.subCatId

        mViewModel.currentPageCountForAudio = -1
        mViewModel.currentPageCountForMusic = -1
        mViewModel.currentPageCountForVideo = -1

        mBinding.shimmerForVideoList.showShimmer(true)
        mBinding.shimmerForAudioList.showShimmer(true)
        mBinding.shimmerForMusicList.showShimmer(true)

        mBinding.shimmerForVideoList.visible()
        mBinding.shimmerForAudioList.visible()
        mBinding.shimmerForMusicList.visible()

        mBinding.rvVideoList.gone()
        mBinding.rvMusicList.gone()
        mBinding.rvGuidedAudioList.gone()

        mBinding.tvNoVideo.gone()
        mBinding.tvNoAudio.gone()
        mBinding.tvNoMusic.gone()

        mViewModel.isVideoResult = false
        mViewModel.isAudioResult = false
        mViewModel.isMusicResult = false

        getFilteredText()
        callAudioContentAPI(if (mViewModel.subCatId == -1) mViewModel.categoryID else mViewModel.subCatId)
        callVideoContentAPI(if (mViewModel.subCatId == -1) mViewModel.categoryID else mViewModel.subCatId)
        callMusicContentAPI(if (mViewModel.subCatId == -1) mViewModel.categoryID else mViewModel.subCatId)
    }

    companion object {
        var position: Int = -1
    }

    override fun getLayoutId() = R.layout.fragment_wellness

    val startSubscriptionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data?.getBooleanExtra("isSubscribed", false) == true)
                    onRefresh()
            }

        }

    override fun initView() {
        removeLoadersFromList()
        updateProfile()
        mBinding.shimmerForVideoList.showShimmer(true)
        mBinding.shimmerForAudioList.showShimmer(true)
        mBinding.shimmerForMusicList.showShimmer(true)
        mBinding.shimmerForYou.showShimmer(true)
        mBinding.shimmerHeroImage.showShimmer(true)
        mBinding.shimmerTitle.showShimmer(true)
        position = arguments?.getInt("POSITION") ?: -1
        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            onRefresh()
        }
        setupToolbar()
        initRecyclerView()
        setUpCarouselsView()
        if (mViewModel.categoryDataList.isEmpty() || !mViewModel.isConfigChanges) {
            mViewModel.isFilterSelected = false
            mViewModel.currentPageForProgram = -1
            mViewModel.tagsID.clear()
            mViewModel.currentPageCountForAudio = -1
            mViewModel.currentPageCountForMusic = -1
            mViewModel.currentPageCountForVideo = -1

            mViewModel.isVideoResult = false
            mViewModel.isAudioResult = false
            mViewModel.isMusicResult = false
            callCategoryDataApi()
        } else {
            setDataAfterGetCategoryId(position)
        }
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mSubscriptionReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
        )
    }

    private fun onRefresh() {
        mViewModel.programDataList.clear()
        programDataAdapter.setProgramData(mViewModel.programDataList)

        mBinding.shimmerForVideoList.showShimmer(true)
        mBinding.shimmerForAudioList.showShimmer(true)
        mBinding.shimmerForMusicList.showShimmer(true)
        mBinding.shimmerForYou.showShimmer(true)

        mBinding.shimmerTitle.visible()
        mBinding.shimmerHeroImage.visible()
        mBinding.shimmerForVideoList.visible()
        mBinding.shimmerForAudioList.visible()
        mBinding.shimmerForMusicList.visible()
        mBinding.shimmerForYou.visible()

        mBinding.llTitle.gone()
        mBinding.ivHero.gone()
        mBinding.rvProgram.gone()
        mBinding.rvVideoList.gone()
        mBinding.rvMusicList.gone()
        mBinding.rvGuidedAudioList.gone()

        mBinding.lblNoProgram.gone()
        mBinding.tvNoVideo.gone()
        mBinding.tvNoAudio.gone()
        mBinding.tvNoMusic.gone()

        mBinding.swipeToRefreshLayout.isRefreshing = false
        mViewModel.isFilterSelected = false
        mViewModel.currentPageForProgram = -1
        mViewModel.currentPageCountForAudio = -1
        mViewModel.currentPageCountForMusic = -1
        mViewModel.currentPageCountForVideo = -1
        mViewModel.tagsID.clear()

        mViewModel.isVideoResult = false
        mViewModel.isAudioResult = false
        mViewModel.isMusicResult = false
        callCategoryDataApi()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observeApiCallbacks() {
        mViewModel.fetchCategoryDataResponse.observe(this@WellnessFragment) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.data?.list?.let { categoryList ->
                    mViewModel.categoryDataList.clear()

                    if (categoryList.size > 0) {
                        mViewModel.categoryDataList.addAll(categoryList)
                        setDataAfterGetCategoryId(position)
                    }
                }
            }

            mViewModel.fetchCategoryDataResponse.postValue(null)
        }

        mViewModel.fetchCategoryProgramById.observe(this@WellnessFragment) { response ->
            response?.let { requestState ->
                if (mViewModel.currentPageForProgram == 0) {
                    mViewModel.programDataList.clear()
                }

                requestState.apiResponse?.data?.let { data ->
                    mViewModel.totalForProgramCount = data.totalRecords ?: 0
                    mViewModel.programDataList.removeAll {
                        it == null
                    }

                    data.list?.let {
                        mViewModel.programDataList.addAll(it)
                    }
                    setProgramData()
                }
            }

            mViewModel.fetchCategoryProgramById.postValue(null)
        }

        mViewModel.fetchVideoContentByFilterResponse.observe(this@WellnessFragment) { response ->
            response?.let { requestState ->
                if (mViewModel.currentPageCountForVideo == 0) {
                    mViewModel.videoList.clear()
                }

                requestState.apiResponse?.data?.videoDetail?.let { data ->

                    mViewModel.isVideoResult = true
                    mViewModel.videoListCount = data.totalRecords ?: 0

                    mViewModel.videoList.removeAll {
                        it == null
                    }

                    data.list?.let {
                        mViewModel.videoList.addAll(it)
                    }
                    setVideoData()
                }
            }

            mViewModel.fetchVideoContentByFilterResponse.postValue(null)
        }

        mViewModel.fetchMusicContentByFilterResponse.observe(this@WellnessFragment) { response ->
            response?.let { requestState ->
                if (mViewModel.currentPageCountForMusic == 0) {
                    mViewModel.musicList.clear()
                }

                requestState.apiResponse?.data?.musicDetail?.let { data ->
                    mViewModel.isMusicResult = true
                    mViewModel.musicListCount = data.totalRecords ?: 0

                    mViewModel.musicList.removeAll {
                        it == null
                    }

                    data.list?.let {
                        mViewModel.musicList.addAll(it)
                    }

                    setMusicData()
                }
            }

            mViewModel.fetchMusicContentByFilterResponse.postValue(null)
        }

        mViewModel.fetchAudioContentByFilterResponse.observe(this@WellnessFragment) { response ->
            response?.let { requestState ->
                if (mViewModel.currentPageCountForAudio == 0) {
                    mViewModel.audioList.clear()
                }

                requestState.apiResponse?.data?.guidedMeditationAudioDetail?.let { data ->

                    mViewModel.isAudioResult = true
                    mViewModel.audioListCount = data.totalRecords ?: 0

                    mViewModel.audioList.removeAll {
                        it == null
                    }

                    data.list?.let {
                        mViewModel.audioList.addAll(it)
                    }
                    setAudioData()
                }
            }

            mViewModel.fetchAudioContentByFilterResponse.postValue(null)
        }

        mViewModel.fetchCategoryTagsByIdResponse.observe(this@WellnessFragment) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.data?.tagsDetails?.let { list ->
                    mViewModel.tagsDataList.clear()
                    mViewModel.tagsDataList.addAll(list)
                    mViewModel.selectedTagsDataList.clear()
                }
            }

            mViewModel.fetchCategoryTagsByIdResponse.postValue(null)
        }
    }


    private fun updateProfile() {
        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }


    private fun setUpCarouselsView() {

        var pageData: PageAdapter.Page = PageAdapter.Page(getString(R.string.physical_body))
        mViewModel.adapterData.add(pageData)

        pageData = PageAdapter.Page(getString(R.string.internal_organs))
        mViewModel.adapterData.add(pageData)

        pageData = PageAdapter.Page(getString(R.string.emotions))
        mViewModel.adapterData.add(pageData)

        pageData = PageAdapter.Page(getString(R.string.relationships_capital))
        mViewModel.adapterData.add(pageData)

        pageData = PageAdapter.Page(getString(R.string.body_channels))
        mViewModel.adapterData.add(pageData)

        val pageAdapter = PageAdapter(mViewModel.adapterData, object : PageAdapter.OnItemClicked {
            @SuppressLint("NotifyDataSetChanged")
            override fun onItemClicked(position: Int) {
                mBinding.carouselViewSideBySidePreviewScale.apply {
                    if (viewPager2.currentItem != position) {
                        viewPager2.currentItem = position

                        (adapter as PageAdapter).selectedPos = position
                        adapter.notifyDataSetChanged()
                        manageViewByPosition(position)

                    }
                    mBinding.viewPager.currentItem = position
                }
            }
        }).apply {
//            setData(adapterData)
        }

        mBinding.carouselViewSideBySidePreviewScale.apply {
            adapter = pageAdapter

            viewPager2.currentItem = 0
            viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

                @SuppressLint("NotifyDataSetChanged")
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    (adapter as PageAdapter).selectedPos = position
                    adapter.notifyDataSetChanged()

                    manageViewByPosition(position)
                }

            })
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(mSubscriptionReceiver)
        super.onDestroy()
    }

    override fun displayMessage(message: String) {
    }

    private fun callCategoryDataApi() {
        mViewModel.fetchAllCategoryData(this@WellnessFragment, mDisposable)
    }

    private fun callCategoryTagsByIdAPI(id: ArrayList<Int>) {
        mViewModel.fetchCategoryTagsById(
            id,
            this@WellnessFragment, mDisposable
        )
    }

    private fun callAudioContentAPI(id: Int) {
        mViewModel.currentPageCountForAudio += 1
        mViewModel.fetchAudioCategoryDetailContentByFilter(
            id,
            mViewModel.tagsID,
            Constants.Guided_Meditation_Audio,
            mViewModel.currentPageCountForAudio,
            mViewModel.perPageCount,
            mViewModel.searchText,
            this@WellnessFragment,
            mDisposable
        )
    }

    private fun callVideoContentAPI(id: Int) {
        mViewModel.currentPageCountForVideo += 1
        mViewModel.fetchVideoCategoryDetailContentByFilter(
            id,
            mViewModel.tagsID,
            Constants.VIDEO,
            mViewModel.currentPageCountForVideo,
            mViewModel.perPageCount,
            mViewModel.searchText,
            this@WellnessFragment,
            mDisposable
        )
    }

    private fun callMusicContentAPI(id: Int) {
        mViewModel.currentPageCountForMusic += 1
        mViewModel.fetchMusicCategoryDetailContentByFilter(
            id,
            mViewModel.tagsID,
            Constants.MUSIC,
            mViewModel.currentPageCountForMusic,
            mViewModel.perPageCount,
            mViewModel.searchText,
            this@WellnessFragment,
            mDisposable
        )
    }

    private fun callCategoryProgramListByIdAPI() {
        mViewModel.currentPageForProgram += 1

        mViewModel.fetchCategoryProgramListByID(
            mViewModel.categoryID,
            mViewModel.currentPageForProgram,
            mViewModel.perPageCount,
            mViewModel.searchText,
            this@WellnessFragment, mDisposable
        )
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.gone()
        mBinding.mToolbar.cardProfile.visible()
    }

    private fun initRecyclerView() {
        mBinding.rvProgram.adapter = programDataAdapter
        mBinding.rvFeatureSession.adapter = featureSessionAdapter
        mBinding.rvUpcomingSession.adapter = upcomingSessionAdapter
        mBinding.rvVideoList.adapter = mVideoAdapter
        mBinding.rvGuidedAudioList.adapter = mAudioAdapter
        mBinding.rvMusicList.adapter = mMusicAdapter
    }

    override fun postInit() {
    }

    override fun initObserver() {
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            requireActivity().onBackPressed()
        }
        mBinding.mToolbar.cardProfile.clickWithDebounce {
            (requireActivity() as MainActivity).hideWellnessDialog()
            UserMenuActivity.startActivity(activity as MainActivity)
        }
        mBinding.rvProgram.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvProgram.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvProgram.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if ((!mViewModel.isLoadingProgramData) and ((totalItem == (lastVisibleItem + 2)) or (totalItem == (lastVisibleItem + 3)))) {
                    mViewModel.isLoadingProgramData = true
                    callCategoryProgramListByIdAPI()
                }
            }
        })

        mBinding.rvVideoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvVideoList.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvVideoList.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()

                if ((!mViewModel.isLoadingVideoData) and ((totalItem == (lastVisibleItem + 2)) or (totalItem == (lastVisibleItem + 1)))) {
                    mViewModel.isLoadingVideoData = true
                    mViewModel.isVideoResult = false
                    callVideoContentAPI(mViewModel.idNeedToPassInCategory)
                }
            }
        })
        mBinding.rvGuidedAudioList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvGuidedAudioList.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvGuidedAudioList.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if ((!mViewModel.isLoadingAudioData) and ((totalItem == (lastVisibleItem + 2)) or (totalItem == (lastVisibleItem + 1)))) {
                    mViewModel.isLoadingAudioData = true
                    mViewModel.isAudioResult = false
                    callAudioContentAPI(mViewModel.idNeedToPassInCategory)
                }
            }
        })
        mBinding.rvMusicList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvMusicList.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvMusicList.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if ((!mViewModel.isLoadingMusicData) and ((totalItem == (lastVisibleItem + 2)) or (totalItem == (lastVisibleItem + 1)))) {
                    mViewModel.isLoadingMusicData = true
                    mViewModel.isMusicResult = false
                    callMusicContentAPI(mViewModel.idNeedToPassInCategory)
                }
            }
        })

        mBinding.btnFilter.setOnClickListener {
            showMeditationFilterDialog()
        }

        mBinding.tvProgramsSeeAll.setOnClickListener {
            ProgramsListActivity.startActivity(
                requireActivity() as AppCompatActivity,
                mViewModel.categoryID.toString()
            )
        }

        mBinding.btnBookConsultation.setOnClickListener {
            BookConsultationActivity.startActivity(requireActivity() as AppCompatActivity)
        }

        mBinding.llHideShow.setOnClickListener {
            if (isFieldHealingShow) {
                mBinding.llFieldHealing.visibility = View.GONE
                mBinding.ivArrowUpDown.rotation = 0f
                mBinding.tvHideShow.text = getString(R.string.show)
                isFieldHealingShow = false
            } else {
                mBinding.llFieldHealing.visibility = View.VISIBLE
                mBinding.ivArrowUpDown.rotation = 180f
                mBinding.tvHideShow.text = getString(R.string.hide)
                isFieldHealingShow = true
            }
        }

        mBinding.ivPBFemale.setOnClickListener {
            mBinding.ivPBMale.setImageResource(R.drawable.vd_male_icon)
            mBinding.ivPBFemale.setImageResource(R.drawable.vd_female_icon_select)
            mBinding.ivMalePhysicalBody.visibility = View.GONE
            mBinding.ivFemalePhysicalBody.visibility = View.VISIBLE
        }

        mBinding.ivPBMale.setOnClickListener {
            mBinding.ivPBFemale.setImageResource(R.drawable.vd_female_icon)
            mBinding.ivPBMale.setImageResource(R.drawable.vd_male_icon_select)
            mBinding.ivFemalePhysicalBody.visibility = View.GONE
            mBinding.ivMalePhysicalBody.visibility = View.VISIBLE
        }

        mBinding.ivMale.setOnClickListener {
            mBinding.ivFemale.setImageResource(R.drawable.vd_female_icon)
            mBinding.ivMale.setImageResource(R.drawable.vd_male_icon_select)
            mBinding.ivFemaleInternalOrgans.visibility = View.GONE
            mBinding.ivMaleInternalOrgans.visibility = View.VISIBLE
        }

        mBinding.ivFemale.setOnClickListener {
            mBinding.ivMale.setImageResource(R.drawable.vd_male_icon)
            mBinding.ivFemale.setImageResource(R.drawable.vd_female_icon_select)
            mBinding.ivMaleInternalOrgans.visibility = View.GONE
            mBinding.ivFemaleInternalOrgans.visibility = View.VISIBLE
        }

        mBinding.ivBodyChannelMale.setOnClickListener {
            mBinding.ivBodyChannelFeMale.setImageResource(R.drawable.vd_female_icon)
            mBinding.ivBodyChannelMale.setImageResource(R.drawable.vd_male_icon_select)
            mBinding.ivFemaleBody.visibility = View.GONE
            mBinding.ivMaleBody.visibility = View.VISIBLE
        }

        mBinding.ivBodyChannelFeMale.setOnClickListener {
            mBinding.ivBodyChannelMale.setImageResource(R.drawable.vd_male_icon)
            mBinding.ivBodyChannelFeMale.setImageResource(R.drawable.vd_female_icon_select)
            mBinding.ivMaleBody.visibility = View.GONE
            mBinding.ivFemaleBody.visibility = View.VISIBLE
        }

        mBinding.tvVideoSeeAll.setOnClickListener {
            AllMeditationsActivity.startActivity(
                requireActivity() as AppCompatActivity,
                mBinding.tvCategoryName.text.toString(),
                Constants.VIDEO,
                mViewModel.categoryDataList[position],
                mViewModel.subCatId
            )
        }

        mBinding.tvGuidedAudioSeeAll.setOnClickListener {
            AllMeditationsActivity.startActivity(
                requireActivity() as AppCompatActivity,
                mBinding.tvCategoryName.text.toString(),
                Constants.Guided_Meditation_Audio,
                mViewModel.categoryDataList[position],
                mViewModel.subCatId
            )
        }

        mBinding.tvMusicSeeAll.setOnClickListener {
            AllMeditationsActivity.startActivity(
                requireActivity() as AppCompatActivity,
                mBinding.tvCategoryName.text.toString(),
                Constants.MUSIC,
                mViewModel.categoryDataList[position],
                mViewModel.subCatId
            )
        }
    }

    private fun showMeditationFilterDialog() {
        mViewModel.isRefreshClicked = false
        mViewModel.selectedTagsDataList = mViewModel.tagsDataList.filter { it?.isSelected == true }
            .map { it?.id ?: 0 } as ArrayList<Int>
        val filterDialog = WellnessFilterDialog.newInstance(mViewModel,
            object : WellnessFilterDialog.OnWellnessFilterDialogClickListener {
                override fun onTagsSelectedClicked() {
                    initDisposable()
                    mViewModel.tagsDataList.map {
                        it?.isSelected = mViewModel.selectedTagsDataList.contains(it?.id)
                    }
                    getDataBasedOnFilterTags()
                }
            })

        filterDialog.show(
            getFragmentTransactionFromTag(WellnessFilterDialog.TAG),
            WellnessFilterDialog.TAG
        )
    }

    override fun onSessionClick() {
    }

    override fun onMultiSessionClicked(data: ProgramDataModel?) {
        NavigationHandler.handleNavigation(
            data!!,
            requireActivity() as AppCompatActivity,
            startSubscriptionActivityForResult
        )
        /*      data?.let { dataModel ->
                  if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == true && dataModel.isPurchased == false) {
      //                    GET
                      if (dataModel.type == Constants.content) {
                          if (dataModel.contentType == Constants.TEXT) {
                              ReadMeditateActivity.startActivity(
                                  requireActivity() as AppCompatActivity, dataModel.id ?: ""
                              )
                          } else {
                              MeditationDetailActivity.startActivity(
                                  requireActivity() as AppCompatActivity, dataModel.id ?: ""
                              )
                          }
                      } else {
                          activity?.let {
                              ProgramDetailsActivity.startActivity(
                                  it,
                                  dataModel.id ?: "",
                                  false
                              )
                          }
                      }
      //                        SubscriptionActivity.startActivity(activity as AppCompatActivity)
                  } else if (dataModel.isLocked == true && dataModel.isSubscribed == false) {
      //                    lock
                      if (dataModel.unlockDays != null) {
                          if (dataModel.unlockDays!! < 1)
                              showUnlockImageDialog(requireContext()) else {
                          }
                      } else if (dataModel.unlockDays == null) {
                          showUnlockImageDialog(requireContext())
                      } else {

                      }
                  } else if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == false) {
      //                        Subscribe
                      SubscriptionActivity.startActivityForResult(
                          activity as AppCompatActivity,
                          startSubscriptionActivityForResult
                      )
                  } else {
      //                    can access
                      activity?.let {
                          ProgramDetailsActivity.startActivity(
                              it,
                              dataModel.id ?: "",
                              false
                          )
                      }
                  }
              }*/
    }

    private fun setDataAfterGetCategoryId(position: Int) {
        if (mViewModel.categoryDataList.size > 0) {
            mViewModel.categoryID = mViewModel.categoryDataList[position].categoryId!!.toInt()

            mBinding.apply {
                tvCategoryName.text = mViewModel.categoryDataList[position].categoryName
                ivHero.loadImage(
                    mViewModel.categoryDataList[position].heroImage,
                    R.drawable.ic_hero_default
                )
                shimmerHeroImage.gone()
                ivHero.visible()

                shimmerTitle.gone()
                llTitle.visible()

                getFilteredText()

                if (mViewModel.categoryDataList[position].subCategoryDetails.isNotEmpty()) {
                    mViewModel.subCatList.clear()
                    mViewModel.subCatList.addAll(mViewModel.categoryDataList[position].subCategoryDetails)

                    wellnessSubCategoryAdapter.list.clear()
                    mBinding.rvCategory.adapter = wellnessSubCategoryAdapter
                    wellnessSubCategoryAdapter.list.addAll(mViewModel.subCatList)

                    if (mViewModel.subCatList.size > 0) {
                        mBinding.llSubCategory.visible()
                        mBinding.rvCategory.visible()
                    } else {
                        mBinding.llSubCategory.gone()
                        mBinding.rvCategory.gone()
                    }

                }
            }

            if (mViewModel.programDataList.isEmpty() || !mViewModel.isConfigChanges) {
                mViewModel.currentPageForProgram = -1
                callCategoryProgramListByIdAPI()
            } else {
                setProgramData()
            }

            mViewModel.idNeedToPassInCategory = mViewModel.categoryID

            if (mViewModel.audioList.isEmpty() || !mViewModel.isConfigChanges) {
                mViewModel.currentPageCountForAudio = -1
                callAudioContentAPI(if (mViewModel.subCatId == -1) mViewModel.categoryID else mViewModel.subCatId)
            } else {
                setAudioData()
            }

            if (mViewModel.videoList.isEmpty() || !mViewModel.isConfigChanges) {
                mViewModel.currentPageCountForVideo = -1
                callVideoContentAPI(if (mViewModel.subCatId == -1) mViewModel.categoryID else mViewModel.subCatId)
            } else {
                setVideoData()
            }

            if (mViewModel.musicList.isEmpty() || !mViewModel.isConfigChanges) {
                mViewModel.currentPageCountForMusic = -1
                callMusicContentAPI(if (mViewModel.subCatId == -1) mViewModel.categoryID else mViewModel.subCatId)
            } else {
                setMusicData()
            }

            val list: ArrayList<Int> = arrayListOf()
            list.add(mViewModel.categoryID)
            if (mViewModel.subCatId != -1) list.add(mViewModel.subCatId)

            if (mViewModel.tagsDataList.isEmpty() || !mViewModel.isConfigChanges) {
                callCategoryTagsByIdAPI(list)
            } /*else {
                setTagsData()
            }*/
        }
    }

    private fun manageViewByPosition(position: Int) {
        val internalOrgansList = arrayOf(
            getString(R.string.adrenal_glands),
            getString(R.string.blood),
            getString(R.string.blood_vessels),
            getString(R.string.bones),
            getString(R.string.brain),
            getString(R.string.gallbladder),
            getString(R.string.heart),
            getString(R.string.kidneys),
            getString(R.string.large_intestine),
            getString(R.string.liver),
            getString(R.string.lungs),
            getString(R.string.pancreas),
            getString(R.string.skin),
            getString(R.string.spinal_cord),
            getString(R.string.spleen),
            getString(R.string.stomach),
            getString(R.string.small_intestine)
        )
        val emotionsList = arrayOf(
            getString(R.string.happiness),
            getString(R.string.healing_anger),
            getString(R.string.healing) + "\n" + getString(R.string.depression) + "\n" + getString(R.string.anxiety),
            getString(R.string.healing_worry),
            getString(R.string.healing_sadness),
            getString(R.string.healing_fear),
            getString(R.string.healing) + "\n" + getString(R.string.addication)
        )

        val relationShipList = arrayOf(
            getString(R.string.true_love),
            getString(R.string.self_love),
            getString(R.string.parents),
            getString(R.string.children),
            getString(R.string.grandparents),
            getString(R.string.siblings),
            getString(R.string.pets),
            getString(R.string.friends),
            getString(R.string.colleagues)
        )

        when (position) {
            0 -> {
                mBinding.tvSelections.text = getString(R.string._16_selections)

                mBinding.llPhysicalBody.isVisible = true
                mBinding.llTwoThreeFour.isGone = true
                mBinding.llBodyChannel.isGone = true
            }
            1 -> {
                mBinding.tvSelections.text = getString(R.string._22_selections)

                mBinding.llPhysicalBody.isGone = true
                mBinding.llTwoThreeFour.isVisible = true
                mBinding.llBodyChannel.isGone = true

                val mAdapter = HealingNamesAdapter(
                    internalOrgansList,
                    object : HealingNamesAdapter.OnAdapterItemClickListener {
                        override fun onNameClick(mName: String) {

                        }
                    })

                mBinding.rvListOfNames.adapter = mAdapter
            }
            2 -> {
                mBinding.tvSelections.text = getString(R.string._7_selections)

                mBinding.llPhysicalBody.isGone = true
                mBinding.llTwoThreeFour.isVisible = true
                mBinding.llBodyChannel.isGone = true

                val mAdapter = HealingNamesAdapter(
                    emotionsList,
                    object : HealingNamesAdapter.OnAdapterItemClickListener {
                        override fun onNameClick(mName: String) {

                        }
                    })

                mBinding.rvListOfNames.adapter = mAdapter
            }
            3 -> {
                mBinding.tvSelections.text = getString(R.string._9_selections)

                mBinding.llPhysicalBody.isGone = true
                mBinding.llTwoThreeFour.isVisible = true
                mBinding.llBodyChannel.isGone = true

                val mAdapter = HealingNamesAdapter(
                    relationShipList,
                    object : HealingNamesAdapter.OnAdapterItemClickListener {
                        override fun onNameClick(mName: String) {

                        }
                    })

                mBinding.rvListOfNames.adapter = mAdapter
            }
            4 -> {
                mBinding.tvSelections.text = getString(R.string._3_selections)

                mBinding.llPhysicalBody.isGone = true
                mBinding.llTwoThreeFour.isGone = true
                mBinding.llBodyChannel.isVisible = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
        //categoryID = 0
        if (!TaoCalligraphy.instance.isSubscriptionStatusApiCalled)
            onRefresh()
    }

    override fun onFavouriteClicked(dataModel: ProgramDataModel) {
        mViewModel.setProgramFavorite(dataModel.id!!, false, this, mDisposable)
    }

    override fun onAudioAdapterClick(data: ContentData?) {
        NavigationHandler.handleContentNavigation(
            data!!,
            requireActivity() as AppCompatActivity,
            startSubscriptionActivityForResult
        )
    }

    override fun onMusicAdapterClick(data: ContentData?) {
        NavigationHandler.handleContentNavigation(
            data!!,
            requireActivity() as AppCompatActivity,
            startSubscriptionActivityForResult
        )
    }

    override fun onVideoAdapterClick(data: ContentData?) {
        NavigationHandler.handleContentNavigation(
            data!!,
            requireActivity() as AppCompatActivity,
            startSubscriptionActivityForResult
        )
//        data?.let { dataModel ->
//            if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == true && dataModel.isPurchased == false) {
////                    GET
//                MeditationDetailActivity.startActivity(
//                    requireActivity() as AppCompatActivity, dataModel.id.toString()
//                )
////                        SubscriptionActivity.startActivity(activity as AppCompatActivity)
//            } else if (dataModel.isLocked == true && dataModel.isSubscribed == false) {
////                    lock
//                if (dataModel.unlockDays != null) {
//                    if (dataModel.unlockDays!! < 1)
//                        showUnlockImageDialog(requireContext()) else {
//                    }
//                } else if (dataModel.unlockDays == null) {
//                    showUnlockImageDialog(requireContext())
//                } else {
//
//                }
//            } else if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == false) {
////                        Subscribe
//                SubscriptionActivity.startActivityForResult(
//                    activity as AppCompatActivity,
//                    startSubscriptionActivityForResult
//                )
//            } else {
////                    can access
//                MeditationDetailActivity.startActivity(
//                    requireActivity() as AppCompatActivity, dataModel.id.toString()
//                )
//            }
//        }
    }

    override fun onAudioAdapterFavoriteClick(id: String, adapterPosition: Int) {
        mViewModel.favoriteMeditationContent(
            id,
            this@WellnessFragment,
            mDisposable
        )
    }

    override fun onMusicAdapterFavoriteClick(id: String, adapterPosition: Int) {
        mViewModel.favoriteMeditationContent(
            id,
            this@WellnessFragment,
            mDisposable
        )
    }

    override fun onVideoAdapterFavoriteClick(id: String, adapterPosition: Int) {
        mViewModel.favoriteMeditationContent(
            id,
            this@WellnessFragment,
            mDisposable
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
        if (model.isAvailable) {
            initView()
            noInternetConnectionDialog?.dismiss()
        } else {
            if (isAdded) {
                showNoInternetDialog()
            }
        }
    }

    private fun getFilteredText() {
        val tagNameList = mViewModel.tagsDataList.filter {
            it?.isSelected == true
        }.map {
            it?.name
        }
        val s: String = TextUtils.join(", ", tagNameList)
        val subCatName = mViewModel.subCatList.find {
            it?.isSelected == true
        }?.name

        val typefaceNormal = TextAppearanceSpan(context, R.style.TextViewJostRegularStyle)
        val typefaceSemiBold = TextAppearanceSpan(context, R.style.TextViewJostSemiBoldStyle)
        val typefaceBold = TextAppearanceSpan(context, R.style.TextViewJostBoldStyle)

        val spannableFilteredText = SpannableStringBuilder()
        spannableFilteredText.let {
            if (mViewModel.videoListCount + mViewModel.musicListCount + mViewModel.audioListCount > 0) {
                it.append((mViewModel.videoListCount + mViewModel.musicListCount + mViewModel.audioListCount).toString())
                mBinding.run {
                    llVideoTitle.visible()
                    llGuidedAudioTitle.visible()
                    llMusicTitle.visible()
                }
            } else {
                it.append(getString(R.string.no_results_found))

                mBinding.run {
                    llVideoTitle.gone()
                    llGuidedAudioTitle.gone()
                    llMusicTitle.gone()
                    tvNoVideo.gone()
                    tvNoAudio.gone()
                    tvNoMusic.gone()
                }
            }
            var start = 0
            var end = it.length
            it.setSpan(
                typefaceBold,
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            it.append(" ${getString(R.string.str_in)}")
            start = end + 1
            end = it.length

            it.setSpan(
                typefaceNormal,
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            it.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.medium_grey)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            it.append(" ${mViewModel.categoryDataList[position].categoryName} ")
            start = end + 1
            end = it.length
            it.setSpan(
                typefaceBold,
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (subCatName != null || s.isNotEmpty()) {
                it.append(getString(R.string.str_arrow))

                it.setSpan(typefaceBold, end, it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        val filterSpan = SpannableStringBuilder()
        if (subCatName != null) {
            filterSpan.append(" $subCatName ")
            val start = 0
            val end = filterSpan.length

            filterSpan.setSpan(
                typefaceSemiBold,
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (s.length > 1) {
                filterSpan.append(getString(R.string.str_arrow))

                filterSpan.setSpan(
                    typefaceBold,
                    end,
                    filterSpan.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        if (s.length > 1) {
            filterSpan.append(" $s")

            val start = 0
            val end = filterSpan.length

            filterSpan.setSpan(
                typefaceSemiBold,
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        filterSpan.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.gold)),
            0,
            filterSpan.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val finalString = SpannableStringBuilder()
        finalString.append(spannableFilteredText)
        finalString.append(filterSpan)

        mBinding.txtFilteredData.text = finalString

        if (mViewModel.isFilterSelected) {
            mBinding.filterRemoved.gone()
            mBinding.filterImage.visible()
        } else {
            mBinding.filterRemoved.visible()
            mBinding.filterImage.gone()

            if (mViewModel.videoListCount + mViewModel.musicListCount + mViewModel.audioListCount > 0) {
                mBinding.btnFilter.visible()
            } else {
                mBinding.btnFilter.gone()
            }
        }
    }

    private val mSubscriptionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            onRefresh()
        }
    }

    private fun setProgramData() {
        mBinding.shimmerForYou.hideShimmer()
        mBinding.shimmerForYou.gone()

        mViewModel.isLoadingProgramData =
            mViewModel.programDataList.size >= mViewModel.totalForProgramCount

        if (mViewModel.programDataList.size > 0) {
            mBinding.apply {
                rvProgram.visible()
                lblNoProgram.gone()
            }
        } else {
            mBinding.apply {
                rvProgram.gone()
                lblNoProgram.visible()
            }
        }

        if (!mViewModel.isLoadingProgramData)
            mViewModel.programDataList.add(null)

        programDataAdapter.setProgramData(mViewModel.programDataList)
    }

    private fun setAudioData() {
        mBinding.shimmerForAudioList.hideShimmer()
        mBinding.shimmerForAudioList.gone()
        mBinding.rvGuidedAudioList.visible()

        mViewModel.isLoadingAudioData = mViewModel.audioList.size >= mViewModel.audioListCount

        if (mViewModel.audioList.size > 0) {
            mBinding.apply {
                tvNoAudio.gone()
            }
        } else {
            mBinding.apply {
                rvGuidedAudioList.gone()
                tvNoAudio.visible()
            }
        }

        if (mViewModel.isVideoResult && mViewModel.isAudioResult && mViewModel.isMusicResult)
            getFilteredText()

        if (!mViewModel.isLoadingAudioData)
            mViewModel.audioList.add(null)

        mAudioAdapter.setAudioData(mViewModel.audioList)
    }

    private fun setMusicData() {
        mBinding.shimmerForMusicList.hideShimmer()
        mBinding.shimmerForMusicList.gone()
        mBinding.rvMusicList.visible()

        mViewModel.isLoadingMusicData =
            mViewModel.musicList.size >= mViewModel.musicListCount

        if (mViewModel.musicList.size > 0) {
            mBinding.apply {
                tvNoMusic.gone()
            }
        } else {
            mBinding.apply {
                rvMusicList.gone()
                tvNoMusic.visible()
            }
        }

        if (mViewModel.isVideoResult && mViewModel.isAudioResult && mViewModel.isMusicResult)
            getFilteredText()

        if (!mViewModel.isLoadingMusicData)
            mViewModel.musicList.add(null)

        mMusicAdapter.setMusicData(mViewModel.musicList)
    }

    private fun setVideoData() {
        mBinding.shimmerForVideoList.hideShimmer()
        mBinding.shimmerForVideoList.gone()
        mBinding.rvVideoList.visible()

        mViewModel.isLoadingVideoData = mViewModel.videoList.size >= mViewModel.videoListCount

        if (mViewModel.videoList.size > 0) {
            mBinding.apply {
                tvNoVideo.gone()
            }
        } else {
            mBinding.apply {
                rvVideoList.gone()
                tvNoVideo.visible()
            }
        }
        if (mViewModel.isVideoResult && mViewModel.isAudioResult && mViewModel.isMusicResult)
            getFilteredText()

        if (!mViewModel.isLoadingVideoData)
            mViewModel.videoList.add(null)

        mVideoAdapter.setVideoData(mViewModel.videoList)
    }

    override fun onDestroyView() {
        mViewModel.isConfigChanges = requireActivity().isChangingConfigurations
        super.onDestroyView()
    }

    private fun removeLoadersFromList() {
        mViewModel.audioList.removeAll {
            it == null
        }
        mViewModel.videoList.removeAll {
            it == null
        }
        mViewModel.musicList.removeAll {
            it == null
        }
        mViewModel.programDataList.removeAll {
            it == null
        }
    }

    private fun getDataBasedOnFilterTags() {
        val tagNameList = mViewModel.tagsDataList.filter {
            it?.isSelected == true
        }.map {
            it?.name
        }
        mViewModel.isFilterSelected = tagNameList.isNotEmpty()

        callContentApiForTopicFilter()
    }
}