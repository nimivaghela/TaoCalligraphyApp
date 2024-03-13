package com.app.taocalligraphy.ui.home.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.FetchHomeDataRequest
import com.app.taocalligraphy.models.request.SearchHomeDataModel
import com.app.taocalligraphy.models.request.SortHomeDataModel
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.models.response.home_screen.FetchHomeContentDataResponse
import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataModel
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val mainRepository: MainRepository) :
    BaseViewModel() {

    private val perPageCount = 10
    var selectedFragment = Constants.home
    var selectedWellnessCategory = 0

    var isSubscriptionStatusApiLoading = false
    var isSubscriptionErrorMessage = false

    var currentPageForYouCount = -1
    var totalForYouCount: Int = 0
    var forYouDataList: ArrayList<ProgramDataModel?> = arrayListOf()

    var currentPageNewReleaseCount = -1
    var totalNewReleaseCount: Int = 0
    var newReleasesDataList: ArrayList<ProgramDataModel?> = arrayListOf()

    var currentPageHowToMeditateCount = -1
    var totalHowToMeditateCount: Int = 0
    var howToMeditateDataList: ArrayList<HowToMeditateDataModel?> = arrayListOf()

    val categoryDataList: ArrayList<CategoryDataModel> = arrayListOf()

    fun isDataLoaded(): Boolean {
        return categoryDataList.size <= 0 || forYouDataList.size <= 0 || newReleasesDataList.size <= 0
    }

    val fetchNewReleaseDataResponse = MutableLiveData<RequestState<FetchHomeContentDataResponse>>()
    val fetchForYouDataResponse = MutableLiveData<RequestState<FetchHomeContentDataResponse>>()
    val fetchHowToMeditateDataResponse =
        MutableLiveData<RequestState<FetchHomeContentDataResponse>>()

    fun fetchNewReleaseDataAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageNewReleaseCount += 1
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.type] = Constants.newRelease
        mainRepository.fetchHomeContentDataAPI(
            FetchHomeDataRequest().apply {
                current_page = currentPageNewReleaseCount
                per_page = perPageCount
                search.add(SearchHomeDataModel("", ""))
                sort = SortHomeDataModel("", "")
            },
            queryMap,
            baseView,
            disposable,
            fetchNewReleaseDataResponse
        )
    }

    fun fetchForYouDataAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageForYouCount += 1
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.type] = Constants.forYou
        mainRepository.fetchHomeContentDataAPI(
            FetchHomeDataRequest().apply {
                current_page = currentPageForYouCount
                per_page = perPageCount
                search.add(SearchHomeDataModel("", ""))
                sort = SortHomeDataModel("", "")
            },
            queryMap,
            baseView,
            disposable,
            fetchForYouDataResponse
        )
    }

    fun fetchHowToMeditateDataAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageHowToMeditateCount += 1
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.type] = Constants.howToMeditate
        mainRepository.fetchHomeContentDataAPI(
            FetchHomeDataRequest().apply {
                current_page = currentPageHowToMeditateCount
                per_page = perPageCount
                search.add(SearchHomeDataModel("", ""))
                sort = SortHomeDataModel("", "")
            },
            queryMap,
            baseView,
            disposable,
            fetchHowToMeditateDataResponse
        )
    }


}