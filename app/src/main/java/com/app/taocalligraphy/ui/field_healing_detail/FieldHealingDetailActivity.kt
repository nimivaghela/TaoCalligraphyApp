package com.app.taocalligraphy.ui.field_healing_detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityFieldHealingDetailBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FieldHealingDetailActivity : BaseActivity<ActivityFieldHealingDetailBinding>() {

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, FieldHealingDetailActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_field_healing_detail

    override fun initView() {
        setupToolbar()
    }

    private fun setupToolbar() {
        setToolbar(mBinding.toolbar.innerToolbar, getString(R.string.heart), true)
    }

    override fun observeApiCallbacks() {

    }

    override fun handleListener() {

        mBinding.btnBegin.setOnClickListener {
            StartFieldHealingActivity.startActivity(this)
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