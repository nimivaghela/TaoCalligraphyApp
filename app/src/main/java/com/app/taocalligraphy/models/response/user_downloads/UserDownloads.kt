package com.app.taocalligraphy.models.response.user_downloads

data class UserDownloads(
    val list: List<DownloadedContent> = ArrayList(),
    val totalRecords: Int = -1
)

data class DownloadedContent(
    val category: List<Category> = ArrayList(),
    val contentId: Int = -1,
    val contentImage: String = "",
    val thumbnailImage: String? = "",
    val isLocked: Boolean = false,
    val isSubscribed: Boolean = false,
    val sizeInMb: String = "",
    val title: String = "",
    val unlockDays: Any
)

data class Category(
    val parentCategory: String = "",
    val subCategory: String = ""
)