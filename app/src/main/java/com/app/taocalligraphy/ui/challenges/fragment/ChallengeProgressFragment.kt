package com.app.taocalligraphy.ui.challenges.fragment

import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentChallengeProgressBinding
import com.app.taocalligraphy.databinding.FragmentProgramsProgressBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.challenges.adapter.ChallengeProgressAdapter
import com.app.taocalligraphy.ui.program.adapter.ProgramProgressAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChallengeProgressFragment : BaseFragment<FragmentChallengeProgressBinding>() {

    override fun getLayoutId() = R.layout.fragment_challenge_progress

    private val challengeProgressAdapter by lazy {
        return@lazy ChallengeProgressAdapter()
    }

    override fun initView() {
        mBinding.rvProgress.adapter = challengeProgressAdapter
    }

    override fun observeApiCallbacks() {

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