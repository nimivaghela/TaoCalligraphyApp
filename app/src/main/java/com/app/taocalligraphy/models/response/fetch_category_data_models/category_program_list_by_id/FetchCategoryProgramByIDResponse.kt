package com.app.taocalligraphy.models.response.fetch_category_data_models.category_program_list_by_id

import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.google.gson.annotations.SerializedName

data class FetchCategoryProgramByIDResponse(
    @SerializedName("list")
    val list: ArrayList<ProgramDataModel?>?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
)

