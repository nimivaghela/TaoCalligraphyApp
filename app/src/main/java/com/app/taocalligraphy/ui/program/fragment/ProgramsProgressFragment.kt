package com.app.taocalligraphy.ui.program.fragment

import androidx.fragment.app.viewModels
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentProgramsProgressBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.program.ProgramDetailsActivity
import com.app.taocalligraphy.ui.program.adapter.ProgramProgressAdapter
import com.app.taocalligraphy.ui.program.viewmodel.ProgramViewModel
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ProgramsProgressFragment :
    BaseFragment<FragmentProgramsProgressBinding>() {

    override fun getLayoutId() = R.layout.fragment_programs_progress

    private val programProgressAdapter by lazy {
        return@lazy ProgramProgressAdapter()
    }

    private val mViewModel: ProgramViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    companion object {
        var programId = ""
        var isFromHistoryCompletedProgram: Boolean = false
        fun newInstance(
            programId: String,
            isFromHistoryCompletedProgram: Boolean,
        ): ProgramsProgressFragment {
            this.programId = programId
            this.isFromHistoryCompletedProgram = isFromHistoryCompletedProgram
            return ProgramsProgressFragment()
        }
    }

    override fun initView() {
        mBinding.rvProgress.adapter = programProgressAdapter

        if (mViewModel.programProgressList.isEmpty()) {
            mViewModel.userProgressDetailsApi(
                programId,
                ProgramDetailsActivity.daysList?.programDay.toString(),
                isFromHistoryCompletedProgram,
                this,
                mDisposable
            )
        } else {
            checkProgressDataAvailable()
        }
    }

    override fun observeApiCallbacks() {
        mViewModel.userProgressDetailListLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.data?.let {
                    mViewModel.programProgressList = it
                    checkProgressDataAvailable()
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { activity?.longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { activity?.longToast(it, errorObj.type) }
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
            mViewModel.userProgressDetailListLiveData.postValue(null)
        }
    }

    private fun checkProgressDataAvailable() {
        programProgressAdapter.setProgressData(mViewModel.programProgressList)
        if (mViewModel.programProgressList.isNotEmpty()) {
            mBinding.tvNoProgress.gone()
        } else {
            mBinding.tvNoProgress.visible()
        }
    }

    override fun postInit() {
    }

    override fun initObserver() {
    }

    override fun handleListener() {
    }

    override fun displayMessage(message: String) {
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
}