package com.app.taocalligraphy.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import androidx.activity.viewModels
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivitySplashBinding
import com.app.taocalligraphy.models.UserModulePermission
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.DEEP_LINK_PATH_DAILY_WISDOM
import com.app.taocalligraphy.other.Constants.DEEP_LINK_PATH_FORGOT_PASSWORD
import com.app.taocalligraphy.other.Constants.DEEP_LINK_PATH_MEDITATION_CONTENT
import com.app.taocalligraphy.other.Constants.DEEP_LINK_PATH_MEDITATION_TIMER
import com.app.taocalligraphy.other.Constants.DEEP_LINK_PATH_PROFILE
import com.app.taocalligraphy.other.Constants.DEEP_LINK_PATH_PROGRAM
import com.app.taocalligraphy.other.Constants.DEEP_LINK_PATH_READ_MEDITATION_CONTENT
import com.app.taocalligraphy.other.Constants.DEEP_LINK_PATH_REFERRAL_CODE
import com.app.taocalligraphy.other.Constants.DEEP_LINK_PATH_WATCH_MEDITATION_CONTENT
import com.app.taocalligraphy.other.Constants.QUERY_PARAM_CONTENTID
import com.app.taocalligraphy.other.Constants.QUERY_PARAM_DAILY_WISDOM_ID
import com.app.taocalligraphy.other.Constants.QUERY_PARAM_MEDITATION_TIMER_ID
import com.app.taocalligraphy.other.Constants.QUERY_PARAM_PROGRAM_ID
import com.app.taocalligraphy.other.Constants.QUERY_PARAM_REFERRAL_CODE
import com.app.taocalligraphy.other.Constants.QUERY_PARAM_RESET_PASSWORD_VERIFICATION
import com.app.taocalligraphy.other.Constants.QUERY_PARAM_USERID
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.home.MainActivity
import com.app.taocalligraphy.ui.playlist.PlaylistsActivity
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.questionary.QuestionnairesResultActivity
import com.app.taocalligraphy.ui.reset_password.ResetPasswordActivity
import com.app.taocalligraphy.ui.welcome.WelcomeActivity
import com.app.taocalligraphy.ui.welcome_login.WelcomeLoginActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.extensions.logError
import com.app.taocalligraphy.utils.extensions.logInfo
import com.app.taocalligraphy.utils.extensions.longToast
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    override var TAG = "SplashActivity"
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    var hashKey = ""
    private val mProfileViewModel: ProfileViewModel by viewModels()

    override fun getResource() = R.layout.activity_splash

    companion object {
        const val SPLASH_TIME_OUT: Long = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noInternetConnectionDialog?.dismiss()
        receivedDynamicLink()
    }

    private fun receivedDynamicLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                val deepLink: Uri?
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    logError(msg = "==> Received Link = ${deepLink.toString()}")
                    if (deepLink != null) {
                        when (deepLink.path) {
                            DEEP_LINK_PATH_PROFILE -> {
                                userHolder.setDynamicLinkUserID(
                                    deepLink.getQueryParameter(
                                        QUERY_PARAM_USERID
                                    ).toString()
                                )
                            }
                            DEEP_LINK_PATH_MEDITATION_CONTENT-> {
                                userHolder.setDynamicLinkMeditationContentID(
                                    deepLink.getQueryParameter(
                                        QUERY_PARAM_CONTENTID
                                    ).toString()
                                )
                            }
                            DEEP_LINK_PATH_WATCH_MEDITATION_CONTENT-> {
                                userHolder.setDynamicLinkWatchMeditationContentID(
                                    deepLink.getQueryParameter(
                                        QUERY_PARAM_CONTENTID
                                    ).toString()
                                )
                            }
                            DEEP_LINK_PATH_READ_MEDITATION_CONTENT -> {
                                userHolder.setDynamicLinkReadMeditationContentID(
                                    deepLink.getQueryParameter(
                                        QUERY_PARAM_CONTENTID
                                    ).toString()
                                )
                            }
                            DEEP_LINK_PATH_FORGOT_PASSWORD -> {
                                TaoCalligraphy.instance.isRedirectToResetPassword = true
                                TaoCalligraphy.instance.resetPasswordToken =
                                    deepLink.getQueryParameter(
                                        QUERY_PARAM_RESET_PASSWORD_VERIFICATION
                                    ).toString()
                            }
                            DEEP_LINK_PATH_REFERRAL_CODE -> {
                                if (!userHolder.isLogin) {
                                    TaoCalligraphy.instance.isRedirectToReferralCode = true
                                    TaoCalligraphy.instance.referralCode =
                                        deepLink.getQueryParameter(QUERY_PARAM_REFERRAL_CODE)
                                            .toString()
                                }
                            }
                            DEEP_LINK_PATH_PROGRAM -> {
                                userHolder.setDynamicLinkProgramID(
                                    deepLink.getQueryParameter(
                                        QUERY_PARAM_PROGRAM_ID
                                    ).toString()
                                )
                            }
                            DEEP_LINK_PATH_MEDITATION_TIMER -> {
                                userHolder.setDynamicLinkMeditationTimerID(
                                    deepLink.getQueryParameter(
                                        QUERY_PARAM_MEDITATION_TIMER_ID
                                    ).toString()
                                )
                            }
                            DEEP_LINK_PATH_DAILY_WISDOM -> {
                                userHolder.setDynamicLinkDailyWisdomID(
                                    deepLink.getQueryParameter(
                                        QUERY_PARAM_DAILY_WISDOM_ID
                                    ).toString()
                                )
                            }
                        }
                    }
                }
            }
            .addOnFailureListener(this) {
                logError(msg = "==> Failure Dynamic Link = ${it.printStackTrace()}")
            }
    }

    override fun initView() {
        checkToken()
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            if (userHolder.isLogin) {
                if (userHolder.isQuestionnaireCompleted) {
                    MainActivity.startActivity(this)
                    finishAffinity()
                }else {
                    if (TaoCalligraphy.getAppContext().isRedirectToResetPassword) {
                        ResetPasswordActivity.startActivity(this)
                        TaoCalligraphy.instance.isRedirectToResetPassword = false
                    } else {
                        WelcomeActivity.startActivity(this@SplashActivity)
                    }
                    finishAffinity()
                }
            } else {
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                if (TaoCalligraphy.getAppContext().isRedirectToResetPassword) {
                    ResetPasswordActivity.startActivity(this)
                    TaoCalligraphy.instance.isRedirectToResetPassword = false
                } else if (TaoCalligraphy.getAppContext().isRedirectToReferralCode) {
                    WelcomeLoginActivity.isFromLogin = false
                    WelcomeLoginActivity.startActivity(this)
                    TaoCalligraphy.instance.isRedirectToReferralCode = false
                } else {
                    WelcomeActivity.startActivity(this@SplashActivity)
                }
                finish()
            }
        }
        logInfo(msg = "userToken id ==> ${userHolder.id}")
        logInfo(msg = "userToken ==> ${userHolder.accessToken}")
        logInfo(msg = "mAuthToken ==> ${userHolder.mAuthToken}")
        logInfo(msg = "device lang ==> ${Locale.getDefault().language.uppercase()}")
        try {
            val info: PackageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT)
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }

        if(userHolder.isLogin) {
            mProfileViewModel.userProfile(this, mDisposable)
//            mProfileViewModel.getUserModulePermissionApi(this, mDisposable)
        }
        else{
            if (::handler.isInitialized) {
            if (::runnable.isInitialized) {
                handler.postDelayed(runnable, 2000)
            }
        }
        }
    }

    override fun onResume() {
        super.onResume()
        noInternetConnectionDialog?.dismiss()
//        if (::handler.isInitialized) {
//            if (::runnable.isInitialized) {
//                handler.postDelayed(runnable, SPLASH_TIME_OUT)
//            }
//        }
    }

    override fun onPause() {
        super.onPause()
        if (::handler.isInitialized) {
            if (::runnable.isInitialized) {
                handler.removeCallbacks(runnable)
            }
        }
    }

    override fun observeApiCallbacks() {
        mProfileViewModel.userProfileLiveData.observe(this) { response ->
            response?.let { requestState ->
//                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { it ->
                    if (it.data != null) {
                        userHolder.isLogin = true
                        it.data?.apply {
                            userHolder.setUserData(
                                userHolder.accessToken.toString(),
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
                        }
                    }

                    if (::handler.isInitialized) {
                        if (::runnable.isInitialized) {
                            handler.postDelayed(runnable, SPLASH_TIME_OUT)
                        }
                    }

                }

                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR -> errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR -> errorObj.customMessage?.let { longToast(it, errorObj.type) }
                    }
                    if (::handler.isInitialized) {
                        if (::runnable.isInitialized) {
                            handler.postDelayed(runnable, SPLASH_TIME_OUT)
                        }
                    }
                }

            }
        }

//        mProfileViewModel.userModulePermissionData.observe(this) { response ->
//            response?.let { requestState ->
////                showProgressIndicator(mBinding.llProgress, requestState.progress)
//                requestState.apiResponse?.let { it ->
//                    var enumSet = UserHolder.EnumUserModulePermission.values().toHashSet()
//                        it.data?.let {
//                            it.forEach { item ->
//                                if(enumSet.any { it.name.equals(item.moduleName ?: "") }) {
//                                    UserHolder.EnumUserModulePermission.valueOf(item.moduleName ?: "").permission = item
//                                }
//                            }
//                        }
//                }
//
//                requestState.error?.let { errorObj ->
//                    when (errorObj.errorState) {
//                        Constants.NETWORK_ERROR -> errorObj.customMessage?.let { longToast(it, errorObj.type) }
//                        Constants.CUSTOM_ERROR -> errorObj.customMessage?.let { longToast(it, errorObj.type) }
//                    }
//                }
//
//            }
//        }

    }

    override fun handleListener() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun noInternetListener(model: IsNetworkAvailableListener) {
    }
}