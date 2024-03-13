package com.app.taocalligraphy.ui.meditation

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityMeditationCurrentPainRateBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.eventbus.MeditationContentFavouriteListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.meditation.viewmodel.MeditationContentViewModel
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class MeditationCurrentPainRateActivity : BaseActivity<ActivityMeditationCurrentPainRateBinding>() {

    companion object {
        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
            meditationContentResponse: MeditationContentResponse?,
            isFromDownload: Boolean,
            isFromProgram: Boolean,
            programContentId: String,
            isFromQuestionnaires: Boolean = false
        ) {
            val intent = Intent(activity, MeditationCurrentPainRateActivity::class.java)
            intent.putExtra(Constants.MeditationContent, meditationContentResponse)
            intent.putExtra(Constants.Param.isFromDownload, isFromDownload)
            intent.putExtra(Constants.Param.isFromProgram, isFromProgram)
            intent.putExtra(Constants.Param.programContentId, programContentId)
            intent.putExtra(Constants.Param.isFromQuestionnaires, isFromQuestionnaires)
            activity.startActivity(intent)
            activity.overridePendingTransition(0, 0)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_meditation_current_pain_rate
    }

    val meditationContentResponse: MeditationContentResponse? by lazy {
        return@lazy intent.extras?.getParcelable(Constants.MeditationContent) as MeditationContentResponse?
    }

    private val isFromDownload by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromDownload, false)
    }
    private val isFromProgram by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromProgram, false)
    }
    private val programContentId by lazy {
        return@lazy intent.extras?.getString(Constants.Param.programContentId, "") ?: ""
    }

    private val isFromQuestionnaires by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromQuestionnaires, false) ?: false
    }

    private val mViewModel: MeditationContentViewModel by viewModels()
    var likeStatus: Boolean? = null

    override fun initView() {
        contentDownloadHelper.setView(this, mBinding.toolbar)
        setupToolbar()
        animatePurchaseItem()
        setData()

        when (mViewModel.currentPainRating) {
            1 -> setEmojiExtremePain()
            2 -> setEmojiHighPain()
            3 -> setEmojiNormalPain()
            4 -> setEmojiLowPain()
            5 -> setEmojiNoPain()
        }
    }

    private fun setData() {
        meditationContentResponse?.let {
            contentDownloadHelper.setMeditationContentData(it)
            mBinding.tvTitle.text = it.ratingDetails?.ratingQuestion ?: ""
            mBinding.txtFeelLow.text = it.ratingDetails?.ratingWorst ?: ""
            mBinding.txtFeelHappy.text = it.ratingDetails?.ratingBest ?: ""
            mBinding.ivMeditationImage.loadImage(
                it.backgroundImageMobile,
                R.drawable.img_default_for_content,
                true
            )
            setMeditationFavouriteStatus(
                mBinding.toolbar.ivFavToolbar,
                it.isFavorites,
                false
            )

            setLikeDislikeView()
        }

        val content = SpannableString(getString(R.string.skip_for_now))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        mBinding.btnSkip.text = content
    }

    private fun setupToolbar() {
        mBinding.toolbar.ivBackToolbar.visible()
        if (((meditationContentResponse?.subscription?.isAccessible
                ?: true) == true && meditationContentResponse?.isPaidContent == true && meditationContentResponse?.isPurchased == false) || (meditationContentResponse?.subscription?.isAccessible
                ?: true) == false
        ) {
            mBinding.toolbar.cardFav.gone()
            mBinding.toolbar.ivLikeToolbar.gone()
            mBinding.toolbar.ivDownloadToolbar.gone()
            mBinding.toolbar.ivShareToolbar.gone()
        } else {
            if (isNetwork()) {
                mBinding.toolbar.ivLikeToolbar.gone()
                mBinding.toolbar.ivShareToolbar.visible()
                if (UserHolder.EnumUserModulePermission.USE_DOWNLOAD_FUNCTION.permission?.canAccess
                        ?: true
                ) {
                    mBinding.toolbar.ivDownloadToolbar.visible()
                    mBinding.toolbar.ivDownloadToolbar.isEnabled = true
                    mBinding.toolbar.ivDownloadToolbar.isClickable = true
                    mBinding.toolbar.ivDownloadToolbar.alpha = 1.0f
                } else {
                    mBinding.toolbar.ivDownloadToolbar.visible()
                    mBinding.toolbar.ivDownloadToolbar.isEnabled = false
                    mBinding.toolbar.ivDownloadToolbar.isClickable = false
                    mBinding.toolbar.ivDownloadToolbar.alpha = 0.5f
                }
                if (UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess
                        ?: true
                ) {
                    mBinding.toolbar.cardFav.visible()
                    mBinding.toolbar.cardFav.isEnabled = true
                    mBinding.toolbar.cardFav.isClickable = true
                    mBinding.toolbar.ivFavToolbar.isEnabled = true
                    mBinding.toolbar.ivFavToolbar.isClickable = true
                    mBinding.toolbar.cardFav.alpha = 0.8f
                } else {
                    mBinding.toolbar.cardFav.visible()
                    mBinding.toolbar.cardFav.isEnabled = false
                    mBinding.toolbar.cardFav.isClickable = false
                    mBinding.toolbar.ivFavToolbar.isEnabled = false
                    mBinding.toolbar.ivFavToolbar.isClickable = false
                    mBinding.toolbar.cardFav.alpha = 0.5f
                }
            }
        }

        if (isFromQuestionnaires) {
            mBinding.toolbar.cardFav.gone()
            mBinding.toolbar.ivDownloadToolbar.gone()
        }
    }

    private fun setLikeDislikeView() {
        meditationContentResponse?.let {
            likeStatus = it.isLiked
            when (it.isLiked) {
                true -> {
                    mBinding.toolbar.ivLikeToolbar.setImageResource(R.drawable.ic_vd_like)
                }
                false -> {
                    mBinding.toolbar.ivLikeToolbar.setImageResource(R.drawable.ic_vd_dislike)
                }
                else -> {
                    mBinding.toolbar.ivLikeToolbar.setImageResource(R.drawable.ic_vd_dislike)
                }
            }
        }
    }

    private fun animatePurchaseItem() {
        /*mBinding.ivMascotPain.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.slide_in_down
            )
        )*/
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
        mViewModel.preAssessmentFeedbackLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { it ->
                    openStartMeditationScreen()
                }
                longToastState(requestState.error)
            }
        }

        mViewModel.favouriteMeditationContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { it ->
                    meditationContentResponse?.isFavorites =
                        meditationContentResponse?.isFavorites != true
                    setMeditationFavouriteStatus(
                        mBinding.toolbar.ivFavToolbar,
                        meditationContentResponse?.isFavorites,
                        true
                    )
                    EventBus.getDefault()
                        .post(
                            MeditationContentFavouriteListener(
                                meditationContentResponse?.id ?: "",
                                meditationContentResponse?.isFavorites ?: false
                            )
                        )
                }
                longToastState(requestState.error)
            }
        }

        mViewModel.likeDisLikeMeditationContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { it ->
                    meditationContentResponse?.isLiked = likeStatus
                    setLikeDislikeView()
                }
                longToastState(requestState.error)
            }
        }
    }

    override fun handleListener() {
        mBinding.toolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.toolbar.ivFavToolbar.clickWithDebounce {
            meditationContentResponse?.let {
                mViewModel.favoriteMeditationContent(
                    it.id,
                    this,
                    mDisposable
                )
            }
        }

        mBinding.ivExtremePain.clickWithDebounce {
            setEmojiExtremePain()
        }

        mBinding.ivHighPain.clickWithDebounce {
            setEmojiHighPain()
        }

        mBinding.ivNormalPain.clickWithDebounce {
            setEmojiNormalPain()
        }

        mBinding.ivLowPain.clickWithDebounce {
            setEmojiLowPain()
        }

        mBinding.ivNoPain.clickWithDebounce {
            setEmojiNoPain()
        }

        mBinding.btnSaveAndStart.clickWithDebounce {
            mViewModel.preAssessmentFeedback(
                meditationContentResponse?.id ?: "",
                mViewModel.currentPainRating,
                this,
                mDisposable
            )
        }

        mBinding.btnSkip.clickWithDebounce {
            mViewModel.preAssessmentFeedback(
                meditationContentResponse?.id ?: "",
                0,
                this,
                mDisposable
            )
        }

        mBinding.toolbar.ivShareToolbar.clickWithDebounce {
            shareMeditationContent(
                contentTitle = meditationContentResponse?.title.toString(),
                contentDescription = meditationContentResponse?.description.toString(),
                contentId = meditationContentResponse?.id.toString(),
                contentImage = meditationContentResponse?.backgroundImageMobile.toString(),
                url = URL_CONTENT
            )
        }

        mBinding.toolbar.ivLikeToolbar.clickWithDebounce {
            if (!isNetwork()) {
                return@clickWithDebounce
            }
            meditationContentResponse?.let {
                if (it.isLiked != true) {
                    likeStatus = true
                    mViewModel.likeDisLikeMeditationContent(
                        it.id,
                        Constants.like,
                        this,
                        mDisposable
                    )
                } else {
                    likeStatus = false
                    mViewModel.likeDisLikeMeditationContent(
                        it.id,
                        Constants.dislike,
                        this,
                        mDisposable
                    )
                }
            }
            meditationContentResponse?.isLiked = likeStatus
            setLikeDislikeView()
        }
    }

    private fun setEmojiExtremePain() {
        mViewModel.currentPainRating = 1
        setPainInView(mBinding.ivExtremePain, R.drawable.vd_pain_extreme_emoji_active)
        mBinding.lottieMascot.setAnimation("mascot_rating_01.json")
        mBinding.lottieMascot.playAnimation()
    }

    private fun setEmojiHighPain() {
        mViewModel.currentPainRating = 2
        setPainInView(mBinding.ivHighPain, R.drawable.vd_pain_high_emoji_active)
        mBinding.lottieMascot.setAnimation("mascot_rating_02.json")
        mBinding.lottieMascot.playAnimation()
    }

    private fun setEmojiNormalPain() {
        mViewModel.currentPainRating = 3
        setPainInView(mBinding.ivNormalPain, R.drawable.vd_pain_normal_emoji_active)
        mBinding.lottieMascot.setAnimation("mascot_rating_03.json")
        mBinding.lottieMascot.playAnimation()
    }

    private fun setEmojiLowPain() {
        mViewModel.currentPainRating = 4
        setPainInView(mBinding.ivLowPain, R.drawable.vd_pain_low_emoji_active)
        mBinding.lottieMascot.setAnimation("mascot_rating_04.json")
        mBinding.lottieMascot.playAnimation()
    }

    private fun setEmojiNoPain() {
        mViewModel.currentPainRating = 5
        setPainInView(mBinding.ivNoPain, R.drawable.vd_no_pain_emoji_active)
        mBinding.lottieMascot.setAnimation("mascot_rating_05.json")
        mBinding.lottieMascot.playAnimation()
    }

    private fun openStartMeditationScreen() {
        if (isTablet(this)) {
            StartPlayerTabletActivity.startActivity(
                this,
                meditationContentResponse = meditationContentResponse,
                isFromDownload = isFromDownload ?: false,
                isFromProgram = isFromProgram ?: false,
                programContentId = programContentId,
                isFromQuestionnaires = isFromQuestionnaires
            )
        } else {
            StartPlayerActivity.startActivity(
                this,
                meditationContentResponse = meditationContentResponse,
                isFromDownload = isFromDownload ?: false,
                isFromProgram = isFromProgram ?: false,
                programContentId = programContentId,
                isFromQuestionnaires = isFromQuestionnaires
            )
        }
        finish()
    }

    private fun setPainInView(activeImageView: AppCompatImageView, activeImage: Int) {
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

        mBinding.btnSaveAndStart.alpha = 1.0f
        mBinding.viewSave.alpha = 1.0f
        mBinding.btnSaveAndStart.isEnabled = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun favoriteUpdateEventBus(meditationContentFavouriteListener: MeditationContentFavouriteListener) {
        meditationContentResponse?.let {
            if (it.id == meditationContentFavouriteListener.id) {
                if (meditationContentResponse?.isFavorites != meditationContentFavouriteListener.isFavourite) {
                    meditationContentResponse?.isFavorites =
                        meditationContentFavouriteListener.isFavourite
                    setMeditationFavouriteStatus(
                        mBinding.toolbar.ivFavToolbar,
                        meditationContentResponse?.isFavorites,
                        false
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        contentDownloadHelper.removeHandler()
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