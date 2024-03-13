package com.app.taocalligraphy.models.response.profile

import com.google.gson.annotations.SerializedName

    class ReferralsResponse(
        @SerializedName("referralLink")
        val referralLink: String? = "",
        @SerializedName("referralUsersList")
        val referralUsersList: ReferralsResponse.ReferralUsersList
    ) {

        data class ReferralUsersList(
            @SerializedName("totalRecords")
            val totalRecords: Long? = 0,
            @SerializedName("list")
            val list: ArrayList<UserDetails>
        )

        data class UserDetails(
            @SerializedName("userId")
            var userId: Long? = -1,
            @SerializedName("name")
            var name: String? = " ",
            @SerializedName("profilePicUrl")
            var profilePicUrl: String?,
            @SerializedName("joinDate")
            var joinDate: String?,
        )
    }
