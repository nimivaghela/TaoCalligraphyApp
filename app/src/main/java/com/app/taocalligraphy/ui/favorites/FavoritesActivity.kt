package com.app.taocalligraphy.ui.favorites

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
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityFavoritesBinding
import com.app.taocalligraphy.models.dummy_models.RoomDummyDataModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.favorite_models.FavoriteContentDataModel
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.NavigationHandler
import com.app.taocalligraphy.ui.calendar.dialog.PaidSessionBookingDialog
import com.app.taocalligraphy.ui.favorites.adapter.FavoriteMeditationsListAdapter
import com.app.taocalligraphy.ui.favorites.adapter.FavoritesProgramAdapter
import com.app.taocalligraphy.ui.favorites.view_model.FavoriteViewModel
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.meditation.MeditationDetailActivity
import com.app.taocalligraphy.ui.meditation_rooms_detail.TaoCalligraphyFieldsRoomsActivity
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.CommunityRoomsAdapter
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.FeaturedRoomsAdapter
import com.app.taocalligraphy.ui.meditation_rooms_list.adapter.OfficialRoomsAdapter
import com.app.taocalligraphy.ui.program.ProgramDetailsActivity
import com.app.taocalligraphy.ui.program.ProgramsListActivity
import com.app.taocalligraphy.ui.search.adapter.SearchLiveSessionsListAdapter
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@AndroidEntryPoint
class FavoritesActivity : BaseActivity<ActivityFavoritesBinding>(),
    OfficialRoomsAdapter.OnOfficialRoomsItemClickListener,
    FeaturedRoomsAdapter.OnFeaturedRoomsItemClickListener,
    CommunityRoomsAdapter.OnCommunityRoomsItemClickListener,
    SearchLiveSessionsListAdapter.SignupOptionListener,
    FavoritesProgramAdapter.ProgramClicked,
    FavoriteMeditationsListAdapter.OnGetListener {

    private var isLoadingPrograms = true
    private var isLoadingContent = true
    private val mViewModel: FavoriteViewModel by viewModels()

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, FavoritesActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_favorites
    }

    private val favoriteMeditationsListAdapter by lazy {
        return@lazy FavoriteMeditationsListAdapter(this, this)
    }
    private val favoritesProgramAdapter by lazy {
        return@lazy FavoritesProgramAdapter(this)
    }
    private val mSearchLiveSessionsListAdapter by lazy {
        return@lazy SearchLiveSessionsListAdapter(this)
    }
    private val officialRoomsAdapter by lazy {
        return@lazy OfficialRoomsAdapter(this)
    }
    private val featuredRoomsAdapter by lazy {
        return@lazy FeaturedRoomsAdapter(this)
    }
    private val communityRoomsAdapter by lazy {
        return@lazy CommunityRoomsAdapter(this)
    }

    val startSubscriptionActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data?.getBooleanExtra("isSubscribed", false) == true) {
                    onRefresh()
                }
            }
        }

    override fun initView() {
        mViewModel.meditationList.removeAll {
            it == null
        }
        mViewModel.programList.removeAll {
            it == null
        }

        setupToolbar()
        setAdapterData()
        mBinding.shimmerPrograms.showShimmer(true)
        mBinding.shimmerMeditation.showShimmer(true)
        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            mBinding.swipeToRefreshLayout.isRefreshing = false
            onRefresh()
        }

        when (mViewModel.sortField) {
            Constants.latest -> setFilterParams(getString(R.string.latest))
            Constants.A_Z -> setFilterParams(getString(R.string.a_to_z))
        }

        if (isNetwork()) {
            if (mViewModel.programList.isEmpty())
                callFavoriteProgramDataAPI()
            else
                setProgramData()

            if (mViewModel.meditationList.isEmpty())
                callFavoriteContentDataAPI()
            else
                setMeditationData()
        }
        LocalBroadcastManager.getInstance(this@FavoritesActivity).registerReceiver(
            mSubscriptionReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
        )
    }

    private fun onRefresh() {
        if (isNetwork()) {
            mBinding.rvProgramFavorites.gone()
            mBinding.rvMeditationsList.gone()
            mBinding.shimmerPrograms.showShimmer(true)
            mBinding.shimmerMeditation.showShimmer(true)
            mBinding.shimmerMeditation.visible()
            mBinding.shimmerPrograms.visible()
            mBinding.lblNoProgram.gone()
            mBinding.cvNoProgram.gone()
            mBinding.lblNoMeditation.gone()
            mBinding.cvNoMeditation.gone()

            mViewModel.currentPageContentCount = -1
            mViewModel.currentPageProgramCount = -1
            mBinding.tvLatest.text = getString(R.string.latest)
            mViewModel.meditationList.clear()
            mViewModel.programList.clear()
            callFavoriteProgramDataAPI()
            callFavoriteContentDataAPI()
        }
    }

    private fun setAdapterData() {
        mBinding.rvMeditationsList.adapter = favoriteMeditationsListAdapter
        mBinding.rvProgramFavorites.adapter = favoritesProgramAdapter

        val featuredRoomList: ArrayList<RoomDummyDataModel> = arrayListOf()
        featuredRoomList.add(RoomDummyDataModel(true))
        featuredRoomList.add(RoomDummyDataModel(true))
        featuredRoomList.add(RoomDummyDataModel(true))
        featuredRoomList.add(RoomDummyDataModel(true))
        featuredRoomList.add(RoomDummyDataModel(true))
        featuredRoomList.add(RoomDummyDataModel(true))
        featuredRoomList.add(RoomDummyDataModel(true))
        mBinding.rvLiveSessionsList.adapter = mSearchLiveSessionsListAdapter
        officialRoomsAdapter.list.addAll(featuredRoomList)
        mBinding.rvOfficialRooms.adapter = officialRoomsAdapter
        officialRoomsAdapter.notifyDataSetChanged()
        featuredRoomsAdapter.list.addAll(featuredRoomList)
        mBinding.rvFeaturedRooms.adapter = featuredRoomsAdapter
        featuredRoomsAdapter.notifyDataSetChanged()
        communityRoomsAdapter.list.addAll(featuredRoomList)
        mBinding.rvCommunityRooms.adapter = communityRoomsAdapter
        communityRoomsAdapter.notifyDataSetChanged()
    }

    private fun callFavoriteProgramDataAPI() {
        mViewModel.fetchFavoriteProgramData(this, mDisposable)
    }

    private fun callFavoriteContentDataAPI() {
        mViewModel.fetchFavoriteContentData(
            this, mDisposable
        )
    }

    private val mSubscriptionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            onRefresh()
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this@FavoritesActivity)
            .unregisterReceiver(mSubscriptionReceiver)
        super.onDestroy()
    }

    private fun setupToolbar() {
        mBinding.mToolbar.cardProfile.visible()
        mBinding.mToolbar.ivBackToolbar.visible()

        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    @SuppressLint("ResourceAsColor")
    override fun observeApiCallbacks() {
        mViewModel.fetchFavoriteContentResponse.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.data?.let { data ->
                    if (mViewModel.currentPageContentCount == 0) {
                        mViewModel.meditationList.clear()
                    }
                    mViewModel.totalContentCount = requestState.apiResponse?.data?.totalRecords ?: 0
                    mViewModel.meditationList.removeAll {
                        it == null
                    }

                    data.list?.let { list ->
                        mViewModel.meditationList.addAll(list)
                        setMeditationData()
                    } ?: kotlin.run {
                        mBinding.shimmerMeditation.hideShimmer()
                        mBinding.shimmerMeditation.gone()
                        meditationDataNotFound()
                    }
                }
                longToastState(requestState.error)

                mViewModel.fetchFavoriteContentResponse.postValue(null)
            }
        }

        mViewModel.fetchFavoriteProgramResponse.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.data?.let { data ->
                    if (mViewModel.currentPageProgramCount == 0) {
                        mViewModel.programList.clear()
                    }
                    mViewModel.totalProgramCount = requestState.apiResponse?.data?.totalRecords ?: 0
                    mViewModel.programList.removeAll {
                        it == null
                    }

                    data.list?.let { list ->
                        mViewModel.programList.addAll(list)
                        setProgramData()
                    } ?: kotlin.run {
                        mBinding.shimmerPrograms.gone()
                        mBinding.rvProgramFavorites.gone()
                        programDataNotFound()
                    }
                }
                longToastState(requestState.error)

                mViewModel.fetchFavoriteProgramResponse.postValue(null)
            }
        }

    }

    private fun setMeditationData() {
        if (mViewModel.meditationList.size > 0) {
            mBinding.tvMeditationGold.visible()
            mBinding.tvMeditationGrey.gone()
            mBinding.llFilterLatestMeditation.visible()
            mBinding.rvMeditationsList.visible()
            mBinding.lblNoMeditation.gone()
            mBinding.cvNoMeditation.gone()
            mBinding.shimmerMeditation.hideShimmer()
            mBinding.shimmerMeditation.gone()
        } else {
            mBinding.shimmerMeditation.hideShimmer()
            mBinding.shimmerMeditation.gone()
            meditationDataNotFound()
        }

        isLoadingContent = mViewModel.meditationList.size >= mViewModel.totalContentCount

        if (!isLoadingContent)
            mViewModel.meditationList.add(null)

        favoriteMeditationsListAdapter.setFavoritesMeditationData(mViewModel.meditationList)
    }

    private fun setProgramData() {
        if (mViewModel.programList.size > 0) {
            mBinding.shimmerPrograms.gone()
            mBinding.lblNoProgram.gone()
            mBinding.cvNoProgram.gone()
            mBinding.tvProgramGrey.gone()
            mBinding.tvProgramGold.visible()
            mBinding.rvProgramFavorites.visible()
        } else {
            mBinding.shimmerPrograms.gone()
            mBinding.rvProgramFavorites.gone()
            programDataNotFound()
        }

        isLoadingPrograms = mViewModel.programList.size >= mViewModel.totalProgramCount

        if (!isLoadingPrograms)
            mViewModel.programList.add(null)

        favoritesProgramAdapter.setFavoritesProgramData(mViewModel.programList)
    }

    override fun handleListener() {
        mBinding.rvProgramFavorites.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvProgramFavorites.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvProgramFavorites.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if ((!isLoadingPrograms) and ((totalItem == (lastVisibleItem + 2)))) {
                    isLoadingPrograms = true
                    if (isNetwork())
                        callFavoriteProgramDataAPI()
                    else
                        longToast(
                            getString(R.string.no_internet, getString(R.string.to_get_favorites)),
                            Constants.ERROR
                        )
                }
            }
        })

        mBinding.rvMeditationsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItem = mBinding.rvMeditationsList.layoutManager?.itemCount ?: 0
                val lastVisibleItem =
                    ((mBinding.rvMeditationsList.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                if ((!isLoadingContent) and ((totalItem == (lastVisibleItem + 2)))) {
                    isLoadingContent = true
                    if (!mViewModel.meditationList.contains(null))
                        mViewModel.meditationList.add(null)
                    callFavoriteContentDataAPI()
                }
            }
        })

        mBinding.ivDownUpArrow.setOnClickListener {
            hideShowLatestDropDown()
        }

        mBinding.tvLatest.setOnClickListener {
            if (mBinding.llMeditationSort.isVisible) {
                setFilterParams(mBinding.tvLatest.text.toString())
                mViewModel.currentPageContentCount = -1
                callFavoriteContentDataAPI()
            }
            hideShowLatestDropDown()
        }
        mBinding.tvAtoZ.setOnClickListener {
            if (mBinding.llMeditationSort.isVisible) {
                setFilterParams(mBinding.tvAtoZ.text.toString())
                mViewModel.currentPageContentCount = -1
                callFavoriteContentDataAPI()
            }
            hideShowLatestDropDown()
        }
        mBinding.btnNoMeditation.setOnClickListener {
            MainActivity.startActivity(this@FavoritesActivity)
        }
        mBinding.btnNoProgram.setOnClickListener {
            ProgramsListActivity.startActivity(this@FavoritesActivity, categoryId = null)
        }

        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(
                this@FavoritesActivity,
                FavoritesActivity::class.java.simpleName
            )
        }

        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
    }

    private fun setFilterParams(type: String) {
        if (type == getString(R.string.latest)) {
            mViewModel.sortField = Constants.latest
            mViewModel.sortOrder = Constants.desc
            mBinding.tvLatest.text = getString(R.string.latest)
            mBinding.tvAtoZ.text = getString(R.string.a_to_z)
        } else if (type == getString(R.string.a_to_z)) {
            mViewModel.sortField = Constants.A_Z
            mViewModel.sortOrder = Constants.asc
            mBinding.tvLatest.text = getString(R.string.a_to_z)
            mBinding.tvAtoZ.text = getString(R.string.latest)
        }
    }


    private fun hideShowLatestDropDown() {
        if (mBinding.llMeditationSort.isVisible) {
            mBinding.ivDownUpArrow.setImageResource(R.drawable.vd_medium_grey_down_arrow)
            mBinding.llMeditationSort.isGone = true
        } else {
            mBinding.ivDownUpArrow.setImageResource(R.drawable.vd_medium_grey_up_arrow)
            mBinding.llMeditationSort.isVisible = true
//            mBinding.tvLatest.text = getString(R.string.latest)
        }
    }

    override fun onOfficialRoomsAdapterClick(position: Int) {
        TaoCalligraphyFieldsRoomsActivity.startActivity(this, 0)
    }


    override fun isFavoriteSelected(position: Int) {
        val data: FavoriteContentDataModel? = mViewModel.meditationList[position]
        mViewModel.favoriteMeditationContent(
            data?.id ?: "",
            this,
            mDisposable
        )
        if (mViewModel.meditationList.size > 0) {
            mViewModel.meditationList.removeAt(position)
            favoriteMeditationsListAdapter.setFavoritesMeditationData(mViewModel.meditationList)
        }

        mViewModel.meditationList.removeAll {
            it == null
        }
        if (mViewModel.meditationList.size == 0) {
            meditationDataNotFound()
        }

        sendFavoriteBroadCast(data?.id ?: "")
    }

    override fun onGetClicked(position: Int) {
        val data: FavoriteContentDataModel? = mViewModel.meditationList[position]
        NavigationHandler.handleFavouriteNavigation(
            data!!,
            this@FavoritesActivity,
            startSubscriptionActivityForResult
        )
/*        data?.let { dataModel ->
            if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == true && dataModel.isPurchased == false) {
//                    GET
                dataModel.id?.let {
                    openContentDetailScreen(it)
                }
//                        SubscriptionActivity.startActivity(activity as AppCompatActivity)
            } else if (dataModel.isLocked == true && dataModel.isSubscribed == false) {
//                    lock
                if (dataModel.unlockDays != null) {
                    if (dataModel.unlockDays!! < 1)
                        showUnlockImageDialog(this) else {
                    }
                } else if (dataModel.unlockDays == null) {
                    showUnlockImageDialog(this)
                } else {

                }
            } else if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == false) {
//                        Subscribe
                SubscriptionActivity.startActivityForResult(
                    this@FavoritesActivity,
                    startSubscriptionActivityForResult
                )
            } else {
//                    can access
                dataModel.id?.let {
                    openContentDetailScreen(it)
                }
            }
        }*/
    }

    private fun openContentDetailScreen(it: String) {
        MeditationDetailActivity.startActivity(this, it)
    }

    private fun openProgramDetailScreen(it: String) {
        ProgramDetailsActivity.startActivity(
            this,
            it,
            false
        )
    }

    override fun onProgramClicked(position: Int) {
        val data: ProgramDataModel? = mViewModel.programList[position]
        NavigationHandler.handleNavigation(
            data!!,
            this@FavoritesActivity,
            startSubscriptionActivityForResult
        )
/*        data?.let { dataModel ->
            if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == true && dataModel.isPurchased == false) {
//                    GET
                dataModel.id?.let {
                    openProgramDetailScreen(it)
                }
//                        SubscriptionActivity.startActivity(activity as AppCompatActivity)
            } else if (dataModel.isLocked == true && dataModel.isSubscribed == false) {
//                    lock
                if (dataModel.unlockDays != null) {
                    if (dataModel.unlockDays!! < 1)
                        showUnlockImageDialog(this) else {
                    }
                } else if (dataModel.unlockDays == null) {
                    showUnlockImageDialog(this)
                } else {

                }
            } else if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == false) {
//                        Subscribe
                SubscriptionActivity.startActivityForResult(
                    this@FavoritesActivity,
                    startSubscriptionActivityForResult
                )
            } else {
//                    can access
                dataModel.id?.let {
                    openProgramDetailScreen(it)
                }
            }
        }*/
    }

    override fun onProgramFavoriteClicked(position: Int) {
        val data: ProgramDataModel? = mViewModel.programList[position]
        mViewModel.setProgramFavorite(
            data?.id ?: "",
            false,
            this,
            mDisposable
        )
        if (mViewModel.programList.size > 0) {
            mViewModel.programList.removeAt(position)
            favoritesProgramAdapter.setFavoritesProgramData(mViewModel.programList)
        }
        if (mViewModel.programList.size == 0) {
            programDataNotFound()
        }

        sendFavoriteBroadCast(data?.id ?: "")
    }

    private fun sendFavoriteBroadCast(contentId: String) {
        val intent = Intent(Constants.BroadcastIntentFilter.BR_FAVOURITES_CHANGED)
        intent.putExtra(Constants.BroadcastIntentFilter.CONTENT_ID, contentId)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onFeaturedRoomsAdapterClick(position: Int) {
        TaoCalligraphyFieldsRoomsActivity.startActivity(this, 1)
    }

    override fun onCommunityRoomsAdapterClick(position: Int) {
        TaoCalligraphyFieldsRoomsActivity.startActivity(this, 2)
    }

    override fun onSignUpClick() {
        val dialog =
            PaidSessionBookingDialog(this)
        dialog.show()
    }

    private fun programDataNotFound() {
        mBinding.apply {
            tvProgramGold.gone()
            tvProgramGrey.visible()
            rvProgramFavorites.gone()
            lblNoProgram.visible()
            cvNoProgram.visible()
        }
    }

    private fun meditationDataNotFound() {
        mBinding.apply {
            tvMeditationGold.gone()
            tvMeditationGrey.visible()
            llFilterLatestMeditation.gone()
            rvMeditationsList.gone()
            lblNoMeditation.visible()
            cvNoMeditation.visible()
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
}