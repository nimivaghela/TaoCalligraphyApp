package com.app.taocalligraphy.ui.playlist

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityPlaylistsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.request.PlaylistRequest
import com.app.taocalligraphy.models.response.playList.PlaylistApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.playlist.adapter.PlaylistAdapter
import com.app.taocalligraphy.ui.playlist.viewmodel.PlaylistViewModel
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class PlaylistsActivity : BaseActivity<ActivityPlaylistsBinding>(),
    PlaylistAdapter.PlaylistItemClickListener {

    private var endlessRecyclerScrollListener: EndlessRecyclerScrollListener? = null
    private val mViewModel: PlaylistViewModel by viewModels()

    var layoutManager: LinearLayoutManager? = null


    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, PlaylistsActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    private val playlistAdapter by lazy {
        return@lazy PlaylistAdapter(this,this, mutableListOf())
    }

    override fun getResource(): Int {
        return R.layout.activity_playlists
    }

    override fun initView() {
        setupToolbar()
        updateProfile()
        layoutManager = LinearLayoutManager(this)
        mBinding.rvPlaylist.layoutManager = layoutManager
        mBinding.rvPlaylist.adapter = playlistAdapter
//        itemListApiRes.add(0, PlaylistApiResponse.Playlist(0, 0, "", 0, ""))
//        playlistAdapter.updateList(itemListApiRes)
        recyclerViewScrollEvent()
        setFilterParams(getString(if(mViewModel.sortField==Constants.latest) R.string.latest else R.string.a_to_z))

        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(0, 0, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

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
        if (Constants.isPlaylistChangedShowSnackBar) {
            longToast(
                Constants.playListChangeSuccessMessage,
                Constants.SUCCESS
            )
        }

        if(mViewModel.itemListApiRes.size == 0 || Constants.isPlaylistChangedShowSnackBar) {
            if (isNetwork()) {
                mViewModel.currCount = 0
                endlessRecyclerScrollListener?.resetState()
                mViewModel.playListApi(
                    mViewModel.currCount,
                    mViewModel.perPage,
                    "",
                    PlaylistRequest.Sort().apply {
                        this.field = mViewModel.sortField
                        this.order = mViewModel.sortOrder
                    },
                    this@PlaylistsActivity,
                    mDisposable
                )
            }
        }else{
            updateView()
        }

        Constants.isPlaylistChangedShowSnackBar = false
    }

    private fun updateView() {
        if (mViewModel.itemListApiRes.size > 1) {
            mBinding.rvPlaylist.visible()
            mBinding.llFilterLatestMeditation.visible()
            mBinding.tvNotFound.gone()
            playlistAdapter.updateList(mViewModel.itemListApiRes)
        } else {
            mBinding.rvPlaylist.visible()
            playlistAdapter.updateList(mViewModel.itemListApiRes)
            mBinding.llFilterLatestMeditation.gone()
            mBinding.tvNotFound.visible()
        }
    }

    override fun observeApiCallbacks() {
        mViewModel.playListLiveData.observe(this) { response ->
            response?.let { requestState ->
                if (mViewModel.currCount == 0)
                    showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { data ->
                        mBinding.progressBarPlaylist.gone()

                        mViewModel.totalCount = data.totalRecords

                        if (mViewModel.currCount == 0) {
                            mViewModel.itemListApiRes.clear()
                            mViewModel.itemListApiRes.add(0, PlaylistApiResponse.Playlist(0, 0, "", 0, ""))
                        }
                        if (data.list.size <= 0)
                            mViewModel.currCount -= 1

                        mViewModel.itemListApiRes.addAll(data.list)
//                        mViewModel.isLoading = mViewModel.itemListApiRes.size >= 10
//                        isLoading = itemListApiRes.size >= 10
                       updateView()
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let {
                                 longToast(it, errorObj.type)
                            }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let {
                                 longToast(it, errorObj.type)
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
            mViewModel.playListLiveData.postValue(null)
        }
    }

    private fun setFilterParams(type : String){
        if(type == getString(R.string.latest)){
            mViewModel.sortField = Constants.latest
            mViewModel.sortOrder = Constants.desc
            mBinding.tvLatest.text = getString(R.string.latest)
            mBinding.tvAtoZ.text = getString(R.string.a_to_z)
            mViewModel.currCount = 0
        }else if(type == getString(R.string.a_to_z)){
            mViewModel.sortField = Constants.A_Z
            mViewModel.sortOrder = Constants.asc
            mBinding.tvLatest.text = getString(R.string.a_to_z)
            mBinding.tvAtoZ.text = getString(R.string.latest)
            mViewModel.currCount = 0
        }
    }

    override fun handleListener() {

        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this@PlaylistsActivity, UserMenuActivity::class.java.simpleName)
        }

        mBinding.llFilterLatestMeditation.setOnClickListener {
//            mBinding.tvLatest.text = getString(R.string.latest)
            manageVisibilityLatestFilter()
        }

        mBinding.tvLatest.setOnClickListener {
            if(mBinding.llLatestAtoZ.isVisible) {
                setFilterParams(mBinding.tvLatest.text.toString())
                mViewModel.playListApi(
                    mViewModel.currCount,
                    mViewModel.perPage,
                    "",
                    PlaylistRequest.Sort().apply {
                        this.field = mViewModel.sortField
                        this.order = mViewModel.sortOrder
                    },
                    this@PlaylistsActivity,
                    mDisposable
                )
            }
            manageVisibilityLatestFilter()
        }
        mBinding.tvAtoZ.setOnClickListener {
            if (mBinding.llLatestAtoZ.isVisible) {
                setFilterParams(mBinding.tvAtoZ.text.toString())
                mViewModel.playListApi(
                    mViewModel.currCount,
                    mViewModel.perPage,
                    "",
                    PlaylistRequest.Sort().apply {
                        this.field = mViewModel.sortField
                        this.order = mViewModel.sortOrder
                    },
                    this@PlaylistsActivity,
                    mDisposable
                )
            }
            manageVisibilityLatestFilter()
        }

    }

    private fun manageVisibilityLatestFilter() {
        if (mBinding.llLatestAtoZ.isVisible) {
            mBinding.llLatestAtoZ.gone()
            mBinding.ivDownUpArrow.rotation = 0f
        } else {
            mBinding.llLatestAtoZ.visible()
            mBinding.ivDownUpArrow.rotation = 180f
        }
    }

    override fun onPlaylistItemClick(
        adapterPosition: Int,
        mDataList: MutableList<PlaylistApiResponse.Playlist>
    ) {
        if (adapterPosition == 0) {
            if(!(UserHolder.EnumUserModulePermission.CREATE_PLAYLIST.permission?.canAccess ?: true)){
                SubscriptionActivity.startActivityForResult(
                    this@PlaylistsActivity
                )
                return
            }
            CreatePlaylistActivity.startActivity(
                this, false, 0, ""
            )
        } else {
            PlaylistDetailActivity.startActivity(
                this,
                mDataList[adapterPosition].id,
                mDataList[adapterPosition].title
            )
        }
    }

    private fun recyclerViewScrollEvent() {
        layoutManager?.let {
            endlessRecyclerScrollListener =
                object : EndlessRecyclerScrollListener(layoutManager!!) {
                    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                        // Check the arraylist size is less than actual size
                        if ( mViewModel.itemListApiRes.size < mViewModel.totalCount) {
                            mViewModel.currCount++
                            logInfo(msg = "onLoadMore Called")
                            logInfo(msg = "size :- ${mViewModel.itemListApiRes.size}")
                            mBinding.progressBarPlaylist.visible()
                            mViewModel.playListApi(
                                mViewModel.currCount,
                                mViewModel.perPage,
                                "",
                                PlaylistRequest.Sort().apply {
                                    this.field = mViewModel.sortField
                                    this.order = mViewModel.sortOrder
                                },
                                this@PlaylistsActivity,
                                mDisposable
                            )
                        }
                    }
                }

            mBinding.rvPlaylist.addOnScrollListener(endlessRecyclerScrollListener as EndlessRecyclerScrollListener)
        }
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null) {
            longToast(error, Constants.ERROR)
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

    override fun onDestroy() {
        mBinding.rvPlaylist.removeOnScrollListener(endlessRecyclerScrollListener as EndlessRecyclerScrollListener)
        super.onDestroy()
    }

}