package com.app.taocalligraphy.models.eventbus

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MeditationContentFavouriteListener(val id: String, val isFavourite: Boolean) : Parcelable