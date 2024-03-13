package com.app.taocalligraphy.models.response.meditation_content


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList

@Parcelize
@Entity(tableName = "MeditationContent")
data class MeditationContentResponse(
    @SerializedName("availableEndDate")
    val availableEndDate: String? = "",
    @SerializedName("availableFromDate")
    val availableFromDate: String? = "",
    @SerializedName("backgroundImageMobile")
    val backgroundImageMobile: String? = "",
    @SerializedName("backgroundImageWeb")
    val backgroundImageWeb: String? = "",
    @SerializedName("chapters")
    @ColumnInfo(name = "captures")
    val chapters: List<Chapter?>? = ArrayList(),
    @SerializedName("contentDuration")
    val contentDuration: String? = "",
    @SerializedName("contentFile")
    val contentFile: String? = "",
    val contentFileForHls: String? = "",
    val contentFileForDownload: String? = "",
    val oldContentFile: String? = "",
    @SerializedName("contentImage")
    val contentImage: String? = "",
    val thumbnailImage: String? = "",
    @SerializedName("description")
    val description: String? = "",
    @SerializedName("favouritesCount")
    var favouritesCount: String? = "",
    @SerializedName("heartDetails")
    @ColumnInfo(name = "heartDetails")
    val heartDetails: HeartDetails? = HeartDetails(),
    @SerializedName("id")
    @PrimaryKey
    @ColumnInfo(name = "meditation_id")
    val id: String = "",
    @ColumnInfo(name = "createdTime")
    var createdTime: Long? = System.currentTimeMillis(),
    @SerializedName("imageCaption")
    val imageCaption: String? = "",
    @SerializedName("isForInitialExperience")
    val isForInitialExperience: Boolean? = false,
    @SerializedName("isOfflineAvailableAfterSubscription")
    val isOfflineAvailableAfterSubscription: Boolean? = false,
    @SerializedName("isPaidContent")
    var isPaidContent: Boolean? = false,
    @SerializedName("isPurchased")
    var isPurchased: Boolean? = false,
    @SerializedName("isSubscribed")
    var isSubscribed: Boolean? = false,
    @SerializedName("subscription")
    var subscription: Subscription?,
    @SerializedName("isInstructional")
    var isInstructional: Boolean? = false,
    @SerializedName("isShowRating")
    val isShowRating: Boolean? = false,
    @SerializedName("isAssessmentDone")
    val isAssessmentDone: Boolean? = false,
    val isPostAssessmentCompleted: Boolean? = false,
    @SerializedName("isFavorite")
    var isFavorites: Boolean? = false,
    @SerializedName("isLiked")
    var isLiked: Boolean? = false,
    @SerializedName("isUnlockWithHearts")
    val isUnlockWithHearts: Boolean? = false,
    @SerializedName("languageId")
    val languageId: String? = "",
    @SerializedName("contentFileSize")
    val contentFileSize: String? = "",
    @SerializedName("likesCount")
    val likesCount: String? = "",
    @SerializedName("ratingDetails")
    @ColumnInfo(name = "ratingDetails")
    val ratingDetails: RatingDetails? = RatingDetails(),
    @SerializedName("ratingsCount")
    val ratingsCount: String? = "",
    @SerializedName("reviewsList")
    @ColumnInfo(name = "reviewsList")
    val reviewsList: List<Reviews>? = ArrayList(),
    @SerializedName("subtitleWithLanguages")
    @ColumnInfo(name = "subtitles")
    val subtitleWithLanguages: List<SubtitleWithLanguage>? = ArrayList(),
    @SerializedName("categoryDetailsList")
    @ColumnInfo(name = "categoriesDetail")
    val categoryDetailsList: List<CategoryDetails>? = ArrayList(),
    @SerializedName("title")
    val title: String? = "",
    @SerializedName("type")
    val type: String? = "",
    @SerializedName("viewCounts")
    val viewCounts: String? = "",
    var isContentSelected: Boolean = false,
    var isDeleted: Boolean = false,
    @ColumnInfo(name = "feedbackTagList")
    val feedbackTagList: List<FeedbackTag> = emptyList(),
    @SerializedName("currencies")
    @ColumnInfo(name = "currencyList")
    val currencies: List<Currencies> = emptyList(),
    var authorName: String = "",
    @ColumnInfo(name = "tags")
    val tags: ArrayList<String>? = ArrayList()
) : Parcelable {
    @Parcelize
    data class Chapter(
        @SerializedName("chapterId")
        val chapterId: String? = "",
        @SerializedName("chapterName")
        val chapterName: String? = "",
        @SerializedName("chapterTimeStamp")
        val chapterTimeStamp: String? = "",
        @SerializedName("chapterType")
        val chapterType: String? = "",
        var duration: Long = 0L,
        var startTimeStamp: Long = 0L,
        var endTimeStamp: Long = 0L
    ) : Parcelable

    @Parcelize
    data class HeartDetails(
        @SerializedName("requiredDiamondHearts")
        val requiredDiamondHearts: Int? = -1,
        @SerializedName("requiredGoldenHearts")
        val requiredGoldenHearts: Int? = -1,
        @SerializedName("requiredSilverHearts")
        val requiredSilverHearts: Int? = -1
    ) : Parcelable

    @Parcelize
    data class RatingDetails(
        @SerializedName("ratingBest")
        val ratingBest: String? = "",
        @SerializedName("ratingQuestion")
        val ratingQuestion: String? = "",
        @SerializedName("ratingWorst")
        val ratingWorst: String? = ""
    ) : Parcelable

    @Parcelize
    data class Reviews(
        @SerializedName("count")
        val count: Int? = -1,
        @SerializedName("tagValue")
        val tagValue: String? = ""
    ) : Parcelable

    @Parcelize
    data class SubtitleWithLanguage(
        @SerializedName("languageId")
        val languageId: String? = "",
        @SerializedName("languageName")
        val languageName: String? = "",
        @SerializedName("subTitleFile")
        val subTitleFile: String? = ""
    ) : Parcelable

    @Parcelize
    data class CategoryDetails(
        @SerializedName("id")
        val id: String? = "",
        @SerializedName("mainCategory")
        val mainCategory: String? = "",
        @SerializedName("subCategory")
        @ColumnInfo(name = "subCategory")
        val subCategoryList: List<String>? = ArrayList(),
    ) : Parcelable

    @Parcelize
    data class FeedbackTag(
        @SerializedName("id")
        val feedbackId: Int = -1,
        val name: String = ""
    ) : Parcelable

    @Parcelize
    data class Currencies(
        @SerializedName("currencyId")
        val currencyId: Int = -1,
        @SerializedName("price")
        val price: String = "",
        @SerializedName("value")
        val value: String = "",
        @SerializedName("symbol")
        val symbol: String = "",
        @SerializedName("country")
        val country: String = "",
        @SerializedName("countryId")
        val countryId: Int = -1,
    ) : Parcelable

    @Parcelize
    data class Subscription(
        @SerializedName("isAccessible")
        var isAccessible: Boolean? = false,
        @SerializedName("badge")
        var badge: String? = "",
    ): Parcelable
}