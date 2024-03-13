package com.app.taocalligraphy.notification

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.meditation.StartPlayerActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import java.util.*

class AlarmBroadcastReceiver : BroadcastReceiver() {

    val channelName = "AlarmChannel01"

    @SuppressLint("InvalidWakeLockTag")
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val alarmContentId = intent.extras?.getString(Constants.alarmContentId)
            val alarmContentTitle = intent.extras?.getString(Constants.alarmContentTitle)
            val alarmContentImageUrl = intent.extras?.getString(Constants.alarmContentImageUrl)
            val alarmContentFileUrl = intent.extras?.getString(Constants.alarmContentFileUrl)
            val alarmRepeat = intent.extras?.getBoolean(Constants.alarmRepeat, false)
            val alarmRequestCode = intent.extras?.getInt(Constants.alarmRequestCode, 1)

            context?.let {
                NotificationManagerCompat.from(it).cancel(1)
                NotificationManagerCompat.from(it).cancel(2)
                wakeUpScreen(it)
                TaoCalligraphy.instance.initializeExoPlayer()
                val dataSourceFactory: DataSource.Factory =
                    CacheDataSource.Factory().setCache(TaoCalligraphy.instance.getDownloadCache())
                        .setUpstreamDataSourceFactory(TaoCalligraphy.instance.buildDataSourceFactory())
                val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(alarmContentFileUrl)))
                TaoCalligraphy.instance.simpleExoPlayer?.run {
                    setMediaSource(mediaSource)
                    prepare()
                    playWhenReady = true
                    repeatMode = ExoPlayer.REPEAT_MODE_ONE
                }
                val notificationManager =
                    it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                with(notificationManager) {
                    buildVOIPChannel(this)
                }

                val startIntent = Intent(it, StartPlayerActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.Param.contentId, alarmContentId)
                    putExtra(Constants.Param.isFromNotification, true)
                    putExtra(Constants.Param.alarmRequestCode, alarmRequestCode)
                }
                val piAnswerIntent = PendingIntent.getActivity(
                    it,
                    0,
                    startIntent,
                    PendingIntent.FLAG_IMMUTABLE or
                            PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_CANCEL_CURRENT
                )

                val rejectIntent = Intent(it, AlarmActionListener::class.java).apply {
                    action = Constants.snoozeAlarm
                    putExtra(Constants.alarmContentId, alarmContentId)
                    putExtra(Constants.alarmContentTitle, alarmContentTitle)
                    putExtra(Constants.alarmContentImageUrl, alarmContentImageUrl)
                    putExtra(Constants.alarmContentFileUrl, alarmContentFileUrl)
                    putExtra(Constants.alarmRequestCode, alarmRequestCode)
                }
                val piRejectIntent = PendingIntent.getBroadcast(
                    it,
                    0,
                    rejectIntent,
                    PendingIntent.FLAG_IMMUTABLE or
                            PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_CANCEL_CURRENT
                )

                val notificationBuilder = NotificationCompat.Builder(it, channelName)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    notificationBuilder.setSmallIcon(R.drawable.vd_calligraphy_notification)
                    notificationBuilder.color =
                        ResourcesCompat.getColor(context.resources, R.color.gold, null)
                } else {
                    notificationBuilder.setSmallIcon(R.drawable.vd_calligraphy_notification)
                }

                val fullScreenIntent = PendingIntent.getActivity(
                    it,
                    0,
                    Intent(),
                    PendingIntent.FLAG_IMMUTABLE or
                            PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_CANCEL_CURRENT
                )
                val bigTextStyle = NotificationCompat.BigTextStyle()
                bigTextStyle.bigText(alarmContentTitle)
                //bigTextStyle.setBigContentTitle(title)
                notificationBuilder.setStyle(bigTextStyle)
                notificationBuilder.setContentText(alarmContentTitle)
                notificationBuilder.setAutoCancel(true)
                notificationBuilder.setSound(null)
                notificationBuilder.setOngoing(true)
                notificationBuilder.setContentIntent(piAnswerIntent)
                notificationBuilder.setFullScreenIntent(fullScreenIntent, true)
                notificationBuilder.addAction(
                    0,
                    context.getString(R.string.snooze_alarm),
                    piRejectIntent
                )
                notificationBuilder.addAction(
                    0,
                    context.getString(R.string.start_meditation),
                    piAnswerIntent
                )
                notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
                notificationBuilder.setCategory(NotificationCompat.CATEGORY_ALARM)

                notificationManager.notify(
                    1,
                    notificationBuilder.build()
                )

                if (alarmRepeat == true) {
                    setRepeatAlarm(
                        alarmContentId,
                        alarmContentTitle,
                        alarmContentImageUrl,
                        alarmContentFileUrl,
                        alarmRepeat,
                        alarmRequestCode,
                        it
                    )
                }
            }
        }
    }

    private fun setRepeatAlarm(
        alarmContentId: String?,
        alarmContentTitle: String?,
        alarmContentImageUrl: String?,
        alarmContentFileUrl: String?,
        alarmRepeat: Boolean,
        alarmRequestCode: Int?,
        context: Context
    ) {
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
        intent.putExtra(Constants.alarmContentId, alarmContentId)
        intent.putExtra(Constants.alarmContentTitle, alarmContentTitle)
        intent.putExtra(Constants.alarmContentImageUrl, alarmContentImageUrl)
        intent.putExtra(Constants.alarmContentFileUrl, alarmContentFileUrl)
        intent.putExtra(Constants.alarmRepeat, alarmRepeat)
        intent.putExtra(Constants.alarmRequestCode, alarmRequestCode)

        val calender = Calendar.getInstance()
        calender.set(Calendar.WEEK_OF_MONTH, calender.get(Calendar.WEEK_OF_MONTH) + 1)

        val alarmManager =
            context.getSystemService(Service.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmRequestCode ?: 1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )
        //7 * 24 * 60 * 60 * 1000,
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calender.time.time,
            pendingIntent
        )
    }

    private fun buildVOIPChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Channel"
            val descriptionText = "Tao alarm channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelName, name, importance).apply {
                description = descriptionText
            }
            channel.setSound(null, null)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.lightColor = Color.BLUE
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun wakeUpScreen(context: Context) {
        val pm: PowerManager = context.getSystemService(POWER_SERVICE) as PowerManager
        val isScreenOn: Boolean = pm.isInteractive
        if (!isScreenOn) {
            val wl: PowerManager.WakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                "myApp:MyLock"
            )
            wl.acquire(10000)
            val wl_cpu: PowerManager.WakeLock =
                pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myApp:mycpuMyCpuLock")
            wl_cpu.acquire(10000)
        }
    }
}