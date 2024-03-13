package com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model

import com.google.gson.annotations.SerializedName

data class SearchDataByAllTypeResponse(
    @SerializedName("meditationContentList")
    var meditationContentList: MeditationContentListDataModel?,
    @SerializedName("sessionList")
    var sessionList: MeditationContentListDataModel?,
    @SerializedName("meditationRoomsList")
    var meditationRoomsList: MeditationContentListDataModel?,
    @SerializedName("programList")
    var programsList: ProgramListDataModel?,
    @SerializedName("totalResults")
    var totalResults: Int?

)
