package com.app.taocalligraphy.models.eventbus

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaylistEditListener(val isEdited: Boolean, var title: String) : Parcelable
