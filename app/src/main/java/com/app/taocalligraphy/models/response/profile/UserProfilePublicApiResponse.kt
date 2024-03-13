package com.app.taocalligraphy.models.response.profile

import com.google.gson.annotations.SerializedName

data class UserProfilePublicApiResponse(
    @SerializedName("aboutMe")
    var aboutMe: String?,
    @SerializedName("country")
    var country: String?,
    @SerializedName("dateOfBirth")
    var dateOfBirth: String?,
    @SerializedName("gender")
    var gender: String?,
    @SerializedName("interest")
    var interest: List<Interest?>?,
    @SerializedName("isSearchable")
    var isSearchable: Boolean? = false,
    @SerializedName("isViewAboutMe")
    var isViewAboutMe: Boolean? = false,
    @SerializedName("isViewBirthday")
    var isViewBirthday: Boolean? = false,
    @SerializedName("isViewGender")
    var isViewGender: Boolean? = false,
    @SerializedName("profilePicture")
    var profilePicture: String?,
    @SerializedName("isViewInterest")
    var isViewInterest: Boolean? = false,
    @SerializedName("profileLink")
    var profileLink: String?,
    @SerializedName("userLanguages")
    var userLanguages: List<UserLanguage?>?,
    @SerializedName("username")
    var username: String?
) {
    data class UserLanguage(
        @SerializedName("language")
        var language: String?,
        @SerializedName("languageId")
        var languageId: Int?
    )

    data class Interest(
        @SerializedName("id")
        var id: Int?,
        @SerializedName("name")
        var name: String?
    )
}