package com.app.taocalligraphy.ui.welcome

import android.content.Intent
import android.content.res.Configuration
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityWelcomeBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.ui.experience.BeginExperienceActivity
import com.app.taocalligraphy.ui.experience.tablet.BeginExperienceTabletActivity
import com.app.taocalligraphy.ui.welcome.viewmodel.WelcomeViewModel
import com.app.taocalligraphy.ui.welcome_login.WelcomeLoginActivity
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {

    override fun getResource() = R.layout.activity_welcome

    private val viewModel: WelcomeViewModel by viewModels()

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, WelcomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Constants.isResetPasswordShowSnackBar) {
            longToast(
                Constants.resetPasswordSuccessMessage,
                Constants.SUCCESS
            )
            Constants.isResetPasswordShowSnackBar = false
        }
        noInternetConnectionDialog?.dismiss()
    }

    override fun initView() {
        val spanText = SpannableStringBuilder()
        val spanTextFooter = SpannableStringBuilder()

        if (isTablet(this)) {
            val orientation = this.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                spanText.append(getString(R.string.home_is) + " ")
                var lastPos = spanText.length
                spanText.setSpan(
                    TextAppearanceSpan(
                        this,
                        R.style.TextViewCormorantBoldStyleWelcomeNormal
                    ),
                    0, lastPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                var start = lastPos
                spanText.append(getString(R.string.heart) + " ")
                lastPos = spanText.length
                spanText.setSpan(
                    TextAppearanceSpan(
                        this,
                        R.style.TextViewCormorantBoldStyleWelcomeHeart
                    ),
                    start, lastPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                start = lastPos
                spanText.append(getString(R.string.str_is))
                lastPos = spanText.length
                spanText.setSpan(
                    TextAppearanceSpan(
                        this,
                        R.style.TextViewCormorantBoldStyleWelcomeNormal
                    ),
                    start, lastPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spanTextFooter.append(getString(R.string.welcome) + " ")
                var lastPosFooter = spanTextFooter.length
                spanTextFooter.setSpan(
                    TextAppearanceSpan(
                        this,
                        R.style.TextViewCormorantBoldStyleWelcomeNormal
                    ),
                    0, lastPosFooter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                val startFooter = lastPosFooter
                spanTextFooter.append(getString(R.string.home))
                lastPosFooter = spanTextFooter.length
                spanTextFooter.setSpan(
                    TextAppearanceSpan(
                        this,
                        R.style.TextViewCormorantBoldStyleWelcomeHeart
                    ),
                    startFooter, lastPosFooter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                mBinding.lblWelcome.text = spanTextFooter
            } else {
                spanText.append(getString(R.string.home_is) + " ")
                var lastPos = spanText.length
                spanText.setSpan(
                    TextAppearanceSpan(
                        this,
                        R.style.TextViewCormorantBoldStyleWelcomeNormal
                    ),
                    0, lastPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                var start = lastPos
                spanText.append(getString(R.string.heart) + " ")
                lastPos = spanText.length
                spanText.setSpan(
                    TextAppearanceSpan(
                        this,
                        R.style.TextViewCormorantBoldStyleWelcomeHeart
                    ),
                    start, lastPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                start = lastPos
                spanText.append(getString(R.string.str_is))
                lastPos = spanText.length
                spanText.setSpan(
                    TextAppearanceSpan(
                        this,
                        R.style.TextViewCormorantBoldStyleWelcomeNormal
                    ),
                    start, lastPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spanTextFooter.append(getString(R.string.welcome) + " ")
                var lastPosFooter = spanTextFooter.length
                spanTextFooter.setSpan(
                    TextAppearanceSpan(
                        this,
                        R.style.TextViewCormorantBoldStyleWelcomeNormal
                    ),
                    0, lastPosFooter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                val startFooter = lastPosFooter
                spanTextFooter.append(getString(R.string.home))
                lastPosFooter = spanTextFooter.length
                spanTextFooter.setSpan(
                    TextAppearanceSpan(
                        this,
                        R.style.TextViewCormorantBoldStyleWelcomeHeart
                    ),
                    startFooter, lastPosFooter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                mBinding.lblWelcome.text = spanTextFooter
            }
        } else {
            spanText.append(getString(R.string.home_is) + " ")
            var lastPos = spanText.length
            spanText.setSpan(
                TextAppearanceSpan(
                    this,
                    R.style.TextViewCormorantBoldStyleWelcomeNormal
                ),
                0, lastPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            var start = lastPos
            spanText.append(getString(R.string.heart) + " ")
            lastPos = spanText.length
            spanText.setSpan(
                TextAppearanceSpan(
                    this,
                    R.style.TextViewCormorantBoldStyleWelcomeHeart
                ),
                start, lastPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            start = lastPos
            spanText.append(getString(R.string.str_is))
            lastPos = spanText.length
            spanText.setSpan(
                TextAppearanceSpan(
                    this,
                    R.style.TextViewCormorantBoldStyleWelcomeNormal
                ),
                start, lastPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        mBinding.txtWelcomeHeader.text = spanText
    }

    override fun observeApiCallbacks() {
        viewModel.getInitialUserExperience.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.data?.let { userExperience ->
                        viewModel.getInitialUserExperience.value?.apiResponse = null
                        if (isTablet(this)) {
                            BeginExperienceTabletActivity.startActivity(this, userExperience)
                        } else {
                            BeginExperienceActivity.startActivity(this, userExperience)
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        else -> {
                            when (errorObj.errorCode) {
                                Constants.StatusCode.STATUS_404 -> {
                                    longToast(
                                        getString(R.string.initial_experince_content_not_found),
                                        ERROR
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun handleListener() {
        mBinding.btnBeginYourExperienceSplash.clickWithDebounce {
            if (isNetwork()) {
                viewModel.getInitialUserExperience(this, mDisposable)
            } else
                longToast(
                    getString(
                        R.string.no_internet,
                        getString(R.string.to_begin_your_experince)
                    ), ERROR
                )
        }

        mBinding.btnLoginWelcome.clickWithDebounce {
            if (isNetwork()) {
                WelcomeLoginActivity.startActivity(this@WelcomeActivity)
                WelcomeLoginActivity.isFromLogin = true
            } else {
                longToast(getString(R.string.no_internet, getString(R.string.to_log_in)), ERROR)
            }
        }

        mBinding.btnSignUpWelcome.clickWithDebounce {
            if (isNetwork()) {
                WelcomeLoginActivity.startActivity(this@WelcomeActivity)
                WelcomeLoginActivity.isFromLogin = false
            } else {
                longToast(getString(R.string.no_internet, getString(R.string.to_sign_up)), ERROR)
            }
        }
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        showToast(this, getString(R.string.press_again_to_exit))
        addDelay({ doubleBackToExitPressedOnce = false }, 2000)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
    }
}