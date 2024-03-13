package com.app.taocalligraphy.ui.user_menu.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.FetchHomeDataRequest
import com.app.taocalligraphy.models.request.SearchHomeDataModel
import com.app.taocalligraphy.models.request.SortHomeDataModel
import com.app.taocalligraphy.models.response.home_screen.FetchHomeContentDataResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


@HiltViewModel
class UserMenuViewModel @Inject constructor(private val mainRepository: MainRepository) :
    BaseViewModel() {
    val fetchNewReleaseDataResponse = MutableLiveData<RequestState<FetchHomeContentDataResponse>>()
    val fetchForYouDataResponse = MutableLiveData<RequestState<FetchHomeContentDataResponse>>()


    fun fetchNewReleaseDataAPI(
        programType: String,
        currentPage: Int,
        perPageCount: Int,
        searchField: String,
        searchKeyword: String,
        sortField: String,
        sortOrder: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.type] = programType
        mainRepository.fetchHomeContentDataAPI(
            FetchHomeDataRequest().apply {
                current_page = currentPage
                per_page = perPageCount
                search.add(SearchHomeDataModel(searchField, searchKeyword))
                sort = SortHomeDataModel(sortField, sortOrder)
            },
            queryMap,
            baseView,
            disposable,
            fetchNewReleaseDataResponse
        )
    }

    fun fetchForYouDataAPI(
        programType: String,
        currentPage: Int,
        perPageCount: Int,
        searchField: String,
        searchKeyword: String,
        sortField: String,
        sortOrder: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.type] = programType
        mainRepository.fetchHomeContentDataAPI(
            FetchHomeDataRequest().apply {
                current_page = currentPage
                per_page = perPageCount
                search.add(SearchHomeDataModel(searchField, searchKeyword))
                sort = SortHomeDataModel(sortField, sortOrder)
            },
            queryMap,
            baseView,
            disposable,
            fetchForYouDataResponse
        )
    }


}