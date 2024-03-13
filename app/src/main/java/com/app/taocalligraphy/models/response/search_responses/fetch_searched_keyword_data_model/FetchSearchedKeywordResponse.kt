package com.app.taocalligraphy.models.response.search_responses.fetch_searched_keyword_data_model

import com.google.gson.annotations.SerializedName

data class FetchSearchedKeywordResponse(
    @SerializedName("searchedKeyword")
    var searchedKeyword: ArrayList<String>?
)