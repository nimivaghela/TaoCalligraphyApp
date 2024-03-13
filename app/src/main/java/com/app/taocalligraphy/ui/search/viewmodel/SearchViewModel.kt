package com.app.taocalligraphy.ui.search.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.SearchByAllDataRequest
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.models.response.search_responses.fetch_searched_keyword_data_model.FetchSearchedKeywordResponse
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.MeditationContentListDataModel
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.ProgramListDataModel
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.SearchContentDatamodel
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.SearchDataByAllTypeResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private var searchRepository: SearchRepository) :
    BaseViewModel() {

    val fetchSearchedKeywordResponse = MutableLiveData<RequestState<FetchSearchedKeywordResponse>>()
    val searchDataByAllTypeResponse = MutableLiveData<RequestState<SearchDataByAllTypeResponse>>()
    val contentSortingApiResponse = MutableLiveData<RequestState<MeditationContentListDataModel>>()
    val programSortingApiResponse = MutableLiveData<RequestState<ProgramListDataModel>>()

    private val perPageCount = 10
    var currentPageSearchAll = -1
    var currentPageSortSearchMeditation = -1
    var currentPageSortSearchProgram = -1

    var totalSearchMeditationCount: Int = 0
    var totalSortSearchMeditationCount: Int = 0
    var totalSortSearchProgramCount: Int = 0

    var sortFieldMeditation: String = Constants.latest
    var sortOrderMeditation: String = Constants.desc
    var isLoadingSearchMeditation = true
    var meditationContentList: ArrayList<SearchContentDatamodel?> = arrayListOf()

    var sortFieldProgram: String = Constants.latest
    var sortOrderProgram: String = Constants.desc
    var isLoadingSearchProgram = true
    var programList: ArrayList<ProgramDataModel?> = arrayListOf()

    val categoryDataList: ArrayList<CategoryDataModel> = arrayListOf()
    var searchedKeywordList: ArrayList<String> = ArrayList()
    var searchKeyword: String = ""

    fun fetchSearchedKeywordAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        searchRepository.fetchSearchedKeywordAPI(
            baseView, disposable, fetchSearchedKeywordResponse
        )
    }

    fun contentSortingAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageSortSearchMeditation += 1
        searchRepository.contentSortingAPI(
            SearchByAllDataRequest().apply {
                current_page = currentPageSortSearchMeditation
                per_page = perPageCount
                search = searchKeyword
                sort =
                    SearchByAllDataRequest.SortDataModel(sortFieldMeditation, sortOrderMeditation)
            }, baseView, disposable, contentSortingApiResponse
        )
    }

    fun programSortingAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageSortSearchProgram += 1
        searchRepository.programSortingAPI(
            SearchByAllDataRequest().apply {
                current_page = currentPageSortSearchProgram
                per_page = perPageCount
                search = searchKeyword
                sort = SearchByAllDataRequest.SortDataModel(sortFieldProgram, sortOrderProgram)
            }, baseView, disposable, programSortingApiResponse
        )
    }

    fun searchAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageSearchAll += 1
        currentPageSortSearchMeditation += 1
        currentPageSortSearchProgram += 1
        searchRepository.searchAllTypeDataAPI(
            SearchByAllDataRequest().apply {
                current_page = currentPageSearchAll
                per_page = perPageCount
                search = searchKeyword
                sort = SearchByAllDataRequest.SortDataModel(Constants.latest, Constants.desc)
            }, baseView, disposable, searchDataByAllTypeResponse
        )
    }
}