package com.app.taocalligraphy.ui.meditation_session

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityInviteUserInSessionBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class InviteUserInSessionActivity : BaseActivity<ActivityInviteUserInSessionBinding>() {

    companion object {
        @JvmStatic
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, InviteUserInSessionActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource(): Int {
        return R.layout.activity_invite_user_in_session
    }

    override fun initView() {
    }

    override fun observeApiCallbacks() {
    }

    override fun handleListener() {
        mBinding.btnSendInvite.setOnClickListener {
            if (checkScreenValidation()) {
                mBinding.tvEnterEmail.text = mBinding.etEmailAddress.text.toString()
                mBinding.tvInviteSendStatus.text=getString(R.string.invite_successfully_sent)
                mBinding.llSendAnother.isVisible = true
                mBinding.llEmailContent.isGone = true
            }
        }

        mBinding.btnSendAnother.setOnClickListener {
            mBinding.etEmailAddress.text?.clear()
            mBinding.tvInviteSendStatus.text=getString(R.string.invite_users_to_join)
            mBinding.llSendAnother.isGone = true
            mBinding.llEmailContent.isVisible = true
        }

        mBinding.ivClose.setOnClickListener {
            onBackPressed()
        }

        mBinding.btnClose.setOnClickListener {
            onBackPressed()
        }
    }

    private fun checkScreenValidation(): Boolean {
        if (mBinding.etEmailAddress.text.toString().trim().isEmpty()) {
            mBinding.tlEmailAddress.error = getString(R.string.please_enter_email_address)
            return false
        } else {
            mBinding.tlEmailAddress.error = ""
        }
        if (isEmailInvalid(mBinding.etEmailAddress.text.toString().trim())) {
            mBinding.tlEmailAddress.error = getString(R.string.please_enter_valid_email_address)
            return false
        } else {
            mBinding.tlEmailAddress.error = ""
        }
        return true
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