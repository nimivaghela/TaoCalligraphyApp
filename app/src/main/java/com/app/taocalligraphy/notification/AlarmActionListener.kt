package com.app.taocalligraphy.notification

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.meditation.StartPlayerActivity
import com.app.taocalligraphy.utils.extensions.getTimeBasedOnTimeFormat
import java.util.*

class AlarmActionListener : BroadcastReceiver() {
    private val channelName = "AlarmChannel01"

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            NotificationManagerCompat.from(it).cancel(1)
            TaoCalligraphy.instance.simpleExoPlayer?.run {
                playWhenReady = false
                release()
            }
            when (intent?.action) {
                Constants.startMeditation -> {
                    val contentId = intent.extras?.getString(Constants.Param.contentId, "") ?: ""
                    StartPlayerActivity.startActivity(
                        it,
                        contentId = contentId,
                        isFromNotification = true
                    )
                }
                Constants.snoozeAlarm -> {
                    val alarmManager: AlarmManager =
                        it.getSystemService(Service.ALARM_SERVICE) as AlarmManager
                    val alarmContentTitle = intent.extras?.getString(Constants.alarmContentTitle)
                    val snoozeIntent: Intent = Intent(it, AlarmBroadcastReceiver::class.java)
                        .apply {
                            putExtra(
                                Constants.alarmContentId,
                                intent.extras?.getString(Constants.alarmContentId)
                            )
                            putExtra(
                                Constants.alarmContentTitle,
                                alarmContentTitle
                            )
                            putExtra(
                                Constants.alarmContentImageUrl,
                                intent.extras?.getString(Constants.alarmContentImageUrl)
                            )
                            putExtra(
                                Constants.alarmContentFileUrl,
                                intent.extras?.getString(Constants.alarmContentFileUrl)
                            )
                            putExtra(
                                Constants.alarmRequestCode,
                                intent.extras?.getInt(Constants.alarmRequestCode, 1)
                            )
                        }

                    val calendar: Calendar = Calendar.getInstance()
                    calendar.add(Calendar.MINUTE, 10)
                    calendar.set(Calendar.SECOND, 0)

                    val pendingIntent =
                        PendingIntent.getBroadcast(
                            it, 9, snoozeIntent,
                            PendingIntent.FLAG_IMMUTABLE or
                                    PendingIntent.FLAG_UPDATE_CURRENT or
                                    PendingIntent.FLAG_CANCEL_CURRENT
                        )

                    val stopIntent = Intent(it, AlarmActionListener::class.java).apply {
                        action = Constants.cancelAlarm
                    }

                    val piStopIntent = PendingIntent.getBroadcast(
                        it,
                        0,
                        stopIntent,
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
                    val bigTextStyle = NotificationCompat.BigTextStyle()
                    val time = calendar.getTimeBasedOnTimeFormat(context)
                    bigTextStyle.bigText(
                        context.getString(
                            R.string.alarm_snooze_msg,
                            alarmContentTitle,
                            time
                        )
                    )
                    //bigTextStyle.setBigContentTitle(title)
                    notificationBuilder.setStyle(bigTextStyle)
                    notificationBuilder.setContentText(alarmContentTitle)
                    notificationBuilder.setAutoCancel(true)
                    notificationBuilder.setSound(null)
                    notificationBuilder.setOngoing(true)
                    notificationBuilder.setContentIntent(null)
                    notificationBuilder.addAction(0, context.getString(R.string.stop), piStopIntent)
                    notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
                    notificationBuilder.setCategory(NotificationCompat.CATEGORY_ALARM)

                    val notificationManager =
                        it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    with(notificationManager) {
                        buildVOIPChannel(this)
                    }

                    notificationManager.notify(2, notificationBuilder.build())

                    val date: Date = calendar.time
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.time, pendingIntent)
                }
                Constants.cancelAlarm -> {
                    val alarmManager: AlarmManager =
                        it.getSystemService(Service.ALARM_SERVICE) as AlarmManager

                    NotificationManagerCompat.from(context).cancel(2)

                    val snoozeIntent = Intent(it, AlarmBroadcastReceiver::class.java)
                    val pendingIntent =
                        PendingIntent.getBroadcast(
                            it, 9, snoozeIntent,
                            PendingIntent.FLAG_IMMUTABLE or
                                    PendingIntent.FLAG_UPDATE_CURRENT or
                                    PendingIntent.FLAG_CANCEL_CURRENT
                        )

                    alarmManager.cancel(pendingIntent)
                }
            }
        }
    }

    private fun buildVOIPChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Channel snooze"
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
}