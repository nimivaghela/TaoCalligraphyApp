package com.app.taocalligraphy.other

import android.content.SharedPreferences
import com.app.taocalligraphy.models.UserModulePermission
import com.app.taocalligraphy.models.response.alarm.AlarmResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.FetchCategoryDataResponse
import com.app.taocalligraphy.savedUserDetail
import com.app.taocalligraphy.utils.extensions.prefBoolean
import com.app.taocalligraphy.utils.extensions.prefInt
import com.app.taocalligraphy.utils.extensions.prefString
import com.google.gson.Gson
import javax.inject.Inject

class UserHolder @Inject constructor(private val preference: SharedPreferences) {

    var mAuthToken by preference.prefString("")
    var isLogin by preference.prefBoolean(false)
    var is12HourFormat by preference.prefBoolean(false)

    var accessToken by preference.prefString("")
    var id by preference.prefInt(0)
    var roleName by preference.prefString("")
    var firstName by preference.prefString("")
    var lastName by preference.prefString("")
    var emailAddress by preference.prefString("")
    var mobileNo by preference.prefString("")
    var ageRange by preference.prefString("")
    var gender by preference.prefString("")
    var originalProfilePicUrl by preference.prefString("")
    var thumbProfilePicUrl by preference.prefString("")
    var signupType by preference.prefString("")
    var socialId by preference.prefString("")
    var isEmailVerified by preference.prefBoolean(false)
    var region by preference.prefInt(0)
    var countryId by preference.prefInt(0)
    var isQuestionnaireCompleted by preference.prefBoolean(false)
    var wellnessCategoryResponse by preference.prefString("")
    var alarmResponse by preference.prefString("")
    var getChatCount by preference.prefString("0")
    var isZenModeForMeditation by preference.prefBoolean(true)
    var mDynamicLinkUserID by preference.prefString("")
    var mDynamicLinkMeditationContentID by preference.prefString("")
    var mDynamicLinkWatchMeditationContentID by preference.prefString("")
    var mDynamicLinkReadMeditationContentID by preference.prefString("")
    var mDynamicLinkProgramID by preference.prefString("")
    var mDynamicLinkMediationTimerID by preference.prefString("")
    var mDynamicLinkDailyWisdomID by preference.prefString("")
    var isSubscribed: Boolean? = null
    var isFreeTrialCompleted by preference.prefBoolean(false)
    var freeTrialDays by preference.prefInt(14)
    var freeTrialCompletionDate by preference.prefString("")
    var canAccessDownload by preference.prefBoolean(true)

    fun setDynamicLinkUserID(userID: String) {
        this.mDynamicLinkUserID = userID
    }

    fun setDynamicLinkMeditationContentID(contentId: String) {
        this.mDynamicLinkMeditationContentID = contentId
    }

    fun setDynamicLinkWatchMeditationContentID(contentId: String) {
        this.mDynamicLinkWatchMeditationContentID = contentId
    }

    fun setDynamicLinkReadMeditationContentID(contentId: String) {
        this.mDynamicLinkReadMeditationContentID = contentId
    }

    fun setDynamicLinkProgramID(programId: String) {
        this.mDynamicLinkProgramID = programId
    }

    fun setDynamicLinkMeditationTimerID(meditationTimerId: String) {
        this.mDynamicLinkMediationTimerID = meditationTimerId
    }

    fun setDynamicLinkDailyWisdomID(wisdomId: String) {
        this.mDynamicLinkDailyWisdomID = wisdomId
    }

    fun clearDynamicLinkData() {
        mDynamicLinkUserID = ""
        mDynamicLinkMeditationContentID = ""
        mDynamicLinkWatchMeditationContentID = ""
        mDynamicLinkReadMeditationContentID = ""
        mDynamicLinkProgramID = ""
        mDynamicLinkMediationTimerID = ""
        mDynamicLinkDailyWisdomID = ""
    }

    fun setUserData(
        accessToken: String,
        id: Int?,
        roleName: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        mobileNo: String?,
        ageRange: String?,
        gender: String?,
        originalProfilePicUrl: String?,
        thumbProfilePicUrl: String?,
        signupType: String?,
        socialId: String?,
        isEmailVerified: Boolean?,
        region: Int?,
        mCountryId: Int?,
        isQuestionnaireCompleted: Boolean?,
        isFreeTrialCompleted: Boolean?,
        freeTrialDays: Int?,
        freeTrialCompletionDate: String?,
    ) {
        this.accessToken = accessToken
        if (id != null) {
            this.id = id
        }
        this.roleName = roleName
        this.firstName = firstName
        this.lastName = lastName
        this.emailAddress = email
        this.mobileNo = mobileNo
        this.ageRange = ageRange
        this.gender = gender
        this.originalProfilePicUrl = originalProfilePicUrl
        this.thumbProfilePicUrl = thumbProfilePicUrl
        this.signupType = signupType
        this.socialId = socialId
        if (isEmailVerified != null) {
            this.isEmailVerified = isEmailVerified
        }
        if (region != null) {
            this.region = region
        }
        if (mCountryId != null) {
            this.countryId = mCountryId
        }
        if (isQuestionnaireCompleted != null) {
            this.isQuestionnaireCompleted = isQuestionnaireCompleted
        }
        if (isFreeTrialCompleted != null) {
            this.isFreeTrialCompleted = isFreeTrialCompleted
        }
        if (freeTrialDays != null) {
            this.freeTrialDays = freeTrialDays
        }
        if (freeTrialCompletionDate != null) {
            this.freeTrialCompletionDate = freeTrialCompletionDate
        }
    }

    fun setChatCount(chatCount: String?) {
        this.getChatCount = chatCount
    }


    fun setWellnessCategoryData(data: FetchCategoryDataResponse?) {
        data?.let {
            wellnessCategoryResponse = Gson().toJson(it)
        }
    }

    fun getWellnessCategoryData(): FetchCategoryDataResponse? {
        return Gson().fromJson(
            wellnessCategoryResponse,
            FetchCategoryDataResponse::class.java
        )
    }

    fun setAlarmData(data: AlarmResponse) {
        alarmResponse = Gson().toJson(data)
    }

    fun getAlarmData(): AlarmResponse? {
        return Gson().fromJson(
            alarmResponse,
            AlarmResponse::class.java
        )
    }

    fun setUserZenModeForMeditation(mIsZenModeForMeditation: Boolean) {
        isZenModeForMeditation = mIsZenModeForMeditation
    }

    fun clearUserHolder() {
        if (savedUserDetail.isRememberMe) {
            val varEmailAddress = savedUserDetail.saveEmailAddress
            val varPassword = savedUserDetail.password
            val varIsRememberMe = savedUserDetail.isRememberMe
            val editor: SharedPreferences.Editor = preference.edit()
            editor.clear()
            editor.apply()
            savedUserDetail.saveUserDetails(varEmailAddress, varPassword, varIsRememberMe)
        } else {
            val editor: SharedPreferences.Editor = preference.edit()
            editor.clear()
            editor.apply()
        }
        // clearing user holder on logout
        var enumSet = EnumUserModulePermission.values().toHashSet()
        enumSet.forEach { item ->
                if(enumSet.any { it.name.equals(item.name) }) {
                    EnumUserModulePermission.valueOf(item.name ).permission = null
                }
            }

    }

    enum class EnumUserModulePermission(var permission: UserModulePermission?) {
        CONTENT_LIBRARY(null),
        MEDITATION_ROOM(null),
        CHAT(null),
        JOURNAL(null),
        CALENDAR(null),
        MEDITATION_TIMER(null),
        SEARCH(null),
        NOTIFICATION(null),
        BOOK_CONSULTATION(null),
        LIGHT_TRANSMISSION(null),
        PROFILE(null),
        VIEW_OFFICIAL_MEDITATION_ROOM(null),
        VIEW_COMMUNITY_MEDITATION_ROOM(null),
        CREATE_COMMUNITY_MEDITATION_ROOM(null),
        MEDITATION_ROOM_REQUIRED_APPROVAL(null),
        REPORT_ROOM(null),
        REPORT_SESSION(null),
        REPORT_USERS(null),
        SEARCH_USERS(null),
        USE_DOWNLOAD_FUNCTION(null),
        EDIT_PROFILE(null),
        SEND_APP_FEEDBACK(null),
        USE_REFERRAL_CODE(null),
        COLLECT_REFERRAL_REWARD(null),
        ACCESS_PERSONAL_HISTORY(null),
        ADD_FAVOURITE(null),
        CREATE_MEDITATION_TIMER(null),
        SEE_PROGRAMS(null),
        CREATE_PLAYLIST(null),
        SEE_CHALLENGES(null),
        COLLECT_REWARD_HEARTS(null),
        SPEND_REWARD_HEARTS(null),
        SEE_PERSONAL_STATS(null),
        ACCESS_ADMIN_PANEL(null),
        VIEW_ALL_CONTENTS(null),
        VIEW_ALL_MEDITATION_ROOM(null),
        ACCOUNT_INFO(null),
        GOALS(null),
        INTERESTS(null),
        REFERRALS(null),
        SUBSCRIPTION(null),
        SETTINGS(null),
    }


}

class SaveUserDetail @Inject constructor(private val preference: SharedPreferences) {
    var saveEmailAddress by preference.prefString("")
    var password by preference.prefString("")
    var isRememberMe by preference.prefBoolean(false)

    fun saveUserDetails(
        saveEmailAddress: String?,
        password: String?,
        isRememberMe: Boolean?
    ) {
        this.saveEmailAddress = saveEmailAddress
        this.password = password
        this.isRememberMe = isRememberMe ?: false
    }
}