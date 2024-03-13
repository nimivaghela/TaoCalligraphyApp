package com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model

import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.google.gson.annotations.SerializedName

data class MeditationContentListDataModel(
    @SerializedName("totalRecords")
    var totalRecords: Int = 0,
    @SerializedName("list")
    var list: ArrayList<SearchContentDatamodel>?
)

data class ProgramListDataModel(
    @SerializedName("totalRecords")
    var totalRecords: Int = 0,
    @SerializedName("list")
    var list: ArrayList<ProgramDataModel>?
)
