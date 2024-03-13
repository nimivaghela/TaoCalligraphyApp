package com.app.taocalligraphy.models.response.question_data_models

import com.app.taocalligraphy.models.response.home_screen.ForYouDataModel
import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataListModel

data class QuestionnairesResultResponse(
    val howToMeditate: HowToMeditateDataListModel?,
    val meditations: ForYouDataModel?,
    val programs: ForYouDataModel?
)