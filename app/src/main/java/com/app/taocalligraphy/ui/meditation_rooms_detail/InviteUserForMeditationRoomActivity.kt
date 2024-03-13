package com.app.taocalligraphy.ui.meditation_rooms_detail

import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityInviteUserForMeditationRoomBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.utils.extensions.hideKeyboard
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class InviteUserForMeditationRoomActivity : BaseActivity<ActivityInviteUserForMeditationRoomBinding>() {

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, InviteUserForMeditationRoomActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_invite_user_for_meditation_room

    override fun initView() {

    }

    override fun observeApiCallbacks() {

    }

    override fun handleListener() {

        mBinding.btnSendInvite.setOnClickListener {
            if (checkScreenValidation()) {
                hideKeyboard()
                mBinding.llInviteUserMain.visibility = View.GONE
                mBinding.llInviteSentMain.visibility = View.VISIBLE
            }
        }

        mBinding.ivClose.setOnClickListener {
            onBackPressed()
        }

        mBinding.btnClose.setOnClickListener {
            onBackPressed()
        }

        mBinding.btnSendAnother.setOnClickListener {
            mBinding.etEmailAddress.setText("")
            mBinding.llInviteUserMain.visibility = View.VISIBLE
            mBinding.llInviteSentMain.visibility = View.GONE
        }

    }

    private fun checkScreenValidation(): Boolean {
        if (TextUtils.isEmpty(mBinding.etEmailAddress.text.toString().trim())) {
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