package com.app.taocalligraphy.models.response.question_data_models.fetch_question_data_model

import com.google.gson.annotations.SerializedName

data class FetchQuestionListResponse(
    @SerializedName("lastAttemptedQuestionOrderNo")
    var lastAttemptedQuestionOrderNo: String?,
    @SerializedName("list")
    var list: ArrayList<QuestionDataModel?>?,
)