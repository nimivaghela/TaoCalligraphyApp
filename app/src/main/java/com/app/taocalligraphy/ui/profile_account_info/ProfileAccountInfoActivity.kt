package com.app.taocalligraphy.ui.profile_account_info

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.*
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.databinding.ActivityProfileAccountInfoBinding
import com.app.taocalligraphy.models.eventbus.IsNetworkAvailableListener
import com.app.taocalligraphy.models.response.CommonResponse
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.models.response.profile.LanguageListApiResponse
import com.app.taocalligraphy.models.response.profile.RegionListApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.Constants.SUCCESS
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.ui.home.viewmodel.HomeViewModel
import com.app.taocalligraphy.ui.meditation_rooms_list.dialog.LanguageSelectionDialog
import com.app.taocalligraphy.ui.profile.ChooseProfilePictureActivity
import com.app.taocalligraphy.ui.profile.viewmodel.ProfileViewModel
import com.app.taocalligraphy.ui.profile_account_info.dialog.AgeSelectionDialog
import com.app.taocalligraphy.ui.profile_account_info.dialog.CommonSelectionDialog
import com.app.taocalligraphy.ui.profile_account_info.dialog.CountrySelectionDialog
import com.app.taocalligraphy.ui.profile_account_info.dialog.RegionSelectionDialog
import com.app.taocalligraphy.ui.profile_subscription.SubscriptionActivity
import com.app.taocalligraphy.ui.profile_view.PublicProfileViewActivity
import com.app.taocalligraphy.ui.user_menu.UserMenuActivity
import com.app.taocalligraphy.ui.welcome.WelcomeActivity
import com.app.taocalligraphy.userHolder
import com.app.taocalligraphy.utils.PasswordStrength
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.isNetwork
import com.app.taocalligraphy.utils.loadImageProfile
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class ProfileAccountInfoActivity : BaseActivity<ActivityProfileAccountInfoBinding>() {

    private val mViewModel: ProfileViewModel by viewModels()
    private val mHomeViewModel: HomeViewModel by viewModels()

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

    val filter = InputFilter { source, _, _, _, _, _ ->
        return@InputFilter when {
            source.isNotEmpty() -> {
                source.trim { invalidCharacters.contains(it) || !it.isLetter() }
            }
            else -> null
        }
    }

    companion object {
        fun startActivity(activity: AppCompatActivity) {
            val intent = Intent(activity, ProfileAccountInfoActivity::class.java)
            activity.startActivityWithAnimation(intent)
        }
    }

    override fun getResource() = R.layout.activity_profile_account_info

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.decorView.importantForAutofill =
                View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
        }
        updateProfile()
        setupToolbar()
        setUpData()
        setSpan(
            getString(R.string.your_profile_will_not_searchable),
            mBinding.txtAllowOthersDesc
        )
//        mViewModel.selectedCountryId = userHolder.countryId
        if (isNetwork())
            if(mViewModel.loginResponse == null){
            mViewModel.userProfile(this, mDisposable)
            }else{
                updateViews()
            }

        mBinding.tvShareContent.text = URL_PROFILE
        mBinding.llChangePassword.visibility =
            if (userHolder.signupType == "NORMAL") VISIBLE else GONE

        mBinding.etFirstName.filters = arrayOf(filter)
        mBinding.etLastName.filters = arrayOf(filter)

        setSubscriptionView()
        LocalBroadcastManager.getInstance(this@ProfileAccountInfoActivity).registerReceiver(
            mAccessLevelReceiver,
            IntentFilter(Constants.BroadcastIntentFilter.BR_ACCESS_LEVEL_CHANGED)
        )

    }

    private fun setupToolbar() {
        mBinding.mToolbar.ivBackToolbar.visible()
        mBinding.mToolbar.cardProfile.visible()
    }


    private fun updateProfile() {
        mBinding.mToolbar.ivProfileImageToolbar.loadImageProfile(
            userHolder.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@ProfileAccountInfoActivity).unregisterReceiver(mAccessLevelReceiver)
    }

    private val mAccessLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            setSubscriptionView()
        }
    }

    private fun setUpData() {
        if(mViewModel.mGenderList.isNullOrEmpty()) {
            mViewModel.mGenderList.clear()
            mViewModel.mGenderList.add(CommonResponse(getString(R.string.male), false))
            mViewModel.mGenderList.add(CommonResponse(getString(R.string.female), false))
            mViewModel.mGenderList.add(CommonResponse(getString(R.string.other), false))
            mViewModel.mGenderList.add(CommonResponse(getString(R.string.prefer_not_to_say), false))
        }
        mBinding.datePicker.formatDate("mdy")
        mBinding.datePicker.maxDate = Date().time

        mBinding.datePicker.setOnDateChangedListener { _, _, _, _ ->
            mViewModel.day = mBinding.datePicker.dayOfMonth
            mViewModel.month = mBinding.datePicker.month
            mViewModel.year= mBinding.datePicker.year

            val birthDay: Calendar = GregorianCalendar(mViewModel.year, mViewModel.month, mViewModel.year)

            val today: Calendar = GregorianCalendar()
            today.time = Date()
            val yearsInBetween = (today[Calendar.YEAR] - birthDay[Calendar.YEAR])
            com.app.taocalligraphy.utils.extensions.logInfo(msg = "yearsInBetween ==> $yearsInBetween")
            for (data in mViewModel.mAgeList.indices) {
                if (mViewModel.mAgeList[data].startAge <= yearsInBetween && mViewModel.mAgeList[data].endAge >= yearsInBetween) {
                    mViewModel.mAgeList[data].isSelected = true
                    mBinding.tvAge.text = mViewModel.mAgeList[data].ageRange
                    mViewModel.selectedAge = data
                } else {
                    mViewModel.mAgeList[data].isSelected = false
                }
            }
            mViewModel.month = mBinding.datePicker.month+1
        }
    }

    override fun observeApiCallbacks() {
        mViewModel.userProfileLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { it ->
                    if (it.data != null) {
                        userHolder.isLogin = true
                        it.data?.apply {
                            // Updating data
                            mViewModel.loginResponse = this
                            mViewModel.userId = id
                            mViewModel.isShowMeditationRoomSessionInOtherLanguage = isShowMeditationRoomSessionInOtherLanguage
                            mBinding.switchSetBirthdate.isChecked = !dateOfBirth.isNullOrEmpty()
                            mViewModel.spokenLanguage = spokenLanguages as ArrayList<Int>
                            mViewModel.isSearchable = isSearchable
                            mViewModel.viewBirthday = viewBirthday
                            mViewModel.viewAboutMe = viewAboutMe
                            mViewModel.viewGender = viewGender
                            mViewModel.viewInterest = viewInterest

                            mViewModel.mAgeList.clear()
                            mViewModel.mAgeList.addAll(ageRangeDetails)
                            userHolder.is12HourFormat = is12HourFormat ?: !getTimePickerTimeFormat(
                                this@ProfileAccountInfoActivity
                            )
                            mViewModel.dateOfBirth = dateOfBirth
                            if (mViewModel.mGenderList.isNotEmpty()) {
                                mViewModel.selectedGender = mViewModel.mGenderList.indexOfLast { it.title == gender }
                            }

                            runOnUiThread {
                                mBinding.etFirstName.setText(firstName)
                                mBinding.etLastName.setText(lastName)
                                mBinding.etEmailAddress.setText(email)
                                mBinding.etPhoneNumber.setText(mobileNo)
                                mBinding.etAboutMe.setText(aboutMe)
                            }


                            mViewModel.dateOfBirth?.let { dateOfBirth ->
                                if (dateOfBirth.isNotEmpty()) {
                                    val time1: List<String> = dateOfBirth.split("-")
                                    mViewModel.year = time1[0].toInt() ?: -1
                                    mViewModel.month = time1[1].toInt() ?: -1
                                    mViewModel.day  = time1[2].toInt() ?: -1
                                }
                            }

                            mViewModel.originalProfilePicUrl = originalProfilePicUrl

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

                            // Updating view Based on data
                            updateViews()

                            if (isNetwork()) {
                                mViewModel.regionsApi(
                                    0,
                                    this@ProfileAccountInfoActivity,
                                    mDisposable
                                )

                                mViewModel.getLanguages(
                                    this@ProfileAccountInfoActivity,
                                    mDisposable
                                )
                            }
                        }
                        mViewModel.userProfileLiveData.postValue(null)
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
        mViewModel.languagesLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { data ->
                    if (data.data != null) {
                        data.data?.let { it ->
                            mViewModel.languageList.clear()
                            mViewModel.languageList.addAll(it.filter { list -> list.isActive!! })
                            mViewModel.languageList.forEach { language ->
                                language.isSelected =
                                    mViewModel.spokenLanguage.any { language.languageId == it }
                            }

                            mBinding.tvLanguage.text =
                                mViewModel.languageList.filter { it.isSelected }.joinToString { it.language!! }

                        }
                        mViewModel.languagesLiveData.postValue(null)
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
        mViewModel.regionsLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { data ->
                    if (data.data != null) {
                        data.data?.let { it ->
                            mViewModel.regionList.clear()
                            mViewModel.regionList.addAll(it)

                            mViewModel.regionList.forEachIndexed { index, region ->
                                region.isSelected = userHolder.region == region.id
                                if (region.isSelected) {
                                    mViewModel.selectedRegionId = region.id ?: 0
                                    mViewModel.selectedRegion = region
                                    mViewModel.selectedRegionPos = index
                                }
                            }

                            mBinding.tvRegion.text =
                                if (mViewModel.selectedRegion != null) mViewModel.selectedRegion!!.name else getString(R.string.select)

                            if (mViewModel.selectedRegion != null)
                                mViewModel.selectedRegion!!.id?.let { it1 ->
                                    mViewModel.countryApi(
                                        0,
                                        it1,
                                        this@ProfileAccountInfoActivity,
                                        mDisposable
                                    )
                                }
                        }
                        mViewModel.regionsLiveData.postValue(null)
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
        mViewModel.countryLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let { data ->
                    if (data.data != null) {
                        data.data?.let { it ->
                            mViewModel.countryList.clear()
                            mViewModel.countryList.addAll(it)

                            mViewModel.countryList.forEachIndexed { index, data ->
                                data.isSelected = data.id == userHolder.countryId
                                if (data.isSelected) {
                                    mViewModel.selectedCountryId = data.id ?: 0
                                    mViewModel.selectedCountry = data
                                    mViewModel.selectedCountryPos = index
                                }
                            }

                            mBinding.tvCountry.text =
                                mViewModel.selectedCountry?.name ?: getString(R.string.select)

                            if (mViewModel.countryList.isEmpty()) {
                                mViewModel.selectedCountryId = 0
                                mViewModel.selectedCountry = null
                                mBinding.tvCountry.text = getString(R.string.select)
                            }
                        }
                    }
                    mViewModel.countryLiveData.postValue(null)
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
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
        mViewModel.userEditProfileLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    longToast(it.message.toString(), it.type ?: SUCCESS)
                    mViewModel.loginResponse = it.data
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

                        mViewModel.viewBirthday = viewBirthday
                        mBinding.switchBirthday.isChecked = viewBirthday
                        mViewModel.viewAboutMe = viewAboutMe
                        mBinding.switchAboutMe.isChecked = viewAboutMe
                        mViewModel.viewGender = viewGender
                        mBinding.switchGender.isChecked = viewGender
                        mViewModel.viewInterest = viewInterest
                        mBinding.switchInterest.isChecked = viewInterest

                    }
                    mViewModel.userEditProfileLiveData.postValue(null)
                }
                requestState.error?.let { errorObj ->
                    when (errorObj.errorState) {
                        Constants.NETWORK_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
                        Constants.CUSTOM_ERROR ->
                            errorObj.customMessage?.let { longToast(it, errorObj.type) }
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
        mViewModel.userProfilePublicLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    mViewModel.userProfile(this, mDisposable)
                    longToast(it.message.toString(), it.type ?: SUCCESS)
                }
                longToastState(requestState.error)
                mViewModel.userProfilePublicLiveData.postValue(null)
            }
        }
        mViewModel.changePasswordLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    mBinding.etPassword.setText("")
                    mBinding.etNewPassword.setText("")
                    mBinding.etConfirmPassword.setText("")
                    longToast(it.message.toString(), it.type ?: SUCCESS)
                    mViewModel.changePasswordLiveData.postValue(null)
//                    mHomeViewModel.userLogout("", this@ProfileAccountInfoActivity, mDisposable)
                }
                longToastState(requestState.error)
            }
        }
        mViewModel.removeProfilePictureData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    it.message?.let { it1 -> longToast(it1, it.type ?: SUCCESS) }
                    if (isNetwork())
                        mViewModel.userProfile(this, mDisposable)

                    mViewModel.removeProfilePictureData.postValue(null)
                }
                longToastState(requestState.error)
            }
        }

        mHomeViewModel.userLogoutLiveData.observe(this) { response ->
            response?.let { requestState ->
                showProgressIndicator(mBinding.llProgress, requestState.progress)
                requestState.apiResponse?.let {
                    cancelAllPreviousAlarm()
                    userHolder.clearUserHolder()
                    LoginManager.getInstance().logOut()
                    notificationManager?.cancelAll()
                    val gso =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    val googleSignInClient = GoogleSignIn.getClient(this, gso)
                    googleSignInClient.signOut()
                    mViewModel.deleteAllDownloads()
                    TaoCalligraphy.instance.getDownloadTracker()?.removeAllDownload()
                    cancelAllPreviousAlarm()
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                    startActivity(intent)
                    finishAffinity()
                }
            }
        }

    }

    fun updateViews() {

        if (mViewModel.originalProfilePicUrl.isEmpty() || (UserHolder.EnumUserModulePermission.EDIT_PROFILE.permission?.canAccess == false)) {
            mBinding.ivRemoveProfile.gone()
        } else {
            mBinding.ivRemoveProfile.visible()
        }

        mBinding.ivUserProfile.loadImageProfile(
            mViewModel.originalProfilePicUrl,
            R.drawable.ic_profile_default
        )

            if (mViewModel.isShowMeditationRoomSessionInOtherLanguage) {
                mBinding.ivYes.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@ProfileAccountInfoActivity,
                        R.drawable.vd_status_selected
                    ),
                )
                mBinding.ivNo.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@ProfileAccountInfoActivity,
                        R.drawable.vd_status_unselected
                    )
                )
            } else {
                mBinding.ivNo.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@ProfileAccountInfoActivity,
                        R.drawable.vd_status_selected
                    ),
                )
                mBinding.ivYes.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@ProfileAccountInfoActivity,
                        R.drawable.vd_status_unselected
                    )
                )
            }

            if (!mBinding.switchSetBirthdate.isChecked)
                mBinding.datePicker.gone()
            else
                mBinding.datePicker.visible()

            for (i in mViewModel.mAgeList.indices) {
                if (mViewModel.mAgeList[i].isSelected) {
                    mBinding.tvAge.text = mViewModel.mAgeList[i].ageRange
                    mViewModel.selectedAge = i
                    break
                }
            }

            val day: Int = mBinding.datePicker.dayOfMonth
            val month: Int = mBinding.datePicker.month
            val year: Int = mBinding.datePicker.year
            val birthDay: Calendar = GregorianCalendar(year, month, day)
            val today: Calendar = GregorianCalendar()
            today.time = Date()
            val yearsInBetween = (today[Calendar.YEAR]
                    - birthDay[Calendar.YEAR])
            for (i in mViewModel.mAgeList.indices) {
                if ((mViewModel.dateOfBirth?.isNotEmpty() == true) && mViewModel.mAgeList[i].startAge <= yearsInBetween && mViewModel.mAgeList[i].endAge >= yearsInBetween
                ) {
                    mViewModel.mAgeList[i].isSelected = true
                    mBinding.tvAge.text = mViewModel.mAgeList[i].ageRange
                    mViewModel.selectedAge = i
                } else {
                    mViewModel.mAgeList[i].isSelected = false
                }
            }

        if(mViewModel.day != -1){
            mBinding.datePicker.updateDate(mViewModel.year,mViewModel.month-1,mViewModel.day)
        }

            if (mViewModel.selectedGender != -1)
                mBinding.tvGender.text = mViewModel.mGenderList[mViewModel.selectedGender].title

            mBinding.switchAllowOthers.isChecked = mViewModel.isSearchable
            mBinding.llLink.visibility = if (mViewModel.isSearchable) GONE else VISIBLE
            mBinding.llLink.animate().alpha(if (mViewModel.isSearchable) 0.0f else 1.0f).duration = 300
            if (mViewModel.isSearchable) {
                mBinding.txtAllowOthersDesc.text =
                    getString(R.string.your_profile_will_searchable)
                mBinding.mbPreviewPrivateProfile.text =
                    getString(R.string.preview_my_public_profile)
            } else {
                mBinding.mbPreviewPrivateProfile.text =
                    getString(R.string.preview_my_private_profile)
                setSpan(
                    getString(R.string.your_profile_will_not_searchable),
                    mBinding.txtAllowOthersDesc
                )
            }

            mBinding.switchBirthday.isChecked = mViewModel.viewBirthday
            mBinding.switchAboutMe.isChecked = mViewModel.viewAboutMe
            mBinding.switchGender.isChecked = mViewModel.viewGender
            mBinding.switchInterest.isChecked = mViewModel.viewInterest

            // updating language data
            mBinding.tvLanguage.text =
                mViewModel.languageList.filter { it.isSelected }.joinToString { it.language!! }
                    ?: ""

            mBinding.tvRegion.text =
                if (mViewModel.selectedRegion != null) mViewModel.selectedRegion!!.name else getString(
                    R.string.select
                )

            mBinding.tvCountry.text =
                mViewModel.selectedCountry?.name ?: getString(R.string.select)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun handleListener() {
        mBinding.mToolbar.ivBackToolbar.clickWithDebounce {
            onBackPressed()
        }
        mBinding.mToolbar.cardProfile.clickWithDebounce {
            UserMenuActivity.startActivity(this@ProfileAccountInfoActivity)
        }
        mBinding.apply {
            etAboutMe.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    if (etAboutMe.hasFocus()) {
                        v.parent.requestDisallowInterceptTouchEvent(true)
                        if (etAboutMe.text.toString().isEmpty()) {
                            when (event.action and MotionEvent.ACTION_MASK) {
                                MotionEvent.ACTION_SCROLL -> {
                                    v.parent.requestDisallowInterceptTouchEvent(false)
                                    return true
                                }
                            }
                        }
                    }
                    return false
                }
            })
            rlProfilePic.setOnClickListener {
                if(!(UserHolder.EnumUserModulePermission.EDIT_PROFILE.permission?.canAccess ?: false)){
                    SubscriptionActivity.startActivityForResult(this@ProfileAccountInfoActivity)
                    return@setOnClickListener
                }

                ChooseProfilePictureActivity.startActivity(this@ProfileAccountInfoActivity)
            }
            btnShare.setOnClickListener {
                hideKeyboard()
                shareUserProfileFun()
//                shareTextUrl()
            }
            mbPreviewPrivateProfile.setOnClickListener {
                hideKeyboard()
                if (!switchBirthday.isChecked && !switchAboutMe.isChecked &&
                    !switchGender.isChecked && !switchInterest.isChecked
                ) {
                    longToast(getString(R.string.error_public_profile_nothing_checked), ERROR)
                } else if ((!switchBirthday.isChecked && mViewModel.viewBirthday) || (switchBirthday.isChecked && !mViewModel.viewBirthday) ||
                    (!switchAboutMe.isChecked && mViewModel.viewAboutMe) || (switchAboutMe.isChecked && !mViewModel.viewAboutMe) ||
                    (!switchGender.isChecked && mViewModel.viewGender) || (switchGender.isChecked && !mViewModel.viewGender) ||
                    (!switchInterest.isChecked && mViewModel.viewInterest) || (switchInterest.isChecked && !mViewModel.viewInterest)
                ) {
                    longToast(getString(R.string.error_public_profile_not_saved), ERROR)
                } else {
                    PublicProfileViewActivity.startActivity(
                        this@ProfileAccountInfoActivity,
                        userHolder.id.toString()
                    )
                }
            }
            btnSaveChanges.setOnClickListener {
                if(!(UserHolder.EnumUserModulePermission.EDIT_PROFILE.permission?.canAccess ?: false)){
                    SubscriptionActivity.startActivityForResult(this@ProfileAccountInfoActivity)
                    return@setOnClickListener
                }

                hideKeyboard()
                if (isNetwork())
                    mViewModel.userProfilePublic(
                        mViewModel.isSearchable, "",
                        mBinding.switchBirthday.isChecked,
                        mBinding.switchAboutMe.isChecked,
                        mBinding.switchGender.isChecked,
                        mBinding.switchInterest.isChecked,
                        this@ProfileAccountInfoActivity,
                        mDisposable
                    )
            }
            btnSave.setOnClickListener {
                if(!(UserHolder.EnumUserModulePermission.EDIT_PROFILE.permission?.canAccess ?: false)){
                    SubscriptionActivity.startActivityForResult(this@ProfileAccountInfoActivity)
                    return@setOnClickListener
                }
                hideKeyboard()

                val day: Int = datePicker.dayOfMonth
                val month: Int = datePicker.month
                val year: Int = datePicker.year
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val cal: Calendar = GregorianCalendar(year, month, day)
                val date = cal.time
                val strDate: String = if(mBinding.switchSetBirthdate.isChecked) dateFormatter.format(date) else ""

                if (checkScreenValidation()) {
                    if (TextUtils.isEmpty(
                            mBinding.etAboutMe.text.toString().trim()
                        ) && mViewModel.viewAboutMe
                    ) {
                        longToast(
                            getString(
                                R.string.error_public_profile_remove_data,
                                getString(R.string.about_me),
                                getString(R.string.about_me),
                                getString(R.string.about_me)
                            ), ERROR
                        )
                        mBinding.etAboutMe.requestFocus()
                        mBinding.scrollView.smoothScrollTo(0, mBinding.etAboutMe.top)
                    } else {
                        if (isNetwork())
                            mViewModel.userEditProfile(
                                mBinding.etFirstName.text.toString(),
                                mBinding.etLastName.text.toString(),
                                mBinding.etEmailAddress.text.toString(),
                                mBinding.etPhoneNumber.text.toString(),
                                if (mViewModel.selectedRegionId > 0) mViewModel.selectedRegionId else null,
                                if (mViewModel.selectedCountryId > 0) mViewModel.selectedCountryId else null,
                                mBinding.etAboutMe.text.toString(),
                                strDate,
                                if (mViewModel.selectedGender > -1) mViewModel.mGenderList[mViewModel.selectedGender].title else "",
                                mViewModel.isShowMeditationRoomSessionInOtherLanguage,
                                mViewModel.spokenLanguage,
                                if (mViewModel.selectedAge > -1) mViewModel.mAgeList[mViewModel.selectedAge].ageRange else "",
                                this@ProfileAccountInfoActivity,
                                mDisposable
                            )
                        else
                            longToast(
                                getString(
                                    R.string.no_internet,
                                    getString(R.string.no_internet_edit_profile)
                                ),
                                ERROR
                            )
                    }
                }
            }

            etNewPassword.addTextChangedListener {
                updatePasswordStrengthView(it.toString())
                updateConfirmPasswordStrengthView()
            }
            etConfirmPassword.addTextChangedListener {
                updateConfirmPasswordStrengthView()
            }

            btnChange.setOnClickListener {
                hideKeyboard()
                if (checkPasswordValidation()) {
                    mBinding.tlNewPassword.isErrorEnabled = false
                    mBinding.tlPassword.isErrorEnabled = false
                    mBinding.tlConfirmPassword.isErrorEnabled = false

                    if (isNetwork())
                        mViewModel.changePasswordApi(
                            mBinding.etPassword.text.toString(),
                            mBinding.etNewPassword.text.toString(),
                            this@ProfileAccountInfoActivity,
                            mDisposable
                        )
                }
            }
            relYes.setOnClickListener {
                mViewModel.isShowMeditationRoomSessionInOtherLanguage = true
                ivYes.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@ProfileAccountInfoActivity,
                        R.drawable.vd_status_selected
                    ),
                )
                ivNo.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@ProfileAccountInfoActivity,
                        R.drawable.vd_status_unselected
                    )
                )
            }
            relNo.setOnClickListener {
                mViewModel.isShowMeditationRoomSessionInOtherLanguage = false
                ivNo.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@ProfileAccountInfoActivity,
                        R.drawable.vd_status_selected
                    ),
                )
                ivYes.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@ProfileAccountInfoActivity,
                        R.drawable.vd_status_unselected
                    )
                )
            }
            switchAllowOthers.setOnCheckedChangeListener { _, isChecked ->
                mViewModel.isSearchable = isChecked
                mBinding.llLink.visibility = if (isChecked) GONE else VISIBLE
                mBinding.llLink.animate()
                    .alpha(if (isChecked) 0.0f else 1.0f).duration = 300

                if (isChecked) {
                    mBinding.txtAllowOthersDesc.text =
                        getString(R.string.your_profile_will_searchable)
                    mBinding.mbPreviewPrivateProfile.text =
                        getString(R.string.preview_my_public_profile)
                } else {
                    mBinding.mbPreviewPrivateProfile.text =
                        getString(R.string.preview_my_private_profile)
                    setSpan(
                        getString(R.string.your_profile_will_not_searchable),
                        mBinding.txtAllowOthersDesc
                    )
                }
            }
            switchBirthday.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && (mViewModel.loginResponse?.dateOfBirth ?: "").isEmpty()) {
                    longToast(
                        getString(
                            R.string.error_public_profile,
                            getString(R.string.birthday)
                        ), ERROR
                    )
                    switchBirthday.isChecked = false
                    return@setOnCheckedChangeListener
                }
            }
            switchAboutMe.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && (mViewModel.loginResponse?.aboutMe ?: "").isEmpty()) {
                    longToast(
                        getString(
                            R.string.error_public_profile,
                            getString(R.string.about_me)
                        ), ERROR
                    )
                    switchAboutMe.isChecked = false
                    return@setOnCheckedChangeListener
                }
            }
            switchGender.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && (mViewModel.loginResponse?.gender ?: "").isEmpty()) {
                    longToast(
                        getString(R.string.error_public_profile, getString(R.string.gender)),
                        ERROR
                    )
                    switchGender.isChecked = false
                    return@setOnCheckedChangeListener
                }
            }
            switchInterest.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked && (mViewModel.loginResponse?.userInterests?.isEmpty() == true)) {
                    longToast(
                        getString(
                            R.string.error_public_profile,
                            getString(R.string.interests)
                        ), ERROR
                    )
                    switchInterest.isChecked = false
                    return@setOnCheckedChangeListener
                }
            }
            switchSetBirthdate.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked ) mBinding.datePicker.visible()
                else
                    mBinding.datePicker.gone()
            }

            ivRemoveProfile.setOnClickListener {
                removeProfilePictureConfirmationDialog()

            }
            relLanguage.setOnClickListener {
                if (mViewModel.languageList.size > 0) {
                    openLanguageSelectionDialog(mViewModel.languageList)
                } else {
                    longToast(getString(R.string.no_langauge_found), ERROR)
                }
            }
            relGender.setOnClickListener {
                for (i in mViewModel.mGenderList.indices) {
                    mViewModel.mGenderList[i].isSelected =
                        i == mViewModel.selectedGender
                }
                openGenderSelectionDialog(mViewModel.mGenderList)
            }
            relAge.setOnClickListener {
//                openAgeSelectionDialog(mAgeList)
            }
            relRegion.setOnClickListener {
                if (mViewModel.regionList.size > 0) {
                    openRegionSelectionDialog(mViewModel.regionList)
                } else {
                    longToast(getString(R.string.no_region_found), ERROR)
                }
            }
            relCountry.setOnClickListener {
                if (mViewModel.countryList.size > 0) {
                    openCountrySelectionDialog(mViewModel.countryList)
                } else {
                    longToast(getString(R.string.no_country_found), ERROR)
                }
            }
        }
    }

    fun setSubscriptionView(){
        if(!(UserHolder.EnumUserModulePermission.EDIT_PROFILE.permission?.canAccess ?: true)){
            mBinding.ivLockSave.visible()
            mBinding.ivLockSaveChanges.visible()
            mBinding.ivRemoveProfile.gone()
        }else{
            mBinding.ivLockSave.gone()
            mBinding.ivLockSaveChanges.gone()
            mBinding.ivRemoveProfile.visible()
        }
    }

    private fun updateConfirmPasswordStrengthView() {
        if (TextUtils.isEmpty(mBinding.etConfirmPassword.text.toString().trim())) {
            mBinding.tvPwdMustMatch.setTextColor(ContextCompat.getColor(this, R.color.dark_grey))
            mBinding.imgPassword.gone()
            mBinding.tvPwdMustMatch.text = getString(R.string.password_must_match)
            mBinding.btnChange.isEnabled = false
            mBinding.btnChange.alpha = 0.5f
        } else if (mBinding.etNewPassword.text.toString()
                .trim() != mBinding.etConfirmPassword.text.toString().trim()
        ) {
            mBinding.tvPwdMustMatch.setTextColor(ContextCompat.getColor(this, R.color.red))
            mBinding.imgPassword.visible()
            mBinding.imgPassword.setBackgroundResource(R.drawable.ic_close)
            mBinding.tvPwdMustMatch.text = getString(R.string.password_do_not_match)
            mBinding.btnChange.isEnabled = false
            mBinding.btnChange.alpha = 0.5f
        } else {
            mBinding.tvPwdMustMatch.setTextColor(ContextCompat.getColor(this, R.color.green))
            mBinding.imgPassword.visible()
            mBinding.imgPassword.setBackgroundResource(R.drawable.ic_check)
            mBinding.tvPwdMustMatch.text = getString(R.string.password_match)
            mBinding.btnChange.isEnabled = true
            mBinding.btnChange.alpha = 1f
        }
    }

    private fun updatePasswordStrengthView(password: String) {
        if (TextView.VISIBLE != mBinding.lblPasswordStrength.visibility)
            return
        if (TextUtils.isEmpty(password)) {
            mBinding.lblPasswordStrength.text = getString(R.string.password_strength)
            mBinding.passwordStrength.progress = 100
            mBinding.passwordStrength.progressTintList = ColorStateList.valueOf(Color.WHITE)
            if (!mBinding.etNewPassword.text.isNullOrBlank())
                mBinding.tlNewPassword.error =
                    getString(R.string.password_must_be_at_least_6_letters_and_must_also_include_special_characters_and_numbers)

            return
        }
        mBinding.passwordStrength.progress = 0
        val str = PasswordStrength.calculateStrength(password)
        mBinding.lblPasswordStrength.text = str.getText(this)
        mBinding.passwordStrength.progressTintList = ColorStateList.valueOf(str.color)
        mBinding.lblPwdMustMatch.gone()
        when (str) {
            PasswordStrength.WEAK -> {
                mBinding.tlNewPassword.error =
                    getString(R.string.password_must_be_at_least_6_letters_and_must_also_include_special_characters_and_numbers)

                mBinding.passwordStrength.progress = 30
            }
            PasswordStrength.MEDIUM -> {
                mBinding.tlNewPassword.error =
                    getString(R.string.password_must_be_at_least_6_letters_and_must_also_include_special_characters_and_numbers)

                mBinding.passwordStrength.progress = 60
            }
            PasswordStrength.STRONG -> {
                mBinding.lblPwdMustMatch.visible()
                mBinding.tlNewPassword.isErrorEnabled = false
                mBinding.passwordStrength.progress = 100
                /* mBinding.passwordStrength.progressDrawable =
                     ContextCompat.getDrawable(this, R.drawable.strong_password_bg)*/
            }
            else -> {
                mBinding.lblPasswordStrength.text = getString(R.string.password_strength)
                mBinding.passwordStrength.progress = 100
                mBinding.passwordStrength.progressTintList = ColorStateList.valueOf(Color.WHITE)
                mBinding.tlNewPassword.error =
                    getString(R.string.password_must_be_at_least_6_letters_and_must_also_include_special_characters_and_numbers)
            }
        }
    }

    private fun removeProfilePictureConfirmationDialog() {
        val title = ""
        val description = "" + getString(R.string.remove_profile_msg)

        val builder = AlertDialog.Builder(this, R.style.DialogTheme)
        builder.setTitle("" + title)
            .setMessage("" + description)
            .setCancelable(true)
            .setPositiveButton(
                "" + getString(R.string.yes)
            ) { dialog, _ ->
                dialog!!.dismiss()
                if (isNetwork())
                    callRemoveProfilePictureAPI()
            }
            .setNegativeButton(
                "" + getString(R.string.no)
            ) { dialog, _ -> dialog!!.dismiss() }
        val alert = builder.create()
        alert.show()
    }

    private fun callRemoveProfilePictureAPI() {
        mViewModel.callRemoveProfilePictureAPI(this, mDisposable)
    }

    private fun openRegionSelectionDialog(regionList: ArrayList<RegionListApiResponse.Data>) {
        val dialog =
            RegionSelectionDialog(this,
                regionList,
                mViewModel.selectedRegionPos,
                object : RegionSelectionDialog.RegionSelectionListener {
                    override fun onRegionSelect(selectedPos: Int) {
                        mViewModel.selectedRegionPos = selectedPos
                        mBinding.tvRegion.text = regionList[selectedPos].name
                        mViewModel.selectedRegionId = regionList[selectedPos].id!!
                        mViewModel.selectedRegion = regionList[selectedPos]!!
                        mBinding.tvCountry.text = getString(R.string.select)
                        mViewModel.selectedCountry = null
                        mViewModel.selectedCountryId = 0
                        mViewModel.selectedCountryPos = 0
                        mViewModel.countryApi(
                            0,
                            mViewModel.selectedRegionId,
                            this@ProfileAccountInfoActivity,
                            mDisposable
                        )
                    }
                }
            )

        dialog.setOnShowListener(object : DialogInterface.OnShowListener {
            override fun onShow(dialog: DialogInterface) {
                val bottomSheet: FrameLayout = (dialog as BottomSheetDialog).findViewById(R.id.design_bottom_sheet)!!
                val lyout: CoordinatorLayout = bottomSheet.getParent() as CoordinatorLayout
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
                    bottomSheet!!
                )
                behavior.peekHeight = bottomSheet.getHeight()
                lyout.getParent().requestLayout()
            }
        })

        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun openCountrySelectionDialog(regionList: ArrayList<RegionListApiResponse.Data>) {
        val dialog =
            CountrySelectionDialog(this,
                regionList,
                mViewModel.selectedCountryPos,
                object : CountrySelectionDialog.CountrySelectionListener {
                    override fun onCountrySelect(selectedPos: Int) {
                        mViewModel.selectedCountryPos = selectedPos
                        mBinding.tvCountry.text = regionList[selectedPos].name
                        mViewModel.selectedCountryId = regionList[selectedPos].id ?: 0
                        mViewModel.selectedCountry = regionList[selectedPos]
                    }
                }
            )
        dialog.setOnShowListener(object : DialogInterface.OnShowListener {
            override fun onShow(dialog: DialogInterface) {
                val bottomSheet: FrameLayout = (dialog as BottomSheetDialog).findViewById(R.id.design_bottom_sheet)!!
                val lyout: CoordinatorLayout = bottomSheet.getParent() as CoordinatorLayout
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
                    bottomSheet!!
                )
                behavior.peekHeight = bottomSheet.getHeight()
                lyout.getParent().requestLayout()
            }
        })
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun openAgeSelectionDialog(mAgeList: ArrayList<LoginResponse.AgeRangeDetail>) {
        val dialog =
            AgeSelectionDialog(this, mAgeList,
                object : AgeSelectionDialog.AgeSelectionListener {
                    override fun onAgeSelect(index: Int) {
                        mBinding.tvAge.text = mAgeList[index].ageRange
                        mViewModel.selectedAge = index
                    }
                }
            )
        dialog.setOnShowListener(object : DialogInterface.OnShowListener {
            override fun onShow(dialog: DialogInterface) {
                val bottomSheet: FrameLayout = (dialog as BottomSheetDialog).findViewById(R.id.design_bottom_sheet)!!
                val lyout: CoordinatorLayout = bottomSheet.getParent() as CoordinatorLayout
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
                    bottomSheet!!
                )
                behavior.peekHeight = bottomSheet.getHeight()
                lyout.getParent().requestLayout()
            }
        })
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun openGenderSelectionDialog(mGenderList: ArrayList<CommonResponse>) {
        val dialog =
            CommonSelectionDialog(this, mGenderList,
                object : CommonSelectionDialog.CommonSelectionListener {
                    override fun onCommonSelect(index: Int) {
                        mBinding.tvGender.text = mGenderList[index].title
                        mViewModel.selectedGender = index
                    }
                }
            )
        dialog.setOnShowListener(object : DialogInterface.OnShowListener {
            override fun onShow(dialog: DialogInterface) {
                val bottomSheet: FrameLayout = (dialog as BottomSheetDialog).findViewById(R.id.design_bottom_sheet)!!
                val lyout: CoordinatorLayout = bottomSheet.getParent() as CoordinatorLayout
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
                    bottomSheet!!
                )
                behavior.peekHeight = bottomSheet.getHeight()
                lyout.getParent().requestLayout()
            }
        })
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun openLanguageSelectionDialog(arrayList: ArrayList<LanguageListApiResponse.Data>) {
        val selectedLang = arrayList.filter { it.isSelected }.map { it.languageId } as ArrayList<Int>

        val dialog =
            LanguageSelectionDialog(this, arrayList, selectedLang,
                object : LanguageSelectionDialog.LanguageSelectionListener {
                    override fun onLanguageSelect(selectedLang: ArrayList<Int>) {
                        val selectedData = ArrayList<LanguageListApiResponse.Data>()
                        mViewModel.languageList.forEach { language ->
                            language.isSelected = false
                            if (selectedLang.any { language.languageId == it }) {
                                language.isSelected = true
                                selectedData.add(language)
                            }
                        }

                        mBinding.tvLanguage.text = selectedData.filter { it.isSelected }.joinToString { it.language!! }
                        mViewModel.spokenLanguage = selectedLang
                    }
                }
            )
        dialog.setOnShowListener(object : DialogInterface.OnShowListener {
            override fun onShow(dialog: DialogInterface) {
                val bottomSheet: FrameLayout = (dialog as BottomSheetDialog).findViewById(R.id.design_bottom_sheet)!!
                val lyout: CoordinatorLayout = bottomSheet.getParent() as CoordinatorLayout
                val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(
                    bottomSheet!!
                )
                behavior.peekHeight = bottomSheet.getHeight()
                lyout.getParent().requestLayout()
            }
        })
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun checkScreenValidation(): Boolean {
        var isValidFirstName = false
        var isValidLastName = false

        if (TextUtils.isEmpty(mBinding.etFirstName.text.toString().trim())) {
            mBinding.tlFirstName.error = getString(R.string.please_enter_first_name)
            mBinding.etFirstName.requestFocus()
            mBinding.scrollView.smoothScrollTo(0, mBinding.etFirstName.top)
            isValidFirstName = false
        } else {
            if (mBinding.etFirstName.text.toString().trim().length > 30) {
                mBinding.tlFirstName.error =
                    getString(R.string.first_name_should_not_exceed_more_than_30_characters)
                mBinding.etFirstName.requestFocus()
                mBinding.scrollView.smoothScrollTo(0, mBinding.etFirstName.top)
                isValidFirstName = false
            } else {
                isValidFirstName = true
                mBinding.tlFirstName.error = ""
            }
        }

        if (TextUtils.isEmpty(mBinding.etLastName.text.toString().trim())) {
            mBinding.tlLastName.error = getString(R.string.please_enter_last_name)
            mBinding.etLastName.requestFocus()
            mBinding.scrollView.smoothScrollTo(0, mBinding.etLastName.top)
            isValidLastName = false
        } else {
            if (mBinding.etLastName.text.toString().trim().length > 30) {
                mBinding.tlLastName.error =
                    getString(R.string.last_name_should_not_exceed_more_than_30_characters)
                mBinding.etLastName.requestFocus()
                mBinding.scrollView.smoothScrollTo(0, mBinding.etLastName.top)
                isValidLastName = false
            } else {
                isValidLastName = true
                mBinding.tlLastName.error = ""
            }
        }

        /*if (TextUtils.isEmpty(mBinding.etPhoneNumber.text.toString().trim())) {
            mBinding.tlPhoneNumber.error = getString(R.string.please_enter_mobile)
            mBinding.etPhoneNumber.requestFocus()
            mBinding.scrollView.smoothScrollTo(0, mBinding.etPhoneNumber.top)
            isValidPhone = false
        } else {
            if (isMobileInvalid(mBinding.etPhoneNumber.text.toString())) {
                mBinding.tlPhoneNumber.error = getString(R.string.please_enter_valid_mobile)
                mBinding.etPhoneNumber.requestFocus()
                mBinding.scrollView.smoothScrollTo(0, mBinding.etPhoneNumber.top)
                isValidPhone = false
            } else {
                isValidPhone = true
                mBinding.tlPhoneNumber.error = ""
            }
        }*/

        /*if (selectedCountry.isNullOrEmpty()) {
            longToast(getString(R.string.please_select_country))
            return false
        }*/

        /*if (selectedRegion == -1) {
            longToast(getString(R.string.please_select_region))
            return false
        }*/
        /*if (selectedGender.isNullOrEmpty()) {
            longToast(getString(R.string.please_select_gender))
            return false
        }*/
        /*if (selectedAge.isNullOrEmpty()) {
            longToast(getString(R.string.please_select_age))
            mBinding.scrollView.smoothScrollTo(0, mBinding.relAge.top)
            return false
        }*/

        /*if (isValidFirstName && isValidLastName && isValidAboutMe && isValidPhone) {
            if (spokenLanguage.isNullOrEmpty()) {
                longToast(getString(R.string.please_select_spoken_language))
                mBinding.scrollView.smoothScrollTo(0, mBinding.relLanguage.top)
                return false
            } else if (selectedRegion == -1) {
                longToast(getString(R.string.please_select_region))
                return false
            }
        } else return false*/
        return isValidFirstName && isValidLastName /*&& isValidAboutMe && isValidPhone*/
    }

    private fun checkPasswordValidation(): Boolean {
        var isValidPassword = true
        var isValidNewPassword = true
        var isValidConfirmPassword = true
        if (TextUtils.isEmpty(mBinding.etPassword.text.toString().trim())) {
            mBinding.tlPassword.error = getString(R.string.please_enter_password)
            isValidPassword = false
        } else {
            mBinding.tlPassword.error = ""
        }

        if (TextUtils.isEmpty(mBinding.etNewPassword.text.toString().trim())) {
            mBinding.tlNewPassword.error = getString(R.string.please_enter_new_password)
            isValidNewPassword = false
        } else {
            if (!isValidPassword(mBinding.etNewPassword.text.toString().trim())) {
                mBinding.tlNewPassword.error =
                    getString(R.string.new_password_must_be_at_least_6_letters_and_must_also_include_special_characters_and_numbers)
                isValidNewPassword = false
            } else {
                if (mBinding.etNewPassword.text.toString().trim().length < 10) {
                    mBinding.tlNewPassword.error =
                        getString(R.string.new_password_must_contain_at_least_6_characters)
                    isValidNewPassword = false
                } else {
                    if (isEmoji(mBinding.etNewPassword.text.toString().trim())) {
                        mBinding.tlNewPassword.error =
                            getString(R.string.please_do_not_enter_emojis_into_the_new_password)
                        isValidNewPassword = false
                    } else {
                        isValidNewPassword = true
                        mBinding.tlNewPassword.error = ""
                    }
                }
            }
        }

        if (TextUtils.isEmpty(mBinding.etConfirmPassword.text.toString().trim())) {
            mBinding.tlConfirmPassword.error = getString(R.string.please_enter_confirm_password)
            isValidConfirmPassword = false
        } else {
            if (mBinding.etConfirmPassword.text.toString()
                    .trim() != mBinding.etNewPassword.text.toString().trim()
            ) {
                mBinding.tlConfirmPassword.error =
                    getString(R.string.confirm_password_and_new_password_must_be_the_same)
                isValidConfirmPassword = false
            } else {
                isValidConfirmPassword = true
                mBinding.tlConfirmPassword.error = ""
            }
        }

        return isValidPassword && isValidNewPassword && isValidConfirmPassword
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
        if (Constants.IS_PROFILE_PICTURE_UPDATE) {
            longToast(Constants.profileUpdatedMessage, SUCCESS)
            if (isNetwork())
                mViewModel.userProfile(this, mDisposable)
            Constants.IS_PROFILE_PICTURE_UPDATE = false
        }
    }

    override fun onUnknownError(error: String?) {
        super.onUnknownError(error)
        if (error != null) {
            longToast(error, ERROR)
        }
    }

    private fun setSpan(s: String, tv: TextView) {
        val status = getString(R.string.str_profile_not)
        val ss1 = SpannableString(s)
        val mIndex1: Int = s.indexOf(status)
        val lastIndex1: Int = mIndex1 + status.length
        ss1.setSpan(
            StyleSpan(Typeface.BOLD),
            mIndex1,
            lastIndex1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        ) //bold
        ss1.setSpan(RelativeSizeSpan(1.1f), mIndex1, lastIndex1, 0) // set size
        tv.text = ss1
    }

//    private fun shareUserProfileFun(){
//        val shareIntent = Intent().apply {
//            action = Intent.ACTION_SEND
//            type = "text/plain"
//            putExtra(
//                Intent.EXTRA_TEXT,
//                URL_PROFILE
//            )
//            putExtra(Intent.EXTRA_TITLE, Constants.appName)
//        }
//        startActivity(Intent.createChooser(shareIntent, Constants.appName))
//    }

    private fun shareUserProfileFun() {
        val profileImage = userHolder.originalProfilePicUrl
       shareUserProfile(
            username = userHolder.firstName + " " + userHolder.lastName,
            userId = userHolder.id.toString(), userProfilePhoto = profileImage.toString(),
            URL_PROFILE
        )
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