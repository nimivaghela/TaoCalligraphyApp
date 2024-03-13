package com.app.taocalligraphy.models.eventbus

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IsNetworkAvailableListener(val isAvailable: Boolean) : Parcelable
