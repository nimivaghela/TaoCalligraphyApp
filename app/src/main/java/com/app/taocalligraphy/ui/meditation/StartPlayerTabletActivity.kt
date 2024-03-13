package com.app.taocalligraphy.ui.meditation

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.util.Size
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityStartPlayerBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.eventbus.MeditationContentFavouriteListener
import com.app.taocalligraphy.models.eventbus.PlaylistContentChangeListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.notification.ExoNotificationService
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_FAST_FORWARD
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_NEXT
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_PAUSE
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_PLAY
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_PREVIOUS
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_REWIND
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_STOP
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.PLAYER_FROM_CONTENT
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.PLAYER_FROM_PLAYLIST
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.playbackSpeed
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.meditation.adapter.MeditationVideoPlaylistAdapter
import com.app.taocalligraphy.ui.meditation.dialog.ChooseSubTitleDialog
import com.app.taocalligraphy.ui.meditation.viewmodel.MeditationContentViewModel
import com.app.taocalligraphy.ui.playlist.adapter.PlaylistViewItemAdapter
import com.app.taocalligraphy.ui.playlist.viewmodel.PlaylistViewModel
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImage
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*
import okhttp3.internal.userAgent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class StartPlayerTabletActivity : BaseActivity<ActivityStartPlayerBinding>(),
    MeditationVideoPlaylistAdapter.VideoPlaylistItemClickListener {

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        myIntent = intent
        if (myIntent?.hasExtra(Constants.FROM) == true && myIntent?.getStringExtra(Constants.FROM)
                .equals(Constants.FROM_PLAYER_NOTIFICATION)
        ) {
            showProgressIndicator(mBinding.llProgress, false)
            logError(msg = "From Player Notification")
            //loadBundle()
            //setData()
        } else {
            logError(msg = "Not From Player Notification")
            LocalBroadcastManager.getInstance(this).unregisterReceiver(playerNotificationReceiver)
            initView()
        }
    }

    companion object {
        lateinit var instance: StartPlayerTabletActivity

        @JvmStatic
        fun startActivity(
            activity: Context,
            videoType: String = Constants.meditationVideo,
            meditationContentResponse: MeditationContentResponse? = null,
            subtitleWithLanguage: MeditationContentResponse.SubtitleWithLanguage? = null,
            contentId: String? = null,
            programId: String? = null,
            isInitialUser: Boolean = false,
            isSubtitleVisible: Boolean = false,
            isFromNotification: Boolean = false,
            isFromDownload: Boolean = false,
            isFromProgram: Boolean = false,
            programContentId: String = "",
            isFromMeditate: Boolean = false,
            videoCurrentPosition: Long = 0,
            isFromQuestionnaires: Boolean = false
        ) {
            val intent = Intent(activity, StartPlayerTabletActivity::class.java)
            intent.putExtra(Constants.videoType, videoType)
//            intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(Constants.MeditationContent, meditationContentResponse)
            intent.putExtra(Constants.Param.contentId, contentId)
            intent.putExtra(Constants.Param.programId, programId)
            intent.putExtra(Constants.Param.isInitialUser, isInitialUser)
            intent.putExtra(Constants.Param.isSubtitleVisible, isSubtitleVisible)
            intent.putExtra(Constants.subtitle, subtitleWithLanguage)
            intent.putExtra(Constants.Param.videoPosition, videoCurrentPosition)
            intent.putExtra(Constants.Param.isFromNotification, isFromNotification)
            intent.putExtra(Constants.Param.isFromDownload, isFromDownload)
            intent.putExtra(Constants.Param.isFromProgram, isFromProgram)
            intent.putExtra(Constants.Param.programContentId, programContentId)
            intent.putExtra(Constants.Param.isFromMeditate, isFromMeditate)
            intent.putExtra(Constants.Param.isFromQuestionnaires, isFromQuestionnaires)
            activity.startActivity(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_start_player
    }

    private val meditationVideoPlaylistAdapter by lazy {
        return@lazy MeditationVideoPlaylistAdapter(this)
    }
    private val playlistViewItemAdapter by lazy {
        return@lazy PlaylistViewItemAdapter(mutableListOf())
    }
    lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    val mViewModel: MeditationContentViewModel by viewModels()
    private val mPlaylistViewModel: PlaylistViewModel by viewModels()

    var handlerMainTimer = Handler(Looper.getMainLooper())
    var subtitleWithLanguage: MeditationContentResponse.SubtitleWithLanguage? = null
    private var settingPopupWindow: PopupWindow? = null
    private var contentImageBitmap: Bitmap? = null
    var likeStatus: Boolean? = null
    var isSimpleExoplayerListenerSet = false


    private var zenModeDisplayed = false
    private var isPreparing = false
    var notificationServiceIntent: Intent? = null
    var playerNotificationService: ExoNotificationService? = null
    var mIsBound = false
    var isFromQuestionnaires = false

    private lateinit var orientationEventListener: OrientationEventListener

    private var fullScreenMediaPlayerResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        mViewModel.isMoveToFullScreen = false

        showProgressIndicator(mBinding.llProgress, false)
        mBinding.playerView.player = null
        mBinding.playerView.player = TaoCalligraphy.instance.simpleExoPlayer
        TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady =
            TaoCalligraphy.instance.isPlayerPlaying

        setSubtitleView()
        mViewModel.isFromFullScreen = true
    }

    var myIntent: Intent? = null
    var videoType = ""
    var contentId: String? = null
    var programContentId: String? = null
    var programId: String? = null
    var isFromNotification = false
    private var isInitialUser = false
    private var isSubtitleVisible = false
    private var videoCurrentPosition = 0L
    private var isFromProgram = false
    private var isFromMeditate = false
    private var alarmRequestCode = 0

    private var previousOrientation: Int = 0

    private fun getMeditationDataFromIntent(): MeditationContentResponse? {
        return myIntent?.extras?.getParcelable(Constants.MeditationContent) as MeditationContentResponse?
    }

    private fun getSubtitleDataFromIntent(): MeditationContentResponse.SubtitleWithLanguage? {
        return myIntent?.extras?.getParcelable(Constants.subtitle) as MeditationContentResponse.SubtitleWithLanguage?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        myIntent = intent
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        loadBundle()
        if (!mViewModel.isConfigChange) {
            TaoCalligraphy.instance.isPlayerPlaying = true
            mViewModel.meditationContent = getMeditationDataFromIntent()
        }
        mBinding.tvTitle.isSelected = true
        mBinding.tvBottomTitle.isSelected = true
        NotificationManagerCompat.from(this).cancel(1)
        if (isFromNotification) {
            TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady = false
            if (alarmRequestCode == 8) {
                userHolder.alarmResponse = ""
                mViewModel.alarmOnOff(this, mDisposable)
            }
        }

        instance = this
        contentDownloadHelper.setView(this, mBinding.toolbar)
        if (!isInitialUser && !mViewModel.isConfigChange) {
            TaoCalligraphy.getAppContext().initializeExoPlayer()
        }
        setExoplayerListener()
        setupToolbar()

        if (!isNetwork() && getMeditationData() == null) {
            showNoInternetDialog()
        } else {
            if (getMeditationData() != null) mViewModel.meditationContent = getMeditationData()
        }

        mBinding.sliderVideoProgress.isEnabled = false
        mBinding.sliderBottomVideoProgress.isEnabled = false
        mBinding.rvMeditationVideoPlaylist.adapter = meditationVideoPlaylistAdapter
        setVideoCornerBackground()

        if (videoType == Constants.howToMeditateVideo) {
            mBinding.rvMeditationVideoPlaylist.gone()
            mBinding.toolbar.ivShareToolbar.visible()
            mBinding.toolbar.cardFav.gone()
            mBinding.toolbar.ivLikeToolbar.gone()
            mBinding.toolbar.ivDownloadToolbar.visible()
        }

        bottomSheetBehavior = BottomSheetBehavior.from<LinearLayout>(mBinding.llUpComingPlaylist)

        if (videoType == Constants.playlistVideo) {
            mBinding.llUpComingPlaylist.visibility = View.VISIBLE
            val orientation = this.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                val layoutParams =
                    mBinding.rrPlayerController.layoutParams as LinearLayout.LayoutParams
                layoutParams.setMargins(
                    0,
                    0,
                    0,
                    resources.getDimension(R.dimen._100sdp).toInt()
                )
                mBinding.rrPlayerController.layoutParams = layoutParams
            } else {
                val layoutParams =
                    mBinding.rrPlayerController.layoutParams as LinearLayout.LayoutParams
                layoutParams.setMargins(0, 0, 0, resources.getDimension(R.dimen._50sdp).toInt())
                mBinding.rrPlayerController.layoutParams = layoutParams
            }

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            mBinding.rvUpNext.adapter = playlistViewItemAdapter

            var viewHeight = -1
            var maxHeightSet = false

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (!maxHeightSet) {
                        maxHeightSet = true
                        bottomSheetBehavior.maxHeight = mBinding.llMeditationData.height
                    }
                    if (viewHeight == -1) {
                        viewHeight = mBinding.llUpNextItem.height
                    }
                    mBinding.llUpNextItem.updateLayoutParams {
                        height = (viewHeight * (1 - slideOffset)).toInt()
                    }
                    mBinding.llUpNextItem.alpha = 1 - slideOffset
                    mBinding.llMeditationData.alpha = 1 - slideOffset
                    mBinding.llNextPlaylist.alpha = slideOffset
                }
            })
        } else {
            val orientation = this.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                val layoutParams =
                    mBinding.rrPlayerController.layoutParams as LinearLayout.LayoutParams
                layoutParams.setMargins(0, 0, 0, resources.getDimension(R.dimen._10sdp).toInt())
                mBinding.rrPlayerController.layoutParams = layoutParams
            } else {
                val layoutParams =
                    mBinding.rrPlayerController.layoutParams as LinearLayout.LayoutParams
                layoutParams.setMargins(0, 0, 0, resources.getDimension(R.dimen._10sdp).toInt())
                mBinding.rrPlayerController.layoutParams = layoutParams
            }
        }

        if (contentId != null && programId == null && isNetwork()) {
            callMeditationDetailAPI(contentId)
        } else if (programId != null && isNetwork() && (!mViewModel.isConfigChange || mViewModel.playlistContentList.isEmpty())) {
            if (mViewModel.playlistContentList.isEmpty()) {
                mViewModel.isConfigChange = false
                callPlaylistListAPI()
            } else {
                setNextContentDataAndCallAPI()
            }
        } else {
            setData(true)
        }

        setOrientationListener()

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(playerNotificationReceiver, IntentFilter(ACTION_CUSTOM_PREVIOUS))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(playerNotificationReceiver, IntentFilter(ACTION_CUSTOM_REWIND))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(playerNotificationReceiver, IntentFilter(ACTION_CUSTOM_PLAY))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(playerNotificationReceiver, IntentFilter(ACTION_CUSTOM_PAUSE))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(playerNotificationReceiver, IntentFilter(ACTION_CUSTOM_FAST_FORWARD))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(playerNotificationReceiver, IntentFilter(ACTION_CUSTOM_NEXT))
        LocalBroadcastManager.getInstance(this@StartPlayerTabletActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )

    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            setupToolbar()
        }
    }

    val playerNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            logError(msg = "playerNotificationReceiver ${intent?.action}")
            when (intent?.action) {
                ACTION_CUSTOM_PLAY -> {
                    mBinding.ivMediaPlayPlause.performClick()
                }
                ACTION_CUSTOM_PAUSE -> {
                    mBinding.ivMediaPlayPlause.performClick()
                }
                ACTION_CUSTOM_STOP -> {
//                    showToast(context ?: this@StartPlayerTabletActivity, "ACTION_STOP CALLED")
                }
                ACTION_CUSTOM_REWIND -> {
//                    showToast(context ?: this@StartPlayerTabletActivity, "ACTION_REWIND CALLED")
                    mBinding.ffBackForwardMedia.performClick()
                }
                ACTION_CUSTOM_FAST_FORWARD -> {
//                    showToast(context ?: this@StartPlayerTabletActivity, "ACTION_FAST_FORWARD CALLED")
                    mBinding.ffMoveForwardMedia.performClick()
                }
                ACTION_CUSTOM_NEXT -> {
//                    val isActionEnabled = intent.getBooleanExtra("isActionEnabled", true)
//                    if (isActionEnabled) {
//                    showToast(context ?: this@StartPlayerTabletActivity, "ACTION_NEXT CALLED")
                    if (videoType != Constants.meditationVideo && !programId.isNullOrEmpty()) {
                        if (mPlaylistViewModel.selectedPlaylistPosition < (mViewModel.playlistContentList.size - 1)) {
                            mPlaylistViewModel.selectedPlaylistPosition =
                                mPlaylistViewModel.selectedPlaylistPosition + 1
                            setNextContentDataAndCallAPI()
                        }
                    }
//                    } else {
//                        showToast(context ?: this@StartPlayerTabletActivity, "ACTION IS DISABLED")
//                    }
                }
                ACTION_CUSTOM_PREVIOUS -> {
//                    val isActionEnabled = intent.getBooleanExtra("isActionEnabled", true)
//                    if (isActionEnabled) {
//                    showToast(context ?: this@StartPlayerTabletActivity, "ACTION_PREVIOUS CALLED")
                    if (videoType != Constants.meditationVideo && !programId.isNullOrEmpty()) {
                        if (mPlaylistViewModel.selectedPlaylistPosition > 0) {
                            mPlaylistViewModel.selectedPlaylistPosition =
                                mPlaylistViewModel.selectedPlaylistPosition - 1
                            setNextContentDataAndCallAPI()
                        }
                    }
//                    } else {
//                        showToast(context ?: this@StartPlayerTabletActivity, "ACTION IS DISABLED")
//                    }
                }
                else -> {

                }
            }
        }
    }

    private fun loadBundle() {
        //logIntentBundle()
        videoType = myIntent?.extras?.getString(Constants.videoType, "") ?: ""
        contentId = myIntent?.extras?.getString(Constants.Param.contentId, null)
        programContentId = myIntent?.extras?.getString(Constants.Param.programContentId, null)
        programId = myIntent?.extras?.getString(Constants.Param.programId, null)
        isFromNotification =
            myIntent?.extras?.getBoolean(Constants.Param.isFromNotification, false) ?: false
        isInitialUser = myIntent?.extras?.getBoolean(Constants.Param.isInitialUser, false) ?: false
        isSubtitleVisible =
            myIntent?.extras?.getBoolean(Constants.Param.isSubtitleVisible, false) ?: false
        videoCurrentPosition = myIntent?.extras?.getLong(Constants.Param.videoPosition, 0) ?: 0
        isFromProgram = myIntent?.extras?.getBoolean(Constants.Param.isFromProgram, false) ?: false
        isFromMeditate =
            myIntent?.extras?.getBoolean(Constants.Param.isFromMeditate, false) ?: false
        alarmRequestCode = myIntent?.extras?.getInt(Constants.alarmRequestCode, 0) ?: 0
        isFromQuestionnaires =
            myIntent?.extras?.getBoolean(Constants.Param.isFromQuestionnaires, false) ?: false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setData(fromInit: Boolean = false) {
        mViewModel.meditationContent?.let {
            Glide.with(this).asBitmap().load(it.backgroundImageMobile)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        TaoCalligraphy.instance.run {
                            notificationTitle = it.title ?: ""
                            notificationImage = resource
                            notificationMediaId = it.id
                            notificationImagePath = ""

                            it.contentDuration?.let { time ->
                                val dataDate = time.split(":")
                                val hours = dataDate[0]
                                val minutes = dataDate[1]
                                val seconds = dataDate[2]
                                notificationDuration =
                                    (hours.toLong() * 60 * 60) + (minutes.toLong() * 60) + seconds.toLong()
                            }
                        }
                        contentImageBitmap = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
            showProgressIndicator(mBinding.llProgress, false)
            contentDownloadHelper.setMeditationContentData(it)

            if (it.type == Constants.TEXT) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                mBinding.cardPlayer.strokeColor = ContextCompat.getColor(this, R.color.dark_grey)
                mBinding.cardPlayer.strokeWidth = 2
                if (isTablet(this)) {
                    (mBinding.ffPlayerView.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                        "1:0.5"
                } else {
                    (mBinding.ffPlayerView.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                        "1:0.65"
                }
                mBinding.ivAudioImage.visible()
                mBinding.playerView.gone()
                mBinding.ivMediaFullScreen.gone()
                mBinding.ivMediaSetting.gone()
                mBinding.ivAudioImage.loadImage(
                    it.contentImage, R.drawable.img_default_for_content, true
                )
                mBinding.tvCaption.visible()
                mBinding.tvCaption.text = it.imageCaption
                mBinding.navigationView.setBackgroundColor(getColor(R.color.gold))
            } else if (it.type != Constants.VIDEO) {
                mBinding.cardPlayer.strokeColor = ContextCompat.getColor(this, R.color.dark_grey)
                mBinding.cardPlayer.strokeWidth = 2
                if (isTablet(this)) {
                    (mBinding.ffPlayerView.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                        "1:0.60"
                } else {
                    (mBinding.ffPlayerView.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                        "1:0.65"
                }
                mBinding.ivAudioImage.visible()
                mBinding.playerView.gone()
                mBinding.ivMediaFullScreen.visible()
                mBinding.ivMediaSetting.visible()
                mBinding.ivAudioImage.loadImage(
                    it.contentImage, R.drawable.img_default_for_content, true
                )
                mBinding.tvCaption.visible()
                mBinding.tvCaption.text = it.imageCaption
                mBinding.navigationView.setBackgroundColor(getColor(R.color.gold))
            } else {
                mBinding.cardPlayer.strokeColor = ContextCompat.getColor(this, R.color.transparent)
                mBinding.cardPlayer.strokeWidth = 0
                (mBinding.ffPlayerView.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                    "1:0.5"
                mBinding.ivAudioImage.gone()
                mBinding.playerView.visible()
                mBinding.ivMediaFullScreen.visible()
                mBinding.ivMediaSetting.visible()
                mBinding.tvCaption.gone()
                mBinding.navigationView.setBackgroundColor(getColor(R.color.transparent))
            }
            mBinding.ivMeditationImage.loadImage(
                it.backgroundImageMobile, R.drawable.img_default_for_content, true
            )
            mBinding.tvTitle.text = it.title
            mBinding.tvBottomTitle.text = it.title
            setMeditationFavouriteStatus(
                mBinding.toolbar.ivFavToolbar, it.isFavorites, false
            )
            subtitleWithLanguage = null
            setSubtitleView()

            if (it.subtitleWithLanguages?.isEmpty()!!) {
                mBinding.ivSubtitle.inVisible()
            } else {
                mBinding.ivSubtitle.visible()
            }

            setLikeDislikeView()

            meditationVideoPlaylistAdapter.chapterList = mViewModel.getChapterList()
            meditationVideoPlaylistAdapter.notifyDataSetChanged()
            if (mViewModel.isConfigChange) {
                setSubtitleStyle()
                mBinding.playerView.player = null
                mBinding.playerView.player = TaoCalligraphy.instance.simpleExoPlayer

                (mBinding.playerView.player as ExoPlayer).addListener(mBinding.subTitleView)
                mBinding.subTitleView.setCues((mBinding.playerView.player as ExoPlayer).currentCues)

                if (TaoCalligraphy.instance.simpleExoPlayer?.isPlaying == true) {
                    mBinding.ivMediaPlayPlause.setImageResource(R.drawable.vd_playing)
                } else {
                    mBinding.ivMediaPlayPlause.setImageResource(R.drawable.vd_pause)
                }
                subtitleWithLanguage = TaoCalligraphy.instance.subtitleWithLanguage
                setSubtitleView()

                if (mIsBound && playerNotificationService != null) {
                    var enablePreviousAction = false
                    var enableNextAction = false
                    if (videoType != Constants.meditationVideo && !programId.isNullOrEmpty()) {
                        enablePreviousAction = mPlaylistViewModel.selectedPlaylistPosition > 0
                        enableNextAction =
                            mPlaylistViewModel.selectedPlaylistPosition < (mViewModel.playlistContentList.size - 1)
                    }
                    mViewModel.meditationContent?.let {
                        playerNotificationService?.updateMediaContent(
                            it, enablePreviousAction, enableNextAction
                        )
                    }
                } else {
                    startNotificationService()
                }
                mViewModel.contentHandlerTimer.removeCallbacks(mViewModel.contentPlayTimeRunner)
                mViewModel.contentHandlerTimer.postDelayed(mViewModel.contentPlayTimeRunner, 0)
            } else {
                setVideoDataInPlayer()
                handlerMainTimer.removeCallbacks(runnableMainTimer)
                handlerMainTimer.postDelayed(
                    runnableMainTimer,
                    (1000 / mViewModel.playerPlaybackSpeed).toLong()
                )
            }

            if (it.type != Constants.TEXT) {
                FullScreenVideoPlayerTabletActivity.instance?.initView()
            } else {
                FullScreenVideoPlayerTabletActivity.instance?.finish()
            }

            if (isSubtitleVisible) {
                subtitleWithLanguage = getSubtitleDataFromIntent()
                setSubtitleAndShow(subtitleWithLanguage)
            }
        }
    }

    private fun callMeditationDetailAPI(id: String?) {
        mViewModel.getMeditationContent(id ?: "", this, mDisposable)
    }

    private fun callPlaylistListAPI() {
        mPlaylistViewModel.playlistContentApi(
            programId?.toInt() ?: 0, 0, 100, "", this, mDisposable
        )
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

    private fun setVideoCornerBackground() {
        mBinding.playerView.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline!!.setRoundRect(0, 0, view!!.width, view.height, 24F)
            }
        }
        mBinding.playerView.clipToOutline = true
    }

    private fun setupToolbar() {
        mBinding.toolbar.ivBackToolbar.visible()
        mBinding.toolbar.ivShareToolbar.visible()
        mBinding.toolbar.ivLikeToolbar.visible()
        mBinding.toolbar.cardFav.visible()

        if (isInitialUser == true) {
            mBinding.toolbar.ivLikeToolbar.gone()
            mBinding.toolbar.cardFav.gone()
            mBinding.toolbar.ivDownloadToolbar.gone()
        } else if (isNetwork()) {
            mBinding.toolbar.ivLikeToolbar.visible()
            mBinding.toolbar.cardFav.visible()
            if (UserHolder.EnumUserModulePermission.USE_DOWNLOAD_FUNCTION.permission?.canAccess
                    ?: true
            ) mBinding.toolbar.ivDownloadToolbar.visible()
            else mBinding.toolbar.ivDownloadToolbar.gone()
        } else {
            mBinding.toolbar.ivLikeToolbar.gone()
            mBinding.toolbar.cardFav.gone()
            mBinding.toolbar.ivDownloadToolbar.gone()
        }

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
        if (UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess ?: true) {
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

        if (isFromQuestionnaires) {
            mBinding.toolbar.cardFav.gone()
            mBinding.toolbar.ivDownloadToolbar.gone()
        }

        if (isFromMeditate) {
            mBinding.toolbar.ivShareToolbar.visible()
            mBinding.toolbar.ivLikeToolbar.gone()
            mBinding.toolbar.cardFav.gone()
        }

    }

    private fun setSubtitleStyle() {
        mBinding.subTitleView.isGone = true
        mBinding.playerView.subtitleView?.isVisible = false
        val captionStyleCompat = CaptionStyleCompat(
            Color.WHITE,
            Color.TRANSPARENT,
            Color.TRANSPARENT,
            CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
            Color.BLACK,
            ResourcesCompat.getFont(this, R.font.font_opensans_bold)
        )
        mBinding.subTitleView.setStyle(captionStyleCompat)
    }

    private fun setVideoDataInPlayer() {
        setSubtitleStyle()
        mBinding.playerView.player = null

        TaoCalligraphy.instance.simpleExoPlayer?.run {
            setMediaSource(getTrackWiseMediaSource())
            isPreparing = true
            prepare()
            if (isInitialUser) seekTo(videoCurrentPosition)
            else seekTo(0, C.TIME_UNSET)

            playWhenReady = TaoCalligraphy.instance.isPlayerPlaying
            repeatMode = ExoPlayer.REPEAT_MODE_OFF
            playbackSpeed = mViewModel.playerPlaybackSpeed
            mBinding.playerView.player = TaoCalligraphy.instance.simpleExoPlayer

            (mBinding.playerView.player as ExoPlayer).addListener(mBinding.subTitleView)
            mBinding.subTitleView.setCues((mBinding.playerView.player as ExoPlayer).currentCues)
        }

        if (mIsBound && playerNotificationService != null) {
            var enablePreviousAction = false
            var enableNextAction = false
            if (videoType != Constants.meditationVideo && !programId.isNullOrEmpty()) {
                enablePreviousAction = mPlaylistViewModel.selectedPlaylistPosition > 0
                enableNextAction =
                    mPlaylistViewModel.selectedPlaylistPosition < (mViewModel.playlistContentList.size - 1)
            }
            mViewModel.meditationContent?.let {
                playerNotificationService?.updateMediaContent(
                    it, enablePreviousAction, enableNextAction
                )
            }
        } else {
            startNotificationService()
        }
    }

    private fun getTrackWiseMediaSource(): MergingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        val contentFile: String? =
            if (!mViewModel.meditationContent?.contentFileForDownload.isNullOrEmpty()) {
                mViewModel.meditationContent?.contentFileForDownload
            } else {
                mViewModel.meditationContent?.contentFile
            }
        val dataSourceFactory: DataSource.Factory =
            if (contentDownloadHelper.isContentDownloaded(contentFile) == true) {
                CacheDataSource.Factory().setCache(TaoCalligraphy.instance.getDownloadCache())
                    .setUpstreamDataSourceFactory(TaoCalligraphy.instance.buildDataSourceFactory())
            } else {
                DefaultHttpDataSource.Factory().setUserAgent(Util.getUserAgent(this, packageName))
            }

        if (isInitialUser == true) {
            val mediaSource =
                HlsMediaSource.Factory(DefaultHttpDataSource.Factory().setUserAgent(userAgent))
                    .createMediaSource(MediaItem.fromUri(Uri.parse(mViewModel.meditationContent?.contentFileForHls)))
            concatenatingMediaSource.addMediaSource(mediaSource)
        } else {
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(contentFile)))
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        if (subtitleWithLanguage == null) {
            return MergingMediaSource(
                concatenatingMediaSource
            )
        } else {
            val subTitle = subtitleWithLanguage?.subTitleFile
            val uriSubtitle = Uri.parse(subTitle)

            val extension: String = MimeTypeMap.getFileExtensionFromUrl(subTitle)

            val mimeType = if (extension.equals(Constants.SubTitles.SUBTITLE_VTT, true)) {
                MimeTypes.TEXT_VTT
            } else {
                MimeTypes.APPLICATION_SUBRIP
            }

            val itemConfig =
                MediaItem.SubtitleConfiguration.Builder(uriSubtitle).setMimeType(mimeType)
                    .setLanguage(getString(R.string.language_en))
                    .setSelectionFlags(C.SELECTION_FLAG_DEFAULT).setRoleFlags(C.ROLE_FLAG_SUBTITLE)
                    .build()

            val subtitleSource: SingleSampleMediaSource = itemConfig.let {
                SingleSampleMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(it, C.TIME_UNSET)
            }
            return MergingMediaSource(
                concatenatingMediaSource, subtitleSource
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun observeApiCallbacks() {
        mPlaylistViewModel.playlistContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let {
                    val span = SpannableStringBuilder()
                    span.append("${getString(R.string.from)} ")

                    val start = span.length

                    span.append(it.playlistName)
                    span.setSpan(
                        TextAppearanceSpan(this, R.style.TextViewJostRegularItalicStyle),
                        start,
                        span.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    mBinding.tvPlaylistNameCollapse.text = span
                    mBinding.tvPlaylistNameExpand.text = span
                    val firstContent: PlaylistContentFilterApiResponse.ContentList
                    val list =
                        it.list.filter { ((it.subscription?.isAccessible ?: false == true) && if (it.isPaidContent == false) true else it.isPurchased == true) }
                    if (list.isNotEmpty()) {
                        firstContent = if (contentId?.isNotEmpty() == true) {
                            list.firstOrNull { it.id == contentId }!!
                        } else {
                            list.first()
                        }
                        if (mPlaylistViewModel.selectedPlaylistPosition == 0)
                            mPlaylistViewModel.selectedPlaylistPosition = list.indexOf(firstContent)
                        mViewModel.playlistContentList.clear()
                        mViewModel.playlistContentList.addAll(list)
                        playlistViewItemAdapter.updateList(mViewModel.playlistContentList)
                        setNextContentDataAndCallAPI()
                    }
                }
                longToastState(requestState.error)
            }
        }

        mViewModel.meditationContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                if (FullScreenVideoPlayerTabletActivity.isScreenVisible) {
                    showProgressIndicator(mBinding.llProgress, false)
                } else {
                    if (!mViewModel.isConfigChange)
                        showProgressIndicator(mBinding.llProgress, requestState.progress)
                }
                requestState.apiResponse?.data?.let { it ->
                    mViewModel.isConfigChange = false
                    mViewModel.meditationContent = it
                    setData()
                    showProgressIndicator(mBinding.llProgress, false)
                }
                longToastState(requestState.error)
            }
            mViewModel.meditationContentLiveData.postValue(null)
        }

        mViewModel.favouriteMeditationContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let {
                    mViewModel.meditationContent?.isFavorites =
                        mViewModel.meditationContent?.isFavorites != true
                    setMeditationFavouriteStatus(
                        mBinding.toolbar.ivFavToolbar,
                        mViewModel.meditationContent?.isFavorites,
                        true
                    )

                    EventBus.getDefault().post(
                        MeditationContentFavouriteListener(
                            mViewModel.meditationContent?.id ?: "",
                            mViewModel.meditationContent?.isFavorites ?: false
                        )
                    )
                }
                longToastState(requestState.error)
            }
        }

        mViewModel.likeDisLikeMeditationContentLiveData.observe(this) { response ->
            response?.let { requestState ->
                requestState.apiResponse?.let {
                    mViewModel.meditationContent?.isLiked = likeStatus
                    setLikeDislikeView()
                }
                longToastState(requestState.error)
            }
        }
    }

    override fun handleListener() {
        mBinding.zenModeView.btnDontShowAgain.setOnClickListener {
            mViewModel.isZenModeDontShow = true
            mBinding.dialogZenMode.gone()
        }
        mBinding.zenModeView.ivCloseDialog.setOnClickListener {
            mBinding.dialogZenMode.gone()
        }

        mBinding.toolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
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

        mBinding.sliderVideoProgress.addOnSliderTouchListener(object :
            Slider.OnSliderTouchListener {
            @SuppressLint("RestrictedApi")
            override fun onStartTrackingTouch(slider: Slider) {
            }

            @SuppressLint("RestrictedApi")
            override fun onStopTrackingTouch(slider: Slider) {
                TaoCalligraphy.instance.simpleExoPlayer?.run {
                    val currentDuration = (duration * slider.value) / 100f
                    seekTo(currentDuration.toLong())
                }
                setCurrentChapterPosition()
            }
        })

        mBinding.sliderBottomVideoProgress.addOnSliderTouchListener(object :
            Slider.OnSliderTouchListener {
            @SuppressLint("RestrictedApi")
            override fun onStartTrackingTouch(slider: Slider) {
            }

            @SuppressLint("RestrictedApi")
            override fun onStopTrackingTouch(slider: Slider) {
                TaoCalligraphy.instance.simpleExoPlayer?.run {
                    val currentDuration = (duration * slider.value) / 100f
                    seekTo(currentDuration.toLong())

                    setCurrentChapterPosition()
                }
            }
        })

        mBinding.sliderVideoProgress.setLabelFormatter { value ->
            val currentDuration =
                (((TaoCalligraphy.instance.simpleExoPlayer?.duration ?: 0) * value) / 100f).toLong()
            currentDuration.getDurationTime()
        }
        mBinding.sliderBottomVideoProgress.setLabelFormatter { value ->
            val currentDuration =
                (((TaoCalligraphy.instance.simpleExoPlayer?.duration ?: 0) * value) / 100f).toLong()
            currentDuration.getDurationTime()
        }

        mBinding.ivMediaPlayPlause.setOnClickListener {
            if (TaoCalligraphy.instance.simpleExoPlayer?.isPlaying == true) {
                Constants.NOTIFICATION_ON_OFF = true
            } else {
                Constants.NOTIFICATION_ON_OFF = false
                if (!mViewModel.isZenModeDontShow && userHolder.isZenModeForMeditation) showZenModeDialog()
            }
            TaoCalligraphy.instance.simpleExoPlayer?.run {
                playWhenReady = !playWhenReady
            }
        }
        mBinding.ivBottomMediaPlayPause.setOnClickListener {
            mBinding.ivMediaPlayPlause.performClick()
        }

        mBinding.ffBackForwardMedia.setOnClickListener {
            var moveToPosition = 0L
            TaoCalligraphy.instance.simpleExoPlayer?.run {
                if (currentPosition > 15000) {
                    moveToPosition = currentPosition - 15000
                }
                seekTo(moveToPosition)
            }
        }

        mBinding.ffMoveForwardMedia.setOnClickListener {
            TaoCalligraphy.instance.simpleExoPlayer?.run {
                var moveToPosition = duration
                if (currentPosition < (duration - 15000)) {
                    moveToPosition = currentPosition + 15000
                }
                seekTo(moveToPosition)
            }
        }

        mBinding.ivSubtitle.setOnClickListener {
            mViewModel.meditationContent?.let {
                if (subtitleWithLanguage == null) {
                    if ((it.subtitleWithLanguages?.size ?: 0) == 1) {
                        setSubtitleAndShow(it.subtitleWithLanguages?.getOrNull(0))
                    } else {
                        openChooseSubtitleDialog()
                    }
                } else {
                    subtitleWithLanguage = null
                    setSubtitleView()
                }
            }
        }

        mBinding.ivMediaFullScreen.setOnClickListener {
            mViewModel.isMoveToFullScreen = true
            mViewModel.isFromFullScreen = true
            FullScreenVideoPlayerTabletActivity.startActivity(
                this,
                fullScreenMediaPlayerResult,
                isInitialUser,
                mViewModel.isFromFullScreen
            )
        }

        mBinding.ivMediaSetting.setOnClickListener {
            showPlaybackDialog()
        }

        mBinding.toolbar.cardFav.setOnClickListener {
            if (!isNetwork()) {
                return@setOnClickListener
            }
            mViewModel.meditationContent?.let {
                mViewModel.favoriteMeditationContent(
                    it.id, this, mDisposable
                )
            }
        }

        mBinding.toolbar.ivLikeToolbar.setOnClickListener {
            if (!isNetwork()) {
                return@setOnClickListener
            }
            mViewModel.meditationContent?.let {
                if (it.isLiked != true) {
                    likeStatus = true
                    mViewModel.likeDisLikeMeditationContent(
                        it.id, Constants.like, this, mDisposable
                    )
                } else {
                    likeStatus = false
                    mViewModel.likeDisLikeMeditationContent(
                        it.id, Constants.dislike, this, mDisposable
                    )
                }
            }
            mViewModel.meditationContent?.isLiked = likeStatus
            setLikeDislikeView()
        }

        mBinding.ivRepeatVideo.setOnClickListener {
            TaoCalligraphy.instance.simpleExoPlayer?.run {
                repeatMode = if (repeatMode == ExoPlayer.REPEAT_MODE_OFF) {
                    mBinding.ivRepeatVideo.setImageResource(R.drawable.vd_video_repeat_on)
                    ExoPlayer.REPEAT_MODE_ALL
                } else {
                    mBinding.ivRepeatVideo.setImageResource(R.drawable.vd_video_repeat)
                    ExoPlayer.REPEAT_MODE_OFF
                }
            }
        }

        playlistViewItemAdapter.setOnClickListener(object : PlaylistViewItemAdapter.OnSelect {
            override fun clickOnItem(
                cardList: MutableList<PlaylistContentFilterApiResponse.ContentList>, position: Int
            ) {
                if (mPlaylistViewModel.selectedPlaylistPosition != position) {
                    mViewModel.isConfigChange = false
                    mPlaylistViewModel.selectedPlaylistPosition = position
                    TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady = false
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    setNextContentDataAndCallAPI()
                }
            }
        })
    }

    private fun showZenModeDialog() {
        mBinding.dialogZenMode.visible()
        zenModeDisplayed = true
        zenModeTimer.postDelayed(zenModeTimerRunner, 5000)
    }

    private fun showPlaybackDialog() {
        if (settingPopupWindow == null) {
            initializeSettingPopupWindow()
        }
        val location = IntArray(2).apply {
            mBinding.cardPlayer.getLocationOnScreen(this)
        }
        val size = settingPopupWindow?.contentView?.let {
            settingPopupWindow?.contentView?.measuredWidth?.let { it1 ->
                Size(
                    it1, it.measuredHeight
                )
            }
        }
        settingPopupWindow?.showAtLocation(
            mBinding.ivMediaSetting,
            Gravity.TOP,
            0,
            location[1] - ((size?.height ?: 0) - mBinding.cardPlayer.height) / 2
        )
        settingPopupWindow?.dimBehind()
        settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
            this,
            mViewModel.playerPlaybackSpeed
        )
    }

    private fun initializeSettingPopupWindow() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.dialog_playback_speed_option, null).apply {
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
        }

        val tv5Speed = view.findViewById<TextView>(R.id.tv5Speed)
        val tv75Speed = view.findViewById<TextView>(R.id.tv75Speed)
        val tvNormalSpeed = view.findViewById<TextView>(R.id.tvNormalSpeed)
        val tv15Speed = view.findViewById<TextView>(R.id.tv15Speed)
        val tv2Speed = view.findViewById<TextView>(R.id.tv2Speed)
        val tvSlower = view.findViewById<TextView>(R.id.tvSlower)
        val tvFaster = view.findViewById<TextView>(R.id.tvFaster)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)

        tv5Speed.setOnClickListener {
            mViewModel.playerPlaybackSpeed = 0.5f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                mViewModel.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        tv75Speed.setOnClickListener {
            mViewModel.playerPlaybackSpeed = 0.75f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                mViewModel.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        tvNormalSpeed.setOnClickListener {
            mViewModel.playerPlaybackSpeed = 1f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                mViewModel.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        tv15Speed.setOnClickListener {
            mViewModel.playerPlaybackSpeed = 1.5f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                mViewModel.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        tv2Speed.setOnClickListener {
            mViewModel.playerPlaybackSpeed = 2f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                mViewModel.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        ivClose.setOnClickListener {
            settingPopupWindow?.dismiss()
        }
        tvSlower.setOnClickListener {
            when (mViewModel.playerPlaybackSpeed) {
                0.75f -> {
                    mViewModel.playerPlaybackSpeed = 0.5f
                }
                1f -> {
                    mViewModel.playerPlaybackSpeed = 0.75f
                }
                1.5f -> {
                    mViewModel.playerPlaybackSpeed = 1f
                }
                2f -> {
                    mViewModel.playerPlaybackSpeed = 1.5f
                }
            }
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                mViewModel.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        tvFaster.setOnClickListener {
            when (mViewModel.playerPlaybackSpeed) {
                0.5f -> {
                    mViewModel.playerPlaybackSpeed = 0.75f
                }
                0.75f -> {
                    mViewModel.playerPlaybackSpeed = 1f
                }
                1f -> {
                    mViewModel.playerPlaybackSpeed = 1.5f
                }
                1.5f -> {
                    mViewModel.playerPlaybackSpeed = 2f
                }
            }
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                mViewModel.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        val width = mViewModel.meditationContent?.let {
            when (it.type) {
                Constants.VIDEO -> {
                    (mBinding.playerView.width * 0.90).toInt()
                }
                Constants.TEXT -> {
                    ViewGroup.LayoutParams.WRAP_CONTENT
                }
                else -> {
                    (mBinding.ivAudioImage.width * 0.90).toInt()
                }
            }
        } ?: ViewGroup.LayoutParams.WRAP_CONTENT
//        settingPopupWindow = PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        settingPopupWindow = PopupWindow(view, width, ViewGroup.LayoutParams.WRAP_CONTENT)
        settingPopupWindow?.isOutsideTouchable = true
        settingPopupWindow?.isFocusable = true
        settingPopupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        popupWindow.showAtLocation(mBinding.ivMediaSetting, Gravity.TOP, 0,
//            mBinding.ivMediaSetting.y.toInt()
//        )
    }

    private fun updatePlayerPlaybackSpeed() {
        TaoCalligraphy.instance.simpleExoPlayer?.run {
            playbackSpeed = mViewModel.playerPlaybackSpeed
            mBinding.playerView.player = TaoCalligraphy.instance.simpleExoPlayer
        }
    }

    private val exoPlayerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_BUFFERING) {
                mViewModel.contentHandlerTimer.removeCallbacks(mViewModel.contentPlayTimeRunner)
            }

            if (playbackState == Player.STATE_READY) {
                mViewModel.contentHandlerTimer.removeCallbacks(mViewModel.contentPlayTimeRunner)
                mViewModel.contentHandlerTimer.postDelayed(mViewModel.contentPlayTimeRunner, 0)
                if (TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady == true) {
                    mViewModel.isPlaying = true

                    Constants.NOTIFICATION_ON_OFF = false
                    if (isPreparing && !zenModeDisplayed && !mViewModel.isZenModeDontShow && userHolder.isZenModeForMeditation) Handler(
                        Looper.getMainLooper()
                    ).postDelayed(
                        { showZenModeDialog() }, 1000
                    )

                    mBinding.ivMediaPlayPlause.setImageResource(R.drawable.vd_playing)
                    mBinding.ivBottomMediaPlayPause.setImageResource(R.drawable.vd_playing)
                }
                if (isPreparing) {
                    isPreparing = false
                }
            }

            if (playbackState == Player.STATE_ENDED) {
                if (TaoCalligraphy.instance.simpleExoPlayer?.repeatMode == ExoPlayer.REPEAT_MODE_OFF) {
                    mViewModel.isPlaying = false
                    mBinding.ivMediaPlayPlause.setImageResource(R.drawable.vd_pause)
                    mBinding.ivBottomMediaPlayPause.setImageResource(R.drawable.vd_pause)
                    if (videoType == Constants.meditationVideo) {
                        if (isFromNotification) {
                            FullScreenVideoPlayerTabletActivity.instance?.finish()
                            onBackPressed()
                        } else {
                            openMeditationFeedbackScreen()
                        }
                        mViewModel.contentHandlerTimer.removeCallbacks(mViewModel.contentPlayTimeRunner)
                    } else if (!programId.isNullOrEmpty()) {
                        mViewModel.isConfigChange = false
                        if (mPlaylistViewModel.selectedPlaylistPosition < (mViewModel.playlistContentList.size - 1)) {
                            mPlaylistViewModel.selectedPlaylistPosition =
                                mPlaylistViewModel.selectedPlaylistPosition + 1
                            setNextContentDataAndCallAPI()
                        } else if (mPlaylistViewModel.selectedPlaylistPosition == (mViewModel.playlistContentList.size - 1)) {
                            FullScreenVideoPlayerTabletActivity.instance?.finish()
                            onBackPressed()
                        }
                    } else if (isFromNotification) {
                        FullScreenVideoPlayerTabletActivity.instance?.finish()
                        onBackPressed()
                    }
                }
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            if (playWhenReady && Player.STATE_READY == TaoCalligraphy.instance.simpleExoPlayer?.playbackState) {
                mViewModel.contentHandlerTimer.removeCallbacks(mViewModel.contentPlayTimeRunner)
                mViewModel.contentHandlerTimer.postDelayed(mViewModel.contentPlayTimeRunner, 0)
                mViewModel.isPlaying = true
                mBinding.ivMediaPlayPlause.setImageResource(R.drawable.vd_playing)
                mBinding.ivBottomMediaPlayPause.setImageResource(R.drawable.vd_playing)
            } else {
                mViewModel.contentHandlerTimer.removeCallbacks(mViewModel.contentPlayTimeRunner)
                mViewModel.isPlaying = false
                mBinding.ivMediaPlayPlause.setImageResource(R.drawable.vd_pause)
                mBinding.ivBottomMediaPlayPause.setImageResource(R.drawable.vd_pause)
            }
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int
        ) {
            //THIS METHOD GETS CALLED FOR EVERY NEW SOURCE THAT IS PLAYED
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) {
                TaoCalligraphy.instance.simpleExoPlayer?.seekTo(mViewModel.getIntroChapterTime())
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            this@StartPlayerTabletActivity.showCustomDialog(
                getString(R.string.content_not_load_msg),
                getString(R.string.dialog_go_back),
                getString(R.string.dialog_refresh),
                getString(R.string.dialog_wait),
                object : CustomDialogListener {
                    override fun onPositiveClicked() {
                        setData()
                    }

                    override fun onNegativeClicked() {
                        onBackPressed()
                    }

                    override fun onNeutralClicked() {
                    }
                })
        }
    }

    private fun setExoplayerListener() {
        TaoCalligraphy.instance.simpleExoPlayer?.addListener(exoPlayerListener)
    }

    fun setNextContentDataAndCallAPI() {
        settingPopupWindow?.dismiss()
        if (!mViewModel.isConfigChange) {
            callPlayTimeAPI()
        }
        val content =
            mViewModel.playlistContentList[mPlaylistViewModel.selectedPlaylistPosition]
        if (!mViewModel.isConfigChange || mViewModel.meditationContent == null) {
            callMeditationDetailAPI(content.id)
        }

        if (mPlaylistViewModel.selectedPlaylistPosition == (mViewModel.playlistContentList.size - 1)) {
            mBinding.tvUpNext.gone()
            mBinding.tvUpNextPlaylistContentName.text = ""
        } else {
            val nextContent =
                mViewModel.playlistContentList.getOrNull(mPlaylistViewModel.selectedPlaylistPosition + 1)

            if (nextContent != null) {
                mBinding.tvUpNext.visible()
                mBinding.tvUpNextPlaylistContentName.text = nextContent.contentName
                mBinding.tvUpNextPlaylistContentName.isSelected = true
            } else {
                mBinding.tvUpNext.gone()
                mBinding.tvUpNextPlaylistContentName.text = ""
            }
        }
    }

    private val runnableMainTimer: Runnable = object : Runnable {
        override fun run() {
            if ((!isFinishing)) {
                setPlayerTime()
                handlerMainTimer.postDelayed(this, (1000 / mViewModel.playerPlaybackSpeed).toLong())
            }
        }
    }

    private fun setSubtitleView() {
        if (subtitleWithLanguage == null) {
            TaoCalligraphy.instance.subtitleWithLanguage = null
            mBinding.subTitleView.isGone = true
            mBinding.ivSubtitle.setImageResource(R.drawable.vd_subtitle)
        } else {
            TaoCalligraphy.instance.subtitleWithLanguage = subtitleWithLanguage
            mBinding.subTitleView.isVisible = true
            mBinding.ivSubtitle.setImageResource(R.drawable.vd_subtitle_on)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setPlayerTime() {
        TaoCalligraphy.instance.simpleExoPlayer?.run {
            if (duration < 0) {
                mBinding.sliderVideoProgress.isEnabled = false
                mBinding.sliderBottomVideoProgress.isEnabled = false
                return
            }
            val currentTime = contentPosition
            val totalDuration = duration
            val currentProgress = (100f * currentTime) / totalDuration
            if (currentProgress > mBinding.sliderVideoProgress.valueTo) {
                mBinding.sliderVideoProgress.value = mBinding.sliderVideoProgress.valueTo
                mBinding.sliderBottomVideoProgress.value =
                    mBinding.sliderBottomVideoProgress.valueTo
            } else {
                mBinding.sliderVideoProgress.value = currentProgress
                mBinding.sliderBottomVideoProgress.value = currentProgress
            }

            val time = "${currentTime.getDurationTime()} / ${totalDuration.getDurationTime()}"
            mBinding.tvMusicTime.text = time
            mBinding.tvBottomMusicTime.text = time
            mBinding.sliderVideoProgress.isEnabled = true
            mBinding.sliderBottomVideoProgress.isEnabled = true
            setCurrentChapterPosition()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setCurrentChapterPosition() {
        TaoCalligraphy.instance.simpleExoPlayer?.run {
            val currentTime = contentPosition

            val indexPosition = meditationVideoPlaylistAdapter.chapterList.indexOfFirst {
                (currentTime >= (it?.startTimeStamp ?: 0)) and (currentTime <= (it?.endTimeStamp
                    ?: 0))
            }
            if (indexPosition != meditationVideoPlaylistAdapter.currentMediaPlayPosition) {
                meditationVideoPlaylistAdapter.currentMediaPlayPosition = indexPosition
                if (indexPosition != -1) {
                    mBinding.rvMeditationVideoPlaylist.smoothScrollToPosition(indexPosition)
                }
                meditationVideoPlaylistAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun openChooseSubtitleDialog() {
        mViewModel.meditationContent?.let {
            var dialog: ChooseSubTitleDialog? = null
            dialog = ChooseSubTitleDialog(
                this,
                it.subtitleWithLanguages,
                object : ChooseSubTitleDialog.SubtitleSelectionListener {
                    override fun onSubtitleSelect(subtitle: MeditationContentResponse.SubtitleWithLanguage?) {
                        dialog?.dismiss()
                        setSubtitleAndShow(subtitle)
                    }
                })
            dialog.show()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun setSubtitleAndShow(subtitle: MeditationContentResponse.SubtitleWithLanguage?) {
        subtitleWithLanguage = subtitle
        setSubtitleView()
        TaoCalligraphy.instance.simpleExoPlayer?.run {
            val currentDuration = currentPosition
            setMediaSource(getTrackWiseMediaSource())
            prepare()
            seekTo(currentDuration)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun playlistUpdateEventBus(playlistContentChangeListener: PlaylistContentChangeListener) {
        showProgressIndicator(mBinding.llProgress, false)
        mViewModel.meditationContent = playlistContentChangeListener.meditationContentResponse
        setData()
    }

    fun openMeditationFeedbackScreen() {
        if (isSimpleExoplayerListenerSet) {
            return
        }
        isSimpleExoplayerListenerSet = true

        if (isFromMeditate) {
            MeditationDetailActivity.instance?.finish()
            FullScreenVideoPlayerTabletActivity.instance?.finish()
            finish()
        } else if (isInitialUser == true || mViewModel.meditationContent?.isInstructional == true) {
            FullScreenVideoPlayerTabletActivity.instance?.finish()
            finish()
        } else if (mViewModel.meditationContent?.isInstructional == true) {
            FullScreenVideoPlayerTabletActivity.instance?.finish()
            finish()
        } else if (isNetwork() && (mViewModel.meditationContent?.isShowRating == true && (mViewModel.meditationContent?.isAssessmentDone == false || mViewModel.meditationContent?.isPostAssessmentCompleted == false))) {
            MeditationPostAssessmentActivity.startActivity(this, mViewModel.meditationContent)
            FullScreenVideoPlayerTabletActivity.instance?.finish()
            finish()
        } else {
            FullScreenVideoPlayerTabletActivity.instance?.finish()
            finish()
        }
    }

    override fun onPause() {
        mViewModel.isPause = true
        if ((mViewModel.meditationContent?.type ?: "") != Constants.TEXT) {
            TaoCalligraphy.instance.simpleExoPlayer?.run {
                val play = playWhenReady
//                playWhenReady = false
                mViewModel.isPlaying = play
                TaoCalligraphy.instance.isPlayerPlaying = play
            }
        }
        super.onPause()
    }

    private fun startNotificationService() {
        stopNotificationService()
        notificationServiceIntent = Intent(this, ExoNotificationService::class.java).putExtra(
            Constants.MeditationContent,
            mViewModel.meditationContent
        )

        if (contentId != null && programId == null) {
            notificationServiceIntent?.putExtra(Constants.FROM, PLAYER_FROM_CONTENT)
        } else if (programId != null) {
            var enablePreviousAction = false
            var enableNextAction = false
            if (videoType != Constants.meditationVideo && !programId.isNullOrEmpty()) {
                enablePreviousAction = mPlaylistViewModel.selectedPlaylistPosition > 0
                enableNextAction =
                    mPlaylistViewModel.selectedPlaylistPosition < (mViewModel.playlistContentList.size - 1)
            }
            notificationServiceIntent?.putExtra("enablePreviousAction", enablePreviousAction)
            notificationServiceIntent?.putExtra("enableNextAction", enableNextAction)
            notificationServiceIntent?.putExtra(Constants.FROM, PLAYER_FROM_PLAYLIST)
        }

        mIsBound =
            bindService(notificationServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(
//                notificationServiceIntent
//            )
//        } else {
//            startService(notificationServiceIntent)
//        }
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            playerNotificationService =
                (iBinder as ExoNotificationService.MainServiceBinder).service
            // service is a private member of MainActivity

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerNotificationService = null
        }

    }

    private fun stopNotificationService() {
        if (mIsBound) {
            unbindService(serviceConnection)
            mIsBound = false
        }
    }

    override fun onResume() {
        mViewModel.isPause = false
        if ((mViewModel.meditationContent?.type ?: "") != Constants.TEXT) {
            if (mViewModel.isPlaying && !mViewModel.isConfigChange) {
                TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady = true
            }
        }

        handlerMainTimer.postDelayed(
            runnableMainTimer,
            (0 / mViewModel.playerPlaybackSpeed).toLong()
        )
        super.onResume()
    }

    override fun onDestroy() {
        if (!isChangingConfigurations) {
            Constants.NOTIFICATION_ON_OFF = true
            if (!isInitialUser) {
                TaoCalligraphy.instance.simpleExoPlayer?.release()
                stopNotificationService()
                if (isNetwork()) callPlayTimeAPI()
            }
            LocalBroadcastManager.getInstance(this).unregisterReceiver(playerNotificationReceiver)
            LocalBroadcastManager.getInstance(this@StartPlayerTabletActivity)
                .unregisterReceiver(mAccessLevelReceiver)
        } else {
            mViewModel.isConfigChange = true
            TaoCalligraphy.instance.isPlayerPlaying =
                TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady ?: false
        }
        if (!mViewModel.isMoveToFullScreen) {
            contentDownloadHelper.removeHandler()
            handlerMainTimer.removeCallbacks(runnableMainTimer)
            mViewModel.contentHandlerTimer.removeCallbacks(mViewModel.contentPlayTimeRunner)
        }
        TaoCalligraphy.instance.simpleExoPlayer?.removeListener(exoPlayerListener)

        orientationEventListener.disable()
        super.onDestroy()
    }

    var zenModeTimer = Handler(Looper.getMainLooper())
    val zenModeTimerRunner = Runnable { mBinding.dialogZenMode.gone() }

    fun callPlayTimeAPI() {
        val currentTimeValue = String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(mViewModel.totalContentTime),
            TimeUnit.MILLISECONDS.toMinutes(mViewModel.totalContentTime) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(mViewModel.totalContentTime)
            ),
            TimeUnit.MILLISECONDS.toSeconds(mViewModel.totalContentTime) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(mViewModel.totalContentTime)
            )
        )
        if (isFromProgram) {
            programContentId?.let {
                mViewModel.programContentPlayTime(
                    it, currentTimeValue
                )
            }
        } else {
            mViewModel.meditationContent?.let {
                mViewModel.contentPlayTime(
                    it.id, currentTimeValue
                )
            }
        }
        mViewModel.totalContentTime = 1000L
        mViewModel.isConfigChange = false
    }

    override fun onMediaChanged(position: Int) {
        val seekDuration = meditationVideoPlaylistAdapter.chapterList[position]?.startTimeStamp
        seekDuration?.let {
            TaoCalligraphy.instance.simpleExoPlayer?.seekTo(it)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
        if (model.isAvailable && getMeditationData() == null) {
            if (!mViewModel.isPause) {
                mViewModel.meditationContent?.let {
                    setData()
                } ?: kotlin.run {
                    LocalBroadcastManager.getInstance(this)
                        .unregisterReceiver(playerNotificationReceiver)
                    initView()
                }
            }
            noInternetConnectionDialog?.dismiss()
        } else {
            if (!isFinishing && getMeditationData() == null) {
                showNoInternetDialog()
            }
        }
    }

    private fun getMeditationData(): MeditationContentResponse? {
        contentId?.let {
            return mViewModel.getMeditationContent(it)
        } ?: kotlin.run {
            mViewModel.meditationContent?.let {
                return mViewModel.getMeditationContent(it.id)
            }
        }
        return null
    }

    override fun onBackPressed() {
        if (isFromNotification && isTaskRoot) {
            MainActivity.startActivity(this)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun setOrientationListener() {
        orientationEventListener = object : OrientationEventListener(this@StartPlayerTabletActivity) {
            override fun onOrientationChanged(orientation: Int) {
                val newOrientation = when (orientation) {
                    in 0..44 -> 0
                    in 45..134 -> 1
                    in 135..224 -> 2
                    in 225..314 -> 3
                    in 315..359 -> 0
                    else -> 0
                }
                if (newOrientation != previousOrientation && ((newOrientation % 2) != (previousOrientation % 2))) {

                    Handler(Looper.getMainLooper()).postDelayed({
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }, 100)
                }
                previousOrientation = newOrientation
            }
        }
        orientationEventListener.enable()
    }


}