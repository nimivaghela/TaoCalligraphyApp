package com.app.taocalligraphy.ui.challenges.fragment

import android.view.View
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentProgramsProgramBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.challenges.adapter.ChallengeDaySelectSingleAdapter
import com.app.taocalligraphy.ui.challenges.adapter.ChallengeDayWiseListAdapter
import com.app.taocalligraphy.ui.challenges.dialog.ShowGratitudeDialog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChallengeStepsFragment : BaseFragment<FragmentProgramsProgramBinding>(),
    ChallengeDaySelectSingleAdapter.OnDayClickListener,
    ChallengeDayWiseListAdapter.StepClickListener {

    private val challengeDaySelectSingleAdapter by lazy {
        return@lazy ChallengeDaySelectSingleAdapter(requireContext(), this)
    }
    private val challengeDayWiseListAdapter by lazy {
        return@lazy ChallengeDayWiseListAdapter(requireContext(), this)
    }

    override fun getLayoutId() = R.layout.fragment_programs_program
    override fun observeApiCallbacks() {

    }
    override fun displayMessage(message: String) {
    }

    override fun initView() {
        mBinding.rvProgramDate.adapter = challengeDaySelectSingleAdapter
        mBinding.rvProgramList.adapter = challengeDayWiseListAdapter
    }

    override fun postInit() {
    }

    override fun initObserver() {
    }

    override fun handleListener() {
    }

    public fun joinedPrograms() {
        mBinding.ivTransparentBackground.visibility = View.VISIBLE
        challengeDaySelectSingleAdapter.onUpdateDateUI()
    }

    override fun onDayClick(position: Int) {

    }

    override fun onStepClick(adapterPosition: Int) {
        if (adapterPosition == 0) {
            showGratitudeDialog()
        }
    }

    private fun showGratitudeDialog() {
        val dialog = ShowGratitudeDialog.newInstance()
        dialog.show(
            getFragmentTransactionFromTag(ShowGratitudeDialog.TAG),
            ShowGratitudeDialog.TAG
        )
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