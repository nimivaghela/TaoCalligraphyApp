package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

class EditJournalRequest(
    @SerializedName("title")
    var title: String = "",
    @SerializedName("journalEntry")
    var journalEntry: String = "",
    @SerializedName("journalId")
    var journalId: String = ""

)