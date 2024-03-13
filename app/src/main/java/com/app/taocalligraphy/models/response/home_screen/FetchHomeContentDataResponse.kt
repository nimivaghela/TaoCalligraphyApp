package com.app.taocalligraphy.models.response.home_screen

import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataListModel
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.google.gson.annotations.SerializedName

data class FetchHomeContentDataResponse(
    @SerializedName("forYou")
    var forYou: ForYouDataModel?,
    @SerializedName("newRelease")
    var newRelease: ForYouDataModel?,
    @SerializedName("meditations")
    var meditations: ForYouDataModel?,
    @SerializedName("programs")
    var programs: ForYouDataModel?,
    @SerializedName("howToMeditate")
    var hotToMeditate: HowToMeditateDataListModel?
)

data class ForYouDataModel(
    @SerializedName("totalRecords")
    var totalRecords: String?,
    @SerializedName("list")
    var list: ArrayList<ProgramDataModel>?,
)

/*data class HomeProgramDataModel(
    @SerializedName("id")
    var id: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("image")
    var image: String?,
    @SerializedName("isFavorites")
    var isFavorites: Boolean?
)*/

