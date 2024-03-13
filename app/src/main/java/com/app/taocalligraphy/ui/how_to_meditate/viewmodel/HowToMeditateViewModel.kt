package com.app.taocalligraphy.ui.how_to_meditate.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.FetchHowToMeditateRequest
import com.app.taocalligraphy.models.response.how_to_meditate_response.FetchHowToMeditateDataListResponse
import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.HowToMeditateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class HowToMeditateViewModel @Inject constructor(var howToMeditateRepository: HowToMeditateRepository) :
    BaseViewModel() {

    var searchKeyword: String = ""
    var sortField: String = ""
    var sortOrder: String = ""
    var perPageLimit: Int = 0
    var isWatchSelected = true
    var isReadSelected = false
    var type: String = Constants.watch
    var currentPageWatchMeditation = -1
    var currentPageReadMeditation = -1
    var isAllWatchDataLoaded = 0
    var isAllReadDataLoaded = 0

    var watchDataList: ArrayList<HowToMeditateDataModel?> = ArrayList()
    var readDataList: ArrayList<HowToMeditateDataModel?> = ArrayList()

    val fetchReadHowToMeditateResponse =
        MutableLiveData<RequestState<FetchHowToMeditateDataListResponse>>()
    val fetchWatchHowToMeditateResponse =
        MutableLiveData<RequestState<FetchHowToMeditateDataListResponse>>()

    fun fetchReadHowToMeditateDataListAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageReadMeditation += 1

        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.type] = type

        howToMeditateRepository.fetchHowToMeditateDataListAPI(
            FetchHowToMeditateRequest().apply {
                this.currentPage = currentPageReadMeditation
                this.perPage = perPageLimit
                this.search = searchKeyword
                this.sort = FetchHowToMeditateRequest.Sort(sortField, sortOrder)
            },
            queryMap,
            baseView, disposable, fetchReadHowToMeditateResponse
        )
    }

    fun fetchWatchHowToMeditateDataListAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageWatchMeditation += 1

        perPageLimit = 3
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.type] = type

        howToMeditateRepository.fetchHowToMeditateDataListAPI(
            FetchHowToMeditateRequest().apply {
                this.currentPage = currentPageWatchMeditation
                this.perPage = perPageLimit
                this.search = searchKeyword
                this.sort = FetchHowToMeditateRequest.Sort(sortField, sortOrder)
            },
            queryMap,
            baseView, disposable, fetchWatchHowToMeditateResponse
        )
    }
}