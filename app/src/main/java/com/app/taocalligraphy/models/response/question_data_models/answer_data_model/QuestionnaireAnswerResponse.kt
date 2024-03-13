package com.app.taocalligraphy.models.response.question_data_models.answer_data_model

import com.app.taocalligraphy.models.response.question_data_models.fetch_question_data_model.KeywordsDataModel
import com.google.gson.annotations.SerializedName

data class QuestionnaireAnswerResponse(
    @SerializedName("questionId")
    var questionId: String?,
    @SerializedName("orderNo")
    var orderNo: String?,
    @SerializedName("gender")
    var gender: String?,
    @SerializedName("age")
    var age: String?,
    @SerializedName("selectedKeyword")
    var selectedKeyword: ArrayList<KeywordsDataModel?>?,
)