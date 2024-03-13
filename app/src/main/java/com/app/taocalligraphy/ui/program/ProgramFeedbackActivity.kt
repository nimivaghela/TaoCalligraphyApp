package com.app.taocalligraphy.ui.program

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.widget.RatingBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityProgramFeedbackBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.program.FeedbackTag
import com.app.taocalligraphy.models.response.program.LinkedProgramData
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.ui.journal.CreateNewJournalActivity
import com.app.taocalligraphy.ui.program.viewmodel.ProgramViewModel
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.google.android.material.chip.Chip
import com.google.gson.JsonArray
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ProgramFeedbackActivity : BaseActivity<ActivityProgramFeedbackBinding>() {

    private val programId by lazy {
        return@lazy intent?.extras?.getInt(Constants.Param.programId) ?: 0
    }
    private val programName by lazy {
        return@lazy intent?.extras?.getString(Constants.Param.programName, "")
    }

    private val isFromHistoryCompletedProgram by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromHistoryCompletedProgram, false)
            ?: false
    }

    private val mViewModel: ProgramViewModel by viewModels()

    companion object {
        fun startActivity(
            activity: Activity,
            programId: Int,
            programName: String,
            isFromHistoryCompletedProgram: Boolean
        ) {
            val intent = Intent(activity, ProgramFeedbackActivity::class.java)
            intent.putExtra(Constants.Param.programId, programId)
            intent.putExtra(Constants.Param.programName, programName)
            intent.putExtra(
                Constants.Param.isFromHistoryCompletedProgram,
                isFromHistoryCompletedProgram
            )
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_program_feedback

    private var createJournalResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.data?.getBooleanExtra("showSnackbar", false) == true) {
                longToast(
                    result.data?.getStringExtra("message") ?: "",
                    result.data?.getStringExtra("type") ?: Constants.SUCCESS
                )
            }
        }

    override fun initView() {
        if (mViewModel.linkedProgramData == null)
            mViewModel.getLinkedProgram(programId, isFromHistoryCompletedProgram, this, mDisposable)

        programName?.let {
            mBinding.txtProgramName.text = it
        }

        mBinding.txtUserName.text = getString(R.string.program_review_title, userHolder.firstName)
        mBinding.lottieMascot.setAnimation("mascot_appears_and_cheers_user.json")
        mBinding.lottieMascot.repeatCount = 0
        mBinding.lottieMascot.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                mBinding.lottieMascot.removeAllAnimatorListeners()
                mBinding.lottieMascot.setAnimation("mascot_standing_idle_loop.json")
                mBinding.lottieMascot.repeatCount = ValueAnimator.INFINITE
                mBinding.lottieMascot.playAnimation()
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
        mBinding.lottieMascot.playAnimation()
    }

    override fun observeApiCallbacks() {
        mViewModel.linkedProgramDataRes.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { linkProgram ->
                        mViewModel.linkedProgramData = linkProgram
                        setData()
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        else -> {
                            errorObj.customMessage?.let { longToast(it, ERROR) }
                        }
                    }
                }
            }
        }
    }

    override fun handleListener() {
        mBinding.btnSkip.clickWithDebounce {
            val jsonTags = JsonArray()
            mViewModel.selectedExperience.forEach {
                jsonTags.add(it.id)
            }
            sendFeedBack(
                mBinding.ratingFeedback.rating.toInt(),
                mBinding.txtFeedback.text.toString(),
                jsonTags
            )
            if (mViewModel.linkedProgramData?.extraLinkedProgramList?.isEmpty() == false)
                ProgramFeedbackSuggestionsActivity.startActivity(
                    this,
                    programId,
                    isFromHistoryCompletedProgram
                )
            else {
                finish()
            }
        }

        mBinding.btnSaveAndJournal.clickWithDebounce {
            val jsonTags = JsonArray()
            mViewModel.selectedExperience.forEach {
                jsonTags.add(it.id)
            }
            sendFeedBack(
                mBinding.ratingFeedback.rating.toInt(),
                mBinding.txtFeedback.text.toString(),
                jsonTags
            )
            CreateNewJournalActivity.startActivity(this, programName ?: "")
            finish()
        }

        mBinding.btnSaveAndClose.clickWithDebounce {
            val jsonTags = JsonArray()
            mViewModel.selectedExperience.forEach {
                jsonTags.add(it.id)
            }
            sendFeedBack(
                mBinding.ratingFeedback.rating.toInt(),
                mBinding.txtFeedback.text.toString(),
                jsonTags
            )

            if (mViewModel.linkedProgramData?.extraLinkedProgramList?.isEmpty() == false)
                ProgramFeedbackSuggestionsActivity.startActivity(
                    this,
                    programId,
                    isFromHistoryCompletedProgram
                )
            else {
                finish()
            }
        }

        mBinding.ratingFeedback.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                if (rating > 0) {
                    mBinding.btnSaveAndClose.alpha = 1.0f
                    mBinding.btnSaveAndClose.isEnabled = true
                    mBinding.btnSaveAndJournal.alpha = 1.0f
                    mBinding.btnSaveAndJournal.isEnabled = true
                } else {
                    mBinding.btnSaveAndClose.alpha = 0.5f
                    mBinding.btnSaveAndClose.isEnabled = false
                    mBinding.btnSaveAndJournal.alpha = 0.5f
                    mBinding.btnSaveAndJournal.isEnabled = false
                }
            }
    }

    private fun sendFeedBack(painLevel: Int, feedback: String, jsonTags: JsonArray) {
        mViewModel.sendProgramFeedback(
            programId,
            painLevel,
            jsonTags,
            feedback,
            isFromHistoryCompletedProgram,
            this,
            mDisposable
        )
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

    private fun setData() {
        mViewModel.linkedProgramData?.let {
            setChipGroupDataDynamically(it.feedbackTagList)
        }
    }

    private fun setChipGroupDataDynamically(feedbackTags: List<FeedbackTag>) {
        mBinding.feedbackChipGroup.removeAllViews()
        feedbackTags.forEach { feedbackTag ->
            val chip: Chip =
                layoutInflater.inflate(
                    R.layout.item_sample_chip,
                    mBinding.feedbackChipGroup,
                    false
                ) as Chip
            chip.text = feedbackTag.name

            chip.isChecked = mViewModel.selectedExperience.contains(feedbackTag)

            chip.setOnClickListener {
                if (mViewModel.selectedExperience.contains(feedbackTag)) {
                    chip.setBackgroundResource(R.drawable.bg_white_gold_border_22dp)
                    chip.setTextColor(ContextCompat.getColor(this, R.color.dark_grey))
                    mViewModel.selectedExperience.remove(feedbackTag)
                } else {
                    mViewModel.selectedExperience.add(feedbackTag)
                    chip.setBackgroundResource(R.drawable.bg_gold_22dp)
                    chip.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
            }
            mBinding.feedbackChipGroup.addView(chip)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mBinding.btnSkip.performClick()
    }
}