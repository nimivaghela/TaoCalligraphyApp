package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

data class FavoriteProgramRequest(
    @SerializedName("current_page")
    var currentPage: Int = -1,
    @SerializedName("per_page")
    var perPage: Int = -1,
    @SerializedName("search")
    var search: String = ""
)

/*{
    "current_page": 0,
    "per_page": 5,
    "search": ""
}*/
