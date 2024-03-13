package com.app.taocalligraphy.models.response.notification_model

data class NotificationListDataModel(
    var type: String? = null,
    var dataList: ArrayList<FetchNotificationListDataResponse.NotificationData.NotificationDataList?>? = null,
    var unReadCount: String? = null,
    var totalCount : String? = null

)
