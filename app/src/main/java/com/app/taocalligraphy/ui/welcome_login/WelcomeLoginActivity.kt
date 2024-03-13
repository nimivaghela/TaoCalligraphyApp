package com.app.taocalligraphy.ui.welcome_login

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.app.taocalligraphy.R
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityWelcomeLoginBinding
import com.app.taocalligraphy.models.GoogleResponse
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.login.LoginActivity
import com.app.taocalligraphy.ui.login.viewmodel.LoginViewModel
import com.app.taocalligraphy.ui.otp_verification.OTPVerificationActivity
import com.app.taocalligraphy.ui.post_signup_questionnaire.WelcomeQuestionnaireActivity
import com.app.taocalligraphy.ui.signup.SignUpActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.PermissionCallback
import com.app.taocalligraphy.utils.PermissionHelper
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_welcome_login.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class WelcomeLoginActivity : BaseActivity<ActivityWelcomeLoginBinding>() {

    private val mViewModel: LoginViewModel by viewModels()

    private val RC_SIGN_IN: Int = 1
    private lateinit var signInClient: GoogleSignInClient
    private lateinit var signInOptions: GoogleSignInOptions

    private lateinit var callbackManager: CallbackManager
    private var email = ""
    private var name = ""
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

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, WelcomeLoginActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }

        var isFromLogin: Boolean = false
    }

    override fun getResource() = R.layout.activity_welcome_login

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
        setupToolbar()
        getIntentData()
        checkToken()
//        askLocationPermission()
        manageLayoutForLoginAndSignUp()
        callbackManager = CallbackManager.Factory.create()
        setupGoogleLogin()
        if (isFromLogin) {
            ivLoginBackground.gone()
            animateLoginImage(mBinding.ivSignupBackground, mBinding.ivLoginBackground)
        } else {
            ivSignupBackground.gone()
            animateSignUpImage(mBinding.ivLoginBackground, mBinding.ivSignupBackground)
        }
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
        }
    }

    private fun manageLayoutForLoginAndSignUp() {
        if (isFromLogin) {
//            mBinding.relMain.background =
//                ContextCompat.getDrawable(this, R.drawable.ic_bg_welcome_login)
            mBinding.relLogin.isVisible = true
            mBinding.relSignup.isGone = true
        } else {
            mBinding.relLogin.isGone = true
            mBinding.relSignup.isVisible = true
//            mBinding.relMain.background = ContextCompat.getDrawable(this, R.drawable.ic_bg_sign_up)
        }
    }

    override fun observeApiCallbacks() {
        mViewModel.userLoginLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llLogin, requestState.progress)
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
                            if (isQuestionnaireCompleted) {
                                MainActivity.startActivity(this@WelcomeLoginActivity)
                                finishAffinity()
                            } else {
                                WelcomeQuestionnaireActivity.startActivity(this@WelcomeLoginActivity)
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
                            longToast(errorObj.customMessage ?: "", errorObj.type)
                        else -> {
                            when (errorObj.errorCode) {
                                Constants.StatusCode.STATUS_404 -> {
                                    val intent = Intent(
                                        this@WelcomeLoginActivity,
                                        SignUpActivity::class.java
                                    )
                                    intent.putExtra("isSocialSignIn", isSocialSignIn)
                                    intent.putExtra("isEmailExist", isEmailExist)
                                    intent.putExtra("registerType", registerType)
                                    intent.putExtra("emailAddress", emailAddress)
                                    intent.putExtra("password", password)
                                    intent.putExtra("isRememberMe", isRememberMe)
                                    intent.putExtra("socialId", socialId)
                                    intent.putExtra("firstName", firstName)
                                    intent.putExtra("lastName", lastName)
                                    startActivityWithAnimation(intent)
                                }
                                else -> {
                                    //                                longToast("" + errorObj.customMessage
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }

        mBinding.btnLoginWithEmail.clickWithDebounce {
            if (isNetwork()) {
                LoginActivity.startActivity(this@WelcomeLoginActivity)
            } else
                longToast(
                    getString(R.string.no_internet, getString(R.string.to_login_with_email)),
                    ERROR
                )
        }
        mBinding.btnSignUpWithEmail.clickWithDebounce {
            if (isNetwork()) {
                SignUpActivity.startActivity(this@WelcomeLoginActivity)
            } else
                longToast(
                    getString(
                        R.string.no_internet,
                        getString(R.string.to_siginup_with_email)
                    ), ERROR
                )
        }
        mBinding.btnGmailSignUp.clickWithDebounce {
            googleLogin()
        }
        mBinding.btnFacebookSignUp.clickWithDebounce {
            facebookLogin()
        }
        mBinding.btnGmailLogin.clickWithDebounce {
            googleLogin()
        }

        mBinding.btnFacebookLogin.clickWithDebounce {
            facebookLogin()
        }
        mBinding.tvSignUp.clickWithDebounce {
            isFromLogin = false
            setupToolbar()
            mBinding.relSignup.isGone = true
            mBinding.relLogin.isVisible = true
            animateRightSideShow(mBinding.relLogin, mBinding.relSignup)
            animateSignUpImage(mBinding.ivLoginBackground, mBinding.ivSignupBackground)
//            animateRightSideShow(mBinding.ivLoginBackground, mBinding.ivSignupBackground)
        }
        mBinding.tvLogin.clickWithDebounce {
            isFromLogin = true
            mBinding.relSignup.isVisible = true
            mBinding.relLogin.isGone = true
            setupToolbar()
            animateLeftSideShow(mBinding.relSignup, mBinding.relLogin)
            animateLoginImage(mBinding.ivSignupBackground, mBinding.ivLoginBackground)
        }
    }

    private fun setupToolbar() {
        mBinding.welcomeTitle.text = if (isFromLogin) {
            getString(R.string.login)
        } else {
            getString(R.string.sign_up)
        }

        mBinding.mToolbar.ivBackToolbar.visible()
    }

    private fun animateRightSideShow(view: View, anotherView: View) {
        val slideRight = Slide(Gravity.END)
        slideRight.duration = 200
        slideRight.addTarget(view.id)

        val slideLeft = Slide(Gravity.START)
        slideLeft.duration = 800
        slideLeft.addTarget(anotherView.id)

        val set = TransitionSet()
        set.addTransition(slideLeft)
        set.addTransition(slideRight)
        TransitionManager.beginDelayedTransition(mBinding.relMain, set)
        view.visibility = if (view.isVisible) View.GONE else View.VISIBLE
        anotherView.visibility = if (view.isVisible) View.GONE else View.VISIBLE
    }

    private fun animateLeftSideShow(view: View, anotherView: View) {
        val slideRight = Slide(Gravity.END)
        slideRight.duration = 800
        slideRight.addTarget(anotherView.id)

        val slideLeft = Slide(Gravity.START)
        slideLeft.duration = 200
        slideLeft.addTarget(view.id)

        val set = TransitionSet()
        set.addTransition(slideLeft)
        set.addTransition(slideRight)
        TransitionManager.beginDelayedTransition(mBinding.relMain, set)
        view.visibility = if (view.isVisible) View.GONE else View.VISIBLE
        anotherView.visibility = if (view.isVisible) View.GONE else View.VISIBLE
    }

    private fun animateSignUpImage(view: View, anotherView: View) {
        view.visibility = View.VISIBLE
        anotherView.visibility = View.GONE
//        anotherView.animate().alpha(0f).setDuration(200)
//        view.animate().alpha(1f).setDuration(800)

        val animationLeftToRightOut =
            TranslateAnimation(-relMain.measuredWidth.toFloat(), 0f, 0f, 0f)
        animationLeftToRightOut.duration = 800
        val animationLeftToRightIn =
            TranslateAnimation(0f, relMain.measuredWidth.toFloat(), 0f, 0f)
        animationLeftToRightIn.duration = 800

        view.startAnimation(animationLeftToRightOut)
        anotherView.startAnimation(animationLeftToRightIn)

    }

    private fun animateLoginImage(view: View, anotherView: View) {
        view.visibility = View.VISIBLE
        anotherView.visibility = View.GONE
//        anotherView.animate().alpha(0f).setDuration(200)
//        view.animate().alpha(1f).setDuration(800)

        val animationLeftToRightOut =
            TranslateAnimation(relMain.measuredWidth.toFloat(), 0f, 0f, 0f)
        animationLeftToRightOut.duration = 800
        val animationLeftToRightIn =
            TranslateAnimation(0f, -relMain.measuredWidth.toFloat(), 0f, 0f)
        animationLeftToRightIn.duration = 800

        view.startAnimation(animationLeftToRightOut)
        anotherView.startAnimation(animationLeftToRightIn)

    }

    private fun googleLogin() {
        if (isNetwork()) {
            val loginIntent: Intent = signInClient.signInIntent
            startActivityForResult(loginIntent, RC_SIGN_IN)
        } else
            longToast(getString(R.string.no_internet, "to Login with Google"), ERROR)
    }

    private fun setupGoogleLogin() {
        signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(this, signInOptions)
    }

    private fun googleFirebaseAuth(acct: GoogleSignInAccount) {
        val gson = Gson()
        val googleResponse: GoogleResponse =
            gson.fromJson(acct.zad(), GoogleResponse::class.java)

        if (googleResponse.displayName.contains(" ")) {
            firstName = googleResponse.displayName.substringBeforeLast(" ")
            lastName = googleResponse.displayName.substringAfterLast(" ")
        } else {
            firstName = googleResponse.displayName
            lastName = ""
        }
        registerType = "GOOGLE"
        emailAddress = googleResponse.email
        this.socialId = googleResponse.id
        password = ""
        isEmailExist = true
        isSocialSignIn = true
        signInClient.signOut()
        if (isNetwork())
            mViewModel.userLogin(
                emailAddress,
                "",
                "" + registerType,
                socialId, getDeviceName(), getDeviceVersion(), address,
                Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID),
                this, mDisposable
            )
        else
            longToast(getString(R.string.no_internet, getString(R.string.to_login)), ERROR)
    }

    private fun facebookLogin() {
        registerType = "FACEBOOK"
        if (isNetwork()) {
            // Login
            //LoginManager.getInstance().loginBehavior = LoginBehavior.NATIVE_WITH_FALLBACK
            LoginManager.getInstance()
                .logInWithReadPermissions(
                    this,
                    listOf("public_profile", "email")
                )
            LoginManager.getInstance().registerCallback(callbackManager, object :
                FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    logError(msg = result.toString())
                    val request =
                        GraphRequest.newMeRequest(result.accessToken) { jsonObject, _ ->
                            try {
                                //here is the data that you want
                                jsonObject?.let {
                                    email = jsonObject.getString("email")
                                    socialId = jsonObject.getString("id")
                                    name = jsonObject.getString("name")
                                }
                                registerType = "FACEBOOK"
                                emailAddress = "" + email
                                socialId = "" + socialId
                                if (name.contains(" ")) {
                                    firstName = name.substringBeforeLast(" ")
                                    lastName = name.substringAfterLast(" ")
                                } else {
                                    firstName = name
                                    lastName = ""
                                }
                                password = ""
                                isSocialSignIn = true
                                isEmailExist = !email.isNullOrEmpty()
                                if (isNetwork())
                                    mViewModel.userLogin(
                                        emailAddress,
                                        "",
                                        "" + registerType,
                                        socialId,
                                        getDeviceName(),
                                        getDeviceVersion(),
                                        address,
                                        Settings.Secure.getString(
                                            contentResolver,
                                            Settings.Secure.ANDROID_ID
                                        ),
                                        this@WelcomeLoginActivity,
                                        mDisposable
                                    )
                                else
                                    longToast(
                                        getString(
                                            R.string.no_internet,
                                            getString(R.string.to_login)
                                        ),
                                        ERROR
                                    )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    val parameters = Bundle()
                    parameters.putString("fields", "name,first_name,email,last_name,id")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    logError(msg = "Cancel")
                }

                override fun onError(error: FacebookException) {
                    logError(msg = "ERROR $error")
                    if (error is FacebookAuthorizationException) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut()
                        }
                    }
                }
            })
        } else
            longToast(
                getString(R.string.no_internet, getString(R.string.to_login_with_facebook)),
                ERROR
            )
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null) {
            if (error.lowercase() != "user not found")
                longToast(error, ERROR)
        }
    }

    override fun unauthorized(error: String?) {
        OTPVerificationActivity.isFromLogin = true
        val intent = Intent(
            this@WelcomeLoginActivity,
            OTPVerificationActivity::class.java
        )
        intent.putExtra("emailAddress", emailAddress)
        intent.putExtra("password", password)
        intent.putExtra("isRememberMe", isRememberMe)
        startActivityWithAnimation(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    googleFirebaseAuth(account)
                }
            } catch (e: ApiException) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
    }
}