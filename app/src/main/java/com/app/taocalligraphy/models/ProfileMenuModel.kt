package com.app.taocalligraphy.models

class ProfileMenuModel(
    var title: String,
    var image: Int,
    var description: String,
    var canAccess: Boolean = true,
    var subsctibeBadge: String = ""
)