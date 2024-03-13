package com.app.taocalligraphy.ui.challenges.fragment

import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentChallengeRewardBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.challenges.adapter.ChallengeRewardsAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChallengeRewardsFragment : BaseFragment<FragmentChallengeRewardBinding>() {

    override fun getLayoutId() = R.layout.fragment_challenge_reward

    private val challengeRewardsAdapter by lazy {
        return@lazy ChallengeRewardsAdapter(requireContext())
    }

    override fun initView() {
        mBinding.rvReward.adapter = challengeRewardsAdapter
    }

    override fun postInit() {
    }

    override fun observeApiCallbacks() {

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