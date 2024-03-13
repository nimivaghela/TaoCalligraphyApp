package com.app.taocalligraphy.ui.experience

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Size
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityFullScreenVideoPlayerBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.playbackSpeed
import com.app.taocalligraphy.ui.meditation.dialog.ChooseSubTitleDialog
import com.app.taocalligraphy.utils.extensions.*
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
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.material.slider.Slider
import okhttp3.internal.userAgent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FullScreenPlayerActivity : BaseActivity<ActivityFullScreenVideoPlayerBinding>() {

    private var isPlaying = true
    private var isFromFullScreen = false

    companion object {
        var isScreenVisible = false
        var instance: FullScreenPlayerActivity? = null
        var isOrientationButtonClicked : Boolean = false

        @JvmStatic
        fun startActivity(
            activity: AppCompatActivity,
            fullScreenMediaPlayerResult: ActivityResultLauncher<Intent>,
            meditationContentResponse: MeditationContentResponse? = null,
            mIsFromFullScreen: Boolean = false
        ) {
            val intent = Intent(activity, FullScreenPlayerActivity::class.java)
            intent.putExtra(Constants.MeditationContent, meditationContentResponse)
            intent.putExtra("isFromFullScreen", mIsFromFullScreen)
            fullScreenMediaPlayerResult.launch(intent)
        }
    }

    private fun getMeditationDataFromIntent(): MeditationContentResponse? {
        return intent.extras?.getParcelable(Constants.MeditationContent)
    }

    var meditationContent: MeditationContentResponse? = null

    override fun getResource(): Int {
        return R.layout.activity_full_screen_video_player
    }

    var handlerMainTimer = Handler(Looper.getMainLooper())
    var handlerControllerHide = Handler(Looper.getMainLooper())
    var isBackPressed = false
    private var settingPopupWindow: PopupWindow? = null

    override fun initView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        isFromFullScreen = intent.getBooleanExtra("isFromFullScreen", false)
        instance = this
        meditationContent = getMeditationDataFromIntent()
        setData()
        setVideoDataInPlayer()
        setSubtitleView()
        handlerMainTimer.removeCallbacks(runnableMainTimer)
        handlerMainTimer.postDelayed(runnableMainTimer, 1)
        handlerControllerHide.removeCallbacks(runnableShowHideControl)
        handlerControllerHide.postDelayed(runnableShowHideControl, 2000)

        requestedOrientation = if (isFromFullScreen) {
            ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        if (!TaoCalligraphy.instance.isPlayerPlaying) {
            mBinding.ivMediaPlayPlause.setImageResource(R.drawable.vd_pause)
        }

        mBinding.ffBackForwardMedia.gone()
        mBinding.ffMoveForwardMedia.gone()
    }

    private fun setData() {
        meditationContent?.let {
            mBinding.ivAudioImage.gone()
            mBinding.playerView.visible()
            mBinding.ivMediaFullScreen.visible()
            //mBinding.ivMediaSetting.visible()
        }
    }

    private fun setVideoDataInPlayer() {
        TaoCalligraphy.instance.simpleExoPlayer?.playWhenReady =
            TaoCalligraphy.instance.isPlayerPlaying
        mBinding.playerView.player = null
        mBinding.playerView.player =
            TaoCalligraphy.instance.simpleExoPlayer
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

        TaoCalligraphy.instance.simpleExoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    handlerControllerHide.removeCallbacks(runnableShowHideControl)
                    mBinding.ivMediaPlayPlause.setImageResource(R.drawable.vd_pause)
                    isPlaying = false
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                isPlaying = if (playWhenReady) {
                    handlerControllerHide.postDelayed(runnableShowHideControl, 2000)
                    mBinding.ivMediaPlayPlause.setImageResource(R.drawable.vd_playing)
                    true
                } else {
                    handlerControllerHide.removeCallbacks(runnableShowHideControl)
                    mBinding.rrMediaController.visible()
                    mBinding.ivMediaPlayPlause.setImageResource(R.drawable.vd_pause)
                    false
                }
            }
        })

        (mBinding.playerView.player as ExoPlayer).addListener(mBinding.subTitleView)
        mBinding.subTitleView.setCues((mBinding.playerView.player as ExoPlayer).currentCues)
    }

    private fun setSubtitleView() {
        if (meditationContent?.subtitleWithLanguages.isNullOrEmpty()) {
            mBinding.ivSubtitle.gone()
        } else {
            mBinding.ivSubtitle.visible()
        }

        if (TaoCalligraphy.instance.subtitleWithLanguage == null) {
            mBinding.subTitleView.isGone = true
            mBinding.ivSubtitle.setImageResource(R.drawable.vd_subtitle)
        } else {
            mBinding.subTitleView.isVisible = true
            mBinding.ivSubtitle.setImageResource(R.drawable.vd_subtitle_on)
        }
    }

    override fun observeApiCallbacks() {
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun handleListener() {
        mBinding.rrMediaController.setOnClickListener {
            showHideController()
        }

        mBinding.ivAudioImage.setOnClickListener {
            showHideController()
        }

        mBinding.playerView.videoSurfaceView?.setOnClickListener {
            showHideController()
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

        mBinding.sliderVideoProgress.setLabelFormatter { value ->
            val currentDuration =
                (((TaoCalligraphy.instance.simpleExoPlayer?.duration ?: 0) * value) / 100f).toLong()
            currentDuration.getDurationTime()
        }

        mBinding.ivMediaPlayPlause.setOnClickListener {
            TaoCalligraphy.instance.simpleExoPlayer?.run {
                playWhenReady = !playWhenReady
            }
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
            meditationContent?.let {
                if (TaoCalligraphy.instance.subtitleWithLanguage == null) {
                    if (it.subtitleWithLanguages?.size == 1) {
                        setSubtitleAndShow(it.subtitleWithLanguages.getOrNull(0))
                    } else {
                        openChooseSubtitleDialog()
                    }
                } else {
                    TaoCalligraphy.instance.subtitleWithLanguage = null
                    setSubtitleView()
                }
            }
        }

        mBinding.ivMediaFullScreen.setOnClickListener {
            isOrientationButtonClicked = true
            onBackPressed()
        }

        mBinding.ivMediaSetting.setOnClickListener {
            showPlaybackDialog()
        }
    }

    private fun showHideController() {
        if (mBinding.rrMediaController.isVisible) {
            mBinding.rrMediaController.isGone = true
            handlerControllerHide.removeCallbacks(runnableShowHideControl)
        } else {
            mBinding.rrMediaController.isVisible = true
            handlerControllerHide.postDelayed(runnableShowHideControl, 2000)
        }
    }

    fun setSubtitleAndShow(subtitle: MeditationContentResponse.SubtitleWithLanguage?) {
        TaoCalligraphy.instance.subtitleWithLanguage = subtitle
        setSubtitleView()
        TaoCalligraphy.instance.simpleExoPlayer?.run {
            val currentDuration =
                currentPosition
            setMediaSource(
                getTrackWiseMediaSource()
            )
            prepare()
            seekTo(currentDuration)
        }
    }

    private fun openChooseSubtitleDialog() {
        meditationContent?.let {
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

    private val runnableMainTimer: Runnable = object : Runnable {
        override fun run() {
            if ((!isFinishing)) {
                setPlayerTime()
                handlerMainTimer.postDelayed(
                    this,
                    (1000 / TaoCalligraphy.instance.playerPlaybackSpeed).toLong()
                )
            }
        }
    }

    private fun setPlayerTime() {
        TaoCalligraphy.instance.simpleExoPlayer?.run {
            if (duration < 0) {
//            mBinding.tvMusicTime.text = "00:00 / 00:00"
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

            mBinding.tvMusicTime.text =
                "${currentTime.getDurationTime()} / ${totalDuration.getDurationTime()}"
        }
    }

    private fun getTrackWiseMediaSource(): MergingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        val contentFile: String? = if (!meditationContent?.contentFileForDownload.isNullOrEmpty()) {
            meditationContent?.contentFileForDownload
        } else {
            meditationContent?.contentFile
        }
        val dataSourceFactory: DataSource.Factory =
            if (contentDownloadHelper.isContentDownloaded(contentFile) == true) {
                CacheDataSource.Factory().setCache(TaoCalligraphy.instance.getDownloadCache())
                    .setUpstreamDataSourceFactory(TaoCalligraphy.instance.buildDataSourceFactory())
            } else {
                DefaultHttpDataSource.Factory().setUserAgent(Util.getUserAgent(this, packageName))
            }
        val mediaSource =
            HlsMediaSource.Factory(DefaultHttpDataSource.Factory().setUserAgent(userAgent))
                .createMediaSource(
                    MediaItem.fromUri(Uri.parse(meditationContent?.contentFileForHls))
                )
        concatenatingMediaSource.addMediaSource(mediaSource)

        if (TaoCalligraphy.instance.subtitleWithLanguage == null) {
            return MergingMediaSource(
                concatenatingMediaSource
            )
        } else {
            val subTitle = TaoCalligraphy.instance.subtitleWithLanguage?.subTitleFile
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

    private val runnableShowHideControl: Runnable = Runnable {
        if ((!isFinishing)) {
            mBinding.rrMediaController.isGone = true
        }
    }

    private fun showPlaybackDialog() {
        if (settingPopupWindow == null) {
            initializeSettingPopupWindow()
        }

        val location = IntArray(2).apply {
            mBinding.ivMediaSetting.getLocationOnScreen(this)
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
            mBinding.ivMediaSetting,
            Gravity.TOP,
            0,
            location[1] - (size?.height ?: 0)
        )
        settingPopupWindow?.dimBehind()
        settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
            this,
            TaoCalligraphy.instance.playerPlaybackSpeed
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
            TaoCalligraphy.instance.playerPlaybackSpeed = 0.5f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                TaoCalligraphy.instance.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        tv75Speed.setOnClickListener {
            TaoCalligraphy.instance.playerPlaybackSpeed = 0.75f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                TaoCalligraphy.instance.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        tvNormalSpeed.setOnClickListener {
            TaoCalligraphy.instance.playerPlaybackSpeed = 1f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                TaoCalligraphy.instance.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        tv15Speed.setOnClickListener {
            TaoCalligraphy.instance.playerPlaybackSpeed = 1.5f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                TaoCalligraphy.instance.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        tv2Speed.setOnClickListener {
            TaoCalligraphy.instance.playerPlaybackSpeed = 2f
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                TaoCalligraphy.instance.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        ivClose.setOnClickListener {
            settingPopupWindow?.dismiss()
        }
        tvSlower.setOnClickListener {
            when (TaoCalligraphy.instance.playerPlaybackSpeed) {
                0.75f -> {
                    TaoCalligraphy.instance.playerPlaybackSpeed = 0.5f
                }
                1f -> {
                    TaoCalligraphy.instance.playerPlaybackSpeed = 0.75f
                }
                1.5f -> {
                    TaoCalligraphy.instance.playerPlaybackSpeed = 1f
                }
                2f -> {
                    TaoCalligraphy.instance.playerPlaybackSpeed = 1.5f
                }
            }
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                TaoCalligraphy.instance.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }
        tvFaster.setOnClickListener {
            when (TaoCalligraphy.instance.playerPlaybackSpeed) {
                0.5f -> {
                    TaoCalligraphy.instance.playerPlaybackSpeed = 0.75f
                }
                0.75f -> {
                    TaoCalligraphy.instance.playerPlaybackSpeed = 1f
                }
                1f -> {
                    TaoCalligraphy.instance.playerPlaybackSpeed = 1.5f
                }
                1.5f -> {
                    TaoCalligraphy.instance.playerPlaybackSpeed = 2f
                }
            }
            settingPopupWindow?.contentView?.setSelectedPlaybackSpeed(
                this,
                TaoCalligraphy.instance.playerPlaybackSpeed
            )
            updatePlayerPlaybackSpeed()
        }

//        val width = (mBinding.playerView.width * 0.90).toInt()
//        settingPopupWindow = PopupWindow(view, width, ViewGroup.LayoutParams.WRAP_CONTENT)
        settingPopupWindow = PopupWindow(
            view,
            resources.getDimension(R.dimen._250sdp).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        settingPopupWindow?.isOutsideTouchable = true
        settingPopupWindow?.isFocusable = true
        settingPopupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        popupWindow.showAtLocation(mBinding.ivMediaSetting, Gravity.TOP, 0,
//            mBinding.ivMediaSetting.y.toInt()
//        )
    }

    private fun updatePlayerPlaybackSpeed() {
        TaoCalligraphy.instance.simpleExoPlayer?.run {
            playbackSpeed = TaoCalligraphy.instance.playerPlaybackSpeed
            mBinding.playerView.player =
                TaoCalligraphy.instance.simpleExoPlayer
        }
    }

    override fun onBackPressed() {
        isBackPressed = true
        overridePendingTransition(0, 0)
        finish()
    }

    override fun onPause() {
        TaoCalligraphy.instance.simpleExoPlayer?.run {
            val play = playWhenReady
            if (!isBackPressed) {
//                playWhenReady = false
                this@FullScreenPlayerActivity.isPlaying = play
            }
            TaoCalligraphy.instance.isPlayerPlaying = play
        }
        super.onPause()
    }

    override fun onStart() {
        isScreenVisible = true
        super.onStart()
    }

    override fun onDestroy() {
        handlerMainTimer.removeCallbacks(runnableMainTimer)
        handlerControllerHide.removeCallbacks(runnableShowHideControl)
        instance = null
        super.onDestroy()
    }

    override fun onResume() {
        TaoCalligraphy.instance.simpleExoPlayer?.run {
            if (isPlaying)
                playWhenReady = TaoCalligraphy.instance.isPlayerPlaying
        }
        super.onResume()
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            onBackPressed()
        } else if (isFromFullScreen) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        super.onConfigurationChanged(newConfig)
    }
}