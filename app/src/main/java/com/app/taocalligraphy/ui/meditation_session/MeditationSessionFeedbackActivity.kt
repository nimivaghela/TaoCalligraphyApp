package com.app.taocalligraphy.ui.meditation_session

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityMeditationSessionFeedbackBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MeditationSessionFeedbackActivity : BaseActivity<ActivityMeditationSessionFeedbackBinding>() {

    companion object {
        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
        ) {
            val intent = Intent(activity, MeditationSessionFeedbackActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_meditation_session_feedback
    }

    override fun initView() {
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
        mBinding.tvFeelRelaxed.setOnClickListener {
            setFeelExperience(mBinding.tvFeelRelaxed)
        }

        mBinding.tvFeelEnergized.setOnClickListener {
            setFeelExperience(mBinding.tvFeelEnergized)
        }

        mBinding.tvFeelHappier.setOnClickListener {
            setFeelExperience(mBinding.tvFeelHappier)
        }

        mBinding.tvFeelCalmer.setOnClickListener {
            setFeelExperience(mBinding.tvFeelCalmer)
        }

        mBinding.tvFeelLighter.setOnClickListener {
            setFeelExperience(mBinding.tvFeelLighter)
        }

        mBinding.tvFeelLessPain.setOnClickListener {
            setFeelExperience(mBinding.tvFeelLessPain)
        }

        mBinding.btnSaveAndClose.setOnClickListener {
            openMeditationRecommendationScreen()
        }

        mBinding.btnSaveAndJournal.setOnClickListener {
            openMeditationRecommendationScreen()
        }

        mBinding.btnSkip.setOnClickListener {
            openMeditationRecommendationScreen()
        }
    }

    private fun setFeelExperience(activeTextView: AppCompatTextView) {
        setNormalView(
            mBinding.tvFeelRelaxed,
            mBinding.tvFeelEnergized,
            mBinding.tvFeelHappier,
            mBinding.tvFeelCalmer,
            mBinding.tvFeelLighter,
            mBinding.tvFeelLessPain,
        )
        activeTextView.setBackgroundResource(R.drawable.bg_gold_22dp)
        activeTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun setNormalView(vararg activeTextView: AppCompatTextView) {
        for (textview in activeTextView) {
            textview.setBackgroundResource(R.drawable.bg_white_gold_border_22dp)
            textview.setTextColor(ContextCompat.getColor(this, R.color.dark_grey))
        }
    }

    private fun openHomeScreen() {
        MainActivity.startActivity(this)
        finishAffinity()
    }

    private fun openMeditationRecommendationScreen() {
        MeditationRecommendationActivity.startActivity(this)
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