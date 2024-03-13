package com.app.taocalligraphy.notification

import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_FAST_FORWARD
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_NEXT
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_NEXT_DISABLED
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_PAUSE
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_PLAY
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_PREVIOUS
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_PREVIOUS_DISABLED
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_REWIND
import com.app.taocalligraphy.notification.ExoNotificationService.Companion.ACTION_CUSTOM_STOP
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import java.util.*

class CustomPlayerNotificationManager(
    val context: Context,
    channelId: String,
    notificationId: Int,
    mediaDescriptionAdapter: MediaDescriptionAdapter,
    notificationListener: NotificationListener?,
    private val customActionReceiver: CustomActionReceiver?,
    smallIconResourceId: Int,
    playActionIconResourceId: Int,
    pauseActionIconResourceId: Int,
    stopActionIconResourceId: Int,
    rewindActionIconResourceId: Int,
    fastForwardActionIconResourceId: Int,
    previousActionIconResourceId: Int,
    nextActionIconResourceId: Int,
    groupKey: String?,
    private val usePreviousAction: Boolean,
    private val useNextAction: Boolean,
    private val usePreviousActionInCompactView: Boolean,
    private val useNextActionInCompactView: Boolean,
    private val usePlayPauseActions: Boolean,
    private val useRewindAction: Boolean,
    private val useFastForwardAction: Boolean,
    private val useRewindActionInCompactView: Boolean,
    private val useFastForwardActionInCompactView: Boolean,
    private val useStopAction: Boolean
) : PlayerNotificationManager(
    context,
    channelId,
    notificationId,
    mediaDescriptionAdapter,
    notificationListener,
    customActionReceiver,
    smallIconResourceId,
    playActionIconResourceId,
    pauseActionIconResourceId,
    stopActionIconResourceId,
    rewindActionIconResourceId,
    fastForwardActionIconResourceId,
    previousActionIconResourceId,
    nextActionIconResourceId,
    groupKey
) {
    override fun createNotification(
        player: Player,
        builder: NotificationCompat.Builder?,
        ongoing: Boolean,
        largeIcon: Bitmap?
    ): NotificationCompat.Builder? {
        return super.createNotification(player, builder, ongoing, largeIcon)
    }

    override fun getActions(player: Player): List<String> {
//        return super.getActions(player);
        val enablePrevious = player.isCommandAvailable(Player.COMMAND_SEEK_TO_PREVIOUS)
        val enableRewind = player.isCommandAvailable(Player.COMMAND_SEEK_BACK)
        val enableFastForward = player.isCommandAvailable(Player.COMMAND_SEEK_FORWARD)
        val enableNext = player.isCommandAvailable(Player.COMMAND_SEEK_TO_NEXT)
        val stringActions: MutableList<String> = ArrayList()
        if (usePreviousAction /*&& enablePrevious*/) {
            if (ExoNotificationService.enablePreviousAction) stringActions.add(ACTION_CUSTOM_PREVIOUS) else stringActions.add(ACTION_CUSTOM_PREVIOUS_DISABLED)
        }
        if (useRewindAction && enableRewind) {
            stringActions.add(ACTION_CUSTOM_REWIND)
        }
        if (usePlayPauseActions) {
            if (shouldShowPauseButton(player)) {
                stringActions.add(ACTION_CUSTOM_PAUSE)
            } else {
                stringActions.add(ACTION_CUSTOM_PLAY)
            }
        }
        if (useFastForwardAction && enableFastForward) {
            stringActions.add(ACTION_CUSTOM_FAST_FORWARD)
        }
        if (useNextAction /*&& enableNext*/) {
            stringActions.add(ACTION_CUSTOM_NEXT)
            if (ExoNotificationService.enableNextAction) stringActions.add(ACTION_CUSTOM_NEXT) else stringActions.add(ACTION_CUSTOM_NEXT_DISABLED)
        }
        if (customActionReceiver != null) {
            stringActions.addAll(customActionReceiver.getCustomActions(player))
        }
        if (useStopAction) {
            stringActions.add(ACTION_CUSTOM_STOP)
        }
        return stringActions
    }

    fun shouldShowPauseButton(player: Player): Boolean {
        return (player.playbackState != Player.STATE_ENDED && player.playbackState != Player.STATE_IDLE && player.playWhenReady) && (player.isPlaying || player.playbackState == Player.STATE_BUFFERING)
    }

    override fun getActionIndicesForCompactView(
        actionNames: List<String>,
        player: Player
    ): IntArray {
//        return super.getActionIndicesForCompactView(actionNames, player)

//        val pauseActionIndex = actionNames.indexOf(ExoNotificationService.ACTION_CUSTOM_PAUSE)
//        val playActionIndex = actionNames.indexOf(ExoNotificationService.ACTION_CUSTOM_PLAY)
//        return intArrayOf(if(pauseActionIndex != -1) pauseActionIndex else playActionIndex)

        val pauseActionIndex = actionNames.indexOf(ACTION_CUSTOM_PAUSE)
        val playActionIndex = actionNames.indexOf(ACTION_CUSTOM_PLAY)
        val leftSideActionIndex = if (usePreviousActionInCompactView) actionNames.indexOf(
            if (ExoNotificationService.enablePreviousAction) ACTION_CUSTOM_PREVIOUS else ACTION_CUSTOM_PREVIOUS_DISABLED,
        ) else if (useRewindActionInCompactView) actionNames.indexOf(ACTION_CUSTOM_REWIND) else -1
        val rightSideActionIndex =
            if (useNextActionInCompactView) actionNames.indexOf(
                if (ExoNotificationService.enableNextAction) ACTION_CUSTOM_NEXT else ACTION_CUSTOM_NEXT_DISABLED
            ) else if (useFastForwardActionInCompactView) actionNames.indexOf(
                ACTION_CUSTOM_FAST_FORWARD
            ) else -1

        val actionIndices = IntArray(3)
        var actionCounter = 0
        if (leftSideActionIndex != -1) {
            actionIndices[actionCounter++] = leftSideActionIndex
        }
        val shouldShowPauseButton = shouldShowPauseButton(player)
        if (pauseActionIndex != -1 && shouldShowPauseButton) {
            actionIndices[actionCounter++] = pauseActionIndex
        } else if (playActionIndex != -1 && !shouldShowPauseButton) {
            actionIndices[actionCounter++] = playActionIndex
        }
        if (rightSideActionIndex != -1) {
            actionIndices[actionCounter++] = rightSideActionIndex
        }
        return Arrays.copyOf(actionIndices, actionCounter)
    }

    override fun getOngoing(player: Player): Boolean {
        return super.getOngoing(player)
    }

    init {
        var builderActions: List<NotificationCompat.Action>? = null
        setUsePreviousAction(usePreviousAction)
        setUsePreviousActionInCompactView(usePreviousActionInCompactView ?: usePreviousAction)
        setUseNextAction(useNextAction)
        setUseNextActionInCompactView(useNextActionInCompactView ?: useNextAction)
        setUsePlayPauseActions(usePlayPauseActions)
        setUseRewindAction(useRewindAction)
        setUseRewindActionInCompactView(useRewindActionInCompactView ?: useRewindAction)
        setUseFastForwardAction(useFastForwardAction)
        setUseFastForwardActionInCompactView(
            useFastForwardActionInCompactView ?: useFastForwardAction
        )
        setUseStopAction(useStopAction)
    }
}