package com.app.taocalligraphy.models.response.fetch_category_data_models.category_tags_by_id

import com.google.gson.annotations.SerializedName

data class FetchCategoryTagsByIdResponse(
    @SerializedName("tagsDetails")
    var tagsDetails: List<TagsDetail>
) {
    data class TagsDetail(
        @SerializedName("id")
        var id: Int,
        @SerializedName("name")
        var name: String,
        var isSelected:Boolean=false
    )
}
