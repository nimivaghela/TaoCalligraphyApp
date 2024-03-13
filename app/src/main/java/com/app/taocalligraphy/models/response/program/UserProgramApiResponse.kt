package com.app.taocalligraphy.models.response.program


import android.os.Parcelable
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class UserProgramApiResponse(
    @SerializedName("averageRatingCount")
    var averageRatingCount: Double?,

    @SerializedName("completedDay")
    var completedDay: Int? = 0,

    @SerializedName("daysList")
    var daysList: ArrayList<Days>?,

    @SerializedName("description")
    var description: String?,

    @SerializedName("programId")
    var programId: Int?,

    /*@SerializedName("programImage")
    var programImage: String?,*/

    val thumbnailImage: String = "",
    @SerializedName("title")
    var title: String?,

    @SerializedName("totalDays")
    var totalDays: Int?,

    @SerializedName("totalRatingCount")
    var totalRatingCount: Int?,

    @SerializedName("isFavorite")
    var isFavorite: Boolean?,

    @SerializedName("isProgramJoined")
    var isProgramJoined: Boolean?,
    @SerializedName("subscription")
    var subscription: Subscription?,
    @SerializedName("isPurchased")
    var isPurchased: Boolean?,
    @SerializedName("isPaidContent")
    var isPaidContent: Boolean? = false,
    @SerializedName("isUnlockWithHearts")
    var isUnlockWithHearts: Boolean?,
    @SerializedName("heartDetails")
    var heartDetails: HeartDetails?,
    @SerializedName("currencies")
    var currencies: ArrayList<MeditationContentResponse.Currencies>?,

    @SerializedName("totalUserJoinedCount")
    var totalUserJoinedCount: Int?,
    val reviewsList: List<Reviews>? = ArrayList(),
    val isPostAssessmentCompleted: Boolean = false,
    val isRatingSkipped: Boolean = false,
    val canRejoin: Boolean = false
): Parcelable {
    @Parcelize
    data class Days(
        @SerializedName("programDate")
        var programDate: String?,
        @SerializedName("programDay")
        var programDay: Int? = -1
    ): Parcelable

    @Parcelize
    data class Reviews(
        @SerializedName("count")
        val count: Int? = -1,
        @SerializedName("tagValue")
        val tagValue: String? = ""
    ): Parcelable

    @Parcelize
    data class HeartDetails(
        @SerializedName("requiredSilverHearts")
        var requiredSilverHearts: Int?,
        @SerializedName("requiredGoldenHearts")
        var requiredGoldenHearts: Int?,
        @SerializedName("requiredDiamondHearts")
        var requiredDiamondHearts: Int?,
    ): Parcelable

    @Parcelize
    data class Subscription(
        @SerializedName("isAccessible")
        var isAccessible: Boolean? = false,
        @SerializedName("badge")
        var badge: String? = "",
    ): Parcelable
}
