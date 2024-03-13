package com.app.taocalligraphy.ui.meditation_session

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityMeditationRecommendationBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.meditation_session.adapter.MeditationProgramAdapter
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MeditationRecommendationActivity : BaseActivity<ActivityMeditationRecommendationBinding>() {

    companion object {
        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
        ) {
            val intent = Intent(activity, MeditationRecommendationActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_meditation_recommendation
    }

    private val meditationProgramAdapter by lazy {
        return@lazy MeditationProgramAdapter()
    }

    override fun initView() {
        mBinding.rvRecommendation.adapter = meditationProgramAdapter
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
        mBinding.tvMeditationTab.setOnClickListener {
            changeTabBackground(
                activeTextView = mBinding.tvMeditationTab,
                otherTextView = arrayOf(mBinding.tvSessionTab, mBinding.tvRoomsTab),
            )
        }

        mBinding.tvSessionTab.setOnClickListener {
            changeTabBackground(
                activeTextView = mBinding.tvSessionTab,
                otherTextView = arrayOf(mBinding.tvMeditationTab, mBinding.tvRoomsTab),
            )
        }

        mBinding.tvRoomsTab.setOnClickListener {
            changeTabBackground(
                activeTextView = mBinding.tvRoomsTab,
                otherTextView = arrayOf(mBinding.tvMeditationTab, mBinding.tvSessionTab),
            )
        }

        mBinding.ivClose.setOnClickListener {
            openHomeScreen()
        }

        mBinding.btnReturnHome.setOnClickListener {
            openHomeScreen()
        }
    }

    private fun changeTabBackground(
        activeTextView: AppCompatTextView,
        vararg otherTextView: AppCompatTextView
    ) {
        for (textview in otherTextView) {
            textview.setBackgroundResource(R.drawable.bg_gray_95_radius_22dp)
            textview.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))
        }

        activeTextView.setBackgroundResource(R.drawable.bg_white_gold_border_22dp)
        activeTextView.setTextColor(ContextCompat.getColor(this, R.color.gold))
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