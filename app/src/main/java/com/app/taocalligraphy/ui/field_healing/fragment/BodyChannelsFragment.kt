package com.app.taocalligraphy.ui.field_healing.fragment

import android.view.View
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentBodyChannelsBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BodyChannelsFragment : BaseFragment<FragmentBodyChannelsBinding>() {

    override fun getLayoutId() = R.layout.fragment_body_channels

    override fun displayMessage(message: String) {

    }

    override fun initView() {

    }

    override fun postInit() {

    }

    override fun initObserver() {

    }

    override fun observeApiCallbacks() {

    }

    override fun handleListener() {
        mBinding.ivMale.setOnClickListener {
            mBinding.ivFemale.setImageResource(R.drawable.vd_female_icon)
            mBinding.ivMale.setImageResource(R.drawable.vd_male_icon_select)
            mBinding.ivFemaleBody.visibility = View.GONE
            mBinding.ivMaleBody.visibility = View.VISIBLE
        }
        mBinding.ivFemale.setOnClickListener {
            mBinding.ivMale.setImageResource(R.drawable.vd_male_icon)
            mBinding.ivFemale.setImageResource(R.drawable.vd_female_icon_select)
            mBinding.ivMaleBody.visibility = View.GONE
            mBinding.ivFemaleBody.visibility = View.VISIBLE
        }
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