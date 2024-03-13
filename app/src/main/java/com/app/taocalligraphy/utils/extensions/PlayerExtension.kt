package com.app.taocalligraphy.utils.extensions

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.app.taocalligraphy.R
import java.util.concurrent.TimeUnit

fun View.setSelectedPlaybackSpeed(context: Context, playerPlaybackSpeed : Float) {
    val tv5Speed = this.findViewById<TextView>(R.id.tv5Speed)
    val tv75Speed = this.findViewById<TextView>(R.id.tv75Speed)
    val tvNormalSpeed = this.findViewById<TextView>(R.id.tvNormalSpeed)
    val tv15Speed = this.findViewById<TextView>(R.id.tv15Speed)
    val tv2Speed = this.findViewById<TextView>(R.id.tv2Speed)
    val tvSlower = this.findViewById<TextView>(R.id.tvSlower)
    val tvFaster = this.findViewById<TextView>(R.id.tvFaster)

    tv5Speed?.setDefaultStyleForPlaybackSpeed(context)
    tv75Speed?.setDefaultStyleForPlaybackSpeed(context)
    tvNormalSpeed?.setDefaultStyleForPlaybackSpeed(context)
    tv15Speed?.setDefaultStyleForPlaybackSpeed(context)
    tv2Speed?.setDefaultStyleForPlaybackSpeed(context)
    tvSlower?.alpha = 1f
    tvFaster?.alpha = 1f

    when (playerPlaybackSpeed) {
        0.5f -> {
            tv5Speed?.setSelectedStyleForPlaybackSpeed()
            tvSlower?.alpha = 0.5f
        }
        0.75f -> {
            tv75Speed?.setSelectedStyleForPlaybackSpeed()
        }
        1f -> {
            tvNormalSpeed?.setSelectedStyleForPlaybackSpeed()
        }
        1.5f -> {
            tv15Speed?.setSelectedStyleForPlaybackSpeed()
        }
        2f -> {
            tv2Speed?.setSelectedStyleForPlaybackSpeed()
            tvFaster?.alpha = 0.5f
        }
    }
}

private fun TextView.setDefaultStyleForPlaybackSpeed(context: Context) {
    this.setTextColor(ContextCompat.getColor(context, R.color.black))
    this.backgroundTintList = ContextCompat.getColorStateList(context, R.color.white)
    this.typeface = ResourcesCompat.getFont(context, R.font.font_jost_regular)
}

private fun TextView.setSelectedStyleForPlaybackSpeed() {
    this.setTextColor(ContextCompat.getColor(context, R.color.white))
    this.backgroundTintList = ContextCompat.getColorStateList(context, R.color.gold)
    this.typeface = ResourcesCompat.getFont(context, R.font.font_jost_medium)
}

fun String.convertContentTimeAsLong() : Long {
    val dataDate = this.split(":")
    val hours = dataDate[0]
    val minutes = dataDate[1]
    val seconds = dataDate[2]
    val contentTotalSeconds =
        (hours.toLong() * 60 * 60) + (minutes.toLong() * 60) + seconds.toLong()
    val contentTotalTime = TimeUnit.SECONDS.toMillis(contentTotalSeconds)
    Log.e("TAG", "convertContentTimeAsLong: $contentTotalTime" )
    return contentTotalTime
}
