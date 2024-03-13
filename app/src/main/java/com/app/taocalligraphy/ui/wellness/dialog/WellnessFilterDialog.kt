package com.app.taocalligraphy.ui.wellness.dialog

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseDialogFragment
import com.app.taocalligraphy.databinding.DialogFilterCategoryBinding
import com.app.taocalligraphy.ui.wellness.viewmodel.CategoryViewModel
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WellnessFilterDialog : BaseDialogFragment<DialogFilterCategoryBinding>() {

    companion object {
        const val TAG = "WellnessFilterDialog"
        var onWellnessFilterDialogClickListener: OnWellnessFilterDialogClickListener? = null
        var mViewModel: CategoryViewModel? = null
        fun newInstance(
            mViewModel: CategoryViewModel,
            onWellnessFilterDialogClickListener: OnWellnessFilterDialogClickListener
        ): WellnessFilterDialog {
            this.onWellnessFilterDialogClickListener = onWellnessFilterDialogClickListener
            this.mViewModel = mViewModel
            return WellnessFilterDialog()
        }
    }

    override fun getResource(): Int {
        return R.layout.dialog_filter_category
    }

    override fun postInit() {
        if (mViewModel?.isRefreshClicked == true) {
            mViewModel?.selectedTagsDataList?.clear()
        }
        setChipGroupDataInFilterDialog()

        if ((mViewModel?.selectedTagsDataList?.size ?: 0) > 0) {
            mBinding.llTags.visible()
        } else {
            mBinding.llTags.gone()
        }

        mBinding.ivRefresh.setOnClickListener {
            mViewModel?.isSubCategoryClicked = false
            mViewModel?.isRefreshClicked = true
            mViewModel?.selectedTagsDataList?.clear()
            setChipGroupDataInFilterDialog()
        }

        mBinding.ivCancelFilterCategoryDialog.setOnClickListener {
            mViewModel?.selectedTagsDataList?.clear()
            dismiss()
        }

        mBinding.btnApplyFilterCategoryDialog.setOnClickListener {
            onWellnessFilterDialogClickListener?.onTagsSelectedClicked()
            dismiss()
        }
    }

    override fun initObserver() {
    }

    override fun handleListener() {
    }

    override fun displayMessage(message: String) {
    }

    private fun setChipGroupDataInFilterDialog() {
        mBinding.progressBarTags.gone()
        mBinding.chipGroup.removeAllViews()
        mViewModel?.tagsDataList?.forEach { tag ->
            val chip: Chip =
                layoutInflater.inflate(
                    R.layout.item_sample_chip,
                    mBinding.chipGroup,
                    false
                ) as Chip
            chip.text = tag?.name

            chip.isChecked = mViewModel?.selectedTagsDataList?.contains(tag?.id) == true

            chip.setOnClickListener {
                if (mViewModel?.selectedTagsDataList?.contains(tag?.id) == true)
                    mViewModel?.selectedTagsDataList?.remove(tag?.id)
                else
                    mViewModel?.selectedTagsDataList?.add(tag?.id ?: 0)
            }
            val animation: Animation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_left_max_time)

            chip.startAnimation(animation)
            mBinding.chipGroup.addView(chip)
        }
    }

    interface OnWellnessFilterDialogClickListener {
        fun onTagsSelectedClicked()
    }
}