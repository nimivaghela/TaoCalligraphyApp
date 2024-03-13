package com.app.taocalligraphy.ui.notification.viewmodel

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.DeleteNotificationRequest
import com.app.taocalligraphy.models.request.NotificationRequest
import com.app.taocalligraphy.models.request.ReadNotificationRequest
import com.app.taocalligraphy.models.response.notification_model.FetchNotificationListDataResponse
import com.app.taocalligraphy.models.response.notification_model.NotificationListDataModel
import com.app.taocalligraphy.models.response.notification_model.ReadNotificationDataByIdResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.NotificationListRepository
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(private val notificationListRepository: NotificationListRepository) :
    BaseViewModel() {

    var currentPageAll: Int = 0
    var perPageLimit: Int = 10
    var currentPageProgramCount: Int = 0
    var currentPageSubscriptionCount: Int = 0
    var currentPageModificationTimerCount: Int = 0
    var currentPageDailyWisdomCount: Int = 0
    val notificationDataList: ArrayList<NotificationListDataModel?> = arrayListOf()
    var mListState: Parcelable? = null
    var isConfigChanges = false

    val fetchNotificationListDataResponse =
        MutableLiveData<RequestState<FetchNotificationListDataResponse>>()

    val fetchSubscriptionDataList =
        MutableLiveData<RequestState<FetchNotificationListDataResponse>>()

    val fetchMeditationTimerDataList =
        MutableLiveData<RequestState<FetchNotificationListDataResponse>>()

    val fetchDailyWisdomDataList =
        MutableLiveData<RequestState<FetchNotificationListDataResponse>>()

    val fetchProgramDataList =
        MutableLiveData<RequestState<FetchNotificationListDataResponse>>()

    val readNotificationDataByIdResponse =
        MutableLiveData<RequestState<ReadNotificationDataByIdResponse>>()

    val deleteNotificationDataByIdResponse =
        MutableLiveData<RequestState<JsonObject>>()

    fun fetchNotificationList(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        notificationListRepository.fetchNotificationListApi(
            NotificationRequest().apply {
                this.currentPage = currentPageAll
                this.perPage = perPageLimit
                this.search = ""
                this.notificationType = Constants.all
            }, baseView, disposable, fetchNotificationListDataResponse
        )
    }

    fun fetchSubscriptionTypeNotificationList(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageSubscriptionCount += 1
        notificationListRepository.fetchNotificationListApi(
            NotificationRequest().apply {
                this.currentPage = currentPageSubscriptionCount
                this.perPage = perPageLimit
                this.search = ""
                this.notificationType = Constants.subscription
            }, baseView, disposable, fetchSubscriptionDataList
        )
    }

    fun fetchProgramTypeNotificationList(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageProgramCount += 1
        notificationListRepository.fetchNotificationListApi(
            NotificationRequest().apply {
                this.currentPage = currentPageProgramCount
                this.perPage = perPageLimit
                this.search = ""
                this.notificationType = Constants.programs
            }, baseView, disposable, fetchProgramDataList
        )
    }

    fun fetchMeditationTimerNotificationList(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageModificationTimerCount += 1
        notificationListRepository.fetchNotificationListApi(
            NotificationRequest().apply {
                this.currentPage = currentPageModificationTimerCount
                this.perPage = perPageLimit
                this.search = ""
                this.notificationType = Constants.meditation
            }, baseView, disposable, fetchMeditationTimerDataList
        )
    }

    fun fetchDailyWisdomNotificationList(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageDailyWisdomCount += 1
        notificationListRepository.fetchNotificationListApi(
            NotificationRequest().apply {
                this.currentPage = currentPageDailyWisdomCount
                this.perPage = perPageLimit
                this.search = ""
                this.notificationType = Constants.dailyWisdom
            }, baseView, disposable, fetchDailyWisdomDataList
        )
    }

    fun readNotificationByID(
        idList: ArrayList<Int>,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val queryMap: HashMap<String, Any?> = HashMap()
        queryMap[Constants.Param.notificationId] = idList
        notificationListRepository.readNotificationByID(
            ReadNotificationRequest().apply {
                this.notificationId = idList
            },
            baseView, disposable, readNotificationDataByIdResponse
        )
    }

    fun deleteNotificationByID(
        dataList: ArrayList<Int>,
        type: String?,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        notificationListRepository.deleteNotificationByID(
            DeleteNotificationRequest().apply {
                this.notificationId = dataList
                this.type = type
            }, baseView, disposable, deleteNotificationDataByIdResponse
        )
    }

}