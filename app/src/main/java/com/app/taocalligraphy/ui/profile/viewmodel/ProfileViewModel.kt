package com.app.taocalligraphy.ui.profile.viewmodel

import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.MeditationTimerModel
import com.app.taocalligraphy.models.ProfileMenuModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.UserModulePermission
import com.app.taocalligraphy.models.request.*
import com.app.taocalligraphy.models.response.CommonResponse
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.models.response.profile.*
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.ProfileRepository
import com.app.taocalligraphy.ui.home.viewmodel.HomeViewModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val mainRepository: ProfileRepository) :
    BaseViewModel() {

    //---------------------------------------- Profile Activity Variables --------------------------
    var goalsData: String = ""
    var interestData: String = ""
    var subscriptionPlan: String = ""
    var subscription: String = ""
    var showGoals = false
    var isProfileApiCalled = false;
    var mProfileMenuList = mutableListOf<ProfileMenuModel>()

    //---------------------------------------- Profile Account Info Activity Variables --------------------------

    var spokenLanguage: ArrayList<Int> = ArrayList()
    var mGenderList = ArrayList<CommonResponse>()
    var mAgeList = ArrayList<LoginResponse.AgeRangeDetail>()
    var selectedRegionId: Int = 0
    var selectedCountryId: Int = 0
    var selectedGender: Int = -1
    var selectedAge: Int = -1
    var selectedRegion: RegionListApiResponse.Data? = null
    var selectedCountry: RegionListApiResponse.Data? = null
    var isSearchable: Boolean = false
    var viewBirthday: Boolean = false
    var viewAboutMe: Boolean = false
    var viewGender: Boolean = false
    var viewInterest: Boolean = false
    var dateOfBirth: String? = ""
    var isShowMeditationRoomSessionInOtherLanguage: Boolean = false
    var userId = 0
    var languageList = ArrayList<LanguageListApiResponse.Data>()
    var regionList = ArrayList<RegionListApiResponse.Data>()
    var countryList = ArrayList<RegionListApiResponse.Data>()
    var selectedRegionPos = 0
    var selectedCountryPos = 0
    var day : Int = -1
    var month : Int = -1
    var year : Int = -1
    var originalProfilePicUrl : String = ""
    var loginResponse: LoginResponse? = null


    //---------------------------------------- Public Profile view Activity  Variables --------------------------

    var publicProfileData: UserProfilePublicApiResponse? = null


    //---------------------------------------- Goals Activity Variables --------------------------

    var isEditable = true
    var userGoalsScreenList = ArrayList<UserGoalsScreenApiResponse.UserGoalsScreenList?>()
    var mMeditationTargetArrayList = ArrayList<MeditationTimerModel>()
    var selectedMeditationTimer: String = "";

    //---------------------------------------- Interests Activity Variables --------------------------

     var userInterestList = java.util.ArrayList<UserInterestApiResponse.InterestList?>()
     var isViewInterests: Boolean = false

    //---------------------------------------- Interests Activity Variables --------------------------

    var settingsLanguageList = java.util.ArrayList<LanguageListApiResponse.Data>()
    var selectedSession = 1
    var mSessionArrayList = java.util.ArrayList<MeditationTimerModel>()
    var isInAppNotificationEnabled: Boolean = false
    var isEmailNotificationEnabled: Boolean = false
    var isZenModeEnabled: Boolean = false
    var is12HourFormat: Boolean? = null
    var isZenModeDetailEnabled: Boolean = false
    var isSilenceDuringMeditation: Boolean = false
    var isSilenceDuringSession: Boolean = false
    var isSilenceDuringOtherApps: Boolean = false
    var selectedAppLanguage: Int = -1
    var settingsData : UserSettingsApiResponse? = null

    val userProfileLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val languagesLiveData = MutableLiveData<RequestState<LanguageListApiResponse>>()
    val settingsLanguagesLiveData = MutableLiveData<RequestState<LanguageListApiResponse>>()
    val regionsLiveData = MutableLiveData<RequestState<RegionListApiResponse>>()
    val removeProfilePictureData = MutableLiveData<RequestState<JsonObject>>()
    val countryLiveData = MutableLiveData<RequestState<RegionListApiResponse>>()
    val changePasswordLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val userEditProfileLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val userProfilePublicLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val userProfilePictureEditLiveData = MutableLiveData<RequestState<UserProfileEditApiResponse>>()
    val getUserProfilePublicLiveData = MutableLiveData<RequestState<UserProfilePublicApiResponse>>()
    val getUserInterestLiveData = MutableLiveData<RequestState<UserInterestApiResponse>>()
    val userInterestLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val userSettingsLiveData = MutableLiveData<RequestState<UserSettingsApiResponse>>()
    val userModulePermissionData = MutableLiveData<RequestState<List<UserModulePermission>>>()
    val deleteAccountLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val userUpdateSettingsLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val userFeedbackLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val userGoalsScreen1LiveData = MutableLiveData<RequestState<UserGoalsScreenApiResponse>>()
    val userGoalsScreen2LiveData = MutableLiveData<RequestState<UserGoalsScreenApiResponse>>()
    val userGoalLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val leftMenuResponse = MutableLiveData<RequestState<LeftMenuResponse>>()

    fun userProfile(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.userProfile(
            baseView,
            disposable,
            userProfileLiveData
        )
    }

    fun getLanguages(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.getLanguages(
            baseView,
            disposable,
            languagesLiveData
        )
    }

    fun getSettingsLanguages(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.getSettingsLanguages(
            baseView,
            disposable,
            settingsLanguagesLiveData
        )
    }

    fun regionsApi(
        languageId: Int,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.languageId] = languageId
        mainRepository.regionsApi(params, baseView, disposable, regionsLiveData)
    }

    fun callRemoveProfilePictureAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.callRemoveProfilePictureAPI(baseView, disposable, removeProfilePictureData)
    }


    fun countryApi(
        languageId: Int,
        regionId: Int,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.languageId] = languageId
        params[Constants.Param.regionId] = regionId
        mainRepository.countryApi(params, baseView, disposable, countryLiveData)
    }

    fun changePasswordApi(
        currentPassword: String,
        newPassword: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.currentPassword] = currentPassword
        params[Constants.Param.newPassword] = newPassword
        mainRepository.changePasswordApi(params, baseView, disposable, changePasswordLiveData)
    }

    fun userEditProfile(
        firstName: String,
        lastName: String,
        email: String,
        mobileNo: String,
        regionId: Int?,
        countryId: Int?,
        aboutMe: String,
        dateOfBirth: String,
        gender: String,
        isShowSessionRoomInOtherLanguage: Boolean,
        spokenLanguages: List<Int>,
        ageRange: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {


        mainRepository.userEditProfile(
            UserProfileRequest(
                aboutMe = aboutMe,
                ageRange = ageRange,
                countryId = countryId,
                dateOfBirth = dateOfBirth,
                email = email,
                firstName = firstName,
                gender = gender,
                isShowSessionRoomInOtherLanguage = isShowSessionRoomInOtherLanguage,
                lastName = lastName,
                mobileNo = mobileNo,
                regionId = regionId,
                spokenLanguages = spokenLanguages
            ), baseView, disposable, userEditProfileLiveData
        )
    }

    fun userInterestApi(
        isViewInterest: Boolean,
        keywordIds: JsonArray,
        deletedKeywordIds: JsonArray,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.userInterestApi(
            UserInterestRequest().apply {
                this.isViewInterest = isViewInterest
                this.keywordIds = keywordIds
                this.deletedKeywordIds = deletedKeywordIds
            }, baseView, disposable, userInterestLiveData
        )
    }

    fun userProfilePictureEdit(
        image: MultipartBody.Part,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.userProfilePictureEdit(
            image,
            baseView,
            disposable,
            userProfilePictureEditLiveData
        )
    }

    fun userProfilePublic(
        isSearchable: Boolean,
        profileLink: String,
        viewBirthday: Boolean,
        viewAboutMe: Boolean,
        viewGender: Boolean,
        viewInterest: Boolean,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.userProfilePublic(
            UserProfilePublicRequest().apply {
                this.isSearchable = isSearchable
                this.profileLink = profileLink
                this.viewBirthday = viewBirthday
                this.viewAboutMe = viewAboutMe
                this.viewGender = viewGender
                this.viewInterest = viewInterest
            },
            baseView, disposable, userProfilePublicLiveData,
        )
    }

    fun getUserProfilePublicApi(
        userId: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.userId] = userId
        mainRepository.getUserProfilePublicApi(
            params,
            baseView,
            disposable,
            getUserProfilePublicLiveData
        )
    }

    fun getUserInterestsApi(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.getUserInterestsApi(baseView, disposable, getUserInterestLiveData)
    }

    fun getUserSettingsApi(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.getUserSettingsApi(baseView, disposable, userSettingsLiveData)
    }

    fun getUserModulePermissionApi(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.getUserModulePermissionApi(baseView, disposable, userModulePermissionData)
    }


    fun deleteAccountApi(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.deleteAccountApi(baseView, disposable, deleteAccountLiveData)
    }

    fun userSettingsApi(
        appLanguage: Int,
        isEmailNotificationEnabled: Boolean,
        isInAppNotificationEnabled: Boolean,
        isSilenceDuringMeditation: Boolean,
        isSilenceDuringOtherApps: Boolean,
        isSilenceDuringSession: Boolean,
        isZenModeEnabled: Boolean,
        is12HourFormat: Boolean,
        sessionReminderTimeInMinute: Int,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.userSettingsApi(
            UserSettingsRequest().apply {
                this.appLanguage = appLanguage
                this.isEmailNotificationEnabled = isEmailNotificationEnabled
                this.isInAppNotificationEnabled = isInAppNotificationEnabled
                this.isSilenceDuringMeditation = isSilenceDuringMeditation
                this.isSilenceDuringOtherApps = isSilenceDuringOtherApps
                this.isSilenceDuringSession = isSilenceDuringSession
                this.isZenModeEnabled = isZenModeEnabled
                this.is12HourFormat = is12HourFormat
                this.sessionReminderTimeInMinute = sessionReminderTimeInMinute
            },
            baseView, disposable, userUpdateSettingsLiveData,
        )
    }

    fun userFeedbackApi(
        feedbackNature: String,
        message: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.userFeedbackApi(
            UserFeedbackRequest().apply {
                this.feedbackNature = feedbackNature
                this.message = message
            },
            baseView, disposable, userFeedbackLiveData,
        )
    }

    fun getUserGoalsScreen2Api(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.getUserGoalsScreen2Api(
            baseView, disposable, userGoalsScreen2LiveData,
        )
    }

    fun getLeftMenuDataApi(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.getLeftMenuDataApi(
            baseView, disposable, leftMenuResponse
        )
    }

    fun getUserGoalsScreen1Api(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.getUserGoalsScreen1Api(
            baseView, disposable, userGoalsScreen1LiveData,
        )
    }

    fun userGoalsApi(
        deletedKeywordIds: JsonArray,
        keywordIds: JsonArray,
        screen: Int,
        dailyMeditationTarget: Int,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.userGoalsApi(
            UserGoalsRequest().apply {
                this.deletedKeywordIds = deletedKeywordIds
                this.keywordIds = keywordIds
                this.screen = screen
                this.dailyMeditationTarget = dailyMeditationTarget
            },
            baseView, disposable, userGoalLiveData,
        )
    }

}