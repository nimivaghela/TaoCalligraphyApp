package com.app.taocalligraphy.ui.home.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.FetchHomeDataRequest
import com.app.taocalligraphy.models.request.SearchHomeDataModel
import com.app.taocalligraphy.models.request.SortHomeDataModel
import com.app.taocalligraphy.models.response.home_screen.FetchHomeContentDataResponse
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class SeeAllViewModel @Inject constructor(private val mainRepository: MainRepository) :
    BaseViewModel() {

    var isLoadingPrograms: Boolean = false
    var isLoadingMeditation: Boolean = false
    var currentPageMeditationCount = 0
    var isMeditationsLoadMoreData: Boolean = true
    var totalMeditationsCount: Int = 10
    var perPageCount = 10

    private val searchData = ""
    private val searchKeyword = ""
    private val sortField = ""
    private val sortOrder = ""

    var currentPageProgramsCount = 0
    var isProgramsLoadMoreData: Boolean = true
    var totalProgramsCount: Int = 10

    var selectedTabType: String = Constants.meditations

    val meditationList: ArrayList<ProgramDataModel?> = arrayListOf()
    val programList: ArrayList<ProgramDataModel?> = arrayListOf()

    val fetchMeditationDataResponse = MutableLiveData<RequestState<FetchHomeContentDataResponse>>()
    val fetchProgramsDataResponse = MutableLiveData<RequestState<FetchHomeContentDataResponse>>()

    fun fetchViewAllMeditationsDataAPI(
        listType: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        perPageCount = 10
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.listType] = listType
        queryMap[Constants.Param.type] = selectedTabType
        mainRepository.fetchViewAllContentDataAPI(
            FetchHomeDataRequest().apply {
                current_page = currentPageMeditationCount
                per_page = perPageCount
                search.add(SearchHomeDataModel(searchData, searchKeyword))
                sort = SortHomeDataModel(sortField, sortOrder)
            },
            queryMap,
            baseView,
            disposable,
            fetchMeditationDataResponse
        )
    }

    fun fetchViewAllProgramsDataAPI(
        listType: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        perPageCount = 10
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.listType] = listType
        queryMap[Constants.Param.type] = selectedTabType
        mainRepository.fetchViewAllContentDataAPI(
            FetchHomeDataRequest().apply {
                current_page = currentPageProgramsCount
                per_page = perPageCount
                search.add(SearchHomeDataModel(searchData, searchKeyword))
                sort = SortHomeDataModel(sortField, sortOrder)
            },
            queryMap,
            baseView,
            disposable,
            fetchProgramsDataResponse
        )
    }

}