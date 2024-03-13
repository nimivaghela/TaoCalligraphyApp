package com.app.taocalligraphy.ui.search

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentSearchBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.SearchContentDatamodel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.NavigationHandler
import com.app.taocalligraphy.ui.calendar.dialog.PaidSessionBookingDialog
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.home.adapter.WellnessCategoryAdapter
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.CommunityRoomsAdapter
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.search.adapter.SearchLiveSessionsListAdapter
import com.app.taocalligraphy.ui.search.adapter.SearchMeditationsListAdapter
import com.app.taocalligraphy.ui.search.adapter.SearchProgramAdapter
import com.app.taocalligraphy.ui.search.adapter.SearchTextAdapter
import com.app.taocalligraphy.ui.search.viewmodel.SearchViewModel
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(),
    WellnessCategoryAdapter.OnItemClickListener,
    CommunityRoomsAdapter.OnCommunityRoomsItemClickListener,
    SearchLiveSessionsListAdapter.SignupOptionListener,
    SearchMeditationsListAdapter.OnItemClickListener,
    SearchTextAdapter.OnItemListener, SearchProgramAdapter.OnMultiSessionListener {

    val mViewModel: SearchViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    private val mWellnessCategoryAdapter by lazy {
        return@lazy WellnessCategoryAdapter(this)
    }
    private val mSearchTextAdapter by lazy {
        return@lazy SearchTextAdapter(this)
    }
    private val mSearchMeditationsListAdapter by lazy {
        return@lazy SearchMeditationsListAdapter(this)
    }
    private val mSearchProgramsListAdapter by lazy {
        return@lazy SearchProgramAdapter(this)
    }
    private val mSearchLiveSessionsListAdapter by lazy {
        return@lazy SearchLiveSessionsListAdapter(this)
    }
    private val mCommunityRoomsAdapter by lazy {
        return@lazy CommunityRoomsAdapter(this)
    }

    override fun getLayoutId() = R.layout.fragment_search

    override fun displayMessage(message: String) {

    }

    val startSubscriptionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data?.getBooleanExtra("isSubscribed", false) == true) {
                    mViewModel.currentPageSearchAll = -1
                    mViewModel.meditationContentList.clear()
                    mViewModel.programList.clear()
                    manageViewAfterSearchApi()
                }
            }

        }

    @SuppressLint("NotifyDataSetChanged")
    override fun observeApiCallbacks() {
        mViewModel.fetchSearchedKeywordResponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { response ->
                        response.searchedKeyword?.let { list ->
                            mViewModel.searchedKeywordList.clear()
                            mViewModel.searchedKeywordList.addAll(list)
                            mSearchTextAdapter.setSearchData(mViewModel.searchedKeywordList)
                        }
                    }
                }
                longToastState(requestState.error)
            }

            mViewModel.fetchSearchedKeywordResponse.postValue(null)
        }

        mViewModel.fetchCategoryDataResponse.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse.let { fetchCategoryData ->
                    fetchCategoryData?.let { data ->
                        mViewModel.categoryDataList.clear()
                        data.data?.list?.let { list ->
                            mViewModel.categoryDataList.addAll(list)
                            showCategoriesData()
                        }
                    }
                }
                longToastState(requestState.error)
            }

            mViewModel.fetchCategoryDataResponse.postValue(null)
        }

        mViewModel.searchDataByAllTypeResponse.observe(this) { response ->
            response?.let { requestState ->
                if (mViewModel.currentPageSearchAll == 0) {
                    showProgressIndicator(mBinding.llProgress, requestState.progress)
                    mViewModel.meditationContentList.clear()
                    mViewModel.programList.clear()
                }
                requestState.apiResponse?.data?.let { searchResult ->
                    showProgressIndicator(mBinding.llProgress, false)
                    mViewModel.totalSearchMeditationCount = searchResult.totalResults ?: 0
                    mBinding.tvTotalResultCount.text = mViewModel.totalSearchMeditationCount.toString()
                    searchResult.meditationContentList?.let { it ->
                        mViewModel.totalSortSearchMeditationCount = it.totalRecords
                        mViewModel.meditationContentList.removeAll { data ->
                            data == null
                        }
                        mViewModel.meditationContentList.addAll(it.list!!)
                        setMeditationData()
                    } ?: run {
                        hideRecyclerView()
                    }

                    searchResult.programsList?.let { it ->
                        mViewModel.totalSortSearchProgramCount = it.totalRecords
                        mViewModel.programList.removeAll { data ->
                            data == null
                        }
                        mViewModel.programList.addAll(it.list!!)
                        setProgramData()
                    } ?: run {
                        hideProgramView()
                    }
                }
                longToastState(requestState.error)
            }

            mViewModel.searchDataByAllTypeResponse.postValue(null)
        }

        mViewModel.contentSortingApiResponse.observe(this) { response ->
            response?.let { requestState ->
                if (mViewModel.currentPageSortSearchMeditation == 0) {
                    clearMeditationList()
                    showProgressIndicator(mBinding.llProgress, requestState.progress)
                }
                requestState.apiResponse?.data?.list?.let { response ->
                    showProgressIndicator(mBinding.llProgress, false)
                    mViewModel.meditationContentList.removeAll {
                        it == null
                    }

                    mViewModel.meditationContentList.addAll(response)
                    setMeditationData()
                }
                longToastState(requestState.error)
            }

            mViewModel.contentSortingApiResponse.postValue(null)
        }

        mViewModel.programSortingApiResponse.observe(this) { response ->
            response?.let { requestState ->
                if (mViewModel.currentPageSortSearchProgram == 0) {
                    clearProgramList()
                    showProgressIndicator(mBinding.llProgress, requestState.progress)
                }
                requestState.apiResponse?.data?.list?.let { response ->
                    showProgressIndicator(mBinding.llProgress, false)

                    mViewModel.programList.removeAll {
                        it == null
                    }

                    mViewModel.programList.addAll(response)
                    setProgramData()
                }
                longToastState(requestState.error)
            }
            mViewModel.programSortingApiResponse.postValue(null)
        }

        mViewModel.favouriteMeditationContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let {

                }
                longToastState(requestState.error)
            }
        }
    }

    private fun hideRecyclerView() {
        mViewModel.meditationContentList.clear()
        mBinding.rvMeditationsList.gone()
        mBinding.lblNoMeditation.visible()
    }

    private fun hideProgramView() {
        mViewModel.programList.clear()
        mBinding.rvProgramsList.gone()
        mBinding.lblNoProgram.visible()
    }

    override fun initView() {
        mViewModel.programList.removeAll { data ->
            data == null
        }
        mViewModel.meditationContentList.removeAll { data ->
            data == null
        }
        mBinding.shimmerForYouWellnessFirstRow.showShimmer(true)

        setupToolbar()
        setUpData()

        mBinding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mBinding.etSearch.text.toString().trim().isNotEmpty()) {
                    requireActivity().runOnUiThread {
                        mViewModel.currentPageSearchAll = -1
                        mViewModel.currentPageSortSearchProgram = -1
                        mViewModel.currentPageSortSearchMeditation = -1
                        mViewModel.meditationContentList.clear()
                        mViewModel.programList.clear()
                        manageViewAfterSearchApi()
                    }
                } else {
                    activity?.longToast(
                        getString(R.string.please_enter_what_s_on_your_mind_with_question),
                        ERROR
                    )
                }
            }
            false
        }

        mBinding.btnGo.setOnClickListener {
            if (mBinding.etSearch.text.toString().trim().isNotEmpty()) {
                requireActivity().runOnUiThread {
                    mViewModel.currentPageSearchAll = -1
                    mViewModel.currentPageSortSearchProgram = -1
                    mViewModel.currentPageSortSearchMeditation = -1
                    mViewModel.meditationContentList.clear()
                    mViewModel.programList.clear()
                    manageViewAfterSearchApi()
                }
            } else {
                activity?.longToast(
                    getString(R.string.please_enter_what_s_on_your_mind_with_question),
                    ERROR
                )
            }
        }

        mBinding.etSearch.addTextChangedListener { text ->
            mViewModel.searchKeyword = text.toString()
            text?.length?.let {
                if (it >= 1) {
                    mViewModel.searchKeyword = text.toString()
                    mBinding.rlSearchBackground.setBackgroundResource(R.drawable.bg_gray95_border_gold_35dp)
                    mBinding.ivClearSearch.visibility = View.VISIBLE
                } else {
                    mViewModel.currentPageSearchAll = -1
                    mBinding.rlSearchBackground.setBackgroundResource(R.drawable.bg_gray95_border_white_35dp)
                    mBinding.ivClearSearch.visibility = View.GONE
                    mBinding.llAfterSearch.visibility = View.GONE
                    mBinding.llBeforeSearch.visibility = View.VISIBLE
                }
            } ?: kotlin.run {

            }
        }

        mBinding.root.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            mBinding.root.getWindowVisibleDisplayFrame(rect)
            val screenHeight = mBinding.root.rootView.height
            val keyboardHeight = screenHeight - rect.bottom
            if (keyboardHeight > screenHeight * 0.15) {
                mBinding.rlSearchBackground.setBackgroundResource(R.drawable.bg_gray95_border_gold_35dp)
            } else {
                if (mBinding.etSearch.text.toString().isEmpty()) {
                    mBinding.rlSearchBackground.setBackgroundResource(R.drawable.bg_gray95_border_white_35dp)
                }
            }
        }

        setAclView()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mSubscriptionReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
        )
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )

        if (mViewModel.searchKeyword.isEmpty() && requireActivity().isNetwork()) {
            if (mViewModel.searchedKeywordList.isEmpty())
                callFetchSearchKeywordAPI()
            else
                mSearchTextAdapter.setSearchData(mViewModel.searchedKeywordList)

            if (mViewModel.categoryDataList.isEmpty())
                callFetchWellnessCategoryAPI()
            else
                showCategoriesData()
        } else {
            mBinding.etSearch.setText(mViewModel.searchKeyword)
            mBinding.llBeforeSearch.visibility = View.GONE
            mBinding.llAfterSearch.visibility = View.VISIBLE
            if (mViewModel.meditationContentList.isEmpty() || mViewModel.programList.isEmpty()) {
                mViewModel.currentPageSearchAll = -1
                mViewModel.currentPageSortSearchMeditation = -1
                mViewModel.currentPageSortSearchProgram = -1
                callSearchAllTypeDataAPI()
            } else {
                mBinding.tvTotalResultCount.text = mViewModel.totalSearchMeditationCount.toString()
                setMeditationData()
                setProgramData()
            }

            when (mViewModel.sortFieldMeditation) {
                Constants.latest -> mBinding.tvLatestMeditation.text = getString(R.string.latest)
                Constants.A_Z -> mBinding.tvLatestMeditation.text = getString(R.string.a_to_z)
            }

            when (mViewModel.sortFieldProgram) {
                Constants.latest -> mBinding.tvLatestProgram.text = getString(R.string.latest)
                Constants.A_Z -> mBinding.tvLatestProgram.text = getString(R.string.a_to_z)
            }
        }
    }


    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            setAclView()
        }
    }

    private fun setAclView() {
        if (UserHolder.EnumUserModulePermission.CONTENT_LIBRARY.permission?.canAccess ?: false) {
            mBinding.rvSelectCategorySearch.isEnabled = true
            mBinding.rvSelectCategorySearch.alpha = 1f
            mBinding.ivSearchTitleLock.gone()
        } else {
            mBinding.rvSelectCategorySearch.isEnabled = false
            mBinding.rvSelectCategorySearch.alpha = 0.5f
            mBinding.ivSearchTitleLock.visible()
        }
    }

    private fun manageViewAfterSearchApi() {
        mBinding.llBeforeSearch.visibility = View.GONE
        mBinding.llAfterSearch.visibility = View.VISIBLE
        if (requireActivity().isNetwork())
            callSearchAllTypeDataAPI()
    }

    private fun callSearchAllTypeDataAPI() {
        mViewModel.searchDataByAllTypeResponse.value = null
        mViewModel.searchAPI(
            this,
            mDisposable
        )
    }

    private fun callFetchWellnessCategoryAPI() {
        mViewModel.fetchCategoryDataResponse.value = null
        mViewModel.fetchAllCategoryData(this@SearchFragment, mDisposable)
    }

    private fun callFetchSearchKeywordAPI() {
        mViewModel.fetchSearchedKeywordResponse.value = null
        mViewModel.fetchSearchedKeywordAPI(this, mDisposable)
    }

    private fun callContentSortingAPI() {
        mViewModel.contentSortingApiResponse.value = null
        mViewModel.contentSortingAPI(
            this,
            mDisposable
        )
    }

    private fun callProgramSortingAPI() {
        mViewModel.programSortingApiResponse.value = null
        mViewModel.programSortingAPI(
            this,
            mDisposable
        )
    }

    private fun setupToolbar() {
        mBinding.mToolbar.cardProfile.visible()

        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    private fun setUpData() {
        mBinding.rvSearchText.adapter = mSearchTextAdapter
        mBinding.rvSelectCategorySearch.adapter = mWellnessCategoryAdapter
        mBinding.rvMeditationsList.adapter = mSearchMeditationsListAdapter
        mBinding.rvLiveSessionsList.adapter = mSearchLiveSessionsListAdapter
        mBinding.rvMeditationsRoomsList.adapter = mCommunityRoomsAdapter
        mBinding.rvProgramsList.adapter = mSearchProgramsListAdapter
    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    fun clearSearch() {
        mBinding.rvSearchText.scrollToPosition(0)
        mBinding.etSearch.setText("")
        mViewModel.searchKeyword = ""
    }

    override fun handleListener() {
        mBinding.rvMeditationsList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvMeditationsList.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvMeditationsList.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if ((!mViewModel.isLoadingSearchMeditation) and ((totalItem == (lastVisibleItem + 2)) or (totalItem == (lastVisibleItem + 3)))) {
                    mViewModel.isLoadingSearchMeditation = true
                    if (requireActivity().isNetwork())
                        callContentSortingAPI()
                }
            }
        })

        mBinding.rvProgramsList.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvProgramsList.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvProgramsList.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if ((!mViewModel.isLoadingSearchProgram) and ((totalItem == (lastVisibleItem + 2)) or (totalItem == (lastVisibleItem + 3)))) {
                    mViewModel.isLoadingSearchProgram = true
                    if (requireActivity().isNetwork())
                        callProgramSortingAPI()
                }
            }
        })

        mBinding.ivClearSearch.clickWithDebounce {
            mViewModel.currentPageSearchAll = -1
            mViewModel.currentPageSortSearchMeditation = -1
            mViewModel.currentPageSortSearchProgram = -1
            mBinding.etSearch.setText("")
            mViewModel.searchKeyword = ""
            mBinding.rlSearchBackground.setBackgroundResource(R.drawable.bg_gray95_border_white_35dp)
            mBinding.ivClearSearch.visibility = View.GONE
            mBinding.llAfterSearch.visibility = View.GONE
            mBinding.llBeforeSearch.visibility = View.VISIBLE

            mViewModel.sortFieldMeditation = Constants.latest
            mViewModel.sortOrderMeditation = Constants.desc

            mViewModel.sortFieldProgram = Constants.latest
            mViewModel.sortOrderProgram = Constants.desc

            clearMeditationList()
            clearProgramList()

            mBinding.tvLatestMeditation.text = getString(R.string.latest)
            mBinding.tvLatestProgram.text = getString(R.string.latest)
            if (requireActivity().isNetwork()) {
                callFetchSearchKeywordAPI()
                callFetchWellnessCategoryAPI()
            }
        }

        mBinding.llFilterLatestMeditation.clickWithDebounce {
            mBinding.tvLatestMeditation.text = getString(R.string.latest)
            manageVisibilityLatestFilter()
        }

        mBinding.tvLatestMeditation.clickWithDebounce {
            manageVisibilityLatestFilter()
            mViewModel.sortFieldMeditation = Constants.latest
            mViewModel.sortOrderMeditation = Constants.desc
            mBinding.tvLatestMeditation.text = getString(R.string.latest)
            mViewModel.currentPageSortSearchMeditation = -1
            if (requireActivity().isNetwork() && mBinding.llMeditationLatestAtoZ.isGone)
                callContentSortingAPI()
        }
        mBinding.tvMeditationAtoZ.clickWithDebounce {
            manageVisibilityLatestFilter()
            mViewModel.sortFieldMeditation = Constants.A_Z
            mViewModel.sortOrderMeditation = Constants.asc
            mBinding.tvLatestMeditation.text = getString(R.string.a_to_z)
            mViewModel.currentPageSortSearchMeditation = -1
            if (requireActivity().isNetwork() && mBinding.llMeditationLatestAtoZ.isGone)
                callContentSortingAPI()
        }

        mBinding.llFilterLatestProgram.clickWithDebounce {
            mBinding.tvLatestProgram.text = getString(R.string.latest)
            manageVisibilityLatestFilterProgram()
        }

        mBinding.tvLatestProgram.clickWithDebounce {
            manageVisibilityLatestFilterProgram()
            mViewModel.sortFieldProgram = Constants.latest
            mViewModel.sortOrderProgram = Constants.desc
            mBinding.tvLatestProgram.text = getString(R.string.latest)
            mViewModel.currentPageSortSearchProgram = -1
            if (requireActivity().isNetwork() && mBinding.llProgramLatestAtoZ.isGone)
                callProgramSortingAPI()
        }
        mBinding.tvProgramAtoZ.clickWithDebounce {
            manageVisibilityLatestFilterProgram()
            mViewModel.sortFieldProgram = Constants.A_Z
            mViewModel.sortOrderProgram = Constants.asc
            mBinding.tvLatestProgram.text = getString(R.string.a_to_z)
            mViewModel.currentPageSortSearchProgram = -1
            if (requireActivity().isNetwork() && mBinding.llProgramLatestAtoZ.isGone)
                callProgramSortingAPI()
        }

        mBinding.llFilterLatestMeditationRooms.clickWithDebounce {
            if (mBinding.llRatingAtoZ.isVisible) {
                mBinding.llRatingAtoZ.isGone = true
                mBinding.ivDownUpArrowMeditationRooms.rotation = 0f
            } else {
                mBinding.llRatingAtoZ.isVisible = true
                mBinding.ivDownUpArrowMeditationRooms.rotation = 180f
            }
        }

        mBinding.mToolbar.cardProfile.clickWithDebounce {
            (requireActivity() as MainActivity).hideWellnessDialog()
            UserMenuActivity.startActivity(activity as MainActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        mWellnessCategoryAdapter.setSelectedCategoryPosition(-1)
    }

    private fun manageVisibilityLatestFilter() {
        if (mBinding.llMeditationLatestAtoZ.isVisible) {
            mBinding.llMeditationLatestAtoZ.gone()
            mBinding.ivDownUpArrowMeditation.rotation = 0f
        } else {
            mBinding.llMeditationLatestAtoZ.visible()
            mBinding.ivDownUpArrowMeditation.rotation = 180f
        }
    }

    private fun manageVisibilityLatestFilterProgram() {
        if (mBinding.llProgramLatestAtoZ.isVisible) {
            mBinding.llProgramLatestAtoZ.gone()
            mBinding.ivDownUpArrowProgram.rotation = 0f
        } else {
            mBinding.llProgramLatestAtoZ.visible()
            mBinding.ivDownUpArrowProgram.rotation = 180f
        }
    }

    override fun onItemCategoryClick(position: Int) {
        if (!(UserHolder.EnumUserModulePermission.CONTENT_LIBRARY.permission?.canAccess ?: false)) {
            SubscriptionActivity.startActivityForResult(requireActivity() as AppCompatActivity)
            return
        }

        (activity as MainActivity).setWellNessFragment(position)
    }

    override fun onCommunityRoomsAdapterClick(position: Int) {

    }

    override fun onSignUpClick() {
        val dialog = PaidSessionBookingDialog(requireContext())
        dialog.show()
    }

    override fun onSearchItemClicked(data: SearchContentDatamodel) {
        NavigationHandler.handleSearchNavigation(
            data,
            requireActivity() as AppCompatActivity,
            startSubscriptionActivityForResult
        )
    }

    override fun onSearchTextItemClicked(data: String) {
        mViewModel.searchKeyword = data
        mBinding.etSearch.setText(mViewModel.searchKeyword)
        manageViewAfterSearchApi()
    }


    override fun isFavoriteSelected(data: SearchContentDatamodel) {
        mViewModel.favoriteMeditationContent(
            data.id ?: "",
            this,
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

    private val mSubscriptionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            mViewModel.currentPageSearchAll = -1
            mViewModel.meditationContentList.clear()
            mViewModel.currentPageSortSearchProgram = -1
            mViewModel.programList.clear()
            if (mViewModel.searchKeyword.isEmpty() && requireActivity().isNetwork()) {
                callFetchSearchKeywordAPI()
                callFetchWellnessCategoryAPI()
            } else {
                mBinding.etSearch.setText(mViewModel.searchKeyword)
                manageViewAfterSearchApi()
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(mSubscriptionReceiver)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mAccessLevelReceiver)
        super.onDestroy()
    }

    override fun onMultiSessionClicked(dataModel: ProgramDataModel) {
        NavigationHandler.handleNavigation(
            dataModel,
            requireActivity() as AppCompatActivity,
            startSubscriptionActivityForResult
        )
    }

    override fun onFavouriteClicked(dataModel: ProgramDataModel) {
        mViewModel.setProgramFavorite(
            dataModel.id!!,
            false,
            this,
            mDisposable
        )
    }

    private fun showCategoriesData() {
        mWellnessCategoryAdapter.setCategoryData(mViewModel.categoryDataList)
        mBinding.shimmerForYouWellnessFirstRow.gone()
        mBinding.rvSelectCategorySearch.visible()
    }

    private fun setProgramData() {
        mViewModel.isLoadingSearchProgram =
            mViewModel.programList.size >= mViewModel.totalSortSearchProgramCount

        if (!mViewModel.isLoadingSearchProgram)
            mViewModel.programList.add(null)

        mSearchProgramsListAdapter.setProgramData(mViewModel.programList)

        if (mViewModel.programList.size > 0) {
            mBinding.rvProgramsList.visible()
            mBinding.lblNoProgram.gone()
        } else {
            hideProgramView()
        }

        mBinding.llFilterLatestProgram.isVisible = mViewModel.totalSortSearchProgramCount > 0
    }

    private fun setMeditationData() {
        mViewModel.isLoadingSearchMeditation =
            mViewModel.meditationContentList.size >= mViewModel.totalSortSearchMeditationCount

        if (!mViewModel.isLoadingSearchMeditation)
            mViewModel.meditationContentList.add(null)

        mSearchMeditationsListAdapter.setMeditationData(mViewModel.meditationContentList)

        if (mViewModel.meditationContentList.size > 0) {
            mBinding.rvMeditationsList.visible()
            mBinding.lblNoMeditation.gone()
        } else {
            hideRecyclerView()
        }

        mBinding.llFilterLatestMeditation.isVisible = mViewModel.totalSortSearchMeditationCount > 0
    }

    private fun clearMeditationList() {
        mViewModel.meditationContentList.clear()
        mSearchMeditationsListAdapter.setMeditationData(mViewModel.meditationContentList)
    }

    private fun clearProgramList() {
        mViewModel.programList.clear()
        mSearchProgramsListAdapter.setProgramData(mViewModel.programList)
    }
}