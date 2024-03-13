package com.app.taocalligraphy.models.response.profile

import com.google.gson.annotations.SerializedName

class LanguageListApiResponse : ArrayList<LanguageListApiResponse.Data>() {
    data class Data(
        @SerializedName("isActive")
        val isActive: Boolean?, // true
        @SerializedName("language")
        val language: String?, // English
        @SerializedName("languageId")
        val languageId: Int?, // 1
        var isSelected: Boolean = false
    )
}