package com.app.taocalligraphy.models.response

import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse

data class InitialUserExperienceDetail(
    val categoryList: List<Category>,
    val contentDetails: AllContentDetails,
    val initialContent: MeditationContentResponse
)

data class Category(
    val categoryId: Int,
    val categoryName: String,
    val icon: String,
    val selectedIcon: String
)

data class AllContentDetails(
    val guidedMeditationAudioDetail: List<ContentDetail>,
    val musicDetail: List<ContentDetail>,
    val videoDetail: List<ContentDetail>
)

data class ContentDetail(
    val contentImage: String,
    val thumbnailImage: String = "",
    val id: Int,
    val title: String
)