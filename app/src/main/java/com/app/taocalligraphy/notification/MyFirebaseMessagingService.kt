package com.app.taocalligraphy.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.taocalligraphy.R
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.meditation_timer.PlayMeditationTimerActivity
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.program.ProgramDetailsActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.longToast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FireMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //WebEngage.get().setRegistrationID(token)
        userHolder.mAuthToken = token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data: Map<String, String> = message.data
        if (Constants.NOTIFICATION_ON_OFF)
            sendNotification(
                message.data[Constants.Param.title].toString(),
                message.data[Constants.Param.message].toString(),
                data
            )

    }

    @SuppressLint("WrongConstant")
    private fun sendNotification(title: String, messageBody: String, data: Map<String, String>) {

        createChannelId(
            getString(R.string.notification_channel_ohter),
            getString(R.string.notification_channel_description)
        )

        //  val type = jsonObject.getString("click_action")
        val notificationType = data["notificationType"]
        val notificationId = data["redirectionId"]
        var intent: Intent? = null

        when (notificationType) {
            Constants.programs -> {
                if (notificationId != null) {
                    intent = Intent(this, ProgramDetailsActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(Constants.Param.programId, notificationId)
                        putExtra(Constants.Param.isFromNotification, true)
                    }
                }
            }
            Constants.meditation -> {
                if (notificationId != null) {
                    intent = Intent(this, PlayMeditationTimerActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        putExtra(Constants.Param.meditationTimerId, notificationId)
                        putExtra(Constants.Param.isFromNotification, true)
                    }
                }
            }
            Constants.dailyWisdom -> {
                intent = Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(Constants.Param.isFromNotification, true)
                }
            }
            Constants.subscription -> {
//                intent = Intent(this, SubscriptionActivity::class.java).apply {
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    putExtra(Constants.Param.isFromNotification, true)
//                }
//                this.longToast("Coming Soon." , Constants.INFO)
            }
        }

        intent?.let {
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    this, System.currentTimeMillis().toInt(), it,
                    PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    this, System.currentTimeMillis().toInt(), it,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, "channel_01")
            notificationBuilder.setSmallIcon(R.drawable.vd_calligraphy_notification)

            val bigTextStyle = NotificationCompat.BigTextStyle()
            bigTextStyle.bigText(messageBody)

            if (!TextUtils.isEmpty(title)) {
                notificationBuilder.setContentTitle(title)
                bigTextStyle.setBigContentTitle(title)
            } else {
                notificationBuilder.setContentTitle(getString(R.string.app_name))
                bigTextStyle.setBigContentTitle(getString(R.string.app_name))
            }
            notificationBuilder.setContentText(messageBody)
            notificationBuilder.setStyle(bigTextStyle)
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setSound(defaultSoundUri)
            notificationBuilder.setNumber(data["badge"]?.toInt() ?: 0)
            notificationBuilder.setContentIntent(pendingIntent)

            val notificationManager =
                application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(
                System.currentTimeMillis().toInt(),
                notificationBuilder.build()
            )
        }
    }

    private fun createChannelId(name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("channel_01", name, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.setShowBadge(true)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }
}
