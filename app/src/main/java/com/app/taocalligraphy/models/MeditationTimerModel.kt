package com.app.taocalligraphy.models

data class MeditationTimerModel(
    val title: String,
    val timer: Int,
    var isSelected: Boolean
){
    override fun toString(): String {
        return title
    }
}