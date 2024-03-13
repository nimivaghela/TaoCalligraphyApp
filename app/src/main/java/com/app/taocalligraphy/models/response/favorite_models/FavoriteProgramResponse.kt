package com.app.taocalligraphy.models.response.favorite_models

import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.google.gson.annotations.SerializedName

data class FavoriteProgramResponse(
    @SerializedName("list")
    val list: ArrayList<ProgramDataModel?>?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
)
