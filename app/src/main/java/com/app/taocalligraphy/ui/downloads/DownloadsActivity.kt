package com.app.taocalligraphy.ui.downloads

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityDownloadsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.WARNING
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.downloads.adapter.DownloadedCategoryAdapter
import com.app.taocalligraphy.ui.downloads.adapter.DownloadsAdapter
import com.app.taocalligraphy.ui.favorites.FavoritesActivity
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.meditation.MeditationDetailActivity
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class DownloadsActivity : BaseActivity<ActivityDownloadsBinding>(),
    DownloadsAdapter.DownloadItemClickListener, DownloadedCategoryAdapter.CategoryClickListener {

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, DownloadsActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    private val viewModel: DownloadViewModel by viewModels()

    override fun getResource(): Int {
        return R.layout.activity_downloads
    }

    private val downloadedCategoryAdapter by lazy {
        return@lazy DownloadedCategoryAdapter(this, viewModel)
    }
    private val downloadsAdapter by lazy {
        return@lazy DownloadsAdapter(this, viewModel)
    }

    override fun initView() {
        setupToolbar()
        updateProfile()
        if (viewModel.userDownloadsList.isEmpty())
            viewModel.getUserDownloadsFromStorage()

        mBinding.rvDownloads.adapter = downloadsAdapter
        userHolder.getWellnessCategoryData()?.list?.let {
            downloadedCategoryAdapter.categoryList = it
            mBinding.rvCategory.adapter = downloadedCategoryAdapter
        }

        checkDownloadEmpty()

        mBinding.swipeToRefreshLayout.setOnRefreshListener {
            mBinding.swipeToRefreshLayout.isRefreshing = false
        }

        LocalBroadcastManager.getInstance(this@DownloadsActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )
        setSubscriptionView()

        if (!viewModel.isEditEnable) {
            mBinding.tvEditDownload.text = getString(R.string.edit)
            viewModel.userDownloadsList.map { meditationContentResponse ->
                meditationContentResponse.isContentSelected = false
            }
            downloadsAdapter.setUserDownloads(viewModel.userDownloadsList)
            checkDownloadItemSelected()
            checkDownloadEmpty()
        } else {
            mBinding.tvEditDownload.text = getString(R.string.done)
            downloadsAdapter.setUserDownloads(viewModel.userDownloadsList)
            checkDownloadItemSelected()
        }

        if (viewModel.selectedPosition >= 0) {
            mBinding.tvCurrentCategoryName.text =
                downloadedCategoryAdapter.categoryList[viewModel.selectedPosition].categoryName
        } else {
            mBinding.tvCurrentCategoryName.text = getString(R.string.all_categories)
        }

        when (viewModel.sortBy) {
            0 -> {
                mBinding.tvSortBy.text = getString(R.string.sort_by)
            }
            1 -> {
                mBinding.tvSortBy.text = getString(R.string.most_recent)
            }
            else -> {
                mBinding.tvSortBy.text = getString(R.string.a_z)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@DownloadsActivity)
            .unregisterReceiver(mAccessLevelReceiver)
    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            setSubscriptionView()
        }
    }

    private fun setSubscriptionView() {
        if (UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess ?: false) {
            mBinding.ivLockDownloadFavourites.gone()
        } else {
            mBinding.ivLockDownloadFavourites.visible()
        }
        if (UserHolder.EnumUserModulePermission.SEARCH.permission?.canAccess ?: false) {
            mBinding.ivLockFindSomeThing.gone()
        } else {
            mBinding.ivLockFindSomeThing.visible()
        }
    }


    private fun setupToolbar() {
        mBinding.mToolbar.cardProfile.visible()
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.ivDeleteToolbar.visible()
        mBinding.mToolbar.ivDeleteToolbar.setImageResource(R.drawable.ic_sync)
    }

    private fun updateProfile() {
        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    override fun observeApiCallbacks() {
        TaoCalligraphy.instance.meditationDbRepo.getAllMeditationContentsDesc(false).observe(this) {
            it.let { downloadsList ->
                downloadsList.forEach { content ->
                    if (!viewModel.userDownloadsList.any { userContent -> userContent.id == content.id })
                        viewModel.userDownloadsList.add(content)
                }

                filterDownloadList()
                checkDownloadEmpty()
                setDownloadText()
            }
        }

        /*TaoCalligraphy.instance.meditationDbRepo.getAllMeditationContent(true).observe(this) {
            it?.let { downloadsList ->
                deleteDownloads(downloadsList)
            }
        }*/
    }

    private fun setDownloadText() {
        val spanDownload = SpannableStringBuilder()

        spanDownload.append("${getString(R.string.total_download_size)} ")
        val start = spanDownload.length
        spanDownload.append("${viewModel.userDownloadsList.getTotalFileSize()} ")

        spanDownload.setSpan(
            TextAppearanceSpan(this, R.style.TextViewJostBoldStyle),
            start,
            spanDownload.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        mBinding.tvTotalMb.text = spanDownload
    }

    private fun deleteDownloads(downloadsList: List<MeditationContentResponse>) {
        if (isNetwork() && downloadsList.isNotEmpty()) {
            val jsonArray = JsonArray()
            downloadsList.forEach { content ->
                jsonArray.add(content.id.toInt())
                viewModel.deleteUserDownload(content.id)
            }

            val jsonObject = JsonObject()
            jsonObject.add("contentIds", jsonArray)

            viewModel.deleteDownloadsFromServer(jsonObject)
        }
    }

    override fun onPause() {
        super.onPause()
        downloadsAdapter.handlerList.forEachIndexed { index, handler ->
            handler.removeCallbacks { downloadsAdapter.runnableList[index] }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()

        }

        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(
                this@DownloadsActivity,
                DownloadsActivity::class.java.simpleName
            )
        }


        mBinding.tvEditDownload.setOnClickListener {
            if (viewModel.isEditEnable) {
                mBinding.tvEditDownload.text = getString(R.string.edit)
                viewModel.userDownloadsList.map { meditationContentResponse ->
                    meditationContentResponse.isContentSelected = false
                }
                viewModel.isEditEnable = !viewModel.isEditEnable
                filterDownloadList()
                checkDownloadItemSelected()
                checkDownloadEmpty()
            } else {
                mBinding.tvEditDownload.text = getString(R.string.done)
                viewModel.isEditEnable = !viewModel.isEditEnable
                filterDownloadList()
                checkDownloadItemSelected()
            }
        }

        mBinding.rrAllCategories.setOnClickListener {
            showHideCategoryView(true)
        }

        mBinding.llSortByFilter.setOnClickListener {
            showHideSortByView(0)
        }

        mBinding.tvSortByMostRecent.setOnClickListener {
            showHideSortByView(1)
        }

        mBinding.tvSortByAToZ.setOnClickListener {
            showHideSortByView(2)
        }

        mBinding.mToolbar.ivDeleteToolbar.setOnClickListener {
            if (isNetwork()) {
                if (viewModel.userDownloadsList.isNotEmpty()) {
                    showProgressIndicator(mBinding.llProgress, true)
                    viewModel.syncDownloads(object : OnDownloadSyncListener {
                        override fun onDownloadSync() {
                            showProgressIndicator(mBinding.llProgress, false)
                        }
                    })
                }
            }
        }

        mBinding.llDeleteSelectedDownload.setOnClickListener {
            val selectedDownloadList = viewModel.userDownloadsList.filter {
                it.isContentSelected
            }
            if (selectedDownloadList.isNotEmpty()) {
                selectedDownloadList.forEach {
                    viewModel.userDownloadsList.remove(it)
                    val mediaFile: String? = if (!it.contentFileForDownload.isNullOrEmpty()) {
                        it.contentFileForDownload
                    } else {
                        it.contentFile
                    }
                    it.let { download ->
                        viewModel.updateUserDownload(true, download.id)

                        val isPresent = viewModel.userDownloadsList.any { content ->
                            val contentFile: String? =
                                if (!content.contentFileForDownload.isNullOrEmpty()) {
                                    content.contentFileForDownload
                                } else {
                                    content.contentFile
                                }
                            (mediaFile == contentFile
                                    || it.subtitleWithLanguages == content.subtitleWithLanguages) && !content.isDeleted
                        }
                        if (isPresent) {
                            TaoCalligraphy.instance.getDownloadTracker()
                                ?.removeDownload(Uri.parse(mediaFile))
                            download.subtitleWithLanguages?.forEach { subtitle ->
                                TaoCalligraphy.instance.getDownloadTracker()
                                    ?.removeDownload(Uri.parse(subtitle.subTitleFile))
                            }
                        }
                    }
                }
                downloadsAdapter.setUserDownloads(viewModel.userDownloadsList)
                checkDownloadItemSelected()
                checkDownloadEmpty()
            } else {
                longToast(getString(R.string.please_select_downloads_to_delete), WARNING)
            }
        }

        mBinding.btnDownloadFromFavourites.setOnClickListener {
            if (!(UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess
                    ?: false)
            ) {
                SubscriptionActivity.startActivityForResult(this@DownloadsActivity)
                return@setOnClickListener
            }

            FavoritesActivity.startActivity(this)
            finish()
        }

        mBinding.btnFindSomethingNew.setOnClickListener {
            if (!(UserHolder.EnumUserModulePermission.SEARCH.permission?.canAccess ?: false)) {
                SubscriptionActivity.startActivityForResult(this@DownloadsActivity)
                return@setOnClickListener
            }

            if (isNetwork()) {
                MainActivity.startActivity(this, true)
                finishAffinity()
            }
        }
    }

    private fun showHideCategoryView(isAllCategorySelect: Boolean) {
        if (mBinding.llCategoryList.isVisible) {
            mBinding.ivArrow.setImageResource(R.drawable.vd_down_arrow_gold)
            if (isAllCategorySelect) {
                viewModel.selectedPosition = -1
            }
            mBinding.llCategoryList.gone()
            if (viewModel.selectedPosition >= 0) {
                mBinding.tvCurrentCategoryName.text =
                    downloadedCategoryAdapter.categoryList[viewModel.selectedPosition].categoryName
            } else {
                mBinding.tvCurrentCategoryName.text = getString(R.string.all_categories)
            }
            filterDownloadList()
        } else {
            mBinding.ivArrow.setImageResource(R.drawable.vd_up_arrow_gold)
            mBinding.llCategoryList.visible()
            mBinding.tvCurrentCategoryName.text = getString(R.string.all_categories)
        }
    }

    private fun showHideSortByView(sortType: Int) {
        if (mBinding.llSortByList.isVisible) {
            mBinding.ivArrowSort.setImageResource(R.drawable.vd_down_arrow_gold)
            mBinding.llSortByList.gone()
            viewModel.sortBy = sortType
            when (viewModel.sortBy) {
                0 -> {
                    mBinding.tvSortBy.text = getString(R.string.sort_by)
                }
                1 -> {
                    mBinding.tvSortBy.text = getString(R.string.most_recent)
                }
                else -> {
                    mBinding.tvSortBy.text = getString(R.string.a_z)
                }
            }
            filterDownloadList()
        } else {
            mBinding.ivArrowSort.setImageResource(R.drawable.vd_up_arrow_gold)
            mBinding.llSortByList.visible()
            mBinding.tvSortBy.text = getString(R.string.sort_by)
        }
    }

    private fun filterDownloadList() {
        var data = viewModel.userDownloadsList
        val sortedData = ArrayList<MeditationContentResponse>()
        if (viewModel.selectedPosition != -1) {
            data = viewModel.userDownloadsList.filter {
                it.categoryDetailsList?.any { categoryDetail ->
                    categoryDetail.id == downloadedCategoryAdapter.categoryList[viewModel.selectedPosition].categoryId
                } as Boolean
            } as ArrayList<MeditationContentResponse>
        }
        if (viewModel.sortBy == 1) {
            data = data
        } else if (viewModel.sortBy == 2) {
            sortedData.addAll(data.sortedBy { it.title })
            data = sortedData
        }
        downloadsAdapter.setUserDownloads(data)

        if (data.isNotEmpty()) {
            mBinding.tvEditDownload.visible()
            mBinding.mToolbar.ivDeleteToolbar.alpha = 1f
            mBinding.mToolbar.ivDeleteToolbar.isEnabled = true
            mBinding.tvNotFound.gone()
            checkDownloadItemSelected()
        } else {
            mBinding.tvEditDownload.inVisible()
            mBinding.mToolbar.ivDeleteToolbar.alpha = 0.5f
            mBinding.mToolbar.ivDeleteToolbar.isEnabled = false
            mBinding.tvNotFound.visible()
            if (viewModel.selectedPosition >= 0) {
                mBinding.tvCurrentCategoryName.text =
                    downloadedCategoryAdapter.categoryList[viewModel.selectedPosition].categoryName
                mBinding.tvNotFound.text = getString(R.string.no_download_found_for_category, downloadedCategoryAdapter.categoryList[viewModel.selectedPosition].categoryName)
            } else {
                mBinding.tvCurrentCategoryName.text = getString(R.string.all_categories)
            }
            mBinding.llDeleteSelectedDownload.gone()
        }
    }

    private fun checkDownloadItemSelected() {
        if (viewModel.userDownloadsList.any {
                it.isContentSelected
            }) {
            mBinding.llDeleteSelectedDownload.visible()
        } else {
            mBinding.llDeleteSelectedDownload.gone()
        }
    }

    private fun checkDownloadEmpty() {
        if (viewModel.userDownloadsList.isEmpty()) {
            mBinding.ffDownload.gone()
            mBinding.rlNoDownload.visible()
            mBinding.tvTotalMb.gone()
            mBinding.noDownloadImages?.visible()
            mBinding.downloadBg?.gone()
            mBinding.downloadBgShadow?.gone()
        } else {
            mBinding.ffDownload.visible()
            mBinding.rlNoDownload.gone()
            mBinding.tvTotalMb.visible()
            mBinding.noDownloadImages?.gone()
            mBinding.downloadBg?.visible()
            mBinding.downloadBgShadow?.visible()
        }

        if (viewModel.userDownloadsList.isEmpty() || !isNetwork()) {
            mBinding.mToolbar.ivDeleteToolbar.alpha = 0.5f
            mBinding.mToolbar.ivDeleteToolbar.isEnabled = false
        } else {
            mBinding.mToolbar.ivDeleteToolbar.alpha = 1f
            mBinding.mToolbar.ivDeleteToolbar.isEnabled = true
        }
    }

    override fun onDownloadItemClick() {
        checkDownloadItemSelected()
    }

    override fun onSingleDownloadDelete(adapterPosition: Int) {
        val download = viewModel.userDownloadsList[adapterPosition]
        viewModel.userDownloadsList.removeAt(adapterPosition)
        download.let {
            val contentFile: String? = if (!it.contentFileForDownload.isNullOrEmpty()) {
                it.contentFileForDownload
            } else {
                it.contentFile
            }
            it.subtitleWithLanguages?.forEach { subTitle ->
                TaoCalligraphy.instance.getDownloadTracker()
                    ?.removeDownload(Uri.parse(subTitle.subTitleFile))
            }
            TaoCalligraphy.instance.getDownloadTracker()?.removeDownload(Uri.parse(contentFile))
            viewModel.deleteUserDownload(it.id)
        }
        checkDownloadEmpty()
    }

    override fun onContentView(meditationContentResponse: MeditationContentResponse?) {
        MeditationDetailActivity.startActivity(
            this,
            contentId = meditationContentResponse?.id ?: ""
        )
    }

    override fun onCategoryClick(adapterPosition: Int) {
        showHideCategoryView(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
        if (model.isAvailable) {
            mBinding.mToolbar.ivDeleteToolbar.alpha = 1f
            mBinding.mToolbar.ivDeleteToolbar.isEnabled = true
        } else {
            mBinding.mToolbar.ivDeleteToolbar.alpha = 0.5f
            mBinding.mToolbar.ivDeleteToolbar.isEnabled = false
        }
    }
}

interface OnDownloadSyncListener {
    fun onDownloadSync()
}
