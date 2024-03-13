package com.app.taocalligraphy.ui.reset_password

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityResetPasswordBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.reset_password.viewmodel.ResetPasswordViewModel
import com.app.taocalligraphy.ui.welcome.WelcomeActivity
import com.app.taocalligraphy.utils.PasswordStrength
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ResetPasswordActivity : BaseActivity<ActivityResetPasswordBinding>() {

    private val mViewModel: ResetPasswordViewModel by viewModels()

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, ResetPasswordActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_reset_password

    override fun initView() {
        mBinding.passwordStrength.progressTintList = ColorStateList.valueOf(Color.WHITE)
    }

    override fun observeApiCallbacks() {
        mViewModel.resetPasswordLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    Constants.isResetPasswordShowSnackBar = true
                    Constants.resetPasswordSuccessMessage =
                        it.message.toString()
                    TaoCalligraphy.instance.resetPasswordToken = ""
                    WelcomeActivity.startActivity(this@ResetPasswordActivity)
                    finish()
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            longToast("" + errorObj.customMessage, Constants.ERROR)
                        else -> {
                        }
                    }
                }
            }
            mViewModel.resetPasswordLiveData.postValue(null)
        }
    }

    override fun handleListener() {
        mBinding.etPassword.addTextChangedListener {
            updatePasswordStrengthView(it.toString())
            updateConfirmPasswordStrengthView()
        }

        mBinding.etPassword.setOnFocusChangeListener { _, b ->
            if (b) {
                updatePasswordStrengthView(mBinding.etPassword.text.toString())
            }
        }

        mBinding.etConfirmPassword.addTextChangedListener {
            updateConfirmPasswordStrengthView()
        }

        mBinding.etConfirmPassword.setOnFocusChangeListener { _, b ->
            if (b) {
                updateConfirmPasswordStrengthView()
            }
        }

        mBinding.btnResetPassword.setOnClickListener {
            if (checkScreenValidation()) {
                if (isNetwork())
                    mViewModel.resetPasswordApiRequest(
                        mBinding.etPassword.text.toString(),
                        TaoCalligraphy.instance.resetPasswordToken,
                        this,
                        mDisposable
                    )
                else
                    longToast(getString(R.string.no_internet), Constants.ERROR)
            }
        }
    }

    private fun updateConfirmPasswordStrengthView(isButtonClicked: Boolean = false) {
        if (!isButtonClicked && TextUtils.isEmpty(
                mBinding.etConfirmPassword.text.toString().trim()
            )
        ) {
            mBinding.tvPwdMustMatch.setTextColor(ContextCompat.getColor(this, R.color.dark_grey))
            mBinding.imgPassword.gone()
            mBinding.tvPwdMustMatch.text = getString(R.string.password_must_match)
        } else if (mBinding.etPassword.text.toString()
                .trim() != mBinding.etConfirmPassword.text.toString().trim()
        ) {
            mBinding.tvPwdMustMatch.setTextColor(ContextCompat.getColor(this, R.color.red))
            mBinding.imgPassword.visible()
            mBinding.imgPassword.setBackgroundResource(R.drawable.ic_close)
            mBinding.tvPwdMustMatch.text = getString(R.string.password_do_not_match)
        } else {
            mBinding.tvPwdMustMatch.setTextColor(ContextCompat.getColor(this, R.color.green))
            mBinding.imgPassword.visible()
            mBinding.imgPassword.setBackgroundResource(R.drawable.ic_check)
            mBinding.tvPwdMustMatch.text = getString(R.string.password_match)
        }

    }

    private fun updatePasswordStrengthView(password: String) {
        if (TextView.VISIBLE != mBinding.lblPasswordStrength.visibility)
            return
        if (TextUtils.isEmpty(password)) {
            mBinding.lblPasswordStrength.text = getString(R.string.password_strength)
            mBinding.passwordStrength.progress = 100
            mBinding.passwordStrength.progressTintList = ColorStateList.valueOf(Color.WHITE)
            mBinding.tlPassword.isErrorEnabled = false
            return
        }
        mBinding.passwordStrength.progress = 0
        val str = PasswordStrength.calculateStrength(password)
        mBinding.lblPasswordStrength.text = str.getText(this)
        mBinding.passwordStrength.progressTintList = ColorStateList.valueOf(str.color)
        mBinding.lblPwdMustMatch.gone()
        when (str) {
            PasswordStrength.WEAK -> {
                mBinding.tlConfirmPassword.gone()
                mBinding.lblPwdMustMatch.gone()
                mBinding.tlPassword.error =
                    getString(R.string.password_must_be_at_least_6_letters_and_must_also_include_special_characters_and_numbers)

                mBinding.passwordStrength.progress = 30
            }
            PasswordStrength.MEDIUM -> {
                mBinding.tlConfirmPassword.gone()
                mBinding.lblPwdMustMatch.gone()
                mBinding.tlPassword.error =
                    getString(R.string.password_must_be_at_least_6_letters_and_must_also_include_special_characters_and_numbers)

                mBinding.passwordStrength.progress = 60
            }
            PasswordStrength.STRONG -> {
                mBinding.tlConfirmPassword.visible()
                mBinding.lblPwdMustMatch.visible()
                mBinding.tlPassword.isErrorEnabled = false
                mBinding.passwordStrength.progress = 100
            }
            else -> {
                mBinding.lblPasswordStrength.text = getString(R.string.password_strength)
                mBinding.passwordStrength.progress = 100
                mBinding.passwordStrength.progressTintList = ColorStateList.valueOf(Color.WHITE)
                mBinding.tlPassword.error =
                    getString(R.string.password_must_be_at_least_6_letters_and_must_also_include_special_characters_and_numbers)
            }
        }
    }

    private fun checkScreenValidation(): Boolean {
        var isValidPassword = false
        var isValidConfirmPassword = true

        if (TextUtils.isEmpty(mBinding.etPassword.text.toString().trim())) {
            mBinding.tlPassword.error = getString(R.string.please_enter_password)
            isValidPassword = false
        } else {
            if (mBinding.etPassword.text.toString().trim().length < 10) {
                mBinding.tlPassword.error =
                    getString(R.string.password_must_contain_at_least_6_characters)
                isValidPassword = false
            } else {
                if (!isValidPassword(mBinding.etPassword.text.toString().trim())) {
                    mBinding.tlPassword.error =
                        getString(R.string.password_must_be_at_least_6_letters_and_must_also_include_special_characters_and_numbers)
                    isValidPassword = false
                } else {
                    if (isEmoji(mBinding.etPassword.text.toString().trim())) {
                        mBinding.tlPassword.error =
                            getString(R.string.please_do_not_enter_emojis_into_the_password)
                        isValidPassword = false
                    } else {
                        mBinding.tlPassword.isErrorEnabled = false
                        isValidPassword = true
                    }
                }
            }
        }

        if (TextUtils.isEmpty(
                mBinding.etConfirmPassword.text.toString().trim()
            ) || !isValidPassword(
                mBinding.etConfirmPassword.text.toString().trim()
            ) || mBinding.etConfirmPassword.text.toString()
                .trim().length < 10 && mBinding.etConfirmPassword.text.toString().trim().length > 16
            || (mBinding.etPassword.text.toString()
                .trim() != mBinding.etConfirmPassword.text.toString().trim())
            ||
            isEmoji(mBinding.etConfirmPassword.text.toString().trim())
        ) {
            updateConfirmPasswordStrengthView(true)
            isValidConfirmPassword = false
        }

        return isValidPassword && isValidConfirmPassword
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

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null) {
            longToast(error, Constants.ERROR)
        }
    }

    override fun onOTPExpire(error: String?) {
        super.onOTPExpire(error)
        if (error != null) {
            longToast(error, Constants.ERROR)
        }
    }

    override fun onBackPressed() {
        TaoCalligraphy.instance.resetPasswordToken = ""
        WelcomeActivity.startActivity(this@ResetPasswordActivity)
        finish()
    }

}