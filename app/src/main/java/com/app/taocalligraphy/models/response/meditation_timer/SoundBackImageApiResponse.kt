package com.app.taocalligraphy.models.response.meditation_timer


import com.google.gson.annotations.SerializedName

class SoundBackImageApiResponse : ArrayList<SoundBackImageApiResponse.SoundBackgroundImage>() {
    data class SoundBackgroundImage(
        @SerializedName("backgroundId")
        var backgroundId: Int?,
        @SerializedName("backgroundImageOriginal")
        var backgroundImageOriginal: String?,
        @SerializedName("backgroundImageThumb")
        var backgroundImageThumb: String?,
        @SerializedName("isDefault")
        var isDefault: Boolean?
    )
}

//    data class SoundBackImageApiResponse(
//        @SerializedName("data")
//        var `data`: ArrayList<Data>,
//        @SerializedName("message")
//        var message: String
//    ) {
//        data class Data(
//            @SerializedName("backgroundId")
//            var backgroundId: Int,
//            @SerializedName("backgroundImageOriginal")
//            var backgroundImageOriginal: String,
//            @SerializedName("backgroundImageThumb")
//            var backgroundImageThumb: String
//        )
//    }

/*
ArrayList<SoundBackImageApiResponse.SoundBackgroundImage>(){
    data class SoundBackgroundImage(
        @SerializedName("backgroundId")
        var backgroundId: Int,
        @SerializedName("backgroundImageOriginal")
        var backgroundImageOriginal: String,
        @SerializedName("backgroundImageThumb")
        var backgroundImageThumb: String
    )
}*/
