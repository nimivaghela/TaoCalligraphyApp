package com.app.taocalligraphy.models.eventbus

import android.os.Parcelable
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaylistContentChangeListener(val meditationContentResponse: MeditationContentResponse?) : Parcelable