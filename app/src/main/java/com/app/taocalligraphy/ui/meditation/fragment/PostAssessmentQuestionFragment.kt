package com.app.taocalligraphy.ui.meditation.fragment

import android.animation.ValueAnimator
import androidx.appcompat.widget.AppCompatImageView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseFragment
import com.app.taocalligraphy.databinding.FragmentPostAssessmentQuestionBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.ui.meditation.viewmodel.MeditationContentViewModel
import com.app.taocalligraphy.utils.extensions.clickWithDebounce

class PostAssessmentQuestionFragment : BaseFragment<FragmentPostAssessmentQuestionBinding>() {

    companion object {
        var meditationContentResponse: MeditationContentResponse? = null
        var onContinueClickListener: OnContinueClickListener? = null
        var viewModel: MeditationContentViewModel? = null
        fun newInstance(
            meditationContentResponse: MeditationContentResponse?,
            onContinueClickListener: OnContinueClickListener,
            viewModel: MeditationContentViewModel
        ): PostAssessmentQuestionFragment {
            this.meditationContentResponse = meditationContentResponse
            this.onContinueClickListener = onContinueClickListener
            this.viewModel = viewModel
            return PostAssessmentQuestionFragment()
        }
    }

    override fun getLayoutId() = R.layout.fragment_post_assessment_question

    override fun displayMessage(message: String) {
    }

    override fun initView() {
        mBinding.lottieMascot.setAnimation("mascot_standing_idle_loop.json")

        meditationContentResponse?.let {
            mBinding.txtQuestion.text = it.ratingDetails?.ratingQuestion ?: ""
            mBinding.txtFeelLow.text = it.ratingDetails?.ratingWorst ?: ""
            mBinding.txtFeelHappy.text = it.ratingDetails?.ratingBest ?: ""
        }

        when (viewModel?.postAssessmentPainLevel) {
            1 -> setEmojiExtremePain()
            2 -> setEmojiHighPain()
            3 -> setEmojiNormalPain()
            4 -> setEmojiLowPain()
            5 -> setEmojiNoPain()
        }
    }

    override fun observeApiCallbacks() {
    }

    override fun postInit() {
    }

    override fun initObserver() {
    }

    override fun handleListener() {
        mBinding.btnContinue.clickWithDebounce {
            onContinueClickListener?.onContinueClicked(viewModel?.postAssessmentPainLevel ?: 0)
        }

        mBinding.ivExtremePain.setOnClickListener {
            setEmojiExtremePain()
        }

        mBinding.ivHighPain.setOnClickListener {
            setEmojiHighPain()
        }

        mBinding.ivNormalPain.setOnClickListener {
            setEmojiNormalPain()
        }

        mBinding.ivLowPain.setOnClickListener {
            setEmojiLowPain()
        }

        mBinding.ivNoPain.setOnClickListener {
            setEmojiNoPain()
        }
    }

    private fun setEmojiExtremePain() {
        viewModel?.postAssessmentPainLevel = 1
        setPainInView(mBinding.ivExtremePain, R.drawable.vd_pain_extreme_emoji_active)
        mBinding.lottieMascot.setAnimation("mascot_rating_01.json")
        mBinding.lottieMascot.playAnimation()
        mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
        setEmojiBackgroundAlpha(mBinding.ivExtremePainBg)
    }

    private fun setEmojiHighPain() {
        viewModel?.postAssessmentPainLevel = 2
        setPainInView(mBinding.ivHighPain, R.drawable.vd_pain_high_emoji_active)
        mBinding.lottieMascot.setAnimation("mascot_rating_02.json")
        mBinding.lottieMascot.playAnimation()
        mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
        setEmojiBackgroundAlpha(mBinding.ivHighPainBg)
    }

    private fun setEmojiNormalPain() {
        viewModel?.postAssessmentPainLevel = 3
        setPainInView(mBinding.ivNormalPain, R.drawable.vd_pain_normal_emoji_active)
        mBinding.lottieMascot.setAnimation("mascot_rating_03.json")
        mBinding.lottieMascot.playAnimation()
        mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
        setEmojiBackgroundAlpha(mBinding.ivNormalPainBg)
    }

    private fun setEmojiLowPain() {
        viewModel?.postAssessmentPainLevel = 4
        setPainInView(mBinding.ivLowPain, R.drawable.vd_pain_low_emoji_active)
        mBinding.lottieMascot.setAnimation("mascot_rating_04.json")
        mBinding.lottieMascot.playAnimation()
        mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
        setEmojiBackgroundAlpha(mBinding.ivLowPainBg)
    }

    private fun setEmojiNoPain() {
        viewModel?.postAssessmentPainLevel = 5
        setPainInView(mBinding.ivNoPain, R.drawable.vd_no_pain_emoji_active)
        mBinding.lottieMascot.setAnimation("mascot_rating_05.json")
        mBinding.lottieMascot.playAnimation()
        mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
        setEmojiBackgroundAlpha(mBinding.ivNoPainBg)
    }

    private fun setPainInView(
        activeImageView: AppCompatImageView,
        activeImage: Int
    ) {
        mBinding.ivExtremePain.setImageResource(R.drawable.vd_pain_extreme_high_emoji_normal)
        mBinding.ivHighPain.setImageResource(R.drawable.vd_pain_high_emoji_normal)
        mBinding.ivNormalPain.setImageResource(R.drawable.vd_pain_normal_emoji_normal)
        mBinding.ivLowPain.setImageResource(R.drawable.vd_pain_low_emoji_normal)
        mBinding.ivNoPain.setImageResource(R.drawable.vd_no_pain_emoji_normal)

        mBinding.ivExtremePain.alpha = 0.4f
        mBinding.ivHighPain.alpha = 0.4f
        mBinding.ivNormalPain.alpha = 0.4f
        mBinding.ivLowPain.alpha = 0.4f
        mBinding.ivNoPain.alpha = 0.4f

        activeImageView.setImageResource(activeImage)
        activeImageView.alpha = 1.0f
        mBinding.btnContinue.alpha = 1.0f
        mBinding.btnContinue.isEnabled = true
    }

    private fun setEmojiBackgroundAlpha(activeImageView: AppCompatImageView?) {
        mBinding.ivExtremePainBg?.alpha = 0.4f
        mBinding.ivHighPainBg?.alpha = 0.4f
        mBinding.ivNormalPainBg?.alpha = 0.4f
        mBinding.ivLowPainBg?.alpha = 0.4f
        mBinding.ivNoPainBg?.alpha = 0.4f

        activeImageView?.alpha = 1.0f
    }

    override fun noInternetListener(model: IsNetworkAvailableListener) {
    }
}

interface OnContinueClickListener {
    fun onContinueClicked(painLevel: Int)
}