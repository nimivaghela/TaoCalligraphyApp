package com.app.taocalligraphy.ui.signup

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.provider.Settings
import android.text.InputFilter
import android.text.TextUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.core.widget.addTextChangedListener
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivitySignUpBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.ui.login.LoginActivity
import com.app.taocalligraphy.ui.otp_verification.OTPVerificationActivity
import com.app.taocalligraphy.ui.post_signup_questionnaire.WelcomeQuestionnaireActivity
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.questionary.QuestionnairesResultActivity
import com.app.taocalligraphy.ui.settings.dialog.StaticContentDialog
import com.app.taocalligraphy.ui.signup.viewmodel.SignUpViewModel
import com.app.taocalligraphy.ui.welcome_login.WelcomeLoginActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.PasswordStrength
import com.app.taocalligraphy.utils.PermissionCallback
import com.app.taocalligraphy.utils.PermissionHelper
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


@AndroidEntryPoint
class SignUpActivity : BaseActivity<ActivitySignUpBinding>() {

    override fun getResource() = R.layout.activity_sign_up
    private val mViewModel: SignUpViewModel by viewModels()

    private var isEmailExist: Boolean = false
    private var isSocialSignIn: Boolean = false
    private var registerType = ""
    private var emailAddress: String = ""
    private var password: String = ""
    private var isRememberMe: Boolean = false
    private var socialId: String = ""
    private var firstName: String = ""
    private var lastName: String = ""
    private var address = ""

    private var invalidCharacters: HashSet<Char> = HashSet(
        listOf(
            '!',
            '-',
            '@',
            '#',
            '£',
            '€',
            '$',
            '¢',
            '¥',
            '§',
            '%',
            '&',
            '*',
            '(',
            ')',
            '_',
            '+',
            '=',
            '{',
            '}',
            '[',
            ']',
            '|',
            '\\',
            '/',
            ':',
            ';',
            '"',
            '<',
            '>',
            ',',
            '?',
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9'
        )
    )

    private val filter = InputFilter { source, _, _, _, _, _ ->
        return@InputFilter when {
            source.isNotEmpty() -> {
                source.trim {
                    invalidCharacters.contains(it) || !it.isLetter()
                }
            }
            else -> null
        }
    }


    companion object {
        var previousActivity: AppCompatActivity? = null
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, SignUpActivity::class.java)
            previousActivity = activity
            activity.startActivityWithAnimation(intent)
        }

        var isCorporateUser: Boolean = false
    }

    override fun initView() {
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(0, 0, 0, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
        permissionHelper = PermissionHelper(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 1001
        )
        setupToolbar()
        getIntentData()
        checkToken()
//        askLocationPermission()
        setSignUpSelection()

        mBinding.passwordStrength.progressTintList = ColorStateList.valueOf(Color.WHITE)

        mBinding.etFirstName.filters = arrayOf(filter)
        mBinding.etLastName.filters = arrayOf(filter)
    }


    private fun getIntentData() {
        val intent = intent
        if (intent != null) {
            if (intent.hasExtra("isSocialSignIn")) {
                isSocialSignIn = intent.getBooleanExtra("isSocialSignIn", false)
            }
            if (intent.hasExtra("isEmailExist")) {
                isEmailExist = intent.getBooleanExtra("isEmailExist", false)
            }
            if (intent.hasExtra("registerType")) {
                registerType = intent.getStringExtra("registerType") ?: ""
            }
            if (intent.hasExtra("emailAddress")) {
                emailAddress = intent.getStringExtra("emailAddress") ?: ""
            }
            if (intent.hasExtra("password")) {
                password = intent.getStringExtra("password") ?: ""
            }
            if (intent.hasExtra("isRememberMe")) {
                isRememberMe = intent.getBooleanExtra("isRememberMe", false)
            }
            if (intent.hasExtra("socialId")) {
                socialId = intent.getStringExtra("socialId") ?: ""
            }
            if (intent.hasExtra("firstName")) {
                firstName = intent.getStringExtra("firstName") ?: ""
            }
            if (intent.hasExtra("lastName")) {
                lastName = intent.getStringExtra("lastName") ?: ""
            }
        }
    }

    private fun setSignUpSelection() {
        if (!isCorporateUser) {
            mBinding.ivPersonal.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.vd_status_selected
                )
            )
            mBinding.ivCorporate.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.vd_status_unselected
                )
            )
            mBinding.tlCorporationCode.isGone = true
        } else {
            mBinding.ivPersonal.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.vd_status_unselected
                )
            )
            mBinding.ivCorporate.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.vd_status_selected
                )
            )
            mBinding.tlCorporationCode.isVisible = true
        }
        if (isSocialSignIn) {
            mBinding.tlPassword.inVisible()
            mBinding.relPassword.inVisible()
            mBinding.tlConfirmPassword.gone()
            mBinding.lblPwdMustMatch.gone()
            mBinding.etFirstName.setText(firstName)
            mBinding.etLastName.setText(lastName)
            mBinding.etEmailAddress.setText(emailAddress)

            mBinding.etEmailAddress.isEnabled = !isEmailExist
            if (isEmailExist)
                mBinding.etEmailAddress.setTextColor(getColor(R.color.dark_grey_50))
        }
    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
    }

    override fun observeApiCallbacks() {

        mViewModel.staticContentResponseModel.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    val dialog = StaticContentDialog.newInstance(
                        response.apiResponse?.data?.title ?: "",
                        response.apiResponse?.data?.description ?: ""
                    )
                    dialog.show(
                        getFragmentTransactionFromTag(StaticContentDialog.TAG),
                        StaticContentDialog.TAG
                    )

                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        else -> {
                            // do Nothing in else
                        }
                    }
                }
                mViewModel.staticContentResponseModel.postValue(null)
            }
        }

        mViewModel.userSignUpnLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    if (isEmailExist) {
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
                                if (isQuestionnaireCompleted) {
                                    QuestionnairesResultActivity.startActivity(this@SignUpActivity)
                                    finishAffinity()
                                } else {
                                    if (isSocialSignIn) {
                                        SubscriptionActivity.startActivityForResult(
                                            this@SignUpActivity,
                                            true
                                        )
                                    } else {
                                        WelcomeQuestionnaireActivity.startActivity(this@SignUpActivity)
                                        finishAffinity()
                                    }
                                }
                            }
                        }
                    } else {
                        OTPVerificationActivity.isFromLogin = false
                        val intent =
                            Intent(this@SignUpActivity, OTPVerificationActivity::class.java)
                        intent.putExtra("emailAddress", emailAddress)
                        intent.putExtra("password", password)
                        intent.putExtra("isRememberMe", isRememberMe)
                        startActivityWithAnimation(intent)
                        finish()
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
                                Constants.StatusCode.STATUS_409 -> {
                                    this@SignUpActivity.showCustomDialog(
                                        message = getString(R.string.user_already_exist),
                                        negativeButton = getString(R.string.dialog_go_back),
                                        positiveButton = getString(R.string.login),
                                        neutralButton = null,
                                        customDialogListener = object : CustomDialogListener {
                                            override fun onPositiveClicked() {
                                                LoginActivity.startActivity(
                                                    this@SignUpActivity,
                                                    mBinding.etEmailAddress.text.toString()
                                                )
                                                finish()
                                            }

                                            override fun onNegativeClicked() {
                                                WelcomeLoginActivity.startActivity(this@SignUpActivity)
                                                WelcomeLoginActivity.isFromLogin = false
                                                previousActivity?.finish()
                                                finish()
                                            }

                                            override fun onNeutralClicked() {
                                            }
                                        })
                                }
                                else -> {}
                            }
                        }
                    }
                }
                mViewModel.userSignUpnLiveData.postValue(null)
            }
        }
    }

    @SuppressLint("HardwareIds")
    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.llLogin.clickWithDebounce {
//            LoginActivity.startActivity(this)
            resetViewAgain()
            WelcomeLoginActivity.startActivity(this)
            WelcomeLoginActivity.isFromLogin = true
        }
        mBinding.tvTermsAndCondition.clickWithDebounce {
            getStaticContent(Constants.StaticContent.TERMS_CONDITIONS)
        }
        mBinding.tvPrivacyPolicy.clickWithDebounce {
            getStaticContent(Constants.StaticContent.PRIVACY_POLICY)
        }

        mBinding.llPersonal.clickWithDebounce {
            isCorporateUser = false
//            setSignUpSelection()
            /* mBinding.apply {
                 etFirstName.text!!.clear()
                 etLastName.text!!.clear()
                 etCorporationCode.text!!.clear()
                 etEmailAddress.text!!.clear()
                 etPassword.text!!.clear()
                 etConfirmPassword.text!!.clear()
             }*/
        }
        mBinding.llCorporate.clickWithDebounce {
            isCorporateUser = true
            setSignUpSelection()
            mBinding.apply {
                etFirstName.text!!.clear()
                etLastName.text!!.clear()
                etCorporationCode.text!!.clear()
                etEmailAddress.text!!.clear()
                etPassword.text!!.clear()
                etConfirmPassword.text!!.clear()
            }
        }

        mBinding.etFirstName.addTextChangedListener {
            firstNameValidation(false)
        }

        mBinding.etFirstName.setOnFocusChangeListener { _, b ->
            if (b) {
                firstNameValidation(false)
            }
        }

        mBinding.etLastName.addTextChangedListener {
            lastNameValidation(false)
        }

        mBinding.etLastName.setOnFocusChangeListener { _, b ->
            if (b) {
                lastNameValidation(false)
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

        mBinding.btnSignUp.clickWithDebounce {
            hideKeyboard()
            if (isSocialSignIn) {
                if (checkSocialScreenValidation()) {
                    emailAddress = mBinding.etEmailAddress.text!!.toString()
                    if (isEmailExist) {
                        if (isNetwork())
                            mViewModel.userSignUp(
                                mBinding.etFirstName.text!!.toString().trim(),
                                mBinding.etLastName.text!!.toString().trim(),
                                mBinding.etEmailAddress.text!!.toString().trim(),
                                "",
                                registerType,
                                socialId,
                                false, getDeviceName(), getDeviceVersion(), address,
                                Settings.Secure.getString(
                                    this.contentResolver,
                                    Settings.Secure.ANDROID_ID
                                ),
                                TaoCalligraphy.instance.referralCode,
                                this,
                                mDisposable
                            )
                        else
                            longToast(
                                getString(
                                    R.string.no_internet,
                                    getString(R.string.to_sign_up)
                                ), ERROR
                            )
                    } else {
                        if (isNetwork())
                            mViewModel.userSignUp(
                                mBinding.etFirstName.text!!.toString().trim(),
                                mBinding.etLastName.text!!.toString().trim(),
                                mBinding.etEmailAddress.text!!.toString().trim(),
                                "",
                                registerType,
                                socialId,
                                true,
                                getDeviceName(),
                                getDeviceVersion(),
                                address,
                                Settings.Secure.getString(
                                    this.contentResolver,
                                    Settings.Secure.ANDROID_ID
                                ),
                                TaoCalligraphy.instance.referralCode,
                                this,
                                mDisposable
                            )
                        else
                            longToast(
                                getString(
                                    R.string.no_internet,
                                    getString(R.string.to_sign_up)
                                ), ERROR
                            )
                    }
                }
            } else {
                if (checkScreenValidation()) {
                    emailAddress = mBinding.etEmailAddress.text!!.toString().trim()
                    password = mBinding.etPassword.text!!.toString().trim()
                    if (isNetwork())
                        mViewModel.userSignUp(
                            mBinding.etFirstName.text!!.toString().trim(),
                            mBinding.etLastName.text!!.toString().trim(),
                            mBinding.etEmailAddress.text!!.toString().trim(),
                            mBinding.etPassword.text!!.toString().trim(),
                            "NORMAL",
                            "",
                            true, getDeviceName(), getDeviceVersion(), address,
                            Settings.Secure.getString(
                                this.contentResolver,
                                Settings.Secure.ANDROID_ID
                            ),
                            TaoCalligraphy.instance.referralCode,
                            this,
                            mDisposable
                        )
                    else
                        longToast(
                            getString(R.string.no_internet, getString(R.string.to_sign_up)),
                            ERROR
                        )
                }

            }
        }
    }

    private fun getStaticContent(type: String) {
        if (isNetwork())
            mViewModel.getStaticData(
                type,
                this,
                mDisposable
            )
        else
            longToast(getString(R.string.no_internet, getString(R.string.to_sign_up)), ERROR)
    }

    private fun resetViewAgain() {
        mBinding.apply {
            etFirstName.text?.clear()
            tlFirstName.error = null
            etLastName.text?.clear()
            tlLastName.error = null
            etLastName.clearFocus()
            etCorporationCode.text?.clear()
            tlCorporationCode.error = null
            etCorporationCode.clearFocus()
            etEmailAddress.text?.clear()
            tlEmailAddress.error = null
            etEmailAddress.clearFocus()
            etPassword.text?.clear()
            tlPassword.error = null
            etPassword.clearFocus()
            etConfirmPassword.text?.clear()
            tlConfirmPassword.error = null
            etConfirmPassword.clearFocus()
            etFirstName.requestFocus()
            tlConfirmPassword.gone()
            lblPwdMustMatch.gone()
            passwordStrength.progress = 0
            passwordStrength.progressTintList = ColorStateList.valueOf(Color.WHITE)
        }
        mBinding.root.requestLayout()
    }

    private fun updateConfirmPasswordStrengthView(isSignupButtonClicked: Boolean = false) {
        if (!isSignupButtonClicked && TextUtils.isEmpty(
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
            mBinding.imgPassword.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
            mBinding.imgPassword.setBackgroundResource(R.drawable.ic_close)
            mBinding.tvPwdMustMatch.text = getString(R.string.password_do_not_match)
        } else {
            mBinding.tvPwdMustMatch.setTextColor(ContextCompat.getColor(this, R.color.white))
            mBinding.imgPassword.visible()
            mBinding.imgPassword.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
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
        when (str) {
            PasswordStrength.WEAK -> {
                mBinding.tlConfirmPassword.gone()
                mBinding.lblPwdMustMatch.gone()
                mBinding.tlPassword.isErrorEnabled = true
                mBinding.tlPassword.error =
                    getString(R.string.password_must_be_at_least_6_letters_and_must_also_include_special_characters_and_numbers)

                mBinding.passwordStrength.progress = 30
            }
            PasswordStrength.MEDIUM -> {
                mBinding.tlConfirmPassword.gone()
                mBinding.lblPwdMustMatch.gone()
                mBinding.tlPassword.isErrorEnabled = true
                mBinding.tlPassword.error =
                    getString(R.string.password_must_be_at_least_6_letters_and_must_also_include_special_characters_and_numbers)

                mBinding.passwordStrength.progress = 60
            }
            PasswordStrength.STRONG -> {
                mBinding.tlConfirmPassword.visible()
                mBinding.lblPwdMustMatch.visible()
                mBinding.tlPassword.isErrorEnabled = false
                mBinding.passwordStrength.progress = 100
                /* mBinding.passwordStrength.progressDrawable =
                     ContextCompat.getDrawable(this, R.drawable.strong_password_bg)*/
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
        var isValidFirstName = false
        var isValidLastName = false
        var isValidEmail = false
        var isValidPassword = false
        var isValidConfirmPassword = true

        isValidFirstName = firstNameValidation(true)
        isValidLastName = lastNameValidation(true)
        isValidEmail = emailValidation(true)

        if (isCorporateUser) {
            if (TextUtils.isEmpty(mBinding.etCorporationCode.text.toString().trim())) {
                mBinding.tlCorporationCode.error = getString(R.string.please_enter_corporate_code)
                return false
            } else {
                mBinding.tlCorporationCode.isErrorEnabled = false
            }
        }

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
        } else if (isValidFirstName && isValidLastName && isValidEmail && isValidPassword && isValidConfirmPassword) {
            if (!mBinding.chbTermsCondition.isChecked) {
                longToast(
                    getString(R.string.please_agree_to_our_terms_and_conditions_and_privacy_policy),
                    ERROR
                )
                return false
            }
        } else return false

        return isValidFirstName && isValidLastName && isValidEmail && isValidPassword && isValidConfirmPassword
    }

    private fun checkSocialScreenValidation(): Boolean {
        var isValidFirstName = false
        var isValidLastName = false
        var isValidEmail = false

        if (TextUtils.isEmpty(mBinding.etFirstName.text.toString().trim())) {
            mBinding.tlFirstName.error = getString(R.string.please_enter_first_name)
            isValidFirstName = false
        } else {
            if (mBinding.etFirstName.text.toString().trim().length > 30) {
                mBinding.tlFirstName.error =
                    getString(R.string.first_name_should_not_exceed_more_than_30_characters)
                isValidFirstName = false
            } else {
                mBinding.tlFirstName.isErrorEnabled = false
                isValidFirstName = true
            }
        }

        if (TextUtils.isEmpty(mBinding.etLastName.text.toString().trim())) {
            mBinding.tlLastName.error = getString(R.string.please_enter_last_name)
            isValidLastName = false
        } else {
            if (mBinding.etLastName.text.toString().trim().length > 30) {
                mBinding.tlLastName.error =
                    getString(R.string.last_name_should_not_exceed_more_than_30_characters)
                isValidLastName = false
            } else {
                mBinding.tlLastName.isErrorEnabled = false
                isValidLastName = true
            }
        }

        if (isCorporateUser) {
            if (TextUtils.isEmpty(mBinding.etCorporationCode.text.toString().trim())) {
                mBinding.tlCorporationCode.error = getString(R.string.please_enter_corporate_code)
                return false
            } else {
                mBinding.tlCorporationCode.isErrorEnabled = false
            }
        }
        if (TextUtils.isEmpty(mBinding.etEmailAddress.text.toString().trim())) {
            mBinding.tlEmailAddress.error = getString(R.string.please_enter_email_address)
            isValidEmail = false
        } else {
            if (isEmailInvalid(mBinding.etEmailAddress.text.toString().trim())) {
                mBinding.tlEmailAddress.error = getString(R.string.please_enter_valid_email_address)
                isValidEmail = false
            } else {
                mBinding.tlEmailAddress.isErrorEnabled = false
                isValidEmail = true
            }
        }
        if (isValidFirstName && isValidLastName && isValidEmail) {
            if (!mBinding.chbTermsCondition.isChecked) {
                longToast(
                    getString(R.string.please_agree_to_our_terms_and_conditions_and_privacy_policy),
                    ERROR
                )
                return false
            }
        } else return false

        return isValidFirstName && isValidLastName && isValidEmail
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null && !error.lowercase()
                .contains("User already exist with this email id".lowercase())
        ) {
            longToast(error, ERROR)
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
    }

    private fun firstNameValidation(isSignupClicked: Boolean): Boolean {
        if (TextUtils.isEmpty(mBinding.etFirstName.text.toString().trim()) && !isSignupClicked) {
            mBinding.tlFirstName.isErrorEnabled = false
            return false
        } else if (TextUtils.isEmpty(mBinding.etFirstName.text.toString().trim())
            && isSignupClicked
        ) {
            mBinding.tlFirstName.isErrorEnabled = true
            mBinding.tlFirstName.error = getString(R.string.please_enter_first_name)
            return false
        } else if (mBinding.etFirstName.text.toString().trim().length < 2) {
            mBinding.tlFirstName.error =
                getString(R.string.first_name_must_contain_at_least_2_characters)
            mBinding.tlFirstName.isErrorEnabled = true
            return false
        } else if (mBinding.etFirstName.text.toString().trim().length > 30) {
            mBinding.tlFirstName.error =
                getString(R.string.first_name_should_not_exceed_more_than_30_characters)
            mBinding.tlFirstName.isErrorEnabled = true
            return false
        } else {
            mBinding.tlFirstName.isErrorEnabled = false
            return true
        }
    }

    private fun lastNameValidation(isSignupClicked: Boolean): Boolean {
        if (TextUtils.isEmpty(mBinding.etLastName.text.toString().trim()) && !isSignupClicked) {
            mBinding.tlLastName.isErrorEnabled = false
            return false
        } else if (TextUtils.isEmpty(mBinding.etLastName.text.toString().trim())
            && isSignupClicked
        ) {
            mBinding.tlLastName.isErrorEnabled = true
            mBinding.tlLastName.error = getString(R.string.please_enter_last_name)
            return false
        } else if (mBinding.etLastName.text.toString().trim().length < 2) {
            mBinding.tlLastName.isErrorEnabled = true
            mBinding.tlLastName.error =
                getString(R.string.last_name_must_contain_at_least_2_characters)
            return false
        } else if (mBinding.etLastName.text.toString().trim().length > 30) {
            mBinding.tlLastName.isErrorEnabled = true
            mBinding.tlLastName.error =
                getString(R.string.last_name_should_not_exceed_more_than_30_characters)
            return false
        } else {
            mBinding.tlLastName.isErrorEnabled = false
            return true
        }
    }

    private fun emailValidation(isSignupClicked: Boolean): Boolean {
        if (TextUtils.isEmpty(mBinding.etEmailAddress.text.toString().trim()) && !isSignupClicked) {
            mBinding.tlEmailAddress.isErrorEnabled = false
            return false
        } else if (TextUtils.isEmpty(mBinding.etEmailAddress.text.toString().trim())
            && isSignupClicked
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
}