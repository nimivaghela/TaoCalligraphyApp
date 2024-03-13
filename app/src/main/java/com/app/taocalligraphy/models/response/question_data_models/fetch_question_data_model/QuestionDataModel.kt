package com.app.taocalligraphy.models.response.question_data_models.fetch_question_data_model

import com.app.taocalligraphy.models.response.question_data_models.fetch_question_data_model.KeywordsDataModel
import com.google.gson.annotations.SerializedName

data class QuestionDataModel(
    @SerializedName("questionId")
    var questionId: String?,
    @SerializedName("question")
    var question: String?,
    @SerializedName("orderNo")
    var orderNo: String?,
    @SerializedName("isMandatory")
    var isMandatory: Boolean?,
    @SerializedName("canSelectMultipleOption")
    var canSelectMultipleOption: Boolean?,
    @SerializedName("keywords")
    var keywords: ArrayList<KeywordsDataModel?>?,

// this 2 list used for local use in app
    var checkAnswerSelectedOrNot: ArrayList<Int> = arrayListOf(),
    var selectAnswerList: ArrayList<Int> = arrayListOf(),
    var unSelectAnswerList: ArrayList<Int> = arrayListOf(),
    var ageRange: String,
    var gender: String
)