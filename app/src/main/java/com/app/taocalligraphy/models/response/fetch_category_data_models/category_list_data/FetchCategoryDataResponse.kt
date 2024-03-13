package com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data

import com.google.gson.annotations.SerializedName

data class FetchCategoryDataResponse(
    @SerializedName("total")
    var total: String?,
    @SerializedName("list")
    var list: ArrayList<CategoryDataModel>?,
)