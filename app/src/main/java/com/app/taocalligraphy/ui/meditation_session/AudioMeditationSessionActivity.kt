package com.app.taocalligraphy.ui.meditation_session

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityAudioMeditationSessionBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.meditation_session.dialog.LeaveEndSessionDialog
import com.app.taocalligraphy.ui.meditation_session.dialog.ManageSessionParticipantsDialog
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

class AudioMeditationSessionActivity : BaseActivity<ActivityAudioMeditationSessionBinding>() {

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity, isHost: Boolean) {
            val intent = Intent(activity, AudioMeditationSessionActivity::class.java)
            intent.putExtra(Constants.isHost, isHost)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_audio_meditation_session
    }

    val isHost by lazy {
        return@lazy intent.extras?.getBoolean(Constants.isHost, false) ?: false
    }

    var handlerMainTimer = Handler(Looper.getMainLooper())
    var handlerWaitTimer = Handler(Looper.getMainLooper())
    var totalWaitTime = 60000L
    var screenStep = 0
    var isAudioMute = true

    override fun initView() {
        setupToolbar()
        if (isHost) {
            mBinding.tvHostName.isGone = true
            mBinding.tvYouAreHosting.isVisible = true
            mBinding.btnJoinSession.text = getString(R.string.prepare_to_start)
            mBinding.llAddToCalender.isGone = true
            mBinding.ffWaitingForHost.isGone = true
            mBinding.llSessionEdit.isVisible = true
            mBinding.tvManageParticipantTitle.text = getString(R.string.manage)
            mBinding.tvLeaveSessionTitle.text = getString(R.string.manage_participants).uppercase()
        }
        setTimerValue()
        handlerMainTimer.postDelayed(runnableMainTimer, 1000)
    }

    private fun setupToolbar() {
        setToolbar(
            mBinding.toolbar.innerToolbar,
            "",
            true,
        )
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {

        mBinding.btnJoinSession.setOnClickListener {
            if (isHost) {
                screenStep = 1
                viewAnimationPrepareToStart()
            } else {
                screenStep = 1
                animateJoinAs()
            }
        }

        mBinding.llJoinAsLoginUser.setOnClickListener {
            screenStep = 2
            viewAnimationAfterJoinAs()
        }

        mBinding.llJoinAsGuestUser.setOnClickListener {
            screenStep = 2
            viewAnimationAfterJoinAs()
            mBinding.imgCalender.setImageResource(R.drawable.vd_ic_frame_7915)
        }

        mBinding.ivAudioMuteStatus.setOnClickListener {
            isAudioMute = if (isAudioMute) {
                mBinding.ivAudioMuteStatus.setImageResource(R.drawable.vd_unmute_audio)
                false
            } else {
                mBinding.ivAudioMuteStatus.setImageResource(R.drawable.vd_mute_audio)
                true
            }
        }

        mBinding.llSessionMemberView.setOnClickListener {
            openSessionMemberParticipantsDialog()
        }

        mBinding.rrChat.setOnClickListener {
            val intent = Intent(this, MeditationSessionChatActivity::class.java)
            intent.putExtra(Constants.isHost, isHost)
            intent.putExtra(Constants.alpha, mBinding.ivSessionBg.alpha)
            val options = ActivityOptions
                .makeSceneTransitionAnimation(this)
            startActivity(intent, options.toBundle())
        }

        mBinding.rrLeaveSession.setOnClickListener {
            if (isHost) {
                openSessionMemberParticipantsDialog()
                //showLeaveEndSessionDialog(false)
            } else {
                showLeaveEndSessionDialog(true)
            }
        }

        mBinding.llInviteUser.setOnClickListener {
            InviteUserInSessionActivity.startActivity(this)
        }

        mBinding.llSessionEdit.setOnClickListener {
            CreateMeditationSessionActivity.startActivity(this, false)
        }

        mBinding.btnStartNow.setOnClickListener {
            mBinding.cardStartNow.isGone = true
            mBinding.cardEndSession.isVisible = true
        }

        mBinding.llEndSessionController.setOnClickListener {
            showLeaveEndSessionDialog(false)
        }
    }

    private fun animateJoinAs() {
        val isShow: Boolean = screenStep == 1
        mBinding.llJoinAs.animate().also {
            it.withStartAction {
                if (isShow) {
                    mBinding.llJoinAs.isVisible = true
                } else {
                    mBinding.ffRemainingTimeForSessionStart.isVisible = true
                    mBinding.cardJoinNow.isVisible = true
                }
            }
            it.withEndAction {
                if (isShow) {
                    mBinding.ffRemainingTimeForSessionStart.isGone = true
                    mBinding.cardJoinNow.isGone = true
                } else {
                    mBinding.llJoinAs.isGone = true
                }
            }
            it.duration = 300
            it.start()
        }
    }

    private fun viewAnimationAfterJoinAs() {
        val isShow: Boolean = screenStep == 2
        mBinding.ffRemainingTimeForSessionStart.animate().also {
            it.withStartAction {
                if (isShow) {
                    mBinding.llJoinAs.isGone = true
                    mBinding.llSessionInfo.isVisible = true
                    if (totalWaitTime > 0) {
                        mBinding.ffRemainingTimeForSessionStart.isVisible = true
                    } else {
                        mBinding.ffWaitingForHost.isVisible = true
                        handlerWaitTimer.postDelayed(runnableWaitTimer, 5000)
                    }
                    mBinding.llSessionDateTime.isGone = true
                    mBinding.llSessionMemberView.isVisible = true
                    mBinding.llChatSession.isVisible = true
                    mBinding.ivSessionBg.alpha = 0.4f
                } else {
                    mBinding.llSessionDateTime.isVisible = true
                    mBinding.llSessionMemberView.isGone = true
                    mBinding.llSessionInfo.isVisible = true
                    mBinding.llChatSession.isGone = true
                    mBinding.ivSessionBg.alpha = 0.2f
                }
            }
            it.withEndAction {
                if (isShow) {
                    mBinding.llSessionInfo.isGone = true
                } else {
                    mBinding.ffRemainingTimeForSessionStart.isGone = true
                    mBinding.ffWaitingForHost.isGone = true
                    mBinding.llSessionLiveOtherUser.isGone = true
                    mBinding.llJoinAs.isVisible = true
                }
            }
            it.duration = if (isShow) 100 else 200
            it.start()
        }
    }

    private fun viewAnimationPrepareToStart() {
        val isShow: Boolean = screenStep == 1
        mBinding.llSessionInfo.animate().also {
            it.withStartAction {
                if (isShow) {
                    mBinding.llSessionInfo.isVisible = true
                    mBinding.llSessionDateTime.isGone = true
                    mBinding.llSessionMemberView.isVisible = true
                    mBinding.llChatSession.isVisible = true
                    mBinding.ivSessionBg.alpha = 0.4f
                    mBinding.tvYouAreHosting.isGone = true
                    mBinding.cardHostImage.isGone = true
                    mBinding.ivAudioMuteStatus.isVisible = true
                } else {
                    mBinding.ffRemainingTimeForSessionStart.isVisible = true
                    mBinding.cardStartNow.isGone = true
                    mBinding.cardEndSession.isGone = true
                    mBinding.llSessionDateTime.isVisible = true
                    mBinding.llSessionMemberView.isGone = true
                    mBinding.llChatSession.isGone = true
                    mBinding.ivSessionBg.alpha = 0.2f
                    mBinding.tvYouAreHosting.isVisible = true
                    mBinding.cardHostImage.isVisible = true
                    mBinding.ivAudioMuteStatus.isGone = true
                }
            }
            it.withEndAction {
                if (isShow) {
                    mBinding.llSessionInfo.isGone = true
                    mBinding.cardJoinNow.isGone = true
                    mBinding.llSessionEdit.isGone = true
                    if (totalWaitTime > 0) {
                        mBinding.ffRemainingTimeForSessionStart.isVisible = true
                        mBinding.cardStartNow.isGone = true
                    } else {
                        mBinding.ffRemainingTimeForSessionStart.isGone = true
                        mBinding.cardStartNow.isVisible = true
                    }
                } else {
                    mBinding.llSessionInfo.isVisible = true
                    mBinding.cardJoinNow.isVisible = true
                    mBinding.llSessionEdit.isVisible = true
                }
            }
            it.duration = if (isShow) 100 else 200
            it.start()
        }
    }

    private fun openSessionMemberParticipantsDialog() {
        val dialog =
            ManageSessionParticipantsDialog(this, isHost)
        dialog.behavior.isDraggable = true
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun showLeaveEndSessionDialog(isLeaveSession: Boolean) {
        val dialog = LeaveEndSessionDialog.newInstance(isLeaveSession)
        dialog.show(
            getFragmentTransactionFromTag(LeaveEndSessionDialog.TAG),
            LeaveEndSessionDialog.TAG
        )
    }

    private val runnableMainTimer: Runnable = object : Runnable {
        override fun run() {
            if ((!isFinishing) and (totalWaitTime > 0)) {
                totalWaitTime -= 1000
                setTimerValue()
                handlerMainTimer.postDelayed(this, 1000)
            } else {
                if (isHost) {
                    if (screenStep == 1) {
                        mBinding.ffRemainingTimeForSessionStart.isGone = true
                        mBinding.cardStartNow.isVisible = true
                    }
                } else {
                    if (screenStep == 2) {
                        mBinding.ffRemainingTimeForSessionStart.isGone = true
                        mBinding.ffWaitingForHost.isVisible = true
                        handlerWaitTimer.postDelayed(runnableWaitTimer, 5000)
                    }
                }
            }
        }
    }

    private val runnableWaitTimer: Runnable = object : Runnable {
        override fun run() {
            if ((!isFinishing)) {
                if (screenStep == 2) {
                    mBinding.ffWaitingForHost.isGone = true
                    mBinding.llSessionLiveOtherUser.isVisible = true
                    mBinding.ivSessionBg.alpha = 1.0f
                }
            }
        }
    }

    fun setTimerValue() {
        val currentTimeValueMinutes = String.format(
            "%02d",
            TimeUnit.MILLISECONDS.toMinutes(totalWaitTime) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(totalWaitTime)
            ),
        )

        val currentTimeValueSecond = String.format(
            "%02d",
            TimeUnit.MILLISECONDS.toSeconds(totalWaitTime) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(totalWaitTime)
            )
        )

        mBinding.tvMinutesRemaining.text = currentTimeValueMinutes
        mBinding.tvSecondsRemaining.text = currentTimeValueSecond

        if (currentTimeValueMinutes.toInt() > 0) {
            mBinding.tvMinutesRemaining.setTextColor(ContextCompat.getColor(this, R.color.red))
        } else {
            mBinding.tvMinutesRemaining.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.secondary_black
                )
            )
        }

        if (totalWaitTime.toInt() > 0) {
            mBinding.tvSecondsRemaining.setTextColor(ContextCompat.getColor(this, R.color.red))
        } else {
            mBinding.tvSecondsRemaining.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.secondary_black
                )
            )
        }
    }

    override fun onBackPressed() {
        when (screenStep) {
            0 -> {
                super.onBackPressed()
            }
            1 -> {
                screenStep = 0
                if (isHost) {
                    viewAnimationPrepareToStart()
                } else {
                    animateJoinAs()
                }
            }
            2 -> {
                screenStep = 1
                viewAnimationAfterJoinAs()
                handlerWaitTimer.removeCallbacks(runnableWaitTimer)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerMainTimer.removeCallbacks(runnableMainTimer)
        handlerWaitTimer.removeCallbacks(runnableWaitTimer)
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
}