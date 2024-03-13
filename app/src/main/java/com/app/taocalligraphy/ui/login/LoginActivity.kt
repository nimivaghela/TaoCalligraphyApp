package com.app.taocalligraphy.ui.login

import android.Manifest
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityLoginBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.CUSTOM_ERROR
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.NETWORK_ERROR
import com.app.taocalligraphy.other.Constants.StatusCode.STATUS_404
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.savedUserDetail
import com.app.taocalligraphy.ui.forgot_password.ForgotPasswordActivity
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.login.viewmodel.LoginViewModel
import com.app.taocalligraphy.ui.otp_verification.OTPVerificationActivity
import com.app.taocalligraphy.ui.post_signup_questionnaire.WelcomeQuestionnaireActivity
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.questionary.QuestionnairesResultActivity
import com.app.taocalligraphy.ui.welcome_login.WelcomeLoginActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.PermissionCallback
import com.app.taocalligraphy.utils.PermissionHelper
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    override fun getResource() = R.layout.activity_login
    private val mViewModel: LoginViewModel by viewModels()
    private val mProfileViewModel: ProfileViewModel by viewModels()
    private var emailAddress: String = ""
    private var password: String = ""
    private var socialId: String = ""
    private var firstName: String = ""
    private var lastName: String = ""
    private var address = ""

    companion object {
        fun startActivity(activity: AppCompatActivity, email: String = "") {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.putExtra("emailAddress", email)
            activity.startActivityWithAnimation(intent)
        }


    }

    override fun initView() {
        if (!isTablet(this)) {
            ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updatePadding(0, 0, 0, insets.bottom)
                WindowInsetsCompat.CONSUMED
            }
        }


        permissionHelper = PermissionHelper(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 1001
        )

//        askLocationPermission()

        mBinding.chkRememberMe.isChecked = savedUserDetail.isRememberMe
        if (savedUserDetail.isRememberMe) {
            mBinding.etEmailAddress.setText(savedUserDetail.saveEmailAddress)
            mBinding.etPassword.setText(savedUserDetail.password)
        } else {
            val email =
                intent?.extras?.getString("emailAddress", "") ?: ""

            if (email.isNotBlank()) {
                mBinding.etEmailAddress.setText(email)
            }
        }
        setupToolbar()
        checkToken()


    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
    }

    override fun observeApiCallbacks() {
        mViewModel.userLoginLiveData.observe(this) { response ->
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
                            savedUserDetail.saveUserDetails(
                                email, password,
                                mBinding.chkRememberMe.isChecked
                            )
                            userHolder.is12HourFormat =
                                is12HourFormat ?: !getTimePickerTimeFormat(this@LoginActivity)
                            //mViewModel.getUserDownloads(mViewModel)
//                            mProfileViewModel.getUserModulePermissionApi(this, mDisposable)
                            if (isQuestionnaireCompleted && (isFreeTrialCompleted ?: false)) {
                                MainActivity.startActivity(this@LoginActivity)
                                finishAffinity()
                            } else if (isQuestionnaireCompleted && !(isFreeTrialCompleted
                                    ?: false)
                            ) {
                                QuestionnairesResultActivity.startActivity(this@LoginActivity)
                                finishAffinity()
                            } else {
                                WelcomeQuestionnaireActivity.startActivity(this@LoginActivity)
                                finishAffinity()
                            }
                        }
                    }
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        else -> {
                            when (errorObj.errorCode) {
                                STATUS_404 -> {
//                                   val intent =
//                                    Intent(this@LoginActivity, SignUpActivity::class.java)
//                                    intent.putExtra("emailAddress", emailAddress)
//                                    intent.putExtra("password", password)
//                                    intent.putExtra("socialId", socialId)
//                                    intent.putExtra("firstName", firstName)
//                                    intent.putExtra("lastName", lastName)
//                                    startActivityWithAnimation(intent)
                                }
                                else -> {
//                                    errorObj.customMessage?.let { longToast(it) }
                                }
                            }
                        }
                    }
                }
                mViewModel.userLoginLiveData.postValue(null)
            }
        }

        mProfileViewModel.userModulePermissionData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { it ->
                    val enumSet = UserHolder.EnumUserModulePermission.values().toHashSet()
                    it.data?.let {
                        it.forEach { item ->
                            if (enumSet.any { it.name.equals(item.moduleName ?: "") }) {
                                UserHolder.EnumUserModulePermission.valueOf(
                                    item.moduleName ?: ""
                                ).permission = item
                            }
                        }
                    }
                    if (userHolder.isQuestionnaireCompleted && (userHolder.isFreeTrialCompleted
                            ?: false)
                    ) {
                        MainActivity.startActivity(this@LoginActivity)
                        finishAffinity()
                    } else if (userHolder.isQuestionnaireCompleted && !(userHolder.isFreeTrialCompleted
                            ?: false)
                    ) {
                        QuestionnairesResultActivity.startActivity(this@LoginActivity)
                        finishAffinity()
                    } else {
                        WelcomeQuestionnaireActivity.startActivity(this@LoginActivity)
                        finishAffinity()
                    }
                }

                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR -> errorObj.customMessage?.let {
                            longToast(
                                it,
                                errorObj.type
                            )
                        }
                        Constants.CUSTOM_ERROR -> errorObj.customMessage?.let {
                            longToast(
                                it,
                                errorObj.type
                            )
                        }
                    }
                }

            }
        }

    }

    override fun onVerificationError() {
        OTPVerificationActivity.isFromLogin = true
        val intent = Intent(
            this@LoginActivity,
            OTPVerificationActivity::class.java
        )
        intent.putExtra("emailAddress", emailAddress)
        intent.putExtra("password", password)
        intent.putExtra("isRememberMe", mBinding.chkRememberMe.isChecked)
        startActivityWithAnimation(intent)
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.chkRememberMe.setOnCheckedChangeListener { buttonView, isChecked ->

        }
        mBinding.tvForgotPassword.clickWithDebounce {
            hideKeyboard()
            val email = mBinding.etEmailAddress.text.toString()
            mBinding.etPassword.text!!.clear()
            mBinding.tlPassword.error = null
            mBinding.tlPassword.isErrorEnabled = false
            ForgotPasswordActivity.startActivity(this@LoginActivity, email)
        }

        mBinding.llSignUp.clickWithDebounce {
            WelcomeLoginActivity.startActivity(this)
            WelcomeLoginActivity.isFromLogin = false
//            SignUpActivity.startActivity(this@LoginActivity)
            finish()
        }

        mBinding.btnLogin.clickWithDebounce {
            hideKeyboard()
            if (checkScreenValidation()) {
                emailAddress = mBinding.etEmailAddress.text!!.toString().trim()
                socialId = ""
                firstName = ""
                lastName = ""
                password = mBinding.etPassword.text!!.toString().trim()
                if (isNetwork())
                    mViewModel.userLogin(
                        mBinding.etEmailAddress.text!!.toString().trim(),
                        mBinding.etPassword.text!!.toString().trim(),
                        "NORMAL",
                        "",
                        getDeviceName(),
                        getDeviceVersion(),
                        address,
                        Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID),
                        this@LoginActivity,
                        mDisposable
                    )
                else
                    longToast(getString(R.string.no_internet, getString(R.string.to_login)), ERROR)
            }
        }

        mBinding.etEmailAddress.addTextChangedListener {
            emailValidation(false)
        }

        mBinding.etEmailAddress.setOnFocusChangeListener { _, b ->
            if (b) {
                emailValidation(false)
            }
        }

        mBinding.etPassword.addTextChangedListener {
            passwordValidation(false)
        }

        mBinding.etPassword.setOnFocusChangeListener { _, b ->
            if (b) {
                passwordValidation(false)
            }
        }
    }

    private fun checkScreenValidation(): Boolean {
        val isValidEmail = emailValidation(true)
        val isValidPassword = passwordValidation(true)

        return isValidEmail && isValidPassword
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
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //you need to add default result into helper class to manage results and get result in helper callback
        permissionHelper?.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null) {
            longToast(error, ERROR)
        }
    }

    override fun unauthorized(error: String?) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
    }

    private fun emailValidation(isLoginClicked: Boolean): Boolean {
        if (TextUtils.isEmpty(mBinding.etEmailAddress.text.toString().trim()) && !isLoginClicked) {
            mBinding.tlEmailAddress.isErrorEnabled = false
            return false
        } else if (TextUtils.isEmpty(mBinding.etEmailAddress.text.toString().trim())
            && isLoginClicked
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

    private fun passwordValidation(isLoginClicked: Boolean): Boolean {
        return if (TextUtils.isEmpty(
                mBinding.etPassword.text.toString().trim()
            ) && !isLoginClicked
        ) {
            mBinding.tlPassword.isErrorEnabled = false
            false
        } else if (TextUtils.isEmpty(mBinding.etPassword.text.toString().trim())
            && isLoginClicked
        ) {
            mBinding.tlPassword.isErrorEnabled = true
            mBinding.tlPassword.error = getString(R.string.please_enter_password)
            false
        } else {
            mBinding.tlPassword.isErrorEnabled = false
            true
        }
    }
}