package com.app.taocalligraphy.ui.questionary

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityQuestionnariesResultBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataModel
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.how_to_meditate.ReadMeditateActivity
import com.app.taocalligraphy.ui.meditation.MeditationDetailActivity
import com.app.taocalligraphy.ui.post_signup_questionnaire.viewmodel.QuestionnarieViewModel
import com.app.taocalligraphy.ui.profile_account_info.ProfileAccountInfoActivity
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.program.ProgramDetailsActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class QuestionnairesResultActivity : BaseActivity<ActivityQuestionnariesResultBinding>(),
    QuestionnairesMeditateAdapter.OnMultiSessionListener,
    QuestionnairesProgramAdapter.OnMultiSessionListener,
    QuestionnairesContentAdapter.OnMultiSessionListener {
    private val mViewModel: QuestionnarieViewModel by viewModels()

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, QuestionnairesResultActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    var isUserSubscribed: Boolean = false
    override fun getResource() = R.layout.activity_questionnaries_result

    private val meditationAdapter by lazy {
        return@lazy QuestionnairesContentAdapter(this)
    }

    private val programAdapter by lazy {
        return@lazy QuestionnairesProgramAdapter(this)
    }

    private val howToMeditateAdapter by lazy {
        return@lazy QuestionnairesMeditateAdapter(this)
    }

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(0, 0, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        mBinding.apply {
            txtName.text = getString(R.string.ready_to_start, userHolder.firstName?.trim())
        }

        var spannableString = SpannableStringBuilder()
        spannableString.append("${getString(R.string.meditation_text_bold)} ")
        spannableString[0, spannableString.length] = StyleSpan(Typeface.BOLD)

        var lastPos = spannableString.length
        spannableString.append(getString(R.string.meditation_text_normal))
        spannableString[lastPos, spannableString.length] = StyleSpan(Typeface.NORMAL)

        mBinding.meditationTitle.text = spannableString

        spannableString = SpannableStringBuilder()
        spannableString.append("${getString(R.string.program_text_bold)} ")
        spannableString[0, spannableString.length] = StyleSpan(Typeface.BOLD)

        lastPos = spannableString.length
        spannableString.append(getString(R.string.program_text_normal))
        spannableString[lastPos, spannableString.length] = StyleSpan(Typeface.NORMAL)

        mBinding.programTitle.text = spannableString

        spannableString = SpannableStringBuilder()
        spannableString.append("${getString(R.string.how_to_meditate_text_bold)} ")
        spannableString[0, spannableString.length] = StyleSpan(Typeface.BOLD)

        lastPos = spannableString.length
        spannableString.append(getString(R.string.how_to_meditate_text_normal))
        spannableString[lastPos, spannableString.length] = StyleSpan(Typeface.NORMAL)

        mBinding.howToMeditateTitle.text = spannableString

        spannableString = SpannableStringBuilder()
        spannableString.append("${getString(R.string.profile_text_bold)} ")
        spannableString[0, spannableString.length] = StyleSpan(Typeface.BOLD)

        lastPos = spannableString.length
        spannableString.append(getString(R.string.profile_text_normal))
        spannableString[lastPos, spannableString.length] = StyleSpan(Typeface.NORMAL)

        mBinding.profileTitle.text = spannableString

        mBinding.rvMeditation.adapter = meditationAdapter
        mBinding.rvProgram.adapter = programAdapter
        mBinding.rvHowToMeditate.adapter = howToMeditateAdapter

        if (mViewModel.suggestedData == null) {
            callFetchQuestionnaireResultAPI()
        } else {
            setSuggestedData()
        }
        animateMascot()

        LocalBroadcastManager.getInstance(this@QuestionnairesResultActivity).registerReceiver(
            mSubscriptionReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_SUBSCRIPTION_CHANGED)
        )

    }

    private val mSubscriptionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            callFetchQuestionnaireResultAPI()
            isUserSubscribed = true
        }
    }

    private fun callFetchQuestionnaireResultAPI() {
        mViewModel.fetchQuestionnaireResultAPI(this, mDisposable)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this@QuestionnairesResultActivity)
            .unregisterReceiver(mSubscriptionReceiver)
        super.onDestroy()
    }

    override fun observeApiCallbacks() {
        mViewModel.questionnairesResultResponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let {
                    mViewModel.suggestedData = it
                    setSuggestedData()
                    if (isUserSubscribed) {
                        MainActivity.startActivity(this)
                        finish()
                    }
                }
            }

            mViewModel.questionnairesResultResponse.postValue(null)
        }
    }

    private fun setSuggestedData() {
        mViewModel.suggestedData?.let {
            it.meditations?.list?.let { meditations ->
                if (meditations.isNotEmpty()) {
                    meditationAdapter.list.clear()
                    meditationAdapter.list.addAll(meditations)
                    meditationAdapter.notifyDataSetChanged()
                } else
                    mBinding.meditationView.gone()
            } ?: kotlin.run {
                mBinding.meditationView.gone()
            }

            it.programs?.list?.let { programs ->
                if (programs.isNotEmpty()) {
                    programAdapter.list.clear()
                    programAdapter.list.addAll(programs)
                    programAdapter.notifyDataSetChanged()
                } else
                    mBinding.programView.gone()
            } ?: kotlin.run {
                mBinding.programView.gone()
            }

            it.howToMeditate?.list?.let { meditations ->
                if (meditations.isNotEmpty()) {
                    howToMeditateAdapter.list.clear()
                    howToMeditateAdapter.list.addAll(meditations)
                    howToMeditateAdapter.notifyDataSetChanged()
                } else
                    mBinding.howToMeditateView.gone()
            } ?: kotlin.run {
                mBinding.howToMeditateView.gone()
            }
        }
    }

    fun animateMascot() {
        mBinding.ivMascot.setAnimation("mascot_levitating_opens_eyes_and_descends.json")
        mBinding.ivMascot.repeatCount = 0
        mBinding.ivMascot.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                mBinding.ivMascot.removeAllAnimatorListeners()
                mBinding.ivMascot.setAnimation("mascot_standing_idle_loop.json")
                mBinding.ivMascot.repeatCount = ValueAnimator.INFINITE
                mBinding.ivMascot.playAnimation()
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }

    override fun handleListener() {
        mBinding.apply {

            mBinding.btnProfile.clickWithDebounce {
                ProfileAccountInfoActivity.startActivity(this@QuestionnairesResultActivity)
            }

            mBinding.btnContinue.clickWithDebounce {
                MainActivity.startActivity(this@QuestionnairesResultActivity)
                finish()
            }
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

    override fun onMultiSessionClicked(dataModel: ProgramDataModel) {
//        if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == true && dataModel.isPurchased == false) {
//            if (dataModel.type == Constants.content) {
//                if (dataModel.contentType == Constants.TEXT) {
//                    ReadMeditateActivity.startActivity(
//                        this@QuestionnairesResultActivity, dataModel.id ?: ""
//                    )
//                } else {
//                    MeditationDetailActivity.startActivity(
//                        this@QuestionnairesResultActivity, dataModel.id ?: ""
//                    )
//                }
//            } else {
//                ProgramDetailsActivity.startActivity(
//                    this@QuestionnairesResultActivity,
//                    dataModel.id ?: "",
//                    false
//                )
//            }
//        } else if (dataModel.isLocked == true && dataModel.isSubscribed == false) {
//            if (dataModel.unlockDays != null) {
//                if (dataModel.unlockDays!! < 1)
//                    showUnlockImageDialog(this@QuestionnairesResultActivity)
//            } else if (dataModel.unlockDays == null) {
//                showUnlockImageDialog(this@QuestionnairesResultActivity)
//            }
//        } else if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == false) {
//            SubscriptionActivity.startActivityForResult(this@QuestionnairesResultActivity)
//        } else {
        if (dataModel.type == Constants.content) {
            if (dataModel.contentType == Constants.TEXT) {
                ReadMeditateActivity.startActivity(
                    this@QuestionnairesResultActivity, dataModel.id ?: ""
                )
            } else {
                MeditationDetailActivity.startActivity(
                    this@QuestionnairesResultActivity, dataModel.id ?: "",
                    isFromQuestionnaires = true
                )
            }
        } else {
            ProgramDetailsActivity.startActivity(
                this@QuestionnairesResultActivity,
                dataModel.id ?: "",
                false,
                isFromQuestionnaires = true
            )
        }
//        }
    }

    override fun onFavouriteClicked(dataModel: ProgramDataModel) {
        if (dataModel.type == Constants.content) {
            // call fav content api
            mViewModel.favoriteMeditationContent(
                dataModel.id ?: "",
                this@QuestionnairesResultActivity,
                mDisposable
            )
        } else {
            // call fav program api
            mViewModel.setProgramFavorite(
                dataModel.id!!,
                false,
                this@QuestionnairesResultActivity,
                mDisposable
            )
        }
    }

    override fun onMultiSessionClicked(dataModel: HowToMeditateDataModel) {
//        if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == true && dataModel.isPurchased == false) {
////                    GET
//            if (dataModel.type == Constants.TEXT) {
//                ReadMeditateActivity.startActivity(
//                    this@QuestionnairesResultActivity, dataModel.contentId ?: ""
//                )
//            } else {
//                MeditationDetailActivity.startActivity(
//                    this@QuestionnairesResultActivity, dataModel.contentId ?: ""
//                )
//            }
////                        SubscriptionActivity.startActivity(activity as AppCompatActivity)
//        } else if (dataModel.isLocked == true && dataModel.isSubscribed == false) {
////                    lock
//            if (dataModel.unlockDays != null) {
//                if (dataModel.unlockDays!! < 1)
//                    showUnlockImageDialog(this@QuestionnairesResultActivity)
//            } else if (dataModel.unlockDays == null) {
//                showUnlockImageDialog(this@QuestionnairesResultActivity)
//            }
//        } else if (dataModel.isLocked == false && dataModel.isSubscribed == false && dataModel.isPaidContent == false) {
////                        Subscribe
//            SubscriptionActivity.startActivityForResult(this@QuestionnairesResultActivity)
//        } else {
//                    can access
        if (dataModel.type == Constants.TEXT) {
            ReadMeditateActivity.startActivity(
                this@QuestionnairesResultActivity, dataModel.contentId ?: ""
            )
        } else {
            MeditationDetailActivity.startActivity(
                this@QuestionnairesResultActivity, dataModel.contentId ?: "",
                isFromQuestionnaires = true
            )
        }
//        }
    }

    override fun onBackPressed() {
        MainActivity.startActivity(this)
        finish()
    }
}