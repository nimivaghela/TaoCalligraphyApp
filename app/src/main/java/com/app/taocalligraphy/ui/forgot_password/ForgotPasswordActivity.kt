package com.app.taocalligraphy.ui.forgot_password

import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityForgotPasswordBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.ui.forgot_password.viewmodel.ForgotPasswordViewModel
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {

    private val mViewModel: ForgotPasswordViewModel by viewModels()

    private var emailAddress = ""

    companion object {
        fun startActivity(activity: AppCompatActivity, email: String) {
            val intent = Intent(activity, ForgotPasswordActivity::class.java)
            intent.putExtra(Constants.INTENT_EMAIL, email)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_forgot_password

    override fun initView() {
        emailAddress = intent?.getStringExtra(Constants.INTENT_EMAIL) ?: ""
        if (emailAddress.isNotBlank())
            mBinding.etEmailAddress.setText(emailAddress)
        setupToolbar()
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
    }

    override fun observeApiCallbacks() {
        mViewModel.forgetPasswordLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    EmailSentActivity.startActivity(
                        this@ForgotPasswordActivity,
                        emailAddress,
                        it.message
                    )
                    finish()
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            longToast("" + errorObj.customMessage, ERROR)
                        else -> {
                        }
                    }
                }
            }
            mViewModel.forgetPasswordLiveData.postValue(null)
        }
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.btnContinue.setOnClickListener {
            hideKeyboard()
            if (checkScreenValidation(true)) {
                emailAddress = mBinding.etEmailAddress.text.toString()
                if (isNetwork())
                    mViewModel.forgetPasswordApiRequest(
                        mBinding.etEmailAddress.text.toString(),
                        this,
                        mDisposable
                    )
                else
                    longToast(
                        getString(R.string.no_internet, getString(R.string.to_send_email)),
                        ERROR
                    )
            }
        }

        mBinding.etEmailAddress.addTextChangedListener {
            checkScreenValidation(false)
        }

        mBinding.etEmailAddress.setOnFocusChangeListener { _, b ->
            if (b) {
                checkScreenValidation(false)
            }
        }
    }

    private fun checkScreenValidation(isSendEmailClicked: Boolean): Boolean {
        if (TextUtils.isEmpty(mBinding.etEmailAddress.text.toString().trim()) && !isSendEmailClicked) {
            mBinding.tlEmailAddress.isErrorEnabled = false
            return false
        } else if (TextUtils.isEmpty(mBinding.etEmailAddress.text.toString().trim())
            && isSendEmailClicked
        ) {
            mBinding.tlEmailAddress.isErrorEnabled = true
            mBinding.tlEmailAddress.error = getString(R.string.please_enter_email_address)
            return false
        } else if (isEmailInvalid(mBinding.etEmailAddress.text.toString().trim())) {
            mBinding.tlEmailAddress.isErrorEnabled = true
            mBinding.tlEmailAddress.error = getString(R.string.please_enter_valid_email_address)
            return false
        } else {
            mBinding.tlEmailAddress.isErrorEnabled = false
            return true
        }
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null) {
            longToast(error, ERROR)
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