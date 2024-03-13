package com.app.taocalligraphy.models.response.journal_data_models.create_new_journal

import com.google.gson.annotations.SerializedName

data class CreateNewJournalModelResponse(
    @SerializedName("journalId")
    var journalId: String?,
)
