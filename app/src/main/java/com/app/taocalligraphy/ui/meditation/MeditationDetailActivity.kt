package com.app.taocalligraphy.ui.meditation

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityMeditationDetailBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.eventbus.MeditationContentDownloadListener
import com.app.taocalligraphy.models.eventbus.MeditationContentFavouriteListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.meditation.adapter.ContentCategoryAdapter
import com.app.taocalligraphy.ui.meditation.adapter.ContentReviewAdapter
import com.app.taocalligraphy.ui.meditation.dialog.PaidSessionTwoFieldDialog
import com.app.taocalligraphy.ui.meditation.viewmodel.MeditationContentViewModel
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImage
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@AndroidEntryPoint
class MeditationDetailActivity : BaseActivity<ActivityMeditationDetailBinding>(),
    PaidSessionTwoFieldDialog.OnPayHeartListener,
    PaidSessionTwoFieldDialog.OnPayAmountListener {

    companion object {
        var instance: MeditationDetailActivity? = null

        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
            contentId: String = "1",
            isFromDownload: Boolean = false,
            isFromProgram: Boolean = false,
            programContentId: String = "",
            isFromMeditate: Boolean = false,
            isFromQuestionnaires: Boolean = false
        ) {
            val intent = Intent(activity, MeditationDetailActivity::class.java)
            intent.putExtra(Constants.Param.contentId, contentId)
            intent.putExtra(Constants.Param.isFromDownload, isFromDownload)
            intent.putExtra(Constants.Param.isFromProgram, isFromProgram)
            intent.putExtra(Constants.Param.programContentId, programContentId)
            intent.putExtra(Constants.Param.isFromMeditate, isFromMeditate)
            intent.putExtra(Constants.Param.isFromQuestionnaires, isFromQuestionnaires)
            activity.startActivityWithAnimation(intent)
        }
    }

//    val startSubscriptionActivityForResult =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                if (result.data?.getBooleanExtra("isSubscribed", false) == true) {
//                    onRefresh()
//                }
//            }
//        }

    override fun getResource(): Int {
        return R.layout.activity_meditation_detail
    }

    private val contentId by lazy {
        return@lazy intent.extras?.getString(Constants.Param.contentId, "") ?: ""
    }
    private val programContentId by lazy {
        return@lazy intent.extras?.getString(Constants.Param.programContentId, "") ?: ""
    }

    private val contentReviewAdapter by lazy {
        return@lazy ContentReviewAdapter()
    }

    private val isFromDownload by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromDownload, false)
    }
    private val isFromProgram by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromProgram, false)
    }
    private val isFromMeditate by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromMeditate, false) ?: false
    }

    private val isFromQuestionnaires by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromQuestionnaires, false) ?: false
    }

    private val mViewModel: MeditationContentViewModel by viewModels()

    private val contentCategoryAdapter: ContentCategoryAdapter by lazy {
        ContentCategoryAdapter()
    }

    var likeStatus: Boolean? = null

    override fun initView() {
        instance = this
        contentDownloadHelper.setView(this, mBinding.toolbar)
        mBinding.rvReview.adapter = contentReviewAdapter
        mBinding.tvMeditationTitle.isSelected = true
        mBinding.rvCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvCategory.adapter = contentCategoryAdapter
        LocalBroadcastManager.getInstance(this@MeditationDetailActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )

        if (isNetwork()) {
            if (mViewModel.meditationContent == null)
                callMeditationDetailAPI()
            else
                setData(mViewModel.meditationContent)
        } else {
            setData(getMeditationData())
        }

        when(mViewModel.selectedTab) {
            Constants.about -> {
                setTabWiseController(
                    mBinding.tvAbout,
                    mBinding.tvReviews,
                    mBinding.llAbout,
                    mBinding.llReviews
                )
            }
            Constants.review -> {
                setReviewData()
            }
        }
    }

    fun onRefresh() {
        if (isNetwork()) {
            callMeditationDetailAPI()
        } else {
            setData(getMeditationData())
        }
    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            callMeditationDetailAPI()
        }
    }

    private fun callMeditationDetailAPI() {
        mViewModel.getMeditationContent(contentId, this, mDisposable)
    }

    private fun setupToolbar(meditationContent: MeditationContentResponse?) {
        mBinding.toolbar.ivBackToolbar.visible()

//        mBinding.toolbar.cardFav.visible()
        if (((meditationContent?.subscription?.isAccessible
                ?: true) == true && meditationContent?.isPaidContent == true && meditationContent?.isPurchased == false) || (meditationContent?.subscription?.isAccessible
                ?: true) == false
        ) {
            mBinding.toolbar.cardFav.gone()
            mBinding.toolbar.ivLikeToolbar.gone()
            mBinding.toolbar.ivDownloadToolbar.gone()
            mBinding.toolbar.ivShareToolbar.gone()
        } else {
            if (isNetwork()) {
                mBinding.toolbar.ivShareToolbar.visible()
                if ((UserHolder.EnumUserModulePermission.USE_DOWNLOAD_FUNCTION.permission?.canAccess
                        ?: true)
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
                    mBinding.toolbar.cardFav.alpha = 0.8f
                } else {
                    mBinding.toolbar.cardFav.visible()
                    mBinding.toolbar.cardFav.isEnabled = false
                    mBinding.toolbar.cardFav.isClickable = false
                    mBinding.toolbar.cardFav.alpha = 0.5f
                }
            }
        }

        if (isFromMeditate) {
            mBinding.toolbar.cardFav.gone()
            mBinding.toolbar.ivLikeToolbar.gone()
            mBinding.toolbar.ivShareToolbar.visible()
            mBinding.llTabs.gone()
            mBinding.llAboutData.gone()
        } else {
            mBinding.llTabs.visible()
            mBinding.llAboutData.visible()
        }

        if (isFromQuestionnaires) {
            mBinding.toolbar.cardFav.gone()
            mBinding.toolbar.ivDownloadToolbar.gone()
        }
    }

    override fun observeApiCallbacks() {
        mViewModel.meditationContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { it ->
                    mViewModel.meditationContent = it
                    setData(it)
                }
                longToastState(requestState.error)
            }
            mViewModel.meditationContentLiveData.postValue(null)
        }

        mViewModel.favouriteMeditationContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { it ->
                    mViewModel.meditationContent?.isFavorites =
                        mViewModel.meditationContent?.isFavorites != true
                    setMeditationFavouriteStatus(
                        mBinding.toolbar.ivFavToolbar,
                        mViewModel.meditationContent?.isFavorites,
                        true
                    )
                    if (mViewModel.meditationContent?.isFavorites == true) {
                        mViewModel.meditationContent?.favouritesCount =
                            ((mViewModel.meditationContent?.favouritesCount?.toInt()
                                ?: 0) + 1).toString()
                    } else {
                        mViewModel.meditationContent?.favouritesCount =
                            ((mViewModel.meditationContent?.favouritesCount?.toInt()
                                ?: 0) - 1).toString()
                    }
                    mBinding.tvFavouriteCount.text = mViewModel.meditationContent?.favouritesCount
                }
                longToastState(requestState.error)
            }
            mViewModel.favouriteMeditationContentLiveData.postValue(null)
        }

        mViewModel.likeDisLikeMeditationContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let { it ->
                    mViewModel.meditationContent?.isLiked = likeStatus
                    setLikeDislikeView()
                }
                longToastState(requestState.error)
            }
            mViewModel.likeDisLikeMeditationContentLiveData.postValue(null)
        }
    }

    private fun setLikeDislikeView() {
        mViewModel.meditationContent?.let {
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

    override fun handleListener() {
        mBinding.toolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.toolbar.cardFav.clickWithDebounce {
            if (isNetwork()) {
                mViewModel.meditationContent?.let {
                    mViewModel.favoriteMeditationContent(
                        it.id ?: "",
                        this,
                        mDisposable
                    )
                }
            } else
                longToast(
                    getString(R.string.no_internet, getString(R.string.to_get_fav_meditaion)),
                    Constants.ERROR
                )
        }

        mBinding.toolbar.ivShareToolbar.clickWithDebounce {
            val urlPath = if (isFromMeditate) {
                URL_WATCH_MEDITATION
            } else {
                URL_CONTENT
            }

            shareMeditationContent(
                contentTitle = mViewModel.meditationContent?.title.toString(),
                contentDescription = mViewModel.meditationContent?.description.toString(),
                contentId = mViewModel.meditationContent?.id.toString(),
                contentImage = mViewModel.meditationContent?.backgroundImageMobile.toString(),
                url = urlPath
            )
        }

        mBinding.tvAbout.clickWithDebounce {
            mViewModel.selectedTab = Constants.about
            setTabWiseController(
                mBinding.tvAbout,
                mBinding.tvReviews,
                mBinding.llAbout,
                mBinding.llReviews
            )
        }

        mBinding.tvReviews.clickWithDebounce {
            mViewModel.selectedTab = Constants.review
            setReviewData()
        }

        mBinding.btnGetMeditation.clickWithDebounce {
            mViewModel.meditationContent?.let {
//                value?.isSubscribed || (!value?.isSubscribed && value?.isPaidContent)
//                if(it.isPaidContent)

                if (isNetwork()) {
                    if ((it?.subscription?.isAccessible
                            ?: true) == true && it?.isPaidContent == true && it?.isPurchased == false
                    ) {
//                    GET
                        val dialog =
                            PaidSessionTwoFieldDialog(
                                this,
                                it.isUnlockWithHearts!!,
                                if (mViewModel.meditationContent?.currencies!!.isNotEmpty()) mViewModel.meditationContent?.currencies?.get(
                                    0
                                ) else null,
                                mViewModel.meditationContent?.heartDetails?.requiredDiamondHearts
                                    ?: 0,
                                this, this
                            )
                        dialog.show()
                    } else if ((it?.subscription?.isAccessible ?: true) == false) {
//                        Subscribe
                        SubscriptionActivity.startActivityForResult(
                            this@MeditationDetailActivity
                        )
                    } else {
//                    can access
                        openRatingOrContentScreen()
                    }
                } else {
                    if (isNetwork()) {
                        if (isTablet(this)) {
                            StartPlayerTabletActivity.startActivity(
                                this,
                                meditationContentResponse = mViewModel.meditationContent,
                                isFromDownload = isFromDownload ?: false,
                                isFromProgram = isFromProgram ?: false,
                                programContentId = programContentId,
                                isFromMeditate = isFromMeditate,
                                isFromQuestionnaires = isFromQuestionnaires
                            )
                        } else {
                            StartPlayerActivity.startActivity(
                                this,
                                meditationContentResponse = mViewModel.meditationContent,
                                isFromDownload = isFromDownload ?: false,
                                isFromProgram = isFromProgram ?: false,
                                programContentId = programContentId,
                                isFromQuestionnaires = isFromQuestionnaires
                            )
                        }
                    }
                }
            }
        }

        mBinding.toolbar.ivLikeToolbar.clickWithDebounce {
            if (!isNetwork()) {
                return@clickWithDebounce
            }
            mViewModel.meditationContent?.let {
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
            mViewModel.meditationContent?.isLiked = likeStatus
            setLikeDislikeView()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(meditationContentResponse: MeditationContentResponse?) {
        meditationContentResponse?.let { meditationContent ->
            setLikeDislikeView()

            contentDownloadHelper.setMeditationContentData(meditationContent)
            setupToolbar(meditationContent)
            saveMeditationData()

            setMeditationFavouriteStatus(
                mBinding.toolbar.ivFavToolbar,
                meditationContent.isFavorites,
                false
            )
            mBinding.ivMeditationImage.loadImage(
                meditationContent.backgroundImageMobile,
                R.drawable.img_default_for_content,
                true
            )
            mBinding.tvMeditationTitle.text = meditationContent.title
            if (meditationContent.authorName.isNotEmpty()) {
                mBinding.tvCaption.text =
                    getString(R.string.with) + " " + meditationContent.authorName
            } else {
                mBinding.tvCaption.text = meditationContent.authorName
            }
            mBinding.tvRatingCount.text =
                meditationContent.ratingsCount + " " + getString(R.string.felt_better)
            mBinding.tvLikesCount.text = meditationContent.likesCount
            mBinding.tvFavouriteCount.text = meditationContent.favouritesCount
            mBinding.tvViewCount.text = meditationContent.viewCounts
//            loadHtml(mBinding.tvDescription, meditationContent.description)
            setWebView(meditationContent)
            contentReviewAdapter.reviewList =
                meditationContent.reviewsList as ArrayList<MeditationContentResponse.Reviews>
            contentCategoryAdapter.setCategoryListData(meditationContent.tags ?: arrayListOf())
//            println(getTimeStampFromTime(meditationContent.contentDuration))
            setPurchaseButtonBasedOnPayment()
        }
    }

    private fun setWebView(meditationContent: MeditationContentResponse) {
        if (isTablet(this)) {
            meditationContent.description?.let { mBinding.webView.setPropertiesAndData(it.appendHtmlForTabletTextSize22()) }
        } else {
            meditationContent.description?.let { mBinding.webView.setPropertiesAndData(it.appendHtml()) }
        }

    }

    private fun getMeditationData(): MeditationContentResponse? {
        val data = mViewModel.getMeditationContent(contentId)
        data?.let {
            return data
        }

        return null
    }

    private fun saveMeditationData() {
        if (isNetwork()) {
            val storedData = getMeditationData()
            storedData?.let { data ->
                val dataFile: String? = if (!data.contentFileForDownload.isNullOrEmpty()) {
                    data.contentFileForDownload
                } else {
                    data.contentFile
                }
                mViewModel.meditationContent?.let { newMeditationData ->
                    val contentFile: String? =
                        if (!newMeditationData.contentFileForDownload.isNullOrEmpty()) {
                            newMeditationData.contentFileForDownload
                        } else {
                            newMeditationData.contentFile
                        }
                    if (contentDownloadHelper.isContentDownloaded(contentFile) == false) {
                        TaoCalligraphy.instance.getDownloadTracker()
                            ?.removeDownload(contentDownloadHelper.linkUri(dataFile))
                        val renderersFactory = TaoCalligraphy.instance
                            .buildRenderersFactory()
                        TaoCalligraphy.instance.getDownloadTracker()
                            ?.downloadFile(
                                newMeditationData.title,
                                contentDownloadHelper.linkUri(contentFile),
                                "",
                                renderersFactory,
                                mViewModel
                            )
                    }

                    newMeditationData.subtitleWithLanguages?.forEach { subtitle ->
                        if (contentDownloadHelper.isContentDownloaded(subtitle.subTitleFile) == false) {
                            data.subtitleWithLanguages?.find { subtitleData -> subtitleData.languageId == subtitle.languageId }
                                ?.let { storedSubtitle ->
                                    TaoCalligraphy.instance.getDownloadTracker()
                                        ?.removeDownload(
                                            contentDownloadHelper.linkUri(
                                                storedSubtitle.subTitleFile
                                            )
                                        )
                                }
                            val renderersFactory = TaoCalligraphy.instance
                                .buildRenderersFactory()
                            TaoCalligraphy.instance.getDownloadTracker()
                                ?.downloadFile(
                                    subtitle.languageName,
                                    contentDownloadHelper.linkUri(subtitle.subTitleFile),
                                    "",
                                    renderersFactory,
                                    mViewModel
                                )
                        }
                    }

                    mViewModel.addMeditationToStorage(newMeditationData)
                }
            }
        } else if (isNetwork()) {
            longToast(
                getString(R.string.no_internet, getString(R.string.to_svae_meditation)),
                Constants.ERROR
            )
        }
    }

    private fun setPurchaseButtonBasedOnPayment() {
        mViewModel.meditationContent?.let {
            if ((it?.subscription?.isAccessible
                    ?: true) == true && it?.isPaidContent == true && it?.isPurchased == false
            ) {
//                    GET
                mBinding.btnGetMeditation.text = getString(R.string.get)
                mBinding.btnGetMeditation.icon = getDrawable(R.drawable.vd_arrow_up_white)
                mBinding.btnGetMeditation.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END
                mBinding.btnGetMeditation.iconTint = ColorStateList.valueOf(getColor(R.color.white))

            } else if ((it?.subscription?.isAccessible ?: true) == false) {
//                        Subscribe
                mBinding.btnGetMeditation.text = getString(R.string.subscribe)
                mBinding.btnGetMeditation.icon = null
                mBinding.btnGetMeditation.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END
                mBinding.btnGetMeditation.iconTint =
                    ColorStateList.valueOf(getColor(R.color.transparent))


            } else {
//                    can access
                if (it.type == Constants.VIDEO) {
                    mBinding.btnGetMeditation.text = getString(R.string.watch_now)
                } else {
                    mBinding.btnGetMeditation.text = getString(R.string.listen_now)
                }
                mBinding.btnGetMeditation.icon = null
            }
            mBinding.btnGetMeditation.visible()
        }

    }

    private fun openMeditationStartRatingScreen() {
        MeditationCurrentPainRateActivity.startActivity(
            this,
            mViewModel.meditationContent,
            isFromDownload ?: false,
            isFromProgram ?: false,
            programContentId ?: "",
            isFromQuestionnaires = isFromQuestionnaires
        )
    }

    private fun setTabWiseController(
        activeTextView: AppCompatTextView,
        disableTextview: AppCompatTextView,
        activeLayout: LinearLayout,
        disableLayout: LinearLayout
    ) {
        activeTextView.setBackgroundResource(R.drawable.bg_white_gold_border_22dp)
        disableTextview.setBackgroundResource(R.drawable.bg_gray_95_radius_22dp)
        activeTextView.setTextColor(ContextCompat.getColor(this, R.color.gold))
        disableTextview.setTextColor(ContextCompat.getColor(this, R.color.medium_grey))

        activeLayout.isVisible = true
        disableLayout.isGone = true
    }

    override fun onPayAmountClicked() {
        setMeditationContentPaid()
//        openRatingOrContentScreen()
    }

    override fun onPayHeartClicked() {
        setMeditationContentPaid()
//        openRatingOrContentScreen()
    }

    private fun setMeditationContentPaid() {
        mViewModel.meditationContent?.isPaidContent = false
        setData(mViewModel.meditationContent)
    }

    private fun openRatingOrContentScreen() {
        mViewModel.meditationContent?.let {
            if (it.isInstructional == true) {
                if (isTablet(this)) {
                    StartPlayerTabletActivity.startActivity(
                        this,
                        meditationContentResponse = mViewModel.meditationContent,
                        isFromDownload = isFromDownload ?: false,
                        isFromProgram = isFromProgram ?: false,
                        programContentId = programContentId,
                        isFromMeditate = isFromMeditate,
                        isFromQuestionnaires = isFromQuestionnaires
                    )
                } else {
                    StartPlayerActivity.startActivity(
                        this,
                        meditationContentResponse = mViewModel.meditationContent,
                        isFromDownload = isFromDownload ?: false,
                        isFromProgram = isFromProgram ?: false,
                        programContentId = programContentId,
                        isFromMeditate = isFromMeditate,
                        isFromQuestionnaires = isFromQuestionnaires
                    )
                }
            } else
                if (it.isShowRating == true && (it.isAssessmentDone == false || it.isPostAssessmentCompleted == false)) {
                    openMeditationStartRatingScreen()
                } else {
                    if (isTablet(this)) {
                        StartPlayerTabletActivity.startActivity(
                            this,
                            meditationContentResponse = mViewModel.meditationContent,
                            isFromDownload = isFromDownload ?: false,
                            isFromProgram = isFromProgram ?: false,
                            programContentId = programContentId,
                            isFromMeditate = isFromMeditate,
                            isFromQuestionnaires = isFromQuestionnaires
                        )
                    } else {
                        StartPlayerActivity.startActivity(
                            this,
                            meditationContentResponse = mViewModel.meditationContent,
                            isFromDownload = isFromDownload ?: false,
                            isFromProgram = isFromProgram ?: false,
                            programContentId = programContentId,
                            isFromMeditate = isFromMeditate,
                            isFromQuestionnaires = isFromQuestionnaires
                        )
                    }
                }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun favoriteUpdateEventBus(meditationContentFavouriteListener: MeditationContentFavouriteListener) {
        mViewModel.meditationContent?.let {
            if (it.id == meditationContentFavouriteListener.id) {
                if (mViewModel.meditationContent?.isFavorites != meditationContentFavouriteListener.isFavourite) {
                    mViewModel.meditationContent?.isFavorites =
                        meditationContentFavouriteListener.isFavourite
                    setMeditationFavouriteStatus(
                        mBinding.toolbar.ivFavToolbar,
                        mViewModel.meditationContent?.isFavorites,
                        false
                    )
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun downloadUpdateEventBus(meditationContentDownloadListener: MeditationContentDownloadListener) {
        contentDownloadHelper.updateDownloadFromEventBus(meditationContentDownloadListener.downloadStatus)
    }

    override fun onDestroy() {
        super.onDestroy()
        contentDownloadHelper.removeHandler()
        instance = null
        LocalBroadcastManager.getInstance(this@MeditationDetailActivity)
            .unregisterReceiver(mAccessLevelReceiver)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
        if (model.isAvailable) {
            initView()
            noInternetConnectionDialog?.dismiss()
        } else {
            if (!isFinishing && getMeditationData() == null) {
                showNoInternetDialog()
            }
        }
    }

    private fun setReviewData() {
        setTabWiseController(
            mBinding.tvReviews,
            mBinding.tvAbout,
            mBinding.llReviews,
            mBinding.llAbout
        )

        mViewModel.meditationContent?.reviewsList?.let {
            if (it.isEmpty()) {
                mBinding.noReviews.visible()
                mBinding.rvReview.gone()
            } else {
                mBinding.noReviews.gone()
                mBinding.rvReview.visible()
            }
        } ?: kotlin.run {
            mBinding.noReviews.visible()
            mBinding.rvReview.gone()
        }
    }

}