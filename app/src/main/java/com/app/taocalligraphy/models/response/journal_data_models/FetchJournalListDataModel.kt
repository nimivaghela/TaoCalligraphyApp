package com.app.taocalligraphy.models.response.journal_data_models

import com.google.gson.annotations.SerializedName

data class FetchJournalListDataModel(
    @SerializedName("total")
    var total: String?,
    @SerializedName("journal")
    var journal: ArrayList<JournalDataModel>?
)
