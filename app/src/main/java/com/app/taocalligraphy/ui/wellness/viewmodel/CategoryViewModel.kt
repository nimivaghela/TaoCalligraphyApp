package com.app.taocalligraphy.ui.wellness.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.FetchCategoryDetailContentByFilterRequest
import com.app.taocalligraphy.models.request.FetchCategoryProgramByIDRequest
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_content_by_filter.ContentData
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_content_by_filter.FetchCategoryDetailContentByFilterResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_program_list_by_id.FetchCategoryProgramByIDResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_tags_by_id.FetchCategoryTagsByIdResponse
import com.app.taocalligraphy.models.response.home_screen.ForYouDataModel
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.CategoryRepository
import com.app.taocalligraphy.ui.field_healing.adapter.PageAdapter
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(var categoryRepository: CategoryRepository) :
    BaseViewModel() {

    var selectedTabType = ""
    var currentPage = -1
    var selectedSubCategoryId = -1
    var selectedSybCategoryName = ""
    var selectedPos = 0
    var isLastData = false
    var isResponseReceived = false
    var totalData = 0
    var dataList = ArrayList<ProgramDataModel?>()
    var categoryDataList: ArrayList<CategoryDataModel> = arrayListOf()
    var currentPageCountForVideo: Int = -1
    var currentPageCountForAudio: Int = -1
    var currentPageCountForMusic: Int = -1
    var currentPageForProgram = -1

    var perPageCount: Int = 10
    var searchText: String = ""

    var totalForProgramCount = 0
    var isSubCategoryClicked: Boolean = false
    var isRefreshClicked: Boolean = false

    var musicList: ArrayList<ContentData?> = arrayListOf()
    var audioList: ArrayList<ContentData?> = arrayListOf()
    var videoList: ArrayList<ContentData?> = arrayListOf()
    var tagsID: ArrayList<Int?> = arrayListOf()
    var tagsDataList: ArrayList<FetchCategoryTagsByIdResponse.TagsDetail?> = ArrayList()
    var selectedTagsDataList: ArrayList<Int> = ArrayList()
    val subCatList: ArrayList<CategoryDataModel.SubCategoryDetail?> = arrayListOf()
    var subCatId: Int = -1
    var adapterData: ArrayList<PageAdapter.Page> = arrayListOf()
    var isLoadingProgramData = true
    var isLoadingMusicData = true
    var isLoadingAudioData = true
    var isLoadingVideoData = true

    var isFilterSelected = false
    var isVideoResult = false
    var isAudioResult = false
    var isMusicResult = false
    var musicListCount = 0
    var videoListCount = 0
    var audioListCount = 0
    var programDataList: ArrayList<ProgramDataModel?> = arrayListOf()
    var isConfigChanges = false
    var categoryID: Int = 0
    var idNeedToPassInCategory: Int = 0

    val fetchCategoryProgramById = MutableLiveData<RequestState<FetchCategoryProgramByIDResponse>>()

    val fetchMusicContentByFilterResponse =
        MutableLiveData<RequestState<FetchCategoryDetailContentByFilterResponse>>()
    val fetchVideoContentByFilterResponse =
        MutableLiveData<RequestState<FetchCategoryDetailContentByFilterResponse>>()
    val fetchAudioContentByFilterResponse =
        MutableLiveData<RequestState<FetchCategoryDetailContentByFilterResponse>>()

    fun fetchCategoryProgramListByID(
        id: Int,
        currentPage: Int,
        perPage: Int,
        searchText: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val queryMap: HashMap<String, Any> = HashMap()
        queryMap[Constants.Param.categoryId] = id

        categoryRepository.fetchCategoryProgramListByID(
            queryMap,
            FetchCategoryProgramByIDRequest().apply {
                current_page = currentPage
                per_page = perPage
                search = searchText
            },
            baseView,
            disposable,
            fetchCategoryProgramById
        )
    }

    fun fetchMusicCategoryDetailContentByFilter(
        categoryID: Int,
        tagsId: ArrayList<Int?>,
        contentType: String,
        currentPage: Int,
        perPage: Int,
        searchText: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        categoryRepository.fetchCategoryDetailContentByFilter(
            FetchCategoryDetailContentByFilterRequest().apply {
                this.categoryId = categoryID
                this.contentType = contentType
                this.currentPage = currentPage
                this.perPage = perPage
                this.search = searchText
                this.tagsId = tagsId
            },
            baseView,
            disposable,
            fetchMusicContentByFilterResponse
        )
    }

    fun fetchAudioCategoryDetailContentByFilter(
        categoryID: Int,
        tagsId: ArrayList<Int?>,
        contentType: String,
        currentPage: Int,
        perPage: Int,
        searchText: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        categoryRepository.fetchCategoryDetailContentByFilter(
            FetchCategoryDetailContentByFilterRequest().apply {
                this.categoryId = categoryID
                this.contentType = contentType
                this.currentPage = currentPage
                this.perPage = perPage
                this.search = searchText
                this.tagsId = tagsId
            },
            baseView,
            disposable,
            fetchAudioContentByFilterResponse
        )
    }

    fun fetchVideoCategoryDetailContentByFilter(
        categoryID: Int,
        tagsId: ArrayList<Int?>,
        contentType: String,
        currentPage: Int,
        perPage: Int,
        searchText: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        categoryRepository.fetchCategoryDetailContentByFilter(
            FetchCategoryDetailContentByFilterRequest().apply {
                this.categoryId = categoryID
                this.contentType = contentType
                this.currentPage = currentPage
                this.perPage = perPage
                this.search = searchText
                this.tagsId = tagsId
            },
            baseView,
            disposable,
            fetchVideoContentByFilterResponse
        )
    }

    val getAllCategoryDataRes = MutableLiveData<RequestState<ForYouDataModel>>()

    fun getAllCategoryData(
        contentType: String,
        subCategoryId: Int,
        json: JsonObject,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        categoryRepository.getAllCategoryData(
            contentType,
            subCategoryId,
            json,
            baseView,
            disposable,
            getAllCategoryDataRes
        )
    }

}