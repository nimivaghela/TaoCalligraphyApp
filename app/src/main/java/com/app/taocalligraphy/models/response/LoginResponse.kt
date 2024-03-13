package com.app.taocalligraphy.models.response


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("accessToken")
    var accessToken: String,
    @SerializedName("ageRange")
    var ageRange: String?,
    @SerializedName("email")
    var email: String,
    @SerializedName("firstName")
    var firstName: String,
    @SerializedName("gender")
    var gender: String?,
    @SerializedName("id")
    var id: Int,
    @SerializedName("isEmailVerified")
    var isEmailVerified: Boolean,
    @SerializedName("isQuestionnaireCompleted")
    var isQuestionnaireCompleted: Boolean,
    @SerializedName("lastName")
    var lastName: String,
    @SerializedName("mobile_no")
    var mobileNo: String?,
    @SerializedName("originalProfilePicUrl")
    var originalProfilePicUrl: String,
    @SerializedName("region")
    var region: Int?,
    @SerializedName("aboutMe")
    val aboutMe: String?, // null
    @SerializedName("country")
    var country: Int?,
    @SerializedName("roleName")
    var roleName: String,
    @SerializedName("signupType")
    var signupType: String,
    @SerializedName("socialId")
    var socialId: String,
    @SerializedName("thumbProfilePicUrl")
    var thumbProfilePicUrl: String,
    @SerializedName("dateOfBirth")
    val dateOfBirth: String?, // 1997-01-01
    @SerializedName("isShowMeditationRoomSessionInOtherLanguage")
    val isShowMeditationRoomSessionInOtherLanguage: Boolean, // false
    @SerializedName("spokenLanguages")
    var spokenLanguages: List<Int>,
    @SerializedName("isSearchable")
    var isSearchable: Boolean,
    @SerializedName("is12HourFormat")
    var is12HourFormat: Boolean?,
    @SerializedName("isFreeTrialCompleted")
    var isFreeTrialCompleted: Boolean?,
    @SerializedName("freeTrialDays")
    var freeTrialDays: Int?,
    @SerializedName("freeTrialCompletionDate")
    var freeTrialCompletionDate: String,
    @SerializedName("profileLink")
    var profileLink: String,
    @SerializedName("viewAboutMe")
    var viewAboutMe: Boolean,
    @SerializedName("viewBirthday")
    var viewBirthday: Boolean,
    @SerializedName("viewGender")
    var viewGender: Boolean,
    @SerializedName("viewInterest")
    var viewInterest: Boolean,
    @SerializedName("ageRangeDetails")
    val ageRangeDetails: ArrayList<AgeRangeDetail>,
    @SerializedName("userInterests")
    var userInterests: List<String>,
){
    data class AgeRangeDetail(
        @SerializedName("ageRange")
        val ageRange: String, // 0-29
        @SerializedName("endAge")
        val endAge: Int, // 29
        @SerializedName("isSelected")
        var isSelected: Boolean, // true
        @SerializedName("startAge")
        val startAge: Int // 0
    )
}