package com.app.taocalligraphy.ui.meditation_session

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityMeditationSessionChatBinding
import com.app.taocalligraphy.models.IconNamePopupModel
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.meditation_session.adapter.CustomMenuItemAdapter
import com.app.taocalligraphy.ui.meditation_session.adapter.SessionChatAdapter
import com.app.taocalligraphy.ui.meditation_session.dialog.LeaveEndSessionDialog
import com.app.taocalligraphy.ui.meditation_session.dialog.ManageSessionParticipantsDialog
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MeditationSessionChatActivity : BaseActivity<ActivityMeditationSessionChatBinding>(),
    SessionChatAdapter.ChatMessageListener {

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, MeditationSessionChatActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_meditation_session_chat
    }

    private val isHost by lazy {
        return@lazy intent.extras?.getBoolean(Constants.isHost, false) ?: false
    }

    val alpha: Float by lazy {
        return@lazy intent.extras?.getFloat(Constants.alpha, 1.0f) ?: 1.0f
    }

    private val sessionChatAdapter by lazy {
        return@lazy SessionChatAdapter(this)
    }

    var isAudioMute = false
    var animateButtonY = 0

    override fun initView() {
        setupToolbar()
        mBinding.rvChat.adapter = sessionChatAdapter
        if (isHost) {
            mBinding.ivAudioMuteStatus.isVisible = true
            mBinding.cardManageParticipants.isVisible = true
            mBinding.llHostEndSession.isVisible = true
        } else {
            mBinding.ivAudioMuteStatus.isInvisible = true
            mBinding.cardManageParticipants.isGone = true
            mBinding.llSessionLiveOtherUser.isVisible = true
        }
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
        mBinding.apply {
            llLike.setOnClickListener {
                llLike.isGone = true
                lottieLike.isVisible = true
                lottieLove.isVisible = true
                lottieSmile.isVisible = true
                ivDownArrow.isVisible = true
            }

            ivDownArrow.setOnClickListener {
                llLike.isVisible = true
                lottieLike.isGone = true
                lottieLove.isGone = true
                lottieSmile.isGone = true
                ivDownArrow.isGone = true
            }
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

        mBinding.rrManageParticipants.setOnClickListener {
            openSessionMemberParticipantsDialog()
        }

        mBinding.llEndSession.setOnClickListener {
            showLeaveEndSessionDialog(false)
        }

        mBinding.llSessionMemberView.setOnClickListener {
//            Chat View
            openSessionMemberParticipantsDialog()
        }

        mBinding.lottieLike.setOnClickListener {
            mBinding.lottieLike.playAnimation()
        }

        mBinding.lottieLove.setOnClickListener {
            mBinding.lottieLove.playAnimation()
        }

        mBinding.lottieSmile.setOnClickListener {
            mBinding.lottieSmile.playAnimation()
        }

        mBinding.lottieLike.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                animateReaction(mBinding.lottieLike, R.drawable.vd_thumb_small)
            }

            override fun onAnimationEnd(p0: Animator) {
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }


        })

        mBinding.lottieLove.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                animateReaction(mBinding.lottieLove, R.drawable.vd_heart_small)
            }

            override fun onAnimationEnd(p0: Animator) {
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })

        mBinding.lottieSmile.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
                animateReaction(mBinding.lottieSmile, R.drawable.vd_smiley_small)
            }

            override fun onAnimationEnd(p0: Animator) {
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
    }

    fun animateReaction(lottie: LottieAnimationView, reactionEmoji: Int) {
        val location = IntArray(2)
        lottie.getLocationOnScreen(location)
        mBinding.ivAnimate.x = location[0].toFloat() + lottie.width / 4
        if (animateButtonY == 0) {
            animateButtonY = mBinding.ivAnimate.y.toInt()
        }
        mBinding.ivAnimate.y = (animateButtonY + lottie.height).toFloat()
        mBinding.ivAnimate.setImageResource(reactionEmoji)
        mBinding.ivAnimate.alpha = 1f
        mBinding.ivAnimate.isInvisible = true
        mBinding.ivAnimate.postDelayed(Runnable {
            mBinding.ivAnimate.animate().also {
                it.translationYBy(-500f)
                it.alpha(0f)
                it.duration = 500
                it.withStartAction {
                    mBinding.ivAnimate.isVisible = true
                }
                it.start()
            }
        }, 500)

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onChatMessageClick(itemView: View) {
        if (sessionChatAdapter.customPowerMenu != null) {
            sessionChatAdapter.customPowerMenu = null
            return
        }
        val onMenuItemClickListener: OnMenuItemClickListener<IconNamePopupModel> =
            OnMenuItemClickListener<IconNamePopupModel> { position, item ->
                sessionChatAdapter.customPowerMenu?.dismiss()
                sessionChatAdapter.customPowerMenu = null
            }


        sessionChatAdapter.customPowerMenu = CustomPowerMenu.Builder(this, CustomMenuItemAdapter())
            .setHeaderView(R.layout.item_popup_header) // header used for title
            .setFooterView(R.layout.item_popup_header)
            .addItem(
                IconNamePopupModel(
                    getString(R.string.remove),
                    ContextCompat.getDrawable(this, R.drawable.vd_cross_red)
                )
            )
            .setAnimation(MenuAnimation.SHOW_UP_CENTER) // Animation start point (TOP | LEFT).
            .setMenuRadius(30f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setHeight(200)
            .setWidth(400)
            .setShowBackground(false)
            .setOnMenuItemClickListener(onMenuItemClickListener)
            .build()

        sessionChatAdapter.customPowerMenu?.showAsDropDown(itemView)

        sessionChatAdapter.customPowerMenu?.setTouchInterceptor { view, motionEvent ->
            return@setTouchInterceptor false
        }
    }

    override fun onBackPressed() {
        mBinding.ivSessionBg.setImageResource(R.drawable.ic_dummy_session_bg)
        mBinding.ivSessionBg.alpha = alpha
        (mBinding.ivSessionBg.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "0:0"
        finishAfterTransition()
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