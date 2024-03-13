package com.app.taocalligraphy.ui.meditation_session

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityHostEndSessionBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HostEndSessionActivity : BaseActivity<ActivityHostEndSessionBinding>() {

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, HostEndSessionActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_host_end_session
    }

    override fun initView() {
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
        mBinding.btnScheduleSession.setOnClickListener {
            CreateMeditationSessionActivity.startActivity(this, true)
        }

        mBinding.btnClose.setOnClickListener {
            openHomeScreen()
        }
    }

    private fun openHomeScreen() {
        MainActivity.startActivity(this)
        finishAffinity()
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