package com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryDataModel(
    @SerializedName("categoryId")
    var categoryId: String?,
    @SerializedName("categoryName")
    var categoryName: String?,
    @SerializedName("icon")
    var icon: String?,
    @SerializedName("smallIcon")
    var smallIcon: String?,
    @SerializedName("heroImage")
    var heroImage: String?,
    @SerializedName("heading")
    var heading: String,
    @SerializedName("isActive")
    var isActive: Boolean,
    @SerializedName("isInstructional")
    var isInstructional: Boolean,
    @SerializedName("isSearchable")
    var isSearchable: Boolean,
    @SerializedName("showOnFrontEnd")
    var showOnFrontEnd: Boolean,
    @SerializedName("selectedIcon")
    var selectedIcon: String,
    @SerializedName("subCategoryDetails")
    var subCategoryDetails: List<SubCategoryDetail>,
    @SerializedName("tagsList")
    var tagsList: String?,
    @SerializedName("isFeatured")
    var isFeatured: Boolean,

    val title: String?,
    val image: Int?,
    var isSelected: Boolean = false,

//    @SerializedName("selectedIcon")
//    val selectedIcon: String // https://s3.ap-south-1.amazonaws.com/openxcell-development-private/tao_calligraphy/wellness-categories/icon/1662628199773.svg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20220912T052534Z&X-Amz-SignedHeaders=host&X-Amz-Expires=1800&X-Amz-Credential=AKIAW4UEQAQL332Y77UF%2F20220912%2Fap-south-1%2Fs3%2Faws4_request&X-Amz-Signature=18f7f4f09fb65ad844477af689dfdb1c7641253d7495ae0586c62e4311443d0c
) : Parcelable {
    @Parcelize
    data class SubCategoryDetail(
        @SerializedName("icon")
        var icon: String,
//        @SerializedName("selectedIcon")
//        var selectedIcon: String,
        @SerializedName("id")
        var id: Int,
        @SerializedName("name")
        var name: String,
        @SerializedName("selectedIcon")
        val selectedIcon: String,
        @SerializedName("sortOrder")
        var sortOrder: Int,
        var isSelected: Boolean = false
    ) : Parcelable
}