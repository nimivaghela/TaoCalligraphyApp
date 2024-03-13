package com.app.taocalligraphy.ui.profile_rewards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityProfileRewardsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.profile_rewards.adapter.ProfileRewardAdapter
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProfileRewardsActivity : BaseActivity<ActivityProfileRewardsBinding>() {

    private var mProfileRewardAdapter: ProfileRewardAdapter? = null

    override fun getResource() = R.layout.activity_profile_rewards

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, ProfileRewardsActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun initView() {
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setToolbar(mBinding.toolbar.innerToolbar, getString(R.string.rewards), true)
    }

    private fun setupRecyclerView() {
        mProfileRewardAdapter = ProfileRewardAdapter()
        mBinding.rvRewards.adapter = mProfileRewardAdapter
    }

    override fun observeApiCallbacks() {}

    override fun handleListener() {
        mBinding.apply {
            btnGoToMyRoom.setOnClickListener {

            }
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