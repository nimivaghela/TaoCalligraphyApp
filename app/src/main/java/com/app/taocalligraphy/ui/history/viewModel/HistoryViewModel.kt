package com.app.taocalligraphy.ui.history.viewModel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.*
import com.app.taocalligraphy.models.response.history.FetchMeditationHistoryData
import com.app.taocalligraphy.models.response.history.FetchProgramHistoryData
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.HistoryRepository
import com.app.taocalligraphy.utils.extensions.getCurrentDateWithFormatyyyyMMddFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val historyRepository: HistoryRepository) :
    BaseViewModel() {

    var selectedTabType: String = Constants.programs
    var isInProgressProgramLastPage: Boolean = false
    var isCompletedProgramLastPage: Boolean = false
    var isMeditationLastPage: Boolean = false
    var programType: String = ""
    var date: String = getCurrentDateWithFormatyyyyMMddFormat()
    var days: Int = 1

    var currentPageInProgressProgram: Int = -1
    var currentPageCompletedProgram: Int = -1
    var currentPageMeditation: Int = -1
    var perPage: Int = 10
    private var searchKeyword: String = ""

    var isLoadingMeditationData = false
    var isLoadingCompletedProgramData = false
    var isLoadingInProgressProgramData = false
    var inProgressProgramTotalCount = 0
    var completedProgramTotalCount = 0
    var meditationTotalCount = 0
    var currentProgress = 0

    var meditationList: ArrayList<FetchMeditationHistoryData.MeditationData?> = arrayListOf()
    var inProgressProgram: ArrayList<FetchProgramHistoryData.ForYouProgramData?> = arrayListOf()
    var completedProgram: ArrayList<FetchProgramHistoryData.ForYouProgramData?> = arrayListOf()

    val fetchCompletedProgramHistoryData = MutableLiveData<RequestState<FetchProgramHistoryData>>()
    val fetchInProgressProgramHistoryData = MutableLiveData<RequestState<FetchProgramHistoryData>>()
    val fetchMeditationHistoryData = MutableLiveData<RequestState<FetchMeditationHistoryData>>()

    fun fetchForYouProgramHistoryAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.programType] = programType
        historyRepository.fetchProgramHistoryAPI(
            queryMap,
            ProgramHistoryDataRequest().apply {
                this.current_page = currentPageCompletedProgram
                this.per_page = perPage
                this.search = searchKeyword
            },
            baseView,
            disposable,
            fetchCompletedProgramHistoryData
        )
    }

    fun fetchInProgressProgramHistoryAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.programType] = programType
        historyRepository.fetchProgramHistoryAPI(
            queryMap,
            ProgramHistoryDataRequest().apply {
                this.current_page = currentPageInProgressProgram
                this.per_page = perPage
                this.search = searchKeyword
            },
            baseView,
            disposable,
            fetchInProgressProgramHistoryData
        )
    }

    fun fetchMeditationHistoryAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.days] = days
        queryMap[Constants.Param.date] = date
        historyRepository.fetchMeditationHistoryAPI(
            MeditationHistoryDataRequest().apply {
                this.current_page = currentPageMeditation
                this.per_page = perPage
                this.search = searchKeyword
            },
            queryMap,
            baseView,
            disposable,
            fetchMeditationHistoryData
        )
    }


}