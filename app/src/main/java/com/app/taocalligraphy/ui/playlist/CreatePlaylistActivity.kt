package com.app.taocalligraphy.ui.playlist

import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityCreatePlaylistBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.eventbus.PlaylistEditListener
import com.app.taocalligraphy.models.request.AddPlaylistRequest
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.isPlaylistChangedShowSnackBar
import com.app.taocalligraphy.other.Constants.playListChangeSuccessMessage
import com.app.taocalligraphy.ui.notification.dialog.ChooseMeditationDialog
import com.app.taocalligraphy.ui.playlist.adapter.PlaylistMeditationsAdapter
import com.app.taocalligraphy.ui.playlist.viewmodel.PlaylistViewModel
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnListScrollListener
import com.google.gson.JsonArray
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@AndroidEntryPoint
class CreatePlaylistActivity : BaseActivity<ActivityCreatePlaylistBinding>() {

    private var canLoad: Boolean = true
    private val mViewModel: PlaylistViewModel by viewModels()
    var isLoading = false
    var count = 0
    var layoutmanager: LinearLayoutManager? = null
    private var playListId: Int = 0
    private var playListTitle: String = ""
    private var contentDetailList = ArrayList<PlaylistContentFilterApiResponse.ContentList>()
    private var deletedContentIds = ArrayList<String>()

    companion object {
        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
            isEdit: Boolean,
            playListId: Int,
            playListTitle: String
        ) {
            val intent = Intent(activity, CreatePlaylistActivity::class.java)
            intent.putExtra(Constants.isEdit, isEdit)
            intent.putExtra("playListId", playListId)
            intent.putExtra("playListTitle", playListTitle)
            activity.startActivityWithAnimation(intent)
        }
    }

    val isEdit by lazy {
        return@lazy intent.extras?.getBoolean(Constants.isEdit, false) ?: false
    }

    override fun getResource(): Int {
        return R.layout.activity_create_playlist
    }

    override fun initView() {
        playListId = intent?.extras?.getInt("playListId", 0) ?: 0
        playListTitle = intent?.extras?.getString("playListTitle", "") ?: ""
        if (isEdit) mBinding.tvTitleToolbar.text =
            getString(R.string.edit_playlist) else mBinding.tvTitleToolbar.text =
            getString(R.string.create_playlist)
        setupToolbar()
        fetchData()

        mBinding.etSessionName.addTextChangedListener {
            mBinding.tlSessionName.isErrorEnabled = false
        }
        if (isTablet(this)) {
            ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(0, 0, 0, insets.bottom)
                WindowInsetsCompat.CONSUMED
            }
        }
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
    }

    private fun fetchData() {
        mBinding.rvPlaylist.adapter = mViewModel.playlistMeditationsAdapter
        mBinding.rvPlaylist.dragListener = onItemDragListener
        contentDetailList = mViewModel.playlistMeditationsAdapter.dataSet as ArrayList<PlaylistContentFilterApiResponse.ContentList>
        if (isEdit) {
            mBinding.etSessionName.setText(playListTitle)
            nestedScrollViewScrollEvent()
            if (contentDetailList.isEmpty()) {
                if (isNetwork()) {
                    mViewModel.playlistContentApi(
                        playListId,
                        count, 10,
                        "",
                        this@CreatePlaylistActivity,
                        mDisposable
                    )
                }
            } else {
                mViewModel.playlistMeditationsAdapter.dataSet = contentDetailList
            }
        } else {
            if (contentDetailList.isEmpty())
                contentDetailList.add(
                    PlaylistContentFilterApiResponse.ContentList(
                        isForAddItem = true
                    )
                )

            mViewModel.playlistMeditationsAdapter.dataSet = contentDetailList
        }

        mViewModel.playlistMeditationsAdapter.setOnClickListener(object :
            PlaylistMeditationsAdapter.OnSelect {
            override fun clickOnItem(
                contentList: PlaylistContentFilterApiResponse.ContentList,
                position: Int
            ) {
                if (contentList.isForAddItem) {
                    openChooseMeditationDialog()
                } else {
                    deletedContentIds.add(contentList.id)
                    contentDetailList.removeAt(position)
                    mViewModel.playlistMeditationsAdapter.dataSet = contentDetailList
                }
            }
        })
    }

    private val onItemDragListener =
        object : OnItemDragListener<PlaylistContentFilterApiResponse.ContentList> {
            override fun onItemDragged(
                previousPosition: Int,
                newPosition: Int,
                item: PlaylistContentFilterApiResponse.ContentList
            ) {
                // Handle action of item being dragged from one position to another

            }

            override fun onItemDropped(
                initialPosition: Int,
                finalPosition: Int,
                item: PlaylistContentFilterApiResponse.ContentList
            ) {
                try {
                    val contentItem = contentDetailList[initialPosition]
                    contentDetailList.removeAt(initialPosition)
                    contentDetailList.add(finalPosition, contentItem)
                    mViewModel.playlistMeditationsAdapter.dataSet = contentDetailList
                } catch (e: Exception) {
                    logInfo(msg = "${e.printStackTrace()}")
                }
            }
        }

    override fun observeApiCallbacks() {
        mViewModel.addPlaylistContentLiveData.observe(this@CreatePlaylistActivity) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    contentDetailList.clear()
                    isPlaylistChangedShowSnackBar = true
                    playListChangeSuccessMessage =
                        it.message.toString()
                    onBackPressed()
                }
            }
        }

        mViewModel.editPlaylistLiveData.observe(this@CreatePlaylistActivity) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    EventBus.getDefault()
                        .post(PlaylistEditListener(true, mBinding.etSessionName.text.toString()))
                    isPlaylistChangedShowSnackBar = true
                    playListChangeSuccessMessage = it.message.toString()
                    contentDetailList.clear()
                    onBackPressed()
                }
            }
        }

        mViewModel.playlistContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                if (count == 0)
                    showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { contentList ->
                        mBinding.progressBarForResult.gone()
                        if (count == 0) {
                            contentDetailList.clear()
                        }
                        contentDetailList.removeAll { content -> content.isForAddItem }
                        contentDetailList.addAll(contentList.list)
                        canLoad = contentList.totalRecords > contentDetailList.size
                        isLoading = true
                        contentDetailList.add(
                            PlaylistContentFilterApiResponse.ContentList(
                                isForAddItem = true
                            )
                        )
                        mViewModel.playlistMeditationsAdapter.dataSet = contentDetailList
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
        }
        mViewModel.playlistContentLiveData.postValue(null)
    }

    override fun handleListener() {
//        mBinding.llAddNew.clickWithDebounce {
//            openChooseMeditationDialog()
//        }
//        mBinding.ivImage.setOnClickListener {
//            mBinding.llAddNew.performClick()
//        }
//        mBinding.ivAddNew.setOnClickListener {
//            mBinding.llAddNew.performClick()
//        }
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.btnSave.clickWithDebounce {
            if (checkValidation()) {
                if (isNetwork()) {
                    val contentDetails: ArrayList<AddPlaylistRequest.ContentDetail> = ArrayList()
                    mViewModel.playlistMeditationsAdapter.dataSet.forEach { content ->
                        if (contentDetailList.any { it.id == content.id } && !content.isForAddItem)
                            contentDetails.add(
                                AddPlaylistRequest.ContentDetail(
                                    content.id.toInt(),
                                    content.contentOrder
                                )
                            )
                    }
                    contentDetails.forEach {
                        if (deletedContentIds.contains(it.contentId.toString()))
                            deletedContentIds.remove(it.contentId.toString())
                    }
                    if (isEdit) {
                        val jsonArray = JsonArray()
                        deletedContentIds.forEach {
                            jsonArray.add(it)
                        }
                        mViewModel.editPlaylistApi(
                            contentDetails,
                            jsonArray,
                            playListId,
                            1,
                            mBinding.etSessionName.text.toString().trim(),
                            this@CreatePlaylistActivity,
                            mDisposable
                        )
                    } else {
                        mViewModel.addPlaylistApi(
                            contentDetails,
                            1,
                            mBinding.etSessionName.text.toString().trim(),
                            this@CreatePlaylistActivity,
                            mDisposable
                        )
                    }
                }
            }
        }
    }

    private fun checkValidation(): Boolean {
        if (TextUtils.isEmpty(mBinding.etSessionName.text.toString().trim())) {
            mBinding.tlSessionName.error = getString(R.string.please_enter_name_your_list)
            return false
        } else {
            mBinding.tlSessionName.isErrorEnabled = false
        }
        if (contentDetailList.isEmpty()) {
            longToast(getString(R.string.empty_playlist_error), ERROR)
            return false
        }
        return true
    }

    private fun openChooseMeditationDialog() {
        val selectedContent = ArrayList<String>()
        contentDetailList.removeAll { it.isForAddItem }
        contentDetailList.forEach {
            selectedContent.add(it.id)
        }
        deletedContentIds.addAll(selectedContent)
        val dialog = ChooseMeditationDialog.newInstance(object :
            ChooseMeditationDialog.ChooseMeditationScreenListener {
            override fun onChooseMeditationSelect(selectedMeditations: ArrayList<PlaylistContentFilterApiResponse.ContentList>) {
                contentDetailList = selectedMeditations
                contentDetailList.add(
                    PlaylistContentFilterApiResponse.ContentList(
                        isForAddItem = true
                    )
                )
                mViewModel.playlistMeditationsAdapter.dataSet = contentDetailList
            }
        }, true, contentDetailList)
        dialog.show(
            getFragmentTransactionFromTag(ChooseMeditationDialog.TAG),
            ChooseMeditationDialog.TAG
        )
    }

    private fun nestedScrollViewScrollEvent() {

        mBinding.rvPlaylist.scrollListener = object : OnListScrollListener {

            override fun onListScrollStateChanged(scrollState: OnListScrollListener.ScrollState) {
                if (!mBinding.rvPlaylist.canScrollVertically(1)) {
                    if (isLoading && canLoad) {
                        isLoading = false
                        count++
                        mBinding.progressBarForResult.visible()
                        mViewModel.playlistContentApi(
                            playListId,
                            count, 10,
                            "",
                            this@CreatePlaylistActivity,
                            mDisposable
                        )
                    }
                }
            }

            override fun onListScrolled(
                scrollDirection: OnListScrollListener.ScrollDirection,
                distance: Int
            ) {
            }

        }
    }


    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null) {
            longToast(error, ERROR)
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