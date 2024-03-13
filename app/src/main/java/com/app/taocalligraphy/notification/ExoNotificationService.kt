package com.app.taocalligraphy.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.experience.BeginExperienceActivity
import com.app.taocalligraphy.ui.experience.ExperienceMoreActivity
import com.app.taocalligraphy.ui.experience.tablet.BeginExperienceTabletActivity
import com.app.taocalligraphy.ui.experience.tablet.ExperienceMoreTabletActivity
import com.app.taocalligraphy.ui.meditation.StartPlayerActivity
import com.app.taocalligraphy.ui.meditation.StartPlayerTabletActivity
import com.app.taocalligraphy.utils.extensions.isTablet
import com.app.taocalligraphy.utils.extensions.logError
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.CustomActionReceiver


class ExoNotificationService : Service() {

    var playerNotificationManager: PlayerNotificationManager? = null
    private var meditationContent: MeditationContentResponse? = null
    private var player_from: String? = null

    //    private var contentImageBitmap: Bitmap? = null
    private var notificationId = 123
    private var PLAYER_NOTIFICATION_GROUP_KEY = "Tao Music"
    private var PLAYER_NOTIFICATION_CHANNEL_ID = "Tao Music"
    private var PLAYER_NOTIFICATION_CHANNEL_NAME = "Tao Music Channel"

    companion object {

        var enablePreviousAction = false
        var enableNextAction = false

        var PLAYER_FROM_PLAYLIST = "PLAYER_FROM_PLAYLIST"
        var PLAYER_FROM_CONTENT = "PLAYER_FROM_CONTENT"
        var PLAYER_FROM_BEGIN_EXPERIENCE = "PLAYER_FROM_BEGIN_EXPERIENCE"
        var PLAYER_FROM_EXPERIENCE_MORE = "PLAYER_FROM_EXPERIENCE_MORE"

        var ACTION_CUSTOM_PREVIOUS = "ACTION_CUSTOM_PREVIOUS"
        var ACTION_CUSTOM_PREVIOUS_DISABLED = "ACTION_CUSTOM_PREVIOUS_DISABLED"
        var ACTION_CUSTOM_NEXT = "ACTION_CUSTOM_NEXT"
        var ACTION_CUSTOM_NEXT_DISABLED = "ACTION_CUSTOM_NEXT_DISABLED"
        var ACTION_CUSTOM_STOP = "ACTION_CUSTOM_STOP"
        var ACTION_CUSTOM_PLAY = "ACTION_CUSTOM_PLAY"
        var ACTION_CUSTOM_PAUSE = "ACTION_CUSTOM_PAUSE"
        var ACTION_CUSTOM_REWIND = "ACTION_CUSTOM_REWIND"
        var ACTION_CUSTOM_FAST_FORWARD = "ACTION_CUSTOM_FAST_FORWARD"
    }

    fun updateMediaContent(
        meditationContent: MeditationContentResponse,
        enablePreviousAction1: Boolean = false,
        enableNextAction1: Boolean = false
    ) {
        this.meditationContent = meditationContent
        enablePreviousAction = enablePreviousAction1
        enableNextAction = enableNextAction1

//        setPlayerNotification()
//        logError(msg = "updateMediaContent: player_from $player_from")
//        playerNotificationManager?.let {
//            it.setUseFastForwardAction(false)
//            it.setUseFastForwardActionInCompactView(false)
//            it.setUseRewindAction(false)
//            it.setUseRewindActionInCompactView(false)
//            it.setUsePlayPauseActions(false)
//            it.setUseNextAction(false)
//            it.setUseNextActionInCompactView(false)
//            it.setUsePreviousAction(false)
//            it.setUsePreviousActionInCompactView(false)
//            it.invalidate()
//        }
    }

    private val binder: IBinder = MainServiceBinder()

    override fun onBind(intent: Intent?): IBinder? {
        meditationContent =
            intent?.getParcelableExtra<MeditationContentResponse>(Constants.MeditationContent)
        player_from = intent?.getStringExtra(Constants.FROM)
        enablePreviousAction = intent?.getBooleanExtra("enablePreviousAction", false) == true
        enableNextAction = intent?.getBooleanExtra("enableNextAction", false) == true

        setPlayerNotification()
        return binder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
//        contentImageBitmap?.recycle()
        playerNotificationManager?.setPlayer(null)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    private fun setPlayerNotification() {
        val notificationManager =
            getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

//        logError(msg = "setPlayerNotification: player_from $player_from")
        playerNotificationManager = CustomPlayerNotificationManager(
            this,
            PLAYER_NOTIFICATION_CHANNEL_ID,
            notificationId,
            mediaDescriptionAdapter,
            notificationListener,
            customActionReceiver,
            R.drawable.vd_calligraphy_notification,
            R.drawable.exo_notification_play,
            R.drawable.exo_notification_pause,
            R.drawable.exo_notification_stop,
            R.drawable.exo_notification_rewind,
            R.drawable.exo_notification_fastforward,
            R.drawable.exo_notification_previous,
            R.drawable.exo_notification_next,
            PLAYER_NOTIFICATION_GROUP_KEY,
            usePreviousAction = false,//player_from.equals(PLAYER_FROM_PLAYLIST),
            useNextAction = false,//player_from.equals(PLAYER_FROM_PLAYLIST),
            usePreviousActionInCompactView = player_from.equals(PLAYER_FROM_PLAYLIST),
            useNextActionInCompactView = player_from.equals(PLAYER_FROM_PLAYLIST),
            usePlayPauseActions = false,
            useRewindAction = false,
            useFastForwardAction = false,
            useRewindActionInCompactView = true,
            useFastForwardActionInCompactView = true,
            useStopAction = false,
        )
//        playerNotificationManager = PlayerNotificationManager.Builder(
//            this,
//            notificationId,
//            PLAYER_NOTIFICATION_CHANNEL_ID
//        )
//            .setMediaDescriptionAdapter(mediaDescriptionAdapter)
//            .setNotificationListener(notificationListener)
//            .setSmallIconResourceId(R.drawable.vd_calligraphy_notification)
////            .setCustomActionReceiver(customActionReceiver)
//            .build()

        playerNotificationManager?.let {
            val mediaSession = MediaSessionCompat(this, getString(R.string.app_name))
            mediaSession.isActive = true
            it.setMediaSessionToken(mediaSession.sessionToken)
            val mediaSessionConnector = MediaSessionConnector(mediaSession)

//            val title = meditationContent?.title
//            val description = meditationContent?.description?.getHtmlString()
//            mediaSessionConnector.setQueueNavigator(
//                object : TimelineQueueNavigator(mediaSession, 10) {
//                    override fun getMediaDescription(
//                        player: Player,
//                        windowIndex: Int
//                    ): MediaDescriptionCompat {
//                        return MediaDescriptionCompat.Builder()
//                            .setTitle(title)
//                            .setDescription(description)
//                            .build()
//                    }
//                })
            mediaSessionConnector.setClearMediaItemsOnStop(true)
            mediaSessionConnector.setPlayer(TaoCalligraphy.instance.simpleExoPlayer)

            it.setSmallIcon(R.drawable.vd_calligraphy_notification)
            it.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            it.setPlayer(TaoCalligraphy.instance.simpleExoPlayer)
            it.invalidate()
        }
    }

    private fun loadBitmap(url: String, callback: PlayerNotificationManager.BitmapCallback?) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    callback?.onBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    private val mediaDescriptionAdapter =
        object : PlayerNotificationManager.MediaDescriptionAdapter {
            override fun getCurrentContentTitle(player: Player): CharSequence {
                return meditationContent?.title.toString()
            }

            override fun createCurrentContentIntent(player: Player): PendingIntent? {

                lateinit var intent: Intent

                if (player_from.equals(PLAYER_FROM_BEGIN_EXPERIENCE)) {
                    intent = if (isTablet(TaoCalligraphy.instance)) {
                        Intent(
                            applicationContext,
                            BeginExperienceTabletActivity::class.java
                        )
                    } else {
                        Intent(
                            applicationContext,
                            BeginExperienceActivity::class.java
                        )
                    }
                    intent.putExtra("userExperience", meditationContent)
                } else if (player_from.equals(PLAYER_FROM_EXPERIENCE_MORE)) {
                    intent = if (isTablet(TaoCalligraphy.instance)) {
                        Intent(
                            applicationContext,
                            ExperienceMoreTabletActivity::class.java
                        )
                    } else {
                        Intent(
                            applicationContext,
                            ExperienceMoreActivity::class.java
                        )
                    }
                } else {
                    intent = if (isTablet(TaoCalligraphy.instance)) {
                        Intent(
                            applicationContext,
                            StartPlayerTabletActivity::class.java
                        )
                    } else {
                        Intent(
                            applicationContext,
                            StartPlayerActivity::class.java
                        )
                    }
                    intent.putExtra(Constants.MeditationContent, meditationContent)
                }
                intent.putExtra(Constants.FROM, Constants.FROM_PLAYER_NOTIFICATION)
                return PendingIntent.getActivity(
                    applicationContext, 12, intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                )
            }

            override fun getCurrentContentText(player: Player): CharSequence? {
//            return meditationContent?.description?.getHtmlString()
                return null
            }

            override fun getCurrentLargeIcon(
                player: Player,
                callback: PlayerNotificationManager.BitmapCallback
            ): Bitmap? {
//            return contentImageBitmap
                meditationContent?.backgroundImageMobile?.let { loadBitmap(it, callback) }
                return null //or stub image
//                return BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_foreground)
            }

        }

    private val notificationListener = object : PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
//            logError(msg = "onNotificationPosted $ongoing")
            super.onNotificationPosted(notificationId, notification, ongoing)
            try {
                if (ongoing) startForeground(
                    notificationId,
                    notification
                ) else stopForeground(false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onNotificationCancelled(
            notificationId: Int,
            dismissedByUser: Boolean
        ) {
//            logError(msg = "onNotificationCancelled")
            super.onNotificationCancelled(notificationId, dismissedByUser)
        }
    }

    val customActionReceiver = object : CustomActionReceiver {
        override fun createCustomActions(
            context: Context,
            instanceId: Int
        ): MutableMap<String, NotificationCompat.Action> {
            val actionMap: MutableMap<String, NotificationCompat.Action> = HashMap()

            if (player_from.equals(PLAYER_FROM_PLAYLIST)) {
                actionMap[ACTION_CUSTOM_PREVIOUS] = NotificationCompat.Action(
                    R.drawable.exo_controls_previous,
                    ACTION_CUSTOM_PREVIOUS,
                    createPendingIntent(
                        context,
                        instanceId,
                        ACTION_CUSTOM_PREVIOUS,
                        enablePreviousAction
                    )
                )

                actionMap[ACTION_CUSTOM_PREVIOUS_DISABLED] = NotificationCompat.Action(
                    R.drawable.exo_controls_previous_disabled,
                    ACTION_CUSTOM_PREVIOUS_DISABLED,
                    createPendingIntent(
                        context,
                        instanceId,
                        ACTION_CUSTOM_PREVIOUS_DISABLED,
                        enablePreviousAction
                    )
                )
            }

            if (!player_from.equals(PLAYER_FROM_BEGIN_EXPERIENCE) && !player_from.equals(
                    PLAYER_FROM_EXPERIENCE_MORE
                )
            ) {
                actionMap[ACTION_CUSTOM_REWIND] = NotificationCompat.Action(
                    R.drawable.exo_notification_rewind,
                    ACTION_CUSTOM_REWIND,
                    createPendingIntent(context, instanceId, ACTION_CUSTOM_REWIND)
                )
            }

            actionMap[ACTION_CUSTOM_PLAY] = NotificationCompat.Action(
                R.drawable.exo_notification_play,
                ACTION_CUSTOM_PLAY,
                createPendingIntent(context, instanceId, ACTION_CUSTOM_PLAY)
            )

            actionMap[ACTION_CUSTOM_PAUSE] = NotificationCompat.Action(
                R.drawable.exo_notification_pause,
                ACTION_CUSTOM_PAUSE,
                createPendingIntent(context, instanceId, ACTION_CUSTOM_PAUSE)
            )

            if (!player_from.equals(PLAYER_FROM_BEGIN_EXPERIENCE) && !player_from.equals(
                    PLAYER_FROM_EXPERIENCE_MORE
                )
            ) {
                actionMap[ACTION_CUSTOM_FAST_FORWARD] = NotificationCompat.Action(
                    R.drawable.exo_controls_fastforward,
                    ACTION_CUSTOM_FAST_FORWARD,
                    createPendingIntent(context, instanceId, ACTION_CUSTOM_FAST_FORWARD)
                )
            }

            if (player_from.equals(PLAYER_FROM_PLAYLIST)) {
                actionMap[ACTION_CUSTOM_NEXT] = NotificationCompat.Action(
                    R.drawable.exo_controls_next,
                    ACTION_CUSTOM_NEXT,
                    createPendingIntent(context, instanceId, ACTION_CUSTOM_NEXT, enableNextAction)
                )

                actionMap[ACTION_CUSTOM_NEXT_DISABLED] = NotificationCompat.Action(
                    R.drawable.exo_controls_next_disabled,
                    ACTION_CUSTOM_NEXT_DISABLED,
                    createPendingIntent(
                        context,
                        instanceId,
                        ACTION_CUSTOM_NEXT_DISABLED,
                        enableNextAction
                    )
                )
            }
            return actionMap
        }

        override fun getCustomActions(player: Player): List<String> {
            val shouldShowPauseButton =
                (playerNotificationManager as CustomPlayerNotificationManager).shouldShowPauseButton(
                    player
                )
            return if (player_from.equals(PLAYER_FROM_PLAYLIST)) {
                arrayListOf(
                    if (enablePreviousAction) ACTION_CUSTOM_PREVIOUS else ACTION_CUSTOM_PREVIOUS_DISABLED,
                    ACTION_CUSTOM_REWIND,
                    if (shouldShowPauseButton)
                        ACTION_CUSTOM_PAUSE
                    else
                        ACTION_CUSTOM_PLAY,
                    ACTION_CUSTOM_FAST_FORWARD,
                    if (enableNextAction) ACTION_CUSTOM_NEXT else ACTION_CUSTOM_NEXT_DISABLED,
                )
            } else if (player_from.equals(PLAYER_FROM_BEGIN_EXPERIENCE) || player_from.equals(
                    PLAYER_FROM_EXPERIENCE_MORE
                )
            ) {
                arrayListOf(
                    if (shouldShowPauseButton)
                        ACTION_CUSTOM_PAUSE
                    else
                        ACTION_CUSTOM_PLAY
                )
            } else {
                arrayListOf(
                    ACTION_CUSTOM_REWIND,
                    if (shouldShowPauseButton)
                        ACTION_CUSTOM_PAUSE
                    else
                        ACTION_CUSTOM_PLAY,
                    ACTION_CUSTOM_FAST_FORWARD,
                )
            }
        }

        override fun onCustomAction(player: Player, action: String, intent: Intent) {
            logError(msg = "onCustomAction $action")
            when (action) {
                ACTION_CUSTOM_PLAY, ACTION_CUSTOM_PAUSE ->
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                ACTION_CUSTOM_FAST_FORWARD ->
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                ACTION_CUSTOM_REWIND ->
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                ACTION_CUSTOM_NEXT ->
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                ACTION_CUSTOM_PREVIOUS ->
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }

    private fun createPendingIntent(
        context: Context,
        instanceId: Int,
        action: String,
        isActionEnabled: Boolean = true
    ): PendingIntent? {
        val intent = Intent(action).setPackage(context.packageName)
        intent.putExtra("EXTRA_INSTANCE_ID", instanceId)
        intent.putExtra("isActionEnabled", isActionEnabled)
        return PendingIntent.getBroadcast(
            context,
            instanceId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                PLAYER_NOTIFICATION_CHANNEL_ID,
                PLAYER_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            serviceChannel.lightColor = Color.BLUE
            serviceChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    inner class MainServiceBinder : Binder() {
        /**
         * This method should be used only for setting the exoplayer instance.
         * If exoplayer's internal are altered or accessed we can not guarantee
         * things will work correctly.
         */
        val exoPlayerInstance: ExoPlayer?
            get() = TaoCalligraphy.instance.simpleExoPlayer

        //        val sample: Sample
//            get() = mSamples.get(mExoPlayer.getCurrentWindowIndex())
        val service: ExoNotificationService
            get() = this@ExoNotificationService
    }

}