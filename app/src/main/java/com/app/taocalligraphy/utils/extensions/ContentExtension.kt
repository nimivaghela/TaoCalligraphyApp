package com.app.taocalligraphy.utils.extensions

import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import kotlin.math.roundToInt


fun List<MeditationContentResponse.CategoryDetails?>?.getCategoryTitleFromList(): String {
    var title = ""
    this?.forEach {
        title += it?.mainCategory
        if (!it?.subCategoryList.isNullOrEmpty()) {
            title = "$title > " + it?.subCategoryList?.joinToString(
                separator = ","
            )
        }
        title += "\n"
    }
    return title.trim()
}

fun List<PlaylistContentFilterApiResponse.ContentList.CategoryDetails?>?.getContentCategoryTitleFromList(): String {
    var title = ""
    this?.let {
        val total = it.size
        for (i in it.indices) {
            if ((it[i]?.subCategory?.size ?: 0) > 0) {
                title = if (total == (i + 1) && total > 1) {
                    title + it[i]?.mainCategory + " > " + it[i]?.subCategory?.joinToString(
                        separator = ","
                    )
                } else {
                    title + it[i]?.mainCategory + " > " + it[i]?.subCategory?.joinToString(
                        separator = ","
                    ) + "\n"
                }
            } else {
                if (total == (i + 1) && total > 1) {
                    title += it[i]?.mainCategory
                } else {
                    title =
                        title + it[i]?.mainCategory + "\n"
                }
            }
        }
    }
    return title
}


fun String?.getContentFileSize(): String {
    return if (!this.isNullOrEmpty()) {
        val value = this.toFloat()
        if (value >= 1024) {
            String.format("%.2f", value / 1024) + " GB"
        } else {
            val intValue = value.roundToInt()
            "$intValue MB"
        }
    } else {
        "0 MB"
    }
}

fun List<MeditationContentResponse>.getTotalFileSize(): String {
    val fileSize = this.sumOf { it.contentFileSize?.toDouble()?.roundToInt() ?: 0 }
    val total = if (fileSize >= 1024) {
        String.format("%.2f", (fileSize / 1024f)) + " GB"
    } else {
        "$fileSize MB"
    }
    return total
}