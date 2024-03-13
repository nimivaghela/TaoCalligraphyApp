package com.app.taocalligraphy.ui.playlist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityPlaylistDetailBinding
import com.app.taocalligraphy.models.IconNamePopupModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.eventbus.PlaylistEditListener
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.other.Constants.WARNING
import com.app.taocalligraphy.other.Constants.isPlaylistChangedShowSnackBar
import com.app.taocalligraphy.other.Constants.playListChangeSuccessMessage
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.meditation.MeditationDetailActivity
import com.app.taocalligraphy.ui.meditation.StartPlayerActivity
import com.app.taocalligraphy.ui.meditation.StartPlayerTabletActivity
import com.app.taocalligraphy.ui.meditation_session.adapter.CustomMenuItemAdapter
import com.app.taocalligraphy.ui.playlist.adapter.PlaylistViewItemAdapter
import com.app.taocalligraphy.ui.playlist.viewmodel.PlaylistViewModel
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageWithAnimate
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class PlaylistDetailActivity : BaseActivity<ActivityPlaylistDetailBinding>() {

    private val mViewModel: PlaylistViewModel by viewModels()

    var layoutmanager: LinearLayoutManager? = null

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity, playListId: Int, playListTitle: String) {
            val intent = Intent(activity, PlaylistDetailActivity::class.java)
            intent.putExtra("playListId", playListId)
            intent.putExtra("playListTitle", playListTitle)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_playlist_detail
    }

    private val playlistViewItemAdapter by lazy {
        return@lazy PlaylistViewItemAdapter(mutableListOf())
    }

    private var customPowerMenu: CustomPowerMenu<IconNamePopupModel, CustomMenuItemAdapter>? = null
    var menuItemList = ArrayList<IconNamePopupModel>()

    override fun initView() {
        mViewModel.playListId = intent?.extras?.getInt("playListId", 0) ?: 0
        mViewModel.playListTitle = intent?.extras?.getString("playListTitle", "") ?: ""
        mBinding.tvTitle.text = mViewModel.playListTitle
        mBinding.tvTitle.isSelected = true
        setupToolbar()
        layoutmanager = LinearLayoutManager(this)
        mBinding.rvPlaylist.layoutManager = layoutmanager
        mBinding.rvPlaylist.adapter = playlistViewItemAdapter
        playlistViewItemAdapter.setOnClickListener(object : PlaylistViewItemAdapter.OnSelect {
            override fun clickOnItem(
                arrayList: MutableList<PlaylistContentFilterApiResponse.ContentList>,
                position: Int
            ) {
                var dataModel = arrayList[position]

                if ((dataModel?.subscription?.isAccessible
                        ?: true) == false || ((dataModel?.subscription?.isAccessible
                        ?: true) == true && dataModel?.isPaidContent == true && dataModel?.isPurchased == false)
                ) {
                    MeditationDetailActivity.startActivity(
                        this@PlaylistDetailActivity, dataModel.id ?: ""
                    )
                } else {

                    if ((arrayList[position].type == Constants.VIDEO) or (arrayList[position].type == Constants.MUSIC) or ((arrayList[position].type == Constants.AUDIO)) or
                        ((arrayList[position].type == Constants.Guided_Meditation_Audio))
                    ) {
//                    StartPlayerActivity.startActivity(
//                        this@PlaylistDetailActivity,
//                        contentId = arrayList[position].id
//                    )
                        if(isTablet(this@PlaylistDetailActivity)){
                            StartPlayerTabletActivity.startActivity(
                                this@PlaylistDetailActivity,
                                videoType = Constants.playlistVideo,
                                contentId = arrayList[position].id,
                                programId = mViewModel.playListId.toString()
                            )
                        }else{
                            StartPlayerActivity.startActivity(
                                this@PlaylistDetailActivity,
                                videoType = Constants.playlistVideo,
                                contentId = arrayList[position].id,
                                programId = mViewModel.playListId.toString()
                            )
                        }
                    }
                }
            }
        })
        setMenuData()
        nestedScrollViewScrollEvent()
        setSubscriptionUI()
        LocalBroadcastManager.getInstance(this@PlaylistDetailActivity).registerReceiver(
            mSubscriptionReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
        )
        LocalBroadcastManager.getInstance(this@PlaylistDetailActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )

        if(isTablet(this)){
            ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(0, 0, 0, insets.bottom)
                WindowInsetsCompat.CONSUMED
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

    private fun setSubscriptionUI() {
        if(UserHolder.EnumUserModulePermission.CREATE_PLAYLIST.permission?.canAccess ?: false){
            mBinding.mToolbar.ivEditToolbar.isEnabled = true
            mBinding.mToolbar.ivEditToolbar.alpha = 1f
        }else{
            mBinding.mToolbar.ivEditToolbar.isEnabled = false
            mBinding.mToolbar.ivEditToolbar.alpha = 0.5f
        }
        playlistViewItemAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this@PlaylistDetailActivity).unregisterReceiver(mSubscriptionReceiver)
        LocalBroadcastManager.getInstance(this@PlaylistDetailActivity).unregisterReceiver(mAccessLevelReceiver)
        super.onDestroy()
    }

    private var imageIndex = 0
    val handler = Handler(Looper.getMainLooper())
    private val runnableImage = object : Runnable {
        override fun run() {
            if (!isFinishing) {
                mBinding.ivBannerImages.loadImageWithAnimate(
                    mViewModel.bannerList[imageIndex],
                    placeHolder = R.drawable.img_default_for_content,
                    true
                )
                if (mViewModel.bannerList.size > 1) {
                    if (mViewModel.bannerList.size - 1 > imageIndex)
                        imageIndex++
                    else
                        imageIndex = 0
                    handler.postDelayed(this, 3000)
                }
            }
        }
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.ivDeleteToolbar.visible()
        mBinding.mToolbar.ivEditToolbar.visible()
    }

    override fun observeApiCallbacks() {
        mViewModel.playlistContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                if (mViewModel.playListDetailsCount == 0)
                    showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { it ->
                        mBinding.progressBarForResult.gone()
                        if (mViewModel.playListDetailsCount == 0) {
                            mViewModel.playlistDetailsContentList.clear()
                            mViewModel.bannerList.clear()
                        }
                        mViewModel.playlistDetailsContentList.addAll(it.list)
                        mViewModel.isPlaylistDetailsLoading = mViewModel.playlistDetailsContentList.size >= 10
                        for (i in mViewModel.playlistDetailsContentList.indices) {
                            mViewModel.bannerList.add(mViewModel.playlistDetailsContentList[i].backgroundImageMobile)
                        }
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
                mViewModel.playlistContentLiveData.postValue(null)
            }
        }

        mViewModel.playListDeleteLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { it ->
                    isPlaylistChangedShowSnackBar = true
                    playListChangeSuccessMessage =
                        it.message.toString()
                    onBackPressed()
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            longToast("" + errorObj.customMessage, Constants.ERROR)
                        else -> {
                        }
                    }
                }
            }
        }
    }

    private fun updateView() {
        if (mViewModel.playlistDetailsContentList.size > 0) {
            mBinding.rvPlaylist.visible()
            playlistViewItemAdapter.updateList(mViewModel.playlistDetailsContentList)
            //animateImages(mBinding.ivBannerImages, bannerList, 0, true)
            imageIndex = 0
            handler.removeCallbacks(runnableImage)
            handler.postDelayed(runnableImage, 0)
            mBinding.tvNoContentFound.gone()
        } else {
            mBinding.tvNoContentFound.visible()
            mBinding.rvPlaylist.gone()
        }
    }

    override fun handleListener() {
        mBinding.mToolbar.ivDeleteToolbar.clickWithDebounce {
            deleteConfirmationDialog()
        }
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.mToolbar.ivEditToolbar.clickWithDebounce {
            if(!(UserHolder.EnumUserModulePermission.CREATE_PLAYLIST.permission?.canAccess ?: false)){
                SubscriptionActivity.startActivityForResult(this@PlaylistDetailActivity)
                return@clickWithDebounce
            }

            CreatePlaylistActivity.startActivity(this, true, mViewModel.playListId, mViewModel.playListTitle)
        }

        /* mBinding.toolbar.innerToolbar.ivToolBarMore.setOnClickListener {
             openMenuView(mBinding.toolbar.innerToolbar.ivToolBarMore)
         }*/

        mBinding.btnPlayAll.setOnClickListener {
            if (mViewModel.playlistDetailsContentList.filter { ((it.subscription?.isAccessible ?: false == true) && if (it.isPaidContent == false) true else it.isPurchased == true) }
                    .isNotEmpty()) {
                if (mViewModel.playlistDetailsContentList.isNotEmpty()) {
                    val content = mViewModel.playlistDetailsContentList.first()
                    if ((content.type == Constants.VIDEO) or (content.type == Constants.MUSIC) or (content.type == Constants.Guided_Meditation_Audio)) {
                        if(isTablet(this)){
                            StartPlayerTabletActivity.startActivity(
                                this,
                                videoType = Constants.playlistVideo,
                                programId = mViewModel.playListId.toString()
                            )
                        } else{
                            StartPlayerActivity.startActivity(
                                this,
                                videoType = Constants.playlistVideo,
                                programId = mViewModel.playListId.toString()
                            )
                        }

                    }
                }
            } else {
                longToast(getString(R.string.all_content_not_purchased), WARNING)
            }
        }
    }

    private fun setMenuData() {
        menuItemList.clear()
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.edit),
                ContextCompat.getDrawable(this, R.drawable.vd_edit_icon_gold)
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                true
            )
        )
        menuItemList.add(
            IconNamePopupModel(
                getString(R.string.delete),
                ContextCompat.getDrawable(this, R.drawable.vd_delete_icon_gold)
            )
        )
    }

    private fun nestedScrollViewScrollEvent() {
//        mBinding.llNestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
//            if (v.getChildAt(v.childCount - 1) != null) {
//                if (scrollY >= v.getChildAt(v.childCount - 1)
//                        .measuredHeight - v.measuredHeight &&
//                    scrollY > oldScrollY
//                ) {
//                    if (isLoading) {
//                        count++
//                        mBinding.progressBarForResult.visible()
//                        mViewModel.playlistContentApi(
//                            playListId,
//                            count, 10,
//                            "",
//                            this@PlaylistDetailActivity,
//                            mDisposable
//                        )
//                    }
//                }
//            }
//        })
    }

    private fun openMenuView(itemView: View) {
        if (customPowerMenu != null) {
            customPowerMenu = null
            return
        }
        val onMenuItemClickListener: OnMenuItemClickListener<IconNamePopupModel> =
            OnMenuItemClickListener<IconNamePopupModel> { position, item ->
                customPowerMenu?.dismiss()
                customPowerMenu = null
                if (position == 0) {
                    CreatePlaylistActivity.startActivity(this, true, mViewModel.playListId, mViewModel.playListTitle)
                } else {
                    deleteConfirmationDialog()
                }
            }

        customPowerMenu = CustomPowerMenu.Builder(this, CustomMenuItemAdapter())
            .setHeaderView(R.layout.item_popup_header) // header used for title
            .setFooterView(R.layout.item_popup_header)
            .addItemList(menuItemList)
            .setAnimation(MenuAnimation.SHOW_UP_CENTER) // Animation start point (TOP | LEFT).
            .setMenuRadius(30f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setShowBackground(false)
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .build()

        customPowerMenu?.showAsDropDown(itemView, 100, -80)
        customPowerMenu?.setOnDismissedListener {
            customPowerMenu = null
        }
    }

    private fun deleteConfirmationDialog() {
        val title = ""
        val desctiption = "" + getString(R.string.delete_msg)

        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.setTitle("" + title)
            .setMessage("" + desctiption)
            .setCancelable(true)
            .setPositiveButton(
                "" + getString(R.string.yes)
            ) { dialog, which ->
                dialog!!.dismiss()
                if (isNetwork())
                    mViewModel.playListDeleteApi(mViewModel.playListId, this, mDisposable)
                else
                    longToast(
                        getString(R.string.no_internet, getString(R.string.to_delete_playlist)),
                        Constants.ERROR
                    )
            }
            .setNegativeButton(
                "" + getString(R.string.no)
            ) { dialog, which -> dialog!!.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    fun onRefresh() {
        mViewModel.playListDetailsCount = 0
        if (isNetwork()) {
            mViewModel.playlistContentApi(
                mViewModel.playListId,
                mViewModel.playListDetailsCount, 10,
                "",
                this@PlaylistDetailActivity,
                mDisposable
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (isPlaylistChangedShowSnackBar) {
            longToast(
                playListChangeSuccessMessage,
                SUCCESS
            )
            isPlaylistChangedShowSnackBar = false
        }
        mViewModel.playListDetailsCount = 0
        if (isNetwork() && mViewModel.playlistDetailsContentList.isEmpty()) {
            mViewModel.playlistContentApi(
                mViewModel.playListId,
                mViewModel.playListDetailsCount, 10,
                "",
                this@PlaylistDetailActivity,
                mDisposable
            )
        }else{
            updateView()
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

    override fun onPause() {
        handler.removeCallbacks(runnableImage)
        super.onPause()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventEditListener(model: PlaylistEditListener) {
        if (model.isEdited) {
            mViewModel.playListTitle = model.title
            mBinding.tvTitle.text = model.title
        }
    }

}