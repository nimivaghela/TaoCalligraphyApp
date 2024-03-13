package com.app.taocalligraphy.ui.goals.dialog

import android.content.DialogInterface
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseDialogFragment
import com.app.taocalligraphy.databinding.DialogGoalScreenBinding
import com.app.taocalligraphy.models.response.profile.UserGoalsScreenApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.ui.goals.GoalsActivity
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.isNetwork
import com.google.android.material.chip.Chip
import com.google.gson.JsonArray
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GoalScreenDialog :
    BaseDialogFragment<DialogGoalScreenBinding>() {

    private val mViewModel: ProfileViewModel by viewModels()
    private var isGoalsScreen = false

    companion object {
        const val TAG = "GoalScreensDialog"
        var goalScreenListener: GoalScreenListener? = null
        var userGoalsScreenList = ArrayList<UserGoalsScreenApiResponse.UserGoalsScreenList?>()
        var currPage = 1
        private var selectedList = ArrayList<Int>()
        fun newInstance(
            goalListener: GoalScreenListener,
            selectedMeditationTarget: Int
        ): GoalScreenDialog {
            // clearign previous data when Dialogs Reopens
            currPage = 1
            userGoalsScreenList.clear()
            this.goalScreenListener = goalListener
            return GoalScreenDialog()
        }
    }

    override fun getResource() = R.layout.dialog_goal_screen

    override fun postInit() {
        if (requireContext().isNetwork())
            if(userGoalsScreenList.size == 0)
            mViewModel.getUserGoalsScreen1Api(this@GoalScreenDialog, mDisposable)
            else
            updateView()
    }

    override fun initObserver() {
        mViewModel.userGoalsScreen1LiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.list?.let { dataList ->
                    userGoalsScreenList.clear()
                    selectedList.clear()
                    userGoalsScreenList.addAll(dataList)
                    updateView()
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let {
                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let {
                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_SHORT
                                ).show()
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
                mViewModel.userGoalsScreen1LiveData.postValue(null)
            }
        }

        mViewModel.userGoalsScreen2LiveData.observe(this@GoalScreenDialog) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let {
                    userGoalsScreenList.clear()
                    selectedList.clear()
                    it.list?.let { it1 -> userGoalsScreenList.addAll(it1) }
                    currPage = 2
                    updateView()
                    mViewModel.userGoalsScreen2LiveData.postValue(null)
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let {
                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let {
                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_SHORT
                                ).show()
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

        mViewModel.userGoalLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    if (isGoalsScreen) {
//                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
//                            .show()
                        activity?.longToast(it.message.toString(), SUCCESS)
                        goalScreenListener?.onClear()
                        dismiss()
                    } else {
                        mBinding.btnNext.gone()
                        mBinding.chipGroupBrings1.gone()
                        mBinding.btnSave.visible()
                        mBinding.chipGroupBrings2.visible()
                        if (requireContext().isNetwork())
                            mViewModel.getUserGoalsScreen2Api(this@GoalScreenDialog, mDisposable)
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let {
                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let {
                                Toast.makeText(
                                    requireContext(),
                                    it,
                                    Toast.LENGTH_SHORT
                                ).show()
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
            mViewModel.userGoalLiveData.postValue(null)
        }
    }

    private fun updateView(){
        if(currPage == 1){
            updateViewForPage1()
        }else{
            updateViewForPage2()
        }
    }

    private fun updateViewForPage1() {
        mBinding.tvQuestion.text = getString(R.string.what_brings_you_here_question) /*"What Brings You Here?"*/
        for (i in userGoalsScreenList.indices) {
            val chip = layoutInflater.inflate(
                R.layout.item_goal,
                mBinding.chipGroupBrings1,
                false
            ) as Chip
            chip.text = userGoalsScreenList[i]?.name
            mBinding.chipGroupBrings1.addView(chip)
            userGoalsScreenList[i]?.isSelected?.let { isSelected ->
                if (isSelected) {
                    chip.isChecked = true
                    selectedList.add(userGoalsScreenList[i]?.keywordId ?: -1)
                } else
                    chip.isChecked = false
            } ?: kotlin.run {
                chip.isChecked = false
            }

            chip.setOnCheckedChangeListener { chipView, isChecked ->
                chipView.isChecked = isChecked
                val index = mBinding.chipGroupBrings1.indexOfChild(chipView)
                userGoalsScreenList[index]?.isChanged = true
                userGoalsScreenList[index]?.isSelected = isChecked

                if (userGoalsScreenList.any { data -> data?.isSelected == true }) {
                    mBinding.btnNext.alpha = 1.0f
//                                mBinding.shadowViewNext.alpha = 1.0f
                    mBinding.btnNext.isEnabled = true
                } else {
                    mBinding.btnNext.alpha = 0.5f
//                                mBinding.shadowViewNext.alpha = 0.5f
                    mBinding.btnNext.isEnabled = false
                }
            }
        }

        if (userGoalsScreenList.any { data -> data?.isSelected == true }) {
            mBinding.btnNext.alpha = 1.0f
//                        mBinding.shadowViewNext.alpha = 1.0f
            mBinding.btnNext.isEnabled = true
        } else {
            mBinding.btnNext.alpha = 0.5f
//                        mBinding.shadowViewNext.alpha = 0.5f
            mBinding.btnNext.isEnabled = false
        }
    }

    private fun updateViewForPage2() {
        mBinding.tvQuestion.text =
            getString(R.string.what_would_you_like_to_achieve_question) /*"What Would You Like To  Achieve?"*/

        for (i in userGoalsScreenList.indices) {
            val chip = layoutInflater.inflate(
                R.layout.item_goal,
                mBinding.chipGroupBrings2,
                false
            ) as Chip
            chip.text = userGoalsScreenList[i]?.name
            mBinding.chipGroupBrings2.addView(chip)
            userGoalsScreenList[i]?.isSelected?.let { isSelected ->
                if (isSelected) {
                    chip.isChecked = true
                    selectedList.add(userGoalsScreenList[i]?.keywordId ?: -1)
                } else
                    chip.isChecked = false
            } ?: kotlin.run {
                chip.isChecked = false
            }

            chip.setOnCheckedChangeListener { chipView, isChecked ->
                chipView.isChecked = isChecked
                val index = mBinding.chipGroupBrings2.indexOfChild(chipView)
                userGoalsScreenList[index]?.isChanged = true
                userGoalsScreenList[index]?.isSelected = isChecked

                if (userGoalsScreenList.any { data -> data?.isSelected == true }) {
                    mBinding.btnSave.alpha = 1.0f
                    mBinding.btnSave.isEnabled = true
                } else {
                    mBinding.btnSave.alpha = 0.5f
                    mBinding.btnSave.isEnabled = false
                }
            }
        }
        // Hide page 1 and show page 2.
        mBinding.btnNext.gone()
        mBinding.chipGroupBrings1.gone()
        mBinding.btnSave.visible()
        mBinding.chipGroupBrings2.visible()

        if (userGoalsScreenList.any { data -> data?.isSelected == true }) {
            mBinding.btnSave.alpha = 1.0f
            mBinding.btnSave.isEnabled = true
        } else {
            mBinding.btnSave.alpha = 0.5f
            mBinding.btnSave.isEnabled = false
        }
        isGoalsScreen = true
    }

    override fun handleListener() {
        mBinding.btnNext.clickWithDebounce {
            val keywordIds = JsonArray()
            val deletedKeywordIds = JsonArray()
            for (arrayList in userGoalsScreenList) {
                if (arrayList?.isSelected!!)
                    keywordIds.add(arrayList.keywordId)
                else if (selectedList.contains(arrayList.keywordId))
                    deletedKeywordIds.add(arrayList.keywordId)
            }

            if (requireContext().isNetwork())
                mViewModel.userGoalsApi(
                    deletedKeywordIds,
                    keywordIds,
                    1, GoalsActivity.selectedMeditationTarget,
                    this@GoalScreenDialog,
                    mDisposable
                )
        }
        mBinding.btnSave.clickWithDebounce {
            val keywordIds = JsonArray()
            val deletedKeywordIds = JsonArray()
            for (arrayList in userGoalsScreenList) {
                if (arrayList?.isSelected!!)
                    keywordIds.add(arrayList.keywordId)
                else if (selectedList.contains(arrayList.keywordId))
                    deletedKeywordIds.add(arrayList.keywordId)
            }

            if (requireContext().isNetwork())
                mViewModel.userGoalsApi(
                    deletedKeywordIds,
                    keywordIds,
                    2, GoalsActivity.selectedMeditationTarget,
                    this@GoalScreenDialog,
                    mDisposable
                )
        }
        mBinding.ivClose.clickWithDebounce {
            dismiss()
        }
    }

    override fun displayMessage(message: String) {
    }

    override fun onDestroyView() {
        if (dialog != null) {
            dialog?.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        goalScreenListener?.onDismiss()
    }

    interface GoalScreenListener {
        fun onClear()
        fun onDismiss()
    }
}