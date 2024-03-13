package com.app.taocalligraphy.ui.meditation_timer

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityPlayMeditationTimerBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.meditation_timer.MeditationListApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.meditation_timer.viewmodel.MeditationTimerViewModel
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImage
import com.app.taocalligraphy.utils.loadImageWithBlur
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class PlayMeditationTimerActivity : BaseActivity<ActivityPlayMeditationTimerBinding>(),
    View.OnClickListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnSeekCompleteListener {

    private val mViewModel: MeditationTimerViewModel by viewModels()

    val mHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val meditationId by lazy {
        return@lazy intent.extras?.getString(Constants.Param.meditationTimerId, "") ?: ""
    }
    private val isFromNotification by lazy {
        return@lazy intent.extras?.getBoolean(Constants.Param.isFromNotification, false) ?: false
    }

    companion object {
        private var meditationId: String? = ""
        fun startActivity(
            activity: AppCompatActivity,
            meditationId: String?,
            meditationDetail: MeditationListApiResponse.MeditationDetail?,
            isFromNotification: Boolean = false
        ) {
            val intent = Intent(activity, PlayMeditationTimerActivity::class.java)
            this.meditationId = meditationId
            intent.putExtra(Constants.Param.meditationList, meditationDetail)
            intent.putExtra(Constants.Param.meditationTimerId, meditationId)
            intent.putExtra(Constants.Param.isFromNotification, isFromNotification)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_play_meditation_timer

    override fun initView() {
        registerReceiver(
            onDownloadComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )

        setupToolbar()

        if (isFromNotification) {
            callFetchMeditationTimerDetailById(meditationId)
        } else {
            setData()
            setTimerData(intent.getSerializableExtra(Constants.Param.meditationList) as MeditationListApiResponse.MeditationDetail)
        }

        mBinding.zenModeView.btnDontShowAgain.setOnClickListener {
            mViewModel.isZenModeDontShow = true
            mBinding.dialogZenMode.gone()
        }
        mBinding.zenModeView.ivCloseDialog.setOnClickListener {
            mBinding.dialogZenMode.gone()
        }
    }

    private fun callFetchMeditationTimerDetailById(meditationId: String) {
        mViewModel.fetchMeditationTimerDetailByIDApi(
            meditationId,
            this@PlayMeditationTimerActivity,
            mDisposable
        )
    }


    private fun showZenModeDialog() {
        mBinding.dialogZenMode.visible()
        Handler(Looper.getMainLooper()).postDelayed({
            mBinding.dialogZenMode.gone()
        }, 5000)
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
    }

    private fun setData() {
        mBinding.ivPlayOrPause.setOnClickListener(this)
        mBinding.tvEnd.setOnClickListener(this)
        mBinding.tvRestart.setOnClickListener(this)
        mBinding.circularProgressDailyActivityChart.progress = 0.0f
        mBinding.ivPlayOrPause.visible()
        mBinding.ivPlayOrPause.setImageResource(R.drawable.vd_meditation_timer_play)
        mBinding.fmDone.gone()

    }

    private fun setMediaPlayer() {
        if (!mViewModel.isConfigChange) {
            val b = AudioAttributes.Builder()
            b.setUsage(AudioAttributes.USAGE_MEDIA)
            b.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            mViewModel.mediaPlayer.setAudioAttributes(b.build())
            mViewModel.mediaPlayer.setDataSource(mViewModel.arrayMusic?.get(mViewModel.mediaPosition))
            mViewModel.mediaPlayer.setOnPreparedListener(this)
            mViewModel.mediaPlayer.setOnCompletionListener(this)
            mViewModel.mediaPlayer.setOnErrorListener(this)
            mViewModel.mediaPlayer.setOnSeekCompleteListener(this)
            // sync at that time play audio
            mViewModel.mediaPlayer.prepareAsync()
        } else {
            mViewModel.mediaPlayer.setOnPreparedListener(this)
            mViewModel.mediaPlayer.setOnCompletionListener(this)
            mViewModel.mediaPlayer.setOnErrorListener(this)
            mViewModel.mediaPlayer.setOnSeekCompleteListener(this)

            mHandler.removeCallbacks(mUpdateTimeTask)
            if (mViewModel.mediaPlayer.isPlaying) {
                mHandler.post(mUpdateTimeTask)
                mViewModel.timerExt.start()
                mBinding.ivPlayOrPause.setImageResource(R.drawable.vd_meditation_timer_pause)
            } else {
                mViewModel.timerExt.pause()
                mBinding.ivPlayOrPause.setImageResource(R.drawable.vd_meditation_timer_play)
            }
        }
        // sync full audio then play
        mViewModel.firstSongDuration = getDurationOfTheSong(0)
        mViewModel.secondSongDuration = getDurationOfTheSong(1)
        mViewModel.thirdSongDuration = getDurationOfTheSong(2)

        mViewModel.midSongDuration =
            mViewModel.totalSongDuration - (mViewModel.firstSongDuration + mViewModel.thirdSongDuration)
        mViewModel.isPlayingDuration = "00:${(milliSecondsToTimer(0))}"
        mViewModel.isConfigChange = false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivPlayOrPause -> {
                if (mViewModel.mediaPlayer.isPlaying) {
                    Constants.NOTIFICATION_ON_OFF = true
                    mViewModel.currentDuration = mViewModel.mediaPlayer.currentPosition
                    mViewModel.mediaPlayer.pause()
                    mHandler.removeCallbacks(mUpdateTimeTask)
                    mViewModel.timerExt.pause()
                    mBinding.ivPlayOrPause.setImageResource(R.drawable.vd_meditation_timer_play)

                } else {
                    mViewModel.isPlayingMusic = true
                    Constants.NOTIFICATION_ON_OFF = false
                    mViewModel.mediaPlayer.seekTo(mViewModel.currentDuration)
                    mViewModel.mediaPlayer.start()
                    mHandler.removeCallbacks(mUpdateTimeTask)
                    mHandler.post(mUpdateTimeTask)
                    mViewModel.timerExt.start()
                    mBinding.ivPlayOrPause.setImageResource(R.drawable.vd_meditation_timer_pause)
                    if (!mViewModel.isZenModeDontShow && userHolder.isZenModeForMeditation)
                        showZenModeDialog()
                }
            }
            R.id.tvEnd -> {
                onBackPressed()
            }
            R.id.tvRestart -> {
                if (isNetwork()) {
                    mViewModel.isMeditationCompleted = false
                    mViewModel.onTimerFinished.postValue(false)
                    if (SystemClock.elapsedRealtime() - mViewModel.mLastClickTime < 1000) {
                        return
                    }
                    mViewModel.mLastClickTime = SystemClock.elapsedRealtime()
                    Constants.NOTIFICATION_ON_OFF = true
                    mViewModel.playNext = true
                    mBinding.circularProgressDailyActivityChart.progress = 0.0f
                    mBinding.ivPlayOrPause.visible()
                    mBinding.fmDone.gone()
                    mViewModel.meditationRemainingTime = mViewModel.totalSongDuration.toLong()
                    mViewModel.mediaPlayer.reset()
                    mViewModel.mediaPlayer.setDataSource(mViewModel.arrayMusic?.get(mViewModel.mediaPosition))
                    mViewModel.mediaPlayer.prepareAsync()
                } else
                    longToast(
                        getString(
                            R.string.no_internet,
                            getString(R.string.to_restart_meditation_timer)
                        ),
                        Constants.ERROR
                    )
            }
        }
    }

    override fun observeApiCallbacks() {
        mViewModel.fetchMeditationTimerDetailByIDResponse.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.data?.let { fetchMeditationTimerDataById ->
                    setData()
                    setTimerData(fetchMeditationTimerDataById)
                }
                longToastState(requestState.error)
            }
        }

        mViewModel.onTimerFinished.observe(this) {
            if (it) {
                mBinding.tvTime.text = "00:00"
                mBinding.circularProgressDailyActivityChart.progress = 100f
            }
        }


        mViewModel.onTimerUpdated.observe(this) {percentage ->
            mBinding.tvTime.text = milliSecondsToTimer(mViewModel.meditationRemainingTime)

                if (percentage < 100)
                    mBinding.circularProgressDailyActivityChart.progress = percentage.toFloat()
                else
                    mBinding.circularProgressDailyActivityChart.progress = 100f
        }
    }

    private fun setTimerData(data: MeditationListApiResponse.MeditationDetail) {
        mBinding.imgMeditationTimerBack.loadImage(
            data.backgroundImage?.backgroundImageOriginal,
            R.drawable.bg_placeholder_meditation_timer
        )

        mBinding.imgMeditationTimerBg?.loadImageWithBlur(
            data.backgroundImage?.backgroundImageOriginal,
            R.drawable.bg_placeholder_meditation_timer
        )

        mBinding.tvTime.text = data.meditationTime?.toLong()
            ?.let { TimeUnit.MINUTES.toMillis(it) }?.let { milliSecondsToTimer(it) }
        mBinding.tvMeditationName.text = data.name

        mViewModel.totalSongDuration =
            TimeUnit.MINUTES.toMillis(data.meditationTime?.toLong() ?: 0).toInt()

        if (mViewModel.meditationRemainingTime == 0L)
            mViewModel.meditationRemainingTime = mViewModel.totalSongDuration.toLong()

        mViewModel.totalMeditationTime = String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(mViewModel.totalSongDuration.toLong()),
            TimeUnit.MILLISECONDS.toMinutes(mViewModel.totalSongDuration.toLong()) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(mViewModel.totalSongDuration.toLong())
            ),
            TimeUnit.MILLISECONDS.toSeconds(mViewModel.totalSongDuration.toLong()) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(mViewModel.totalSongDuration.toLong())
            )
        )

        if (mViewModel.isMeditationCompleted) {
            mBinding.ivPlayOrPause.setImageResource(R.drawable.vd_meditation_timer_pause)
            mViewModel.playNext = false
            mViewModel.mediaPosition = 0
            mBinding.circularProgressDailyActivityChart.progress = 99.9f
            mBinding.ivPlayOrPause.gone()
            mBinding.fmDone.visible()
        } else if (mViewModel.startSongDownloaded && mViewModel.midSongDownloaded && mViewModel.endSongDownloaded) {
            mBinding.llProgress.gone()
            setMediaPlayer()
        } else {
            mViewModel.isConfigChange = false
            data.apply {
                mViewModel.arrayMusic = arrayListOf()
                mBinding.llProgress.visible()
                download(name, startSound, 1)
                download(name, ambientSound, 2)
                download(name, endSound, 3)
            }
        }
    }

    fun download(
        name: String?,
        data: MeditationListApiResponse.MeditationDetail.Sound?,
        songType: Int
    ) {
        try {
            val externalFile = File(externalCacheDir, Environment.DIRECTORY_MUSIC + "/$name")
            val file = File(
                externalFile,
                "${data?.name}_${data?.soundId}.${
                    contentDownloadHelper.getMimeType(
                        this,
                        Uri.parse(data?.sound)
                    )
                }"
            )
            if (file.exists()) {
                file.delete()
            }

            mViewModel.arrayMusic?.add(file.absolutePath)

            val request: DownloadManager.Request =
                DownloadManager.Request(Uri.parse(data?.sound))
                    .setTitle(data?.name) // Title of the Download Notification
                    .setDescription(data?.name) // Description of the Download Notification
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN) // Visibility of the download Notification
                    .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
                    .setDestinationUri(Uri.fromFile(file))
                    .setAllowedOverRoaming(true) // Set if download is allowed on roaming network
            val downloadManager: DownloadManager =
                application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            when (songType) {
                1 -> {
                    mViewModel.startSongDownloadRequestId =
                        downloadManager.enqueue(request)
                }
                2 -> {
                    mViewModel.midSongDownloadRequestId =
                        downloadManager.enqueue(request)
                }
                else -> {
                    mViewModel.endSongDownloadRequestId =
                        downloadManager.enqueue(request)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            when (id) {
                mViewModel.startSongDownloadRequestId -> {
                    mViewModel.startSongDownloaded = true
                }
                mViewModel.midSongDownloadRequestId -> {
                    mViewModel.midSongDownloaded = true
                }
                mViewModel.endSongDownloadRequestId -> {
                    mViewModel.endSongDownloaded = true
                }
            }
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (mViewModel.startSongDownloaded && mViewModel.midSongDownloaded && mViewModel.endSongDownloaded) {
                mBinding.llProgress.gone()
                setMediaPlayer()
            }
        }
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (mViewModel.playNext) {
            mViewModel.isMeditationCompleted = false
            try {
                mViewModel.mediaPlayer.start()
                mBinding.ivPlayOrPause.setImageResource(R.drawable.vd_meditation_timer_pause)
                mHandler.removeCallbacks(mUpdateTimeTask)
                mHandler.post(mUpdateTimeTask)
                mViewModel.timerExt.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (mViewModel.mediaPosition == 1) {
            mViewModel.isMeditationCompleted = false
            if (mViewModel.midSongDuration >= mViewModel.secondSongDuration * mViewModel.midSongRepeat) {
                mViewModel.midSongRepeat += 1
                mViewModel.mediaPlayer.seekTo(0)
                mViewModel.mediaPlayer.start()
            } else {
                playNextSong()
            }
        } else if (mViewModel.mediaPosition == 2) {
            mHandler.removeCallbacks(mUpdateTimeTask)
            mViewModel.timerExt.pause()
            mBinding.ivPlayOrPause.setImageResource(R.drawable.vd_meditation_timer_pause)
            mViewModel.playNext = false
            mViewModel.mediaPosition = 0
            mBinding.circularProgressDailyActivityChart.progress = 99.9f
            mBinding.ivPlayOrPause.gone()
            mBinding.fmDone.visible()
            updatePlayTime()
            mViewModel.isMeditationCompleted = true
        } else {
            mViewModel.isMeditationCompleted = false
            playNextSong()
        }
    }

    private fun playNextSong() {
        mViewModel.playNext = true
        try {
            mViewModel.mediaPosition += 1
            if (mViewModel.mediaPosition < (mViewModel.arrayMusic?.size ?: 0)) {
                mViewModel.mediaPlayer.reset()
                mViewModel.mediaPlayer.setDataSource(mViewModel.arrayMusic?.get(mViewModel.mediaPosition))
                mViewModel.mediaPlayer.prepareAsync()
                mViewModel.mediaPlayer.start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updatePlayTime() {
        if (isNetwork()) {
            mViewModel.userMeditationTimerPlayDetailsApi(
                mViewModel.isPlayingDuration,
                mViewModel.totalMeditationTime
            )
            mViewModel.isPlayingDuration = "00:00:00"
        } else
            longToast(
                getString(R.string.no_internet, getString(R.string.to_get_meditation_timer_detail)),
                Constants.ERROR
            )
    }

    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            try {
                val currentDuration = mViewModel.mediaPlayer.currentPosition
                if (mViewModel.mediaPosition == 1) {
                    if (mViewModel.midSongRepeat == 0) {
                        if (currentDuration >= mViewModel.midSongDuration) {
                            playNextSong()
                        }
                    } else {
                        if ((mViewModel.secondSongDuration.times(mViewModel.midSongRepeat)).plus(
                                currentDuration
                            ) >= mViewModel.midSongDuration
                        ) {
                            playNextSong()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mHandler.postDelayed(this, 100)
        }
    }

    private fun getDurationOfTheSong(position: Int): Long {
        var timeInMilliSec: Long = 0
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(mViewModel.arrayMusic?.get(position))
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            timeInMilliSec = time!!.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return timeInMilliSec
    }

    private fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = if (hours > 9)
                "$hours:"
            else
                "0$hours:"
        }

        val minutesString = if (minutes > 9)
            "$minutes"
        else
            "0$minutes"

        // Prepending 0 to seconds if it is one digit
        val secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutesString:$secondsString"

        // return timer string
        return finalTimerString
    }

    override fun onDestroy() {
        mHandler.removeCallbacks(mUpdateTimeTask)
        unregisterReceiver(onDownloadComplete)
        if (!isChangingConfigurations) {
            mViewModel.timerExt.pause()
            mViewModel.mediaPlayer.stop()
            mViewModel.mediaPlayer.release()
            updatePlayTime()
            deleteCacheFiles()
            Constants.NOTIFICATION_ON_OFF = true
        } else {
            mViewModel.isConfigChange = true
        }
        super.onDestroy()
    }

    private fun deleteCacheFiles() {
        val externalFile = File(
            externalCacheDir,
            Environment.DIRECTORY_MUSIC + "/${mBinding.tvMeditationName.text.toString()}"
        )
        externalFile.delete()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return true
    }

    override fun onSeekComplete(mp: MediaPlayer?) {
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

    override fun onBackPressed() {
        if (isFromNotification && isTaskRoot) {
            MainActivity.startActivity(this)
            finish()
        } else {
            super.onBackPressed()
        }
    }
}