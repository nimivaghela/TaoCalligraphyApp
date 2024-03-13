package com.app.taocalligraphy.ui.forgot_password

import android.content.Intent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityEmailSentBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.forgot_password.viewmodel.ForgotPasswordViewModel
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.longToast
import com.app.taocalligraphy.utils.extensions.startActivityWithAnimation
import com.app.taocalligraphy.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class EmailSentActivity : BaseActivity<ActivityEmailSentBinding>() {

    private var emailAddress = ""
    private var message = ""

    private val mViewModel: ForgotPasswordViewModel by viewModels()

    companion object {
        fun startActivity(activity: AppCompatActivity, email: String, message: String?) {
            val intent = Intent(activity, EmailSentActivity::class.java)
            intent.putExtra(Constants.INTENT_EMAIL, email)
            intent.putExtra(Constants.INTENT_MESSAGE, message)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_email_sent

    override fun initView() {
        emailAddress = intent?.getStringExtra(Constants.INTENT_EMAIL) ?: ""
        message = intent?.getStringExtra(Constants.INTENT_MESSAGE) ?: ""
        setupToolbar()
        mBinding.tvEmailId.text = emailAddress

        if (!mViewModel.isSuccessMessageVisible) {
            longToast(message, Constants.SUCCESS)
            mViewModel.isSuccessMessageVisible = true
        }
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
    }

    override fun observeApiCallbacks() {

    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
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