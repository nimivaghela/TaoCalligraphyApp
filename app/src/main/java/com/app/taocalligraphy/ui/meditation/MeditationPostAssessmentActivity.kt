package com.app.taocalligraphy.ui.meditation

import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityMeditationPostAssessmentBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.journal.CreateNewJournalActivity
import com.app.taocalligraphy.ui.meditation.adapter.PostAssessmentViewPagerAdapter
import com.app.taocalligraphy.ui.meditation.fragment.OnContinueClickListener
import com.app.taocalligraphy.ui.meditation.fragment.OnSaveClickListener
import com.app.taocalligraphy.ui.meditation.fragment.PostAssessmentQuestionFragment
import com.app.taocalligraphy.ui.meditation.fragment.PostAssessmentTagsFragment
import com.app.taocalligraphy.ui.meditation.viewmodel.MeditationContentViewModel
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImageProfile
import com.google.gson.JsonArray
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class MeditationPostAssessmentActivity : BaseActivity<ActivityMeditationPostAssessmentBinding>(),
    OnContinueClickListener, OnSaveClickListener {

    val meditationContentResponse: MeditationContentResponse? by lazy {
        return@lazy intent.extras?.getParcelable(Constants.MeditationContent) as MeditationContentResponse?
    }

    private val mViewModel: MeditationContentViewModel by viewModels()

    companion object {
        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
            meditationContentResponse: MeditationContentResponse? = null
        ) {
            Log.e("MyTag","start post assessment activity")
            val intent = Intent(activity, MeditationPostAssessmentActivity::class.java)
            intent.putExtra(Constants.MeditationContent, meditationContentResponse)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_meditation_post_assessment

    override fun initView() {
        mBinding.toolbar.ivBackToolbar.visible()
        mBinding.toolbar.cardProfile.visible()
        updateProfile()

        mBinding.txtTitle.text =
            getString(R.string.post_assessment_title_user, userHolder.firstName)

        mBinding.toolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )

        val questionFragment =
            PostAssessmentQuestionFragment.newInstance(meditationContentResponse, this, mViewModel)
        val tagsFragment = PostAssessmentTagsFragment.newInstance(meditationContentResponse, this, mViewModel)

        mBinding.viewPagerAssessment.adapter = PostAssessmentViewPagerAdapter(
            supportFragmentManager,
            arrayListOf(questionFragment, tagsFragment)
        )

        if (mViewModel.isSelectedPain) {
            mBinding.viewPagerAssessment.currentItem = 1
            mBinding.tab1.backgroundTintList =
                ColorStateList.valueOf(this.getColor(R.color.tabs_default))
            mBinding.tab2.backgroundTintList = ColorStateList.valueOf(this.getColor(R.color.gold))
        } else {
            mBinding.viewPagerAssessment.currentItem = 0
        }
    }

    private fun updateProfile() {
        mBinding.toolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
        mBinding.toolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.toolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this@MeditationPostAssessmentActivity)
        }

        mBinding.btnSkip.setOnClickListener {
            sendFeedBack(0, "", JsonArray())
            MeditationDetailActivity.instance?.finish()
            finish()
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

    override fun onContinueClicked(painLevel: Int) {
        mBinding.tab1.backgroundTintList =
            ColorStateList.valueOf(this.getColor(R.color.tabs_default))
        mBinding.tab2.backgroundTintList = ColorStateList.valueOf(this.getColor(R.color.gold))

        mBinding.viewPagerAssessment.currentItem = 1
        mViewModel.postAssessmentPainLevel = painLevel
        mViewModel.isSelectedPain = true
    }

    override fun onSaveClicked(
        isSaveAndClose: Boolean,
        selectedExperience: ArrayList<MeditationContentResponse.FeedbackTag>?,
        feedBack: String
    ) {

        val jsonTags = JsonArray()
        selectedExperience?.forEach {
            jsonTags.add(it.feedbackId)
        }
        sendFeedBack(mViewModel.postAssessmentPainLevel, feedBack, jsonTags)
        if (!isSaveAndClose) {
            if (!(UserHolder.EnumUserModulePermission.JOURNAL.permission?.canAccess ?: true)) {
                SubscriptionActivity.startActivityForResult(
                    this@MeditationPostAssessmentActivity
                )
                return
            }

            meditationContentResponse?.let {
                CreateNewJournalActivity.startActivity(this, title = it.title ?: "")
            }
        }
        MeditationDetailActivity.instance?.finish()
        finish()
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

    override fun onBackPressed() {
        MeditationDetailActivity.instance?.finish()
        finish()
    }
}