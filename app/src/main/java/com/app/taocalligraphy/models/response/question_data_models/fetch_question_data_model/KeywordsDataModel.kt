package com.app.taocalligraphy.models.response.question_data_models.fetch_question_data_model

import com.google.gson.annotations.SerializedName

data class KeywordsDataModel(
    @SerializedName("keywordId")
    var keywordId: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("isSelected")
    var isSelected: Boolean?,
    var isSelectedLocalParam: Boolean?,
)
