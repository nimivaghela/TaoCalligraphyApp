package com.app.taocalligraphy.ui.otp_verification

import android.Manifest
import android.content.Intent
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityOtpverificationBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.ui.otp_verification.viewmodel.OtpVerificationViewModel
import com.app.taocalligraphy.ui.post_signup_questionnaire.WelcomeQuestionnaireActivity
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.questionary.QuestionnairesResultActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.GPSTracker
import com.app.taocalligraphy.utils.PermissionCallback
import com.app.taocalligraphy.utils.PermissionHelper
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_otpverification.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class OTPVerificationActivity : BaseActivity<ActivityOtpverificationBinding>() {

    override fun getResource() = R.layout.activity_otpverification
    private val mViewModel: OtpVerificationViewModel by viewModels()

    var emailAddress: String = ""
    var password: String = ""
    var isRememberMe: Boolean = false
    private var address = ""
    var gpsTracker: GPSTracker? = null

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, OTPVerificationActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }

        var isFromLogin: Boolean = false
    }

    override fun initView() {

        permissionHelper = PermissionHelper(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 1001
        )
        setupToolbar()
        getIntentData()
//        askLocationPermission()
        checkToken()
        mBinding.txtEmail.text = (emailAddress).replace(Regex("(?<=.{1}).(?=.*@)"), "*")
//        mBinding.otpView.requestFocusOTP()
        mBinding.otpView.requestFocus()
        showKeyboard()

        mBinding.etotp.doOnTextChanged { text, start, before, count ->
            if (text!!.length == 6) {
                hideKeyboard()
                if (isNetwork())
                    mViewModel.verifyEmailTokenRequest(
                        mBinding.etotp.text.toString(),
                        emailAddress,
                        password, getDeviceName(), getDeviceVersion(), address,
                        Settings.Secure.getString(
                            contentResolver,
                            Settings.Secure.ANDROID_ID
                        ),
                        this@OTPVerificationActivity, mDisposable
                    )
                else
                    longToast(
                        getString(R.string.no_internet, getString(R.string.to_verify_email)),
                        ERROR
                    )
            } else {
                resetOtp()
            }
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


//        mBinding.otpView.otpListener = object : OTPListener {
//            override fun onInteractionListener() {
//                showKeyboard()
//            }
//
//            override fun onOTPComplete(otp: String) {
//                if (otp.length == 6) {
//                    hideKeyboard()
//                    if (isNetwork())
//                        mViewModel.verifyEmailTokenRequest(
//                            mBinding.otpView.otp!!,
//                            emailAddress,
//                            password, getDeviceName(), getDeviceVersion(), address,
//                            Settings.Secure.getString(
//                                contentResolver,
//                                Settings.Secure.ANDROID_ID
//                            ),
//                            this@OTPVerificationActivity, mDisposable
//                        )
//                    else
//                        longToast(getString(R.string.no_internet,"to verify email"), ERROR)
//                } else
//                    mBinding.otpView.resetState()
//            }
//        }

        otpView.setOnFocusChangeListener { v, hasFocus ->
            showKeyboard()
        }

    }

    private fun setupToolbar() {
        mBinding.toolbar.ivBackToolbar.visible()
    }

    private fun getIntentData() {
        val intent = intent
        if (intent != null) {
            if (intent.hasExtra("emailAddress")) {
                emailAddress = intent.getStringExtra("emailAddress") ?: ""
            }
            if (intent.hasExtra("password")) {
                password = intent.getStringExtra("password") ?: ""
            }
            if (intent.hasExtra("isRememberMe")) {
                isRememberMe = intent.getBooleanExtra("isRememberMe", false)
            }
        }
    }

    override fun observeApiCallbacks() {
        mViewModel.verifyEmailTokenLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    if (it.data != null) {
                        userHolder.isLogin = true
                        it.data?.apply {
                            userHolder.setUserData(
                                accessToken,
                                id,
                                roleName,
                                firstName,
                                lastName,
                                email,
                                mobileNo.toString(),
                                ageRange.toString(),
                                gender.toString(),
                                originalProfilePicUrl,
                                thumbProfilePicUrl,
                                signupType,
                                socialId,
                                isEmailVerified,
                                region,
                                country,
                                isQuestionnaireCompleted,
                                isFreeTrialCompleted,
                                freeTrialDays,
                                freeTrialCompletionDate
                            )
                            userHolder.is12HourFormat = is12HourFormat ?: !getTimePickerTimeFormat(
                                this@OTPVerificationActivity
                            )
                            if (isQuestionnaireCompleted) {
                                QuestionnairesResultActivity.startActivity(this@OTPVerificationActivity)
                                finishAffinity()
                            } else {
                                SubscriptionActivity.startActivityForResult(this@OTPVerificationActivity,true)
                                finishAffinity()
                            }
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            longToast("" + errorObj.customMessage, ERROR)
                        else -> {
                            when (errorObj.errorCode) {
                                Constants.StatusCode.STATUS_401 -> {

                                }
                                Constants.StatusCode.STATUS_404 -> {
                                }
                                else -> {

                                }
                            }
                        }
                    }
                }
            }
        }

        mViewModel.resendOtpLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    longToast(it.message.toString(), SUCCESS)
                    mBinding.etotp.setText("")
                    resetOtp()
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
        }
    }

    override fun handleListener() {
        mBinding.toolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.tVResendCode.clickWithDebounce {
            mBinding.lblErrorMsg.gone()
            if (isNetwork()) {
                mViewModel.resendOtpApiRequest(emailAddress, this, mDisposable)
            }
        }
        mBinding.tVStartOver.clickWithDebounce {
            onBackPressed()
        }
        mBinding.otpView.clickWithDebounce {
        }
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        showOtpError()
        mBinding.lblErrorMsg.visible()
        mBinding.lblErrorMsg.text = getString(R.string.invalid_code_please_try_again)
    }

    override fun unauthorized(error: String?) {
//        super.unauthorized(error)
        showOtpError()
        mBinding.lblErrorMsg.text = error
    }

    override fun onOTPExpire(error: String?) {
        super.onUnknownError(error)
        showOtpError()
        mBinding.lblErrorMsg.visible()
        mBinding.lblErrorMsg.text = error
    }

    fun showOtpError() {
        mBinding.otpView.error = ""
        mBinding.otpView.isErrorEnabled = true
        mBinding.otpView.boxStrokeColor = getColor(R.color.red)
        mBinding.otpView.editText?.setTextColor(getColor(R.color.red))
    }

    fun resetOtp() {
        mBinding.otpView.isErrorEnabled = false
        mBinding.otpView.boxStrokeColor = getColor(R.color.medium_grey_30)
        mBinding.otpView.editText?.setTextColor(getColor(R.color.black))
        lblErrorMsg.gone()
    }

    private fun askLocationPermission() {
        permissionHelper?.requestPermissions(object : PermissionCallback {
            //this execute when all permissions are granted
            override fun onPermissionGranted() {
                address = getGpsLocationData()
            }

            //this will execute when all permissions are denied
            override fun onPermissionDenied() {
            }

            //this will execute when permission denied by system and this will move user to setting screen
            override fun onPermissionDeniedBySystem() {
            }

            // this will execute when some of permission is granted, in that case you can move user to
            // settings screen by calling openSetting with proper message for permissions which are not granted
            override fun onIndividualPermissionGranted(grantedPermission: Array<String>) {
                address = getGpsLocationData()
            }

            //this will execute when permission are not added in Manifest but asking for runtime
            override fun onPermissionNotFoundInManifest() {
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //you need to add default result into helper class to manage results and get result in helper callback
        permissionHelper?.onRequestPermissionResult(requestCode, permissions, grantResults)
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