package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

data class GiveAnswerToQuestionRequest(
    @SerializedName("questionId")
    var questionId: Int = 0,
    @SerializedName("orderNo")
    var orderNo: Int = 0,
    @SerializedName("ageRange")
    var ageRange: String = "",
    @SerializedName("gender")
    var gender: String = "",
    @SerializedName("deletedKeywordsIds")
    var deletedKeywordsIds: ArrayList<Int> = arrayListOf(),
    @SerializedName("keywordsIds")
    var keywordsIds: ArrayList<Int> = arrayListOf(),

    )