package com.app.taocalligraphy.ui.statistics.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.WeekDatesDayListModel
import com.app.taocalligraphy.models.response.DailyMeditationStats
import com.app.taocalligraphy.models.response.FetchDailyStatDataReponse
import com.app.taocalligraphy.models.response.stats_response.FetchMonthStatDataReponse
import com.app.taocalligraphy.models.response.stats_response.FetchWeekStatDataReponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.StatsRepository
import com.app.taocalligraphy.utils.extensions.getCurrentDateWithFormatyyyyMMddFormat
import com.app.taocalligraphy.utils.extensions.getFormattedDateForStats
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import org.joda.time.DateTime
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(private val statsRepository: StatsRepository) :
    BaseViewModel() {

    // Activity State for current tab
    var type: String = Constants.day

    var mWeekLast7DayList = mutableListOf<WeekDatesDayListModel>()

    var meditationAveragePerMonthForWeek: FetchWeekStatDataReponse.MonthlyMeditationStats? = null
    var meditationAveragePerDayForWeek: FetchWeekStatDataReponse.MeditationAveragePerDay? = null
    var meditationAveragePerMonthForMonth: FetchMonthStatDataReponse.MonthlyMeditationStats? = null
    var meditationAveragePerDayForMonth: FetchMonthStatDataReponse.MeditationAveragePerDay? = null

    var todayDate: DateTime = DateTime().withTimeAtStartOfDay()
    var datePassForAPI: DateTime = DateTime().withTimeAtStartOfDay()
    var currentDate = getCurrentDateWithFormatyyyyMMddFormat()

    var isDailySelectedForWeek: Boolean = true
    var isWeekSelectedForWeek: Boolean = true
    var isDailySelectedForMonth: Boolean = true
    var isWeekSelectedForMonth: Boolean = true

    var dailyMeditationStats: DailyMeditationStats? = null
    var weeklyMeditationStats: FetchWeekStatDataReponse? = null
    var monthlyMeditationStats: FetchMonthStatDataReponse? = null

    val fetchDailyStatDataReponse =
        MutableLiveData<RequestState<FetchDailyStatDataReponse>>()
    val fetchWeekStatDataReponse =
        MutableLiveData<RequestState<FetchWeekStatDataReponse>>()
    val fetchMonthStatDataReponse =
        MutableLiveData<RequestState<FetchMonthStatDataReponse>>()

    fun fetchDailyStatsData(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val queryMap: HashMap<String, String> = HashMap()
        queryMap[Constants.Param.date] = datePassForAPI.getFormattedDateForStats()
        queryMap[Constants.Param.type] = Constants.day

        statsRepository.fetchDailyStatsData(
            queryMap,
            baseView,
            disposable,
            fetchDailyStatDataReponse
        )
    }

    fun fetchWeekStatsData(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val queryMap: HashMap<String, String> = HashMap()
        queryMap[Constants.Param.date] = currentDate
        queryMap[Constants.Param.type] = Constants.week

        statsRepository.fetchWeekStatsData(
            queryMap,
            baseView,
            disposable,
            fetchWeekStatDataReponse
        )
    }

    fun fetchMonthStatsData(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val queryMap: HashMap<String, String> = HashMap()
        queryMap[Constants.Param.date] = currentDate
        queryMap[Constants.Param.type] = Constants.month

        statsRepository.fetchMonthStatsData(
            queryMap,
            baseView,
            disposable,
            fetchMonthStatDataReponse
        )
    }

}