package com.app.taocalligraphy.ui.meditation

import android.animation.ValueAnimator
import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityMeditationFeedbackBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.journal.CreateNewJournalActivity
import com.app.taocalligraphy.ui.meditation.viewmodel.MeditationContentViewModel
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.isTablet
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.google.android.material.chip.Chip
import com.google.gson.JsonArray
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class MeditationFeedbackActivity : BaseActivity<ActivityMeditationFeedbackBinding>() {

    companion object {
        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
            meditationContentResponse: MeditationContentResponse? = null
        ) {
            val intent = Intent(activity, MeditationFeedbackActivity::class.java)
            intent.putExtra(Constants.MeditationContent, meditationContentResponse)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_meditation_feedback
    }

    val meditationContentResponse: MeditationContentResponse? by lazy {
        return@lazy intent.extras?.getParcelable(Constants.MeditationContent) as MeditationContentResponse?
    }

    private val mViewModel: MeditationContentViewModel by viewModels()

    private val selectedExperience = ArrayList<MeditationContentResponse.FeedbackTag>()
    private var painLevel = 0

    override fun initView() {
        setupToolbar()
        setData()
        mBinding.lottieMascot.setAnimation("mascot_standing_idle_loop.json")
//        mBinding.lottieMascot.repeatCount = 0

//        mBinding.lottieMascot.addAnimatorListener(object : Animator.AnimatorListener {
//            override fun onAnimationStart(p0: Animator?) {
//
//            }
//
//            override fun onAnimationEnd(p0: Animator?) {
//                mBinding.lottieMascot.removeAllAnimatorListeners()
//                mBinding.lottieMascot.setAnimation("mascot_standing_idle_loop.json")
//                mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
//                mBinding.lottieMascot.playAnimation()
//            }
//
//            override fun onAnimationCancel(p0: Animator?) {
//
//            }
//
//            override fun onAnimationRepeat(p0: Animator?) {
//
//            }
//        })
    }

    private fun setData() {
        meditationContentResponse?.let {
            mBinding.tvTitle.text = it.ratingDetails?.ratingQuestion ?: ""
            mBinding.txtFeelLow.text = it.ratingDetails?.ratingWorst ?: ""
            mBinding.txtFeelHappy.text = it.ratingDetails?.ratingBest ?: ""
            setChipGroupDataDynamically(it.feedbackTagList)
        }
    }

    private fun setupToolbar() {
        setToolbar(mBinding.toolbar.innerToolbar, "You Did It, ${userHolder.firstName}!", true)
        mBinding.toolbar.ivBackToolbar.setImageResource(R.drawable.vd_grey_back_arrow)

        if (!isTablet(this@MeditationFeedbackActivity)) {
            mBinding.toolbar.tvTitleToolbar.textSize = 30f
        }
    }

    override fun observeApiCallbacks() {
        mViewModel.preAssessmentFeedbackLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                }
                longToastState(requestState.error)
            }
        }
    }

    override fun handleListener() {
        mBinding.ivExtremePain.setOnClickListener {
            painLevel = 1
            setPainInView(mBinding.ivExtremePain, R.drawable.vd_pain_extreme_emoji_active)
            mBinding.lottieMascot.setAnimation("mascot_rating_01.json")
            mBinding.lottieMascot.playAnimation()
            mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
        }

        mBinding.ivHighPain.setOnClickListener {
            painLevel = 2
            setPainInView(mBinding.ivHighPain, R.drawable.vd_pain_high_emoji_active)
            mBinding.lottieMascot.setAnimation("mascot_rating_02.json")
            mBinding.lottieMascot.playAnimation()
            mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
        }

        mBinding.ivNormalPain.setOnClickListener {
            painLevel = 3
            setPainInView(mBinding.ivNormalPain, R.drawable.vd_pain_normal_emoji_active)
            mBinding.lottieMascot.setAnimation("mascot_rating_03.json")
            mBinding.lottieMascot.playAnimation()
            mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
        }

        mBinding.ivLowPain.setOnClickListener {
            painLevel = 4
            setPainInView(mBinding.ivLowPain, R.drawable.vd_pain_low_emoji_active)
            mBinding.lottieMascot.setAnimation("mascot_rating_04.json")
            mBinding.lottieMascot.playAnimation()
            mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
        }

        mBinding.ivNoPain.setOnClickListener {
            painLevel = 5
            setPainInView(mBinding.ivNoPain, R.drawable.vd_no_pain_emoji_active)
            mBinding.lottieMascot.setAnimation("mascot_rating_05.json")
            mBinding.lottieMascot.playAnimation()
            mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
        }

        mBinding.btnSaveAndClose.setOnClickListener {
            val jsonTags = JsonArray()
            selectedExperience.forEach {
                jsonTags.add(it.feedbackId)
            }
            sendFeedBack(painLevel, mBinding.txtFeedback.text.toString(), jsonTags)
            MeditationDetailActivity.instance?.finish()
            finish()
        }

        mBinding.btnSaveAndJournal.setOnClickListener {
            val jsonTags = JsonArray()
            selectedExperience.forEach {
                jsonTags.add(it.feedbackId)
            }
            sendFeedBack(painLevel, mBinding.txtFeedback.text.toString(), jsonTags)
            meditationContentResponse?.let {
                CreateNewJournalActivity.startActivityWithResult(this, title = it.title ?: "")
            }
            MeditationDetailActivity.instance?.finish()
            finish()
        }

        mBinding.btnSkip.setOnClickListener {
            sendFeedBack(0, "", JsonArray())
            MeditationDetailActivity.instance?.finish()
            finish()
        }
    }

    private fun sendFeedBack(painLevel: Int, feedback: String, jsonTags: JsonArray) {
        mViewModel.postAssessmentFeedback(
            meditationContentResponse?.id ?: "",
            painLevel,
            jsonTags,
            feedback,
            this,
            mDisposable
        )
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
        mBinding.btnSaveAndClose.alpha = 1.0f
        mBinding.btnSaveAndClose.isEnabled = true
        mBinding.btnSaveAndJournal.alpha = 1.0f
        mBinding.btnSaveAndJournal.isEnabled = true
    }

    private fun setChipGroupDataDynamically(feedbackTags: List<MeditationContentResponse.FeedbackTag>) {
        mBinding.feedbackChipGroup.removeAllViews()
        feedbackTags.forEach { feedbackTag ->
            val chip: Chip =
                layoutInflater.inflate(
                    R.layout.item_sample_chip,
                    mBinding.feedbackChipGroup,
                    false
                ) as Chip
            chip.text = feedbackTag.name

            chip.setOnClickListener {
                if (selectedExperience.contains(feedbackTag)) {
                    chip.setBackgroundResource(R.drawable.bg_white_gold_border_22dp)
                    chip.setTextColor(ContextCompat.getColor(this, R.color.dark_grey))
                    selectedExperience.remove(feedbackTag)
                } else {
                    selectedExperience.add(feedbackTag)
                    chip.setBackgroundResource(R.drawable.bg_gold_22dp)
                    chip.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
            }
            mBinding.feedbackChipGroup.addView(chip)
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