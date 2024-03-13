package com.app.taocalligraphy.ui.notification.dialog

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseDialogFragment
import com.app.taocalligraphy.databinding.DialogChooseMeditationBinding
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.other.MeditationType
import com.app.taocalligraphy.ui.home.viewmodel.HomeViewModel
import com.app.taocalligraphy.ui.notification.adapter.MeditationResultAdapter
import com.app.taocalligraphy.ui.notification.adapter.MeditationTagFilterAdapter
import com.app.taocalligraphy.ui.notification.adapter.MeditationTopicFilterAdapter
import com.app.taocalligraphy.ui.playlist.viewmodel.PlaylistViewModel
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.isNetwork
import com.google.gson.JsonArray
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChooseMeditationDialog :
    BaseDialogFragment<DialogChooseMeditationBinding>(),
    MeditationResultAdapter.OnMeditationContentSelectListener {

    private val mViewModel: PlaylistViewModel by viewModels()

    private val mBaseViewModel: HomeViewModel by viewModels()
    var gridLayoutManager: GridLayoutManager? = null

    private val searchHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var searchRunnable: Runnable? = null


    companion object {
        const val TAG = "ChooseMeditationDialog"
        var contentDetailList = ArrayList<PlaylistContentFilterApiResponse.ContentList>()
        var chooseMeditationScreenListener: ChooseMeditationScreenListener? = null
        var allowedMultipleSelection: Boolean = false
        var shouldContentRefresh : Boolean = false
        fun newInstance(
            chooseMeditationScreenListener: ChooseMeditationScreenListener,
            allowedMultipleSelection: Boolean = false,
            contentDetailList: ArrayList<PlaylistContentFilterApiResponse.ContentList>
        ): ChooseMeditationDialog {
            this.chooseMeditationScreenListener = chooseMeditationScreenListener
            this.allowedMultipleSelection = allowedMultipleSelection
            this.contentDetailList = contentDetailList
            return ChooseMeditationDialog()
        }
    }

    override fun getResource(): Int {
        return R.layout.dialog_choose_meditation
    }

    private val meditationTagFilterAdapter by lazy {
        return@lazy MeditationTagFilterAdapter(mutableListOf())
    }

    private val meditationTopicFilterAdapter by lazy {
        return@lazy MeditationTopicFilterAdapter(mutableListOf())
    }

    private val meditationResultAdapter by lazy {
        return@lazy MeditationResultAdapter(allowedMultipleSelection, this)
    }

    override fun postInit() {
        mBinding.rvTopic.adapter = meditationTopicFilterAdapter
        mBinding.rvTags.adapter = meditationTagFilterAdapter
        gridLayoutManager = GridLayoutManager(requireContext(), 2)
        mBinding.rvMeditationResult.layoutManager = gridLayoutManager
        mBinding.rvMeditationResult.adapter = meditationResultAdapter
        mBinding.btnSelect.isEnabled = false
        mBinding.btnSelect.isClickable = false
        mBinding.btnSelect.alpha = .5f
        mViewModel.isLoading = true
        edtSearchTextChangeEvent()
        nestedScrollViewScrollEvent()
        setClickListener()

        shouldContentRefresh = false
        if (requireContext().isNetwork() && mViewModel.meditationCategoryDataList.size <= 0 && mViewModel.playlistContentFilterList.size <= 0) {
            mBaseViewModel.fetchAllCategoryData(this@ChooseMeditationDialog, mDisposable)
        }else{
            updateCategory()
            updateListView()
        }
    }

    override fun initObserver() {
        mBaseViewModel.fetchCategoryDataResponse.observe(this@ChooseMeditationDialog) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)

                requestState.apiResponse.let { fetchCategoryData ->
                    fetchCategoryData?.let { data ->
                        data.data?.list?.let {
                            mViewModel.meditationCategoryDataList.clear()
                            mViewModel.meditationCategoryDataList.addAll(it)
                            updateCategory()
                        }
                    }
                }
                mBaseViewModel.fetchCategoryDataResponse.postValue(null)
            }
        }

        mViewModel.fetchCategoryTagsByIdsResponse.observe(this@ChooseMeditationDialog) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let {
                    it.data?.tagsDetails?.let { tags ->
                        mViewModel.selectedTagsDataList.clear()
                        tags.forEach { tagDetail ->
                            if (mViewModel.selectedTagIds.contains(tagDetail.id)) {
                                mViewModel.selectedTagsDataList.add(tagDetail)
                            }
                        }
                        updateTagView()
                    }
                }
                mViewModel.fetchCategoryTagsByIdsResponse.postValue(null)
            }
        }

        mViewModel.playlistContentFilterLiveData.observe(this@ChooseMeditationDialog) { response ->
            response?.let { requestState ->
                if (mViewModel.searchKey.isEmpty()) {
                    if (mViewModel.count == 0) {
                        showProgressIndicator(mBinding.llProgress, requestState.progress)
                    }
                } else {
                    if (mViewModel.countWithSearch == 0) {
                        showProgressIndicator(mBinding.llProgress, requestState.progress)
                    }
                }
                hideKeyboard()
                requestState.apiResponse?.let {
                    mBinding.progressBarForResult.gone()
                    it.data?.let { data ->
                        mViewModel.firstTime = false
                        if (mViewModel.searchKey.isEmpty()) {
                            if (mViewModel.count == 0)
                                mViewModel.playlistContentFilterList.clear()
                        } else {
                            if (mViewModel.countWithSearch == 0)
                                mViewModel.playlistContentFilterList.clear()
                        }
                        mViewModel.playlistContentFilterList.addAll(data.list)
                        mViewModel.totalMeditationCount = it.data?.totalRecords ?: 0

                        mViewModel.isLoading =
                            data.totalRecords > mViewModel.playlistContentFilterList.size

                        mViewModel.previouslySearchedText = mBinding.etSearch.text.toString() ?: ""

                        mViewModel.count++
                        mViewModel.countWithSearch++

                        updateListView()
                    }
                }
                mViewModel.playlistContentFilterLiveData.postValue(null)
            }
        }

    }

    private fun updateTagView() {
        if (mViewModel.selectedTagsDataList.isNotEmpty()) {
            mBinding.rvTags.visible()
            mBinding.llTagsAll.gone()
            meditationTagFilterAdapter.updateList(mViewModel.selectedTagsDataList)
        } else {
            mBinding.llTagsAll.visible()
            mBinding.rvTags.gone()
        }
    }

    private fun updateListView() {

        mBinding.tvTotalResultCount.text =
            getString(R.string.meditation_result, (mViewModel.totalMeditationCount).toString())

        mViewModel.playlistContentFilterList.forEach { content ->
            if (contentDetailList.any { selectedList -> selectedList.id == content.id }) {
                mBinding.btnSelect.isEnabled = true
                mBinding.btnSelect.isClickable = true
                mBinding.btnSelect.alpha = 1f
            }
        }

        if (mViewModel.playlistContentFilterList.isNotEmpty()) {
            mBinding.tvNotFound.gone()
            mBinding.rvMeditationResult.visible()
            meditationResultAdapter.updateList(
                mViewModel.playlistContentFilterList,
                contentDetailList
            )
        } else {
            mBinding.tvNotFound.visible()
            mBinding.rvMeditationResult.gone()
        }

    }

    override fun handleListener() {
    }

    override fun displayMessage(message: String) {
    }

    override fun onDestroyView() {
        if (dialog != null) {
            dialog!!.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    interface ChooseMeditationScreenListener {
        fun onChooseMeditationSelect(selectedMeditations: ArrayList<PlaylistContentFilterApiResponse.ContentList>)
    }

    private var mLastClickTime: Long = 0

    private fun setClickListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }

        mBinding.ivRefresh.setOnClickListener {
            mViewModel.selectedCategoryId = 0
            mBinding.etSearch.setText("")
            mViewModel.selectedTagIds.clear()
            mViewModel.selectedTopicsIds.clear()
            shouldContentRefresh = true
            updateCategory()
        }

        mBinding.llCategory.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            mViewModel.filterType = MeditationType.CHOOSE_CATEGORY
            showChooseMeditationFilterDialog()
        }

        mBinding.llTopic.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            mViewModel.filterType = MeditationType.CHOOSE_TOPIC
            showChooseMeditationFilterDialog()
        }

        mBinding.llTags.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            mViewModel.filterType = MeditationType.CHOOSE_TAGS
            showChooseMeditationFilterDialog()
        }

        mBinding.btnSelect.setOnClickListener {
            chooseMeditationScreenListener?.onChooseMeditationSelect(contentDetailList)
            dismiss()
        }
    }

    private fun showChooseMeditationFilterDialog() {
        val dialog = ChooseMeditationFilterDialog.newInstance(
            mViewModel.selectedCategoryId,
            mViewModel.selectedTopicsIds,
            mViewModel.selectedTagIds,
            mViewModel.filterType,
            object : ChooseMeditationFilterDialog.OnChooseMeditationFilterListener {
                override fun onCategorySelected(
                    categoryId: Int,
                    topicsList: ArrayList<Int>,
                    tagsList: ArrayList<Int>
                ) {
                    // in case when user navigates and comes back it is taking old observer but new observer is allocated and api called in old observer.
                    initDisposable()

                    when (mViewModel.filterType) {
                        MeditationType.CHOOSE_CATEGORY -> {
                            // While selecting different category deleting previously selected Tags and Topics
                            if (mViewModel.selectedCategoryId != categoryId) {
                                mViewModel.selectedTopicsIds = ArrayList()
                                mViewModel.selectedTagIds = ArrayList()
                            } else {
                                mViewModel.selectedTopicsIds = topicsList
                                mViewModel.selectedTagIds = tagsList
                            }
                            mViewModel.selectedCategoryId = categoryId
                        }
                        MeditationType.CHOOSE_TOPIC -> {

                            // While selecting different Topic deleting previously selected Tags.
                            mViewModel.selectedCategoryId = categoryId
                            mViewModel.selectedTopicsIds = topicsList
                            mViewModel.selectedTagIds = ArrayList()
                        }
                        else -> {
                            mViewModel.selectedCategoryId = categoryId
                            mViewModel.selectedTopicsIds = topicsList
                            mViewModel.selectedTagIds = tagsList
                        }
                    }

//                    updateCategory(true)
                    shouldContentRefresh = true
                    mBaseViewModel.fetchAllCategoryData(this@ChooseMeditationDialog, mDisposable)
                }
            })
        dialog.show(
            getFragmentTransactionFromTag(ChooseMeditationFilterDialog.TAG),
            ChooseMeditationFilterDialog.TAG
        )
    }


    fun updateCategory() {
        mViewModel.topicsDataList.clear()
        if (mViewModel.selectedCategoryId != 0) {
            val subCategoryData =
                mViewModel.meditationCategoryDataList.firstOrNull { it.categoryId?.toInt() == mViewModel.selectedCategoryId }
            subCategoryData?.let {
                mBinding.tvCategoryName.text = it.categoryName
                mViewModel.topicsDataList.addAll(it.subCategoryDetails)
                mBinding.llCategorySelected.visible()
                mBinding.llCategoryAll.gone()
            } ?: kotlin.run {
                mBinding.llCategorySelected.gone()
                mBinding.llCategoryAll.visible()
            }
        } else {
            mViewModel.meditationCategoryDataList.forEach {
                mViewModel.topicsDataList.addAll(it.subCategoryDetails)
            }
            mBinding.llCategorySelected.gone()
            mBinding.llCategoryAll.visible()
        }

        mBinding.llTopicAll.visible()
        mBinding.rvTopic.gone()
        mViewModel.selectedTopicsDataList.clear()

        if (mViewModel.topicsDataList.isNotEmpty()) {
            mViewModel.topicsDataList.forEach { topics ->
                if (mViewModel.selectedTopicsIds.any { topic -> topic == topics.id }) {
                    mViewModel.selectedTopicsDataList.add(topics)
                }
            }

            if (mViewModel.selectedTopicsDataList.isNotEmpty()) {
                mBinding.llTopicAll.gone()
                mBinding.rvTopic.visible()
                meditationTopicFilterAdapter.updateList(mViewModel.selectedTopicsDataList)
            }
        }

        fetchCataegoryTags()

        if (shouldContentRefresh || (requireContext().isNetwork() && mViewModel.playlistContentFilterList.size <= 0)) {
            mViewModel.count = 0
            mViewModel.countWithSearch = 0
            val jsonArrayTopics = JsonArray()
            mViewModel.selectedTopicsIds.forEach {
                jsonArrayTopics.add(it)
            }

            val jsonArrayTags = JsonArray()
            mViewModel.selectedTagIds.forEach {
                jsonArrayTags.add(it)
            }
            mViewModel.playlistContentFilterApi(
                mViewModel.selectedCategoryId,
                mViewModel.count,
                mViewModel.perpage,
                mViewModel.searchKey,
                jsonArrayTopics,
                jsonArrayTags,
                this@ChooseMeditationDialog,
                mDisposable
            )
        }
    }

    private fun fetchCataegoryTags() {
        mBinding.llTagsAll.visible()
        mBinding.rvTags.gone()

        if (mViewModel.selectedTopicsIds.isNotEmpty()) {
            val jsonArray = JsonArray()
            mViewModel.selectedTopicsIds.forEach {
                jsonArray.add(it)
            }
//            jsonArray.add(selectedCategoryId)
            mViewModel.fetchCategoryTagsByIds(
                jsonArray,
                this@ChooseMeditationDialog,
                mDisposable
            )
        } else if (mViewModel.selectedCategoryId != 0) {
            val jsonArray = JsonArray()
            mViewModel.topicsDataList.forEach { topics ->
                jsonArray.add(topics.id)
            }
            jsonArray.add(mViewModel.selectedCategoryId)
            mViewModel.fetchCategoryTagsByIds(
                jsonArray,
                this@ChooseMeditationDialog,
                mDisposable
            )
        } else {
            val categoryId = JsonArray()
            mViewModel.fetchCategoryTagsByIds(
                categoryId,
                this@ChooseMeditationDialog,
                mDisposable
            )
        }

    }

    private fun nestedScrollViewScrollEvent() {
        mBinding.llNestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
            if (v.getChildAt(v.childCount - 2) != null || v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight && scrollY > oldScrollY) {
                    if (mViewModel.isLoading) {
                        mBinding.progressBarForResult.visible()
                        val jsonArrayTopics = JsonArray()
                        mViewModel.selectedTopicsIds.forEach {
                            jsonArrayTopics.add(it)
                        }

                        val jsonArrayTags = JsonArray()
                        mViewModel.selectedTagIds.forEach {
                            jsonArrayTags.add(it)
                        }
                        mViewModel.isLoading = false
                        mViewModel.playlistContentFilterApi(
                            mViewModel.selectedCategoryId,
                            mViewModel.count,
                            mViewModel.perpage,
                            mViewModel.searchKey,
                            jsonArrayTopics,
                            jsonArrayTags,
                            this@ChooseMeditationDialog,
                            mDisposable
                        )
                    }
                }
            }
        })
    }

    private fun edtSearchTextChangeEvent() {
        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, countText: Int) {
                try {
                    if (mViewModel.firstTime) return

                    searchRunnable?.let {
                        searchHandler.removeCallbacks(it)
                    }

                    mViewModel.searchKey = mBinding.etSearch.text.toString()

                    searchRunnable = Runnable {
                        mViewModel.count = 0
                        mViewModel.countWithSearch = 0

                        if (!TextUtils.isEmpty(mViewModel.searchKey.trim())) {
                            mViewModel.isLoading = false
                            val jsonArrayTopics = JsonArray()
                            mViewModel.selectedTopicsIds.forEach {
                                jsonArrayTopics.add(it)
                            }

                            val jsonArrayTags = JsonArray()
                            mViewModel.selectedTagIds.forEach {
                                jsonArrayTags.add(it)
                            }
                            mViewModel.playlistContentFilterApi(
                                mViewModel.selectedCategoryId,
                                mViewModel.count,
                                mViewModel.perpage,
                                mViewModel.searchKey.trim(),
                                jsonArrayTopics,
                                jsonArrayTags,
                                this@ChooseMeditationDialog,
                                mDisposable
                            )
                        } else {
                            mViewModel.count = 0
                            mViewModel.countWithSearch = 0
                            mViewModel.isLoading = false
                            val jsonArrayTopics = JsonArray()
                            mViewModel.selectedTopicsIds.forEach {
                                jsonArrayTopics.add(it)
                            }

                            val jsonArrayTags = JsonArray()
                            mViewModel.selectedTagIds.forEach {
                                jsonArrayTags.add(it)
                            }
                            mViewModel.playlistContentFilterApi(
                                mViewModel.selectedCategoryId,
                                mViewModel.count,
                                mViewModel.perpage,
                                mViewModel.searchKey,
                                jsonArrayTopics,
                                jsonArrayTags,
                                this@ChooseMeditationDialog,
                                mDisposable
                            )
                        }
                    }

                    if (mViewModel.previouslySearchedText != (mBinding.etSearch.text.toString()
                            ?: "")
                    ) {
                        searchRunnable?.let {
                            searchHandler.postDelayed(it, 800)
                        }
                    }
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onMeditationContentSelected(selectedMeditationList: ArrayList<PlaylistContentFilterApiResponse.ContentList>) {
        contentDetailList = selectedMeditationList
        if (selectedMeditationList.isNotEmpty()) {
            mBinding.btnSelect.isEnabled = true
            mBinding.btnSelect.isClickable = true
            mBinding.btnSelect.alpha = 1f
        } else {
            mBinding.btnSelect.isEnabled = false
            mBinding.btnSelect.isClickable = false
            mBinding.btnSelect.alpha = .5f
        }
    }
}