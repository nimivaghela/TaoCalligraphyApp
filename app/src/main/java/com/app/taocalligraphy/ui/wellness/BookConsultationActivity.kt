package com.app.taocalligraphy.ui.wellness

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityBookConsultationBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.wellness.adapter.CoachedHelpYouInfoAdapter
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class BookConsultationActivity : BaseActivity<ActivityBookConsultationBinding>() {

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, BookConsultationActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_book_consultation
    }

    private val coachedHelpYouInfoAdapter by lazy {
        return@lazy CoachedHelpYouInfoAdapter()
    }

    override fun initView() {
        setupToolbar()
        mBinding.rvHelpYou.adapter = coachedHelpYouInfoAdapter
    }

    private fun setupToolbar() {
        setToolbar(
            mBinding.toolbar.innerToolbar,
            getString(R.string.book_a_consultation),
            true,
        )
        mBinding.toolbar.tvTitleToolbar.textSize = 30f
        mBinding.toolbar.tvTitleToolbar.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.medium_grey
            )
        )
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
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