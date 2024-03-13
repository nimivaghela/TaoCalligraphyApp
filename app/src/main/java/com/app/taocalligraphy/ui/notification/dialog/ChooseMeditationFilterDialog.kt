package com.app.taocalligraphy.ui.notification.dialog

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseDialogFragment
import com.app.taocalligraphy.databinding.DialogChooseMeditationFilterBinding
import com.app.taocalligraphy.other.MeditationType
import com.app.taocalligraphy.ui.home.viewmodel.HomeViewModel
import com.app.taocalligraphy.ui.notification.adapter.CategoryFilterAdapter
import com.app.taocalligraphy.ui.notification.adapter.MeditationTopicFilterAdapter
import com.app.taocalligraphy.ui.notification.adapter.SubCategoryFilterAdapter
import com.app.taocalligraphy.ui.playlist.viewmodel.PlaylistViewModel
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.isNetwork
import com.google.android.material.chip.Chip
import com.google.gson.JsonArray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseMeditationFilterDialog :
    BaseDialogFragment<DialogChooseMeditationFilterBinding>(),
    CategoryFilterAdapter.OnCategorySelectedListener {

    private val mViewModel: PlaylistViewModel by viewModels()
    private val mBaseViewModel: HomeViewModel by viewModels()

    private val categoryFilterAdapter: CategoryFilterAdapter by lazy {
        CategoryFilterAdapter(this, mViewModel)
    }
    private val subCategoryFilterAdapter: SubCategoryFilterAdapter by lazy {
        SubCategoryFilterAdapter()
    }

    companion object {
        const val TAG = "ChooseMeditationFilterDialog"
        var filterType = MeditationType.CHOOSE_CATEGORY
        var onChooseMeditationFilterListener: OnChooseMeditationFilterListener? = null
        var selectedCategoryId = 0
        var selectedTopicsIds = ArrayList<Int>()
        var selectedTagsIds = ArrayList<Int>()
        fun newInstance(
            selectedCategoryId: Int,
            selectedTopicsIds: ArrayList<Int>,
            selectedTagsIds: ArrayList<Int>,
            filterType: MeditationType,
            onChooseMeditationFilterListener: OnChooseMeditationFilterListener
        ): ChooseMeditationFilterDialog {
            this.selectedCategoryId = selectedCategoryId
            this.selectedTopicsIds = selectedTopicsIds
            this.selectedTagsIds = selectedTagsIds
            this.onChooseMeditationFilterListener = onChooseMeditationFilterListener
            this.filterType = filterType
            return ChooseMeditationFilterDialog()
        }
    }

    private val meditationTopicFilterAdapter by lazy {
        return@lazy MeditationTopicFilterAdapter(mutableListOf())
    }

    override fun getResource(): Int {
        return R.layout.dialog_choose_meditation_filter
    }

    override fun postInit() {
        mBinding.rvTopic.adapter = meditationTopicFilterAdapter
        if (filterType == MeditationType.CHOOSE_CATEGORY) {
            mBinding.tvTitle.text = getString(R.string.meditation_choose_category)
            mBinding.tvSelectionItemType.text = getString(R.string.meditation_selection_one)
            mBinding.rvSelectCategory.visible()
            mBinding.llCategory.gone()
            mBinding.svChipGroupBrings.gone()
            if (requireContext().isNetwork() && mViewModel.categoryDataList.size <= 0)
                mBaseViewModel.fetchAllCategoryData(this@ChooseMeditationFilterDialog, mDisposable)
            else
                updateView()

            mBinding.rvSelectCategory.adapter = categoryFilterAdapter

        } else if (filterType == MeditationType.CHOOSE_TOPIC) {
            mBinding.tvTitle.text = getString(R.string.meditation_choose_topic)
            mBinding.tvSelectionItemType.text = getString(R.string.meditation_selection_option)
            mBinding.tvTotalAvailableItem.text = ""
            mBinding.rvSelectCategory.visible()
            mBinding.llCategory.visible()
            mBinding.svChipGroupBrings.gone()

            if (requireContext().isNetwork() && mViewModel.subCategoryDataList.size <= 0)
                mBaseViewModel.fetchAllCategoryData(this@ChooseMeditationFilterDialog, mDisposable)
            else
                updateView()

            mBinding.rvSelectCategory.adapter = subCategoryFilterAdapter
        } else if (filterType == MeditationType.CHOOSE_TAGS) {
            mBinding.tvTitle.text = getString(R.string.meditation_choose_tag)
            mBinding.tvSelectionItemType.text = getString(R.string.meditation_selection_option)
            mBinding.tvTotalAvailableItem.text = ""
            mBinding.llTopic.visible()
            mBinding.llCategory.visible()
            mBinding.svChipGroupBrings.visible()
            mBinding.rvSelectCategory.gone()
            if (requireContext().isNetwork() && mViewModel.tagsDetailList.size <= 0) {
                mBaseViewModel.fetchAllCategoryData(this@ChooseMeditationFilterDialog, mDisposable)
                if (selectedTopicsIds.isNotEmpty()) {
                    val jsonArray = JsonArray()
                    selectedTopicsIds.forEach {
                        jsonArray.add(it)
                    }
//                    jsonArray.add(selectedCategoryId)
                    mViewModel.fetchCategoryTagsByIds(
                        jsonArray,
                        this@ChooseMeditationFilterDialog,
                        mDisposable
                    )
                } else if (selectedCategoryId != 0) {
                    val jsonArray = JsonArray()
                    val handler = Handler(Looper.getMainLooper())
                    handler.post(object : Runnable {
                        override fun run() {
                            if (mViewModel.categoryDataList.isNotEmpty()) {
                                val subCategoryData =
                                    mViewModel.categoryDataList.firstOrNull { it.categoryId?.toInt() == selectedCategoryId }
                                subCategoryData?.let {
                                    it.subCategoryDetails.forEach { subCategoryDetail ->
                                        jsonArray.add(subCategoryDetail.id)
                                    }
                                }

                                jsonArray.add(selectedCategoryId)
                                mViewModel.fetchCategoryTagsByIds(
                                    jsonArray,
                                    this@ChooseMeditationFilterDialog,
                                    mDisposable
                                )
                            } else {
                                handler.postDelayed(this, 500)
                            }
                        }
                    })
                } else {
                    val categoryId = JsonArray()
                    mViewModel.fetchCategoryTagsByIds(
                        categoryId,
                        this@ChooseMeditationFilterDialog,
                        mDisposable
                    )
                }
            } else {
                updateView()
                updateTagsView()
            }
        }
    }

    override fun initObserver() {
        mBaseViewModel.fetchCategoryDataResponse.observe(this@ChooseMeditationFilterDialog) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse.let { fetchCategoryData ->
                    fetchCategoryData?.let { data ->
                        data.data?.list?.let {
                            mViewModel.categoryDataList.clear()
                            mViewModel.categoryDataList = it
                        }

                        updateView()

                    }
                }
                mBaseViewModel.fetchCategoryDataResponse.postValue(null)
            }
        }

        mViewModel.fetchCategoryTagsByIdsResponse.observe(this@ChooseMeditationFilterDialog) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let {
                    it.data?.tagsDetails.let { tags ->
                        mViewModel.tagsDetailList.clear()
                        mViewModel.tagsDetailList.addAll(tags!!)
                        updateTagsView()
                    }
                }
            }
            mViewModel.fetchCategoryTagsByIdsResponse.postValue(null)
        }
    }

    private fun updateTagsView() {
        mBinding.tvTotalAvailableItem.text = getString(
            R.string.meditation_tag_available,
            mViewModel.tagsDetailList.size.toString()
        )
        mViewModel.tagsDetailList.forEach { tag ->
            if (selectedTagsIds.contains(tag.id)) {
                tag.isSelected = true
            }
            val chip = layoutInflater.inflate(
                R.layout.item_interest,
                mBinding.chipGroupBrings,
                false
            ) as Chip
            chip.text = tag.name
            mBinding.chipGroupBrings.addView(chip)
            chip.isChecked = tag.isSelected
            chip.setOnCheckedChangeListener { chipView, isChecked ->
                chipView.isChecked = isChecked
                val index = mBinding.chipGroupBrings.indexOfChild(chipView)
                mViewModel.tagsDetailList[index].isSelected = isChecked
            }
        }
    }

    private fun updateView() {
        if (filterType == MeditationType.CHOOSE_CATEGORY) {
            if (mViewModel.categoryDataList.isNotEmpty()) {
                if (mViewModel.categoryDataList.any { !it.isSelected }) {
                    mViewModel.categoryDataList.map {
                        if (it.categoryId?.toInt() == selectedCategoryId)
                            it.isSelected = true
                    }
                }
                mBinding.tvTotalAvailableItem.text = getString(
                    R.string.meditation_category_available,
                    mViewModel.categoryDataList.size.toString()
                )
                categoryFilterAdapter.updateList()
                mBinding.tvNotFound.gone()
                mBinding.rvSelectCategory.visible()
            } else {
                mBinding.rvSelectCategory.gone()
                mBinding.tvNotFound.visible()
            }
        } else if (filterType == MeditationType.CHOOSE_TOPIC) {
            if (selectedCategoryId != 0) {
                val subCategoryData =
                    mViewModel.categoryDataList.firstOrNull { it.categoryId?.toInt() == selectedCategoryId }
                subCategoryData?.let {
                    mBinding.tvCategoryName.text = it.categoryName
                    mViewModel.subCategoryDataList.clear()
                    mViewModel.subCategoryDataList.addAll(it.subCategoryDetails)
                    mBinding.llCategorySelected.visible()
                } ?: kotlin.run {
                    mBinding.llCategorySelected.gone()
                }
            } else {
                mViewModel.subCategoryDataList.clear()
                mViewModel.categoryDataList.forEach {
                    mViewModel.subCategoryDataList.addAll(it.subCategoryDetails)
                }
                mBinding.llCategorySelected.visible()
            }
            // Sorting based on sort order
            mViewModel.subCategoryDataList.sortByDescending { it.sortOrder }

            mBinding.tvTotalAvailableItem.text =
                getString(
                    R.string.meditation_topic_available,
                    mViewModel.subCategoryDataList.size.toString()
                )

            if (mViewModel.subCategoryDataList.isNotEmpty()) {
                subCategoryFilterAdapter.updateList(
                    mViewModel.subCategoryDataList,
                    selectedTopicsIds
                )
                mBinding.tvNotFound.gone()
                mBinding.rvSelectCategory.visible()
            } else {
                mBinding.rvSelectCategory.gone()
                mBinding.tvNotFound.visible()
            }
        } else if (filterType == MeditationType.CHOOSE_TAGS) {
            if (selectedCategoryId != 0) {
                if (selectedTopicsIds.isNotEmpty()) {
                    mBinding.llTopicSelected.gone()
                    mBinding.rvTopic.visible()
                }
                mBinding.llCategorySelected.visible()
                val subCategoryData =
                    mViewModel.categoryDataList.firstOrNull { it.categoryId?.toInt() == selectedCategoryId }
                subCategoryData?.let {
                    mBinding.tvCategoryName.text = it.categoryName
                    mViewModel.subCategoryDataList.clear()
                    mViewModel.subCategoryDataList.addAll(it.subCategoryDetails)
                    mBinding.llCategorySelected.visible()
                }
            } else {
                mViewModel.subCategoryDataList.clear()
                mViewModel.categoryDataList.forEach {
                    mViewModel.subCategoryDataList.addAll(it.subCategoryDetails)
                }
                if (selectedTopicsIds.isNotEmpty()) {
                    mBinding.llTopicSelected.gone()
                    mBinding.rvTopic.visible()
                }
                mBinding.llCategorySelected.visible()
            }

            if (mViewModel.subCategoryDataList.isNotEmpty()) {
                mViewModel.subCategoryDataList.forEach { topics ->
                    if (selectedTopicsIds.any { topic -> topic == topics.id }) {
                        mViewModel.selectedCategoryDataList.add(topics)
                    }
                }

                if (mViewModel.selectedCategoryDataList.isNotEmpty()) {
                    mBinding.rvTopic.visible()
                    meditationTopicFilterAdapter.updateList(mViewModel.selectedCategoryDataList)
                }
            }
        }
    }

    override fun handleListener() {
        mBinding.ivClose.setOnClickListener {
            mViewModel.categoryDataList.map { category ->
                category.isSelected = (category.categoryId?.toInt() ?: 0) == selectedCategoryId
            }
            dismiss()
        }

        mBinding.btnSelect.setOnClickListener {
            when (filterType) {
                MeditationType.CHOOSE_CATEGORY -> {
                    onChooseMeditationFilterListener?.onCategorySelected(
                        mViewModel.categoryDataList.firstOrNull { it.isSelected }?.categoryId?.toInt()
                            ?: 0, selectedTopicsIds, selectedTagsIds
                    )
                    dismiss()
                }
                MeditationType.CHOOSE_TOPIC -> {
                    onChooseMeditationFilterListener?.onCategorySelected(
                        selectedCategoryId,
                        subCategoryFilterAdapter.selectedSubCategoryIds,
                        selectedTagsIds
                    )
                    dismiss()
                }
                MeditationType.CHOOSE_TAGS -> {
                    selectedTagsIds.clear()
                    mViewModel.tagsDetailList.forEach {
                        if (it.isSelected)
                            selectedTagsIds.add(it.id)
                    }
                    onChooseMeditationFilterListener?.onCategorySelected(
                        selectedCategoryId, selectedTopicsIds, selectedTagsIds
                    )
                    dismiss()
                }
            }
        }
    }

    override fun displayMessage(message: String) {
    }

    override fun onDestroyView() {
        if (dialog != null) {
            dialog!!.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    interface OnChooseMeditationFilterListener {
        fun onCategorySelected(
            categoryId: Int,
            topicsList: ArrayList<Int>,
            tagsList: ArrayList<Int>
        )
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onCategorySelected(id: Int) {
        selectedCategoryId = id
    }
}