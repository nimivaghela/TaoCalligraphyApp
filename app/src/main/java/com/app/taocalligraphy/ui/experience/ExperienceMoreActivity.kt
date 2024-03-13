package com.app.taocalligraphy.ui.experience

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Size
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityExperienceMoreBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.notification.ExoNotificationService
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_PAUSE
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_PLAY
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.playbackSpeed
import com.app.taocalligraphy.ui.experience.adapter.ContentDataAdapter
import com.app.taocalligraphy.ui.experience.adapter.OnCategoryClickListener
import com.app.taocalligraphy.ui.experience.adapter.WellnessCategoryUserExperienceAdapter
import com.app.taocalligraphy.ui.experience.viewmodel.ExperienceMoreViewModel
import com.app.taocalligraphy.ui.meditation.dialog.ChooseSubTitleDialog
import com.app.taocalligraphy.ui.welcome_login.WelcomeLoginActivity
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.CaptionStyleCompat
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.userAgent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ExperienceMoreActivity : BaseActivity<ActivityExperienceMoreBinding>(),
    OnCategoryClickListener {

    private val viewModel: ExperienceMoreViewModel by viewModels()

    override fun getResource() = R.layout.activity_experience_more

    companion object {

        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, ExperienceMoreActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    private var isAlreadyLoaded = false
    private var isMoveToPlayer = false
    private var isPlayerVisible = true
    private var isPause = false
    private var isPlayerPlaying = true
    var subtitleWithLanguage: MeditationContentResponse.SubtitleWithLanguage? = null
    private var settingPopupWindow: PopupWindow? = null
    var playerPlaybackSpeed = 1f
    private var isFromFullScreen = false
    var handlerControllerHide = Handler(Looper.getMainLooper())

    var notificationServiceIntent: Intent? = null
    var playerNotificationService: ExoNotificationService? = null
    var mIsBound = false

    private var previousOrientation: Int? = 0
    private lateinit var orientationEventListener: OrientationEventListener

    private var fullScreenMediaPlayerResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        mBinding.playerView.player = null
        mBinding.playerView.player = TaoCalligraphy.instance.simpleExoPlayer
        TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady =
            TaoCalligraphy.instance.isPlayerPlaying
        subtitleWithLanguage = TaoCalligraphy.instance.subtitleWithLanguage
        playerPlaybackSpeed = TaoCalligraphy.instance.playerPlaybackSpeed
        setSubtitleView()

        isFromFullScreen = true
//        requestedOrientation = if (FullScreenPlayerActivity.isOrientationButtonClicked)
//            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        else
//            ActivityInfo.SCREEN_ORIENTATION_SENSOR
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
    }

    override fun initView() {
        if (isNetwork()) {
            viewModel.getInitialUserExperienceDetail(0, this, mDisposable)
            setupToolbar()
            initRecyclerView()
            setExoplayerListener()
            setOrientationListener()
        } else
            longToast(getString(R.string.no_internet, "to Experience More"), ERROR)

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(playerNotificationReceiver, IntentFilter(ACTION_CUSTOM_PLAY))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(playerNotificationReceiver, IntentFilter(ACTION_CUSTOM_PAUSE))

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    var myIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        myIntent = intent
        super.onCreate(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        myIntent = intent
        if (myIntent?.hasExtra(Constants.FROM) == true && myIntent?.getStringExtra(Constants.FROM)
                .equals(Constants.FROM_PLAYER_NOTIFICATION)
        ) {
            showProgressIndicator(mBinding.llProgress, false)
        } else {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(playerNotificationReceiver)
            initView()
        }
    }

    val playerNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_CUSTOM_PLAY -> {
                    mBinding.ivMediaPlayPause.performClick()
                }
                ACTION_CUSTOM_PAUSE -> {
                    mBinding.ivMediaPlayPause.performClick()
                }
                else -> {

                }
            }
        }
    }

    private fun setupToolbar() {
        mBinding.toolbar.ivBackToolbar.visible()
        //mBinding.toolbar.ivShareToolbar.visible()
    }

    private fun initRecyclerView() {
        if (isTablet(this)) {
            mBinding.rvSelectCategoryExperienceMore.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        } else {
            mBinding.rvSelectCategoryExperienceMore.layoutManager = GridLayoutManager(this, 3)
        }


        mBinding.rvVideoListExperienceMore.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mBinding.rvGuidedAudioListExperienceMore.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mBinding.rvMusicListExperienceMore.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun observeApiCallbacks() {
        viewModel.getInitialUserExperienceDetail.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { userExperienceDetail ->
                        viewModel.userExperienceDetail = userExperienceDetail
                        showUserExperienceDetails()
                        isAlreadyLoaded = true
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                    }
                }
            }
        }
    }

    override fun handleListener() {
        mBinding.nsvExperienceMore.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                if (mBinding.llSwipeUpForMore.visibility == View.VISIBLE) {
                    mBinding.llSwipeUpForMore.gone()
                    mBinding.viewMore.visible()
                }
                isPlayerVisible = false
                TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady = false
            }
            if (scrollY == 0) {
                isPlayerVisible = true
                TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady = isPlayerPlaying
            }
        })

        mBinding.btnSignUpExperienceMore.setOnClickListener {
            isMoveToPlayer = false
            WelcomeLoginActivity.startActivity(this@ExperienceMoreActivity)
            WelcomeLoginActivity.isFromLogin = false
        }

        mBinding.btnLoginExperienceMore.setOnClickListener {
            isMoveToPlayer = false
            WelcomeLoginActivity.startActivity(this@ExperienceMoreActivity)
            WelcomeLoginActivity.isFromLogin = true
        }
        mBinding.toolbar.ivShareToolbar.clickWithDebounce {
            shareContentLink()
        }

        mBinding.toolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.ivMediaFullScreen.setOnClickListener {
            TaoCalligraphy.instance.subtitleWithLanguage = subtitleWithLanguage
            TaoCalligraphy.instance.playerPlaybackSpeed = playerPlaybackSpeed
            isMoveToPlayer = true
            isFromFullScreen = true
            viewModel.userExperienceDetail?.initialContent?.let {
                FullScreenPlayerActivity.startActivity(
                    this,
                    fullScreenMediaPlayerResult,
                    it,
                    isFromFullScreen
                )
            }
        }

        mBinding.ivMediaSetting.setOnClickListener {
            showPlaybackDialog()
        }

        mBinding.ivMediaPlayPause.setOnClickListener {
            TaoCalligraphy.instance.simpleExoPlayer?.run {
                playWhenReady = !playWhenReady

                isPlayerPlaying = playWhenReady
            }
        }

        mBinding.ivSubtitle.setOnClickListener {
            viewModel.userExperienceDetail?.initialContent?.let {
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

        mBinding.sliderVideoProgress.addOnSliderTouchListener(object :
            Slider.OnSliderTouchListener {
            @SuppressLint("RestrictedApi")
            override fun onStartTrackingTouch(slider: Slider) {
            }

            @SuppressLint("RestrictedApi")
            override fun onStopTrackingTouch(slider: Slider) {
                TaoCalligraphy.instance.simpleExoPlayer?.run {
                    val currentDuration =
                        (duration * slider.value) / 100f
                    seekTo(currentDuration.toLong())
                }
            }
        })

        mBinding.playerView.videoSurfaceView?.setOnClickListener {
            showHideController()
        }
    }

    private fun shareContentLink() {
        val contentImage = viewModel.userExperienceDetail?.initialContent?.backgroundImageMobile
        shareMeditationContent(
            contentId = viewModel.userExperienceDetail?.initialContent?.id ?: "",
            contentImage = contentImage ?: "",
            contentTitle = viewModel.userExperienceDetail?.initialContent?.title
                ?: "",
            contentDescription = viewModel.userExperienceDetail?.initialContent?.description.toString(),
            url = URL_CONTENT
        )
    }

    private fun showUserExperienceDetails() {
        if (!isAlreadyLoaded) {
            viewModel.userExperienceDetail?.initialContent?.let {
                it.title.let { title ->
                    mBinding.txtUserExperienceTitle.text = title
                }

                it.description?.let { description ->
                    if (isTablet(this)) {
                        mBinding.txtUserExperienceDesc.setPropertiesAndData(description.appendHtmlForTabletTextSize22())
                    } else {
                        mBinding.txtUserExperienceDesc.setPropertiesAndData(description.appendHtml())
                    }
                }

                setVideoDataInPlayer()
                handlerMainTimer.postDelayed(runnableMainTimer, 1000)
            }
        }

        viewModel.userExperienceDetail?.let {
            if (!isAlreadyLoaded) {
                mBinding.rvSelectCategoryExperienceMore.adapter =
                    WellnessCategoryUserExperienceAdapter(it.categoryList,viewModel.selectedCategory, this)
            }

            mBinding.rvMusicListExperienceMore.adapter =
                ContentDataAdapter(it.contentDetails.musicDetail)
            mBinding.rvGuidedAudioListExperienceMore.adapter =
                ContentDataAdapter(it.contentDetails.guidedMeditationAudioDetail)
            mBinding.rvVideoListExperienceMore.adapter =
                ContentDataAdapter(it.contentDetails.videoDetail)

            if (it.contentDetails.musicDetail.isEmpty()) {
                mBinding.tvNoMusic.visible()
            } else {
                mBinding.tvNoMusic.gone()
            }

            if (it.contentDetails.guidedMeditationAudioDetail.isEmpty()) {
                mBinding.tvNoAudio.visible()
            } else {
                mBinding.tvNoAudio.gone()
            }

            if (it.contentDetails.videoDetail.isEmpty()) {
                mBinding.tvNoVideo.visible()
            } else {
                mBinding.tvNoVideo.gone()
            }
        }
    }

    private fun setExoplayerListener() {
        TaoCalligraphy.instance.simpleExoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    mBinding.ivMediaPlayPause.gone()
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                if (playWhenReady) {
                    handlerControllerHide.removeCallbacks(runnableShowHideControl)
                    handlerControllerHide.postDelayed(runnableShowHideControl, 3000)
                    mBinding.ivMediaPlayPause.setImageResource(R.drawable.vd_playing)
                } else {
                    handlerControllerHide.removeCallbacks(runnableShowHideControl)
                    mBinding.ivMediaPlayPause.setImageResource(R.drawable.vd_pause)
                }
            }
        })
    }

    private fun showHideController() {
        if (mBinding.navigationView.isVisible) {
            mBinding.navigationView.gone()
            mBinding.ivMediaPlayPause.gone()
            handlerControllerHide.removeCallbacks(runnableShowHideControl)
        } else {
            mBinding.navigationView.visible()
            mBinding.ivMediaPlayPause.visible()
            handlerControllerHide.postDelayed(runnableShowHideControl, 3000)
        }
    }

    private val runnableShowHideControl: Runnable = Runnable {
        if ((!isFinishing)) {
            mBinding.navigationView.gone()
            mBinding.ivMediaPlayPause.gone()
        }
    }

    private fun openChooseSubtitleDialog() {
        viewModel.userExperienceDetail?.initialContent?.let {
            var dialog: ChooseSubTitleDialog? = null
            dialog = ChooseSubTitleDialog(
                this,
                it.subtitleWithLanguages,
                object : ChooseSubTitleDialog.SubtitleSelectionListener {
                    override fun onSubtitleSelect(subtitle: MeditationContentResponse.SubtitleWithLanguage?) {
                        dialog?.dismiss()
                        setSubtitleAndShow(subtitle)
                    }
                }
            )
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

    private fun setVideoDataInPlayer() {
        mBinding.playerView.subtitleView?.gone()
        val captionStyleCompat = CaptionStyleCompat(
            Color.WHITE,
            Color.TRANSPARENT,
            Color.TRANSPARENT,
            CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
            Color.BLACK,
            ResourcesCompat.getFont(this, R.font.font_opensans_bold)
        )
        mBinding.subTitleView.setStyle(captionStyleCompat)
        mBinding.playerView.player = null

        TaoCalligraphy.instance.simpleExoPlayer?.run {
            setMediaSource(getTrackWiseMediaSource())
            prepare()

            seekTo(0, C.TIME_UNSET)
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            playWhenReady = TaoCalligraphy.instance.isPlayerPlaying
            playbackSpeed = playerPlaybackSpeed
            mBinding.playerView.player = TaoCalligraphy.instance.simpleExoPlayer

            (mBinding.playerView.player as ExoPlayer).addListener(mBinding.subTitleView)
            mBinding.subTitleView.setCues((mBinding.playerView.player as ExoPlayer).currentCues)
        }
        if (mIsBound && playerNotificationService != null) {
            viewModel.userExperienceDetail?.initialContent?.let {
                playerNotificationService?.updateMediaContent(
                    it
                )
            }
        } else {
            startNotificationService()
        }
    }

    private fun startNotificationService() {
        stopNotificationService()
        notificationServiceIntent = Intent(this, ExoNotificationService::class.java)
            .putExtra(Constants.MeditationContent, viewModel.userExperienceDetail?.initialContent)

        notificationServiceIntent?.putExtra(
            Constants.FROM,
            ExoNotificationService.PLAYER_FROM_EXPERIENCE_MORE
        )

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

    private fun getTrackWiseMediaSource(): MergingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()

        val mediaSource =
            HlsMediaSource.Factory(DefaultHttpDataSource.Factory().setUserAgent(userAgent))
                .createMediaSource(MediaItem.fromUri(Uri.parse(viewModel.userExperienceDetail?.initialContent?.contentFileForHls)))
        concatenatingMediaSource.addMediaSource(mediaSource)

        val dataSourceFactory: DataSource.Factory =
            DefaultHttpDataSource.Factory().setUserAgent(Util.getUserAgent(this, packageName))

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
            val itemConfig = MediaItem.SubtitleConfiguration.Builder(uriSubtitle)
                .setMimeType(mimeType)
                .setLanguage(getString(R.string.language_en))
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                .setRoleFlags(C.ROLE_FLAG_SUBTITLE)
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
    private fun setPlayerTime() {
        TaoCalligraphy.instance.simpleExoPlayer?.run {
            if (duration < 0) {
                mBinding.sliderVideoProgress.isEnabled = false
                return
            }
            val currentTime = contentPosition
            val totalDuration = duration
            val currentProgress = (100f * currentTime) / totalDuration
            if (currentProgress > mBinding.sliderVideoProgress.valueTo) {
                mBinding.sliderVideoProgress.value = mBinding.sliderVideoProgress.valueTo
            } else {
                mBinding.sliderVideoProgress.value = currentProgress
            }

            val time = "${currentTime.getDurationTime()} / ${totalDuration.getDurationTime()}"
            mBinding.tvMusicTime.text = time
            mBinding.sliderVideoProgress.isEnabled = true
        }
    }

    var handlerMainTimer = Handler(Looper.getMainLooper())
    private val runnableMainTimer: Runnable = object : Runnable {
        override fun run() {
            if ((!isFinishing)) {
                setPlayerTime()
                handlerMainTimer.postDelayed(this, (1000 / playerPlaybackSpeed).toLong())
            }
        }
    }

    private fun setSubtitleView() {
        if (subtitleWithLanguage == null) {
            mBinding.subTitleView.gone()
            mBinding.ivSubtitle.setImageResource(R.drawable.vd_subtitle)
        } else {
            mBinding.subTitleView.visible()
            mBinding.ivSubtitle.setImageResource(R.drawable.vd_subtitle_on)
        }
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
                    it1,
                    it.measuredHeight
                )
            }
        }
        settingPopupWindow?.showAtLocation(
            mBinding.cardPlayer,
            Gravity.TOP,
            0,
            location[1] - ((size?.height ?: 0) - mBinding.cardPlayer.height) / 2
        )
        settingPopupWindow?.dimBehind()
        settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(this, playerPlaybackSpeed)
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
            playerPlaybackSpeed = 0.5f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(this, playerPlaybackSpeed)
            updatePlayerPlaybackSpeed()
        }
        tv75Speed.setOnClickListener {
            playerPlaybackSpeed = 0.75f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(this, playerPlaybackSpeed)
            updatePlayerPlaybackSpeed()
        }
        tvNormalSpeed.setOnClickListener {
            playerPlaybackSpeed = 1f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(this, playerPlaybackSpeed)
            updatePlayerPlaybackSpeed()
        }
        tv15Speed.setOnClickListener {
            playerPlaybackSpeed = 1.5f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(this, playerPlaybackSpeed)
            updatePlayerPlaybackSpeed()
        }
        tv2Speed.setOnClickListener {
            playerPlaybackSpeed = 2f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(this, playerPlaybackSpeed)
            updatePlayerPlaybackSpeed()
        }
        ivClose.setOnClickListener {
            settingPopupWindow?.dismiss()
        }
        tvSlower.setOnClickListener {
            when (playerPlaybackSpeed) {
                0.75f -> {
                    playerPlaybackSpeed = 0.5f
                }
                1f -> {
                    playerPlaybackSpeed = 0.75f
                }
                1.5f -> {
                    playerPlaybackSpeed = 1f
                }
                2f -> {
                    playerPlaybackSpeed = 1.5f
                }
            }
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(this, playerPlaybackSpeed)
            updatePlayerPlaybackSpeed()
        }
        tvFaster.setOnClickListener {
            when (playerPlaybackSpeed) {
                0.5f -> {
                    playerPlaybackSpeed = 0.75f
                }
                0.75f -> {
                    playerPlaybackSpeed = 1f
                }
                1f -> {
                    playerPlaybackSpeed = 1.5f
                }
                1.5f -> {
                    playerPlaybackSpeed = 2f
                }
            }
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(this, playerPlaybackSpeed)
            updatePlayerPlaybackSpeed()
        }

        val width = (mBinding.playerView.width * 0.90).toInt()
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
            playbackSpeed = playerPlaybackSpeed
            mBinding.playerView.player =
                TaoCalligraphy.instance.simpleExoPlayer
        }
    }

    override fun onResume() {
        if (isPause) {
            mBinding.playerView.player = null
            mBinding.playerView.player = TaoCalligraphy.instance.simpleExoPlayer
            TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady =
                TaoCalligraphy.instance.isPlayerPlaying
        } else {
            if (isAlreadyLoaded) {
                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    onConfigurationChanged(Configuration())
                }
                if (!isFromFullScreen && !isMoveToPlayer) {
                    TaoCalligraphy.instance.isPlayerPlaying = true
                    setVideoDataInPlayer()
                }
            }
        }
        if (isAlreadyLoaded)
            handlerMainTimer.postDelayed(runnableMainTimer, 1000)
        isPause = false
        isMoveToPlayer = false
        super.onResume()
    }

    override fun onPause() {
        handlerMainTimer.removeCallbacks(runnableMainTimer)
        val play = TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady ?: false
//        TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady = false
        TaoCalligraphy.instance.isPlayerPlaying = play
        if (!isMoveToPlayer)
            isPause = true
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerControllerHide.removeCallbacks(runnableShowHideControl)
        stopNotificationService()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerNotificationReceiver)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
        if (model.isAvailable && !isPause) {
            TaoCalligraphy.instance.isPlayerPlaying = true
            setVideoDataInPlayer()
            setSubtitleView()
            noInternetConnectionDialog?.dismiss()
        } else {
            if (!isFinishing) {
                showNoInternetDialog()
            }
        }
    }

    override fun onCategoryClicked(categoryId: Int) {
        viewModel.selectedCategory = categoryId
        viewModel.getInitialUserExperienceDetail(categoryId, this, mDisposable)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        isFromFullScreen = false
        when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                if (!FullScreenPlayerActivity.isOrientationButtonClicked) {
                    if (isAlreadyLoaded) {
                        TaoCalligraphy.instance.subtitleWithLanguage = subtitleWithLanguage
                        TaoCalligraphy.instance.playerPlaybackSpeed = playerPlaybackSpeed
                        isMoveToPlayer = true
                        FullScreenPlayerActivity.startActivity(
                            this,
                            fullScreenMediaPlayerResult,
                            viewModel.userExperienceDetail?.initialContent,
                            isFromFullScreen
                        )
                    }
                } else {
                    isFromFullScreen = false
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
                }
            }
        }

        super.onConfigurationChanged(newConfig)
    }

    private fun setOrientationListener() {
        orientationEventListener = object : OrientationEventListener(this@ExperienceMoreActivity) {
            override fun onOrientationChanged(orientation: Int) {
                val newOrientation = when (orientation) {
                    in 0..44 -> 0
                    in 45..134 -> 1
                    in 135..224 -> 2
                    in 225..314 -> 3
                    in 315..359 -> 0
                    else -> ORIENTATION_UNKNOWN
                }
                if (newOrientation != previousOrientation) {
                    if (newOrientation == 0) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            FullScreenPlayerActivity.isOrientationButtonClicked = false
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
                        }, 500)
                    }
                }
                previousOrientation = newOrientation
            }
        }
        orientationEventListener.enable()
    }
}