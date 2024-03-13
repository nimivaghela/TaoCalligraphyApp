package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

class CreateJournalRequest(
    @SerializedName("title")
    var title: String = "",
    @SerializedName("journalEntry")
    var journalEntry: String = ""

)