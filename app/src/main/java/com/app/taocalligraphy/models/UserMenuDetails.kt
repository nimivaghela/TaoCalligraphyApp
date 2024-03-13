package com.app.taocalligraphy.models

import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.google.gson.annotations.SerializedName

 data class UserMenuDetails(
     var title: String,
     var image: Int?,
     var canAccess : Boolean? = false,
     var subscriptionBadge: String = "",
)