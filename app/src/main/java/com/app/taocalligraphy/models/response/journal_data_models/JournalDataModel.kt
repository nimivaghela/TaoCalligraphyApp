package com.app.taocalligraphy.models.response.journal_data_models

import com.google.gson.annotations.SerializedName

data class JournalDataModel(
    @SerializedName("journalId")
    var journalId: String?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("date")
    var date: String?,
    @SerializedName("journalEntry")
    var journalEntry: String?,
    @SerializedName("isPinned")
    var isPinned: Boolean? = false,


    var isTitle: Boolean = false,
    var titleName: String?,
    var index: Int = 0,
    var isPinnedJournalDataAvailable: Boolean = false
)