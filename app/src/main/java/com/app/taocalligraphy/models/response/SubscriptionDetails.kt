package com.app.taocalligraphy.models.response

import android.os.Parcelable

import com.app.taocalligraphy.utils.extensions.getCurrentDateUtc
import com.app.taocalligraphy.utils.extensions.getDateFromString
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

    @Parcelize
    data class SubscriptionDetails(
        @SerializedName("price")
        val price: String? = "",
        @SerializedName("subscriptionType")
        val subscriptionType: String? = "",
        @SerializedName("subscriptionPlan")
        val subscriptionPlan: String? = "",
        @SerializedName("subscriptionMethod")
        val subscriptionMethod: String? = "",
        @SerializedName("subscriptionDate")
        val subscriptionDate: String? = "",
        @SerializedName("nextRenewalDate")
        val nextRenewalDate: String? = "",
        @SerializedName("userSubscriptionId")
        val userSubscriptionId: String? = "",
        @SerializedName("cancelDate")
        val cancelDate: String? = "",
        @SerializedName("endDate")
        val endDate: String? = "",
        @SerializedName("isSubscribed")
        val isSubscribed: Boolean? = false,
    ) : Parcelable{

        fun isExpired():Boolean {
            if(endDate.isNullOrEmpty()) return false

            var end = getDateFromString(endDate ?: "")
            var currDate = getCurrentDateUtc()
            return currDate.after(end)
        }

    }