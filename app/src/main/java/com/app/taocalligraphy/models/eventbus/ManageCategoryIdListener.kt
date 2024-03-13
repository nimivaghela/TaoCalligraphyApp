package com.app.taocalligraphy.models.eventbus

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ManageCategoryIdListener(val position: Int) : Parcelable
