package com.app.taocalligraphy.ui.playlist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.*
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_tags_by_id.FetchCategoryTagsByIdResponse
import com.app.taocalligraphy.models.response.home_screen.FetchDailyWisdomDataResponse
import com.app.taocalligraphy.models.response.home_screen.FetchHomeContentDataResponse
import com.app.taocalligraphy.models.response.playList.PlaylistApiResponse
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.MeditationType
import com.app.taocalligraphy.repository.PlayListRepository
import com.app.taocalligraphy.ui.playlist.adapter.PlaylistMeditationsAdapter
import com.app.taocalligraphy.ui.playlist.adapter.PlaylistViewItemAdapter
import com.google.gson.JsonArray
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(private val mainRepository: PlayListRepository) :
    BaseViewModel() {

    // ----------------------------------------- Playlist  Activity -----------------------------------------

    var currCount = 0
    var perPage = 10
    var totalCount = 10
    var sortField: String = Constants.latest
    var sortOrder: String = Constants.desc

    var itemListApiRes: ArrayList<PlaylistApiResponse.Playlist> = ArrayList()

    // ----------------------------------------- Start Player Activity -----------------------------------------

    var selectedPlaylistPosition = 0
    val playlistMeditationsAdapter by lazy {
        return@lazy PlaylistMeditationsAdapter(TaoCalligraphy.instance, mutableListOf())
    }

    // ----------------------------------------- Choose Meditation Dialog -----------------------------------------

    val playlistContentFilterList = ArrayList<PlaylistContentFilterApiResponse.ContentList>()

    val meditationCategoryDataList = mutableListOf<CategoryDataModel>()
    val topicsDataList = mutableListOf<CategoryDataModel.SubCategoryDetail>()
    val selectedTopicsDataList = mutableListOf<CategoryDataModel.SubCategoryDetail>()
    val selectedTagsDataList = mutableListOf<FetchCategoryTagsByIdResponse.TagsDetail>()

    var filterType = MeditationType.CHOOSE_CATEGORY
    var firstTime = true
    var isLoading = false
    var count = 0
    var countWithSearch = 0
    var perpage = 20
    var totalMeditationCount = 0

    var selectedCategoryId = 0
    var selectedTagIds = ArrayList<Int>()
    var selectedTopicsIds = ArrayList<Int>()

    var previouslySearchedText : String = ""
    var searchKey = ""

    // ----------------------------------------- Choose Filter Dialog -----------------------------------------

    var categoryDataList = ArrayList<CategoryDataModel>()
    var selectedCategoryDataList = ArrayList<CategoryDataModel.SubCategoryDetail>()
    var subCategoryDataList = ArrayList<CategoryDataModel.SubCategoryDetail>()
    var tagsDetailList = ArrayList<FetchCategoryTagsByIdResponse.TagsDetail>()


    // ----------------------------------------- Playlist Details Activity -----------------------------------------
    val playlistDetailsContentList = ArrayList<PlaylistContentFilterApiResponse.ContentList>()

    var isPlaylistDetailsLoading = false
    var playListDetailsCount = 0
    var playListId: Int = 0
    var playListTitle: String = ""
    var bannerList = ArrayList<String>()

    // ----------------------------------------- Live Data -----------------------------------------

    val fetchHomeContentDataResponse = MutableLiveData<RequestState<FetchHomeContentDataResponse>>()

    val fetchCategoryTagsByIdsResponse =
        MutableLiveData<RequestState<FetchCategoryTagsByIdResponse>>()

    val playlistContentFilterLiveData =
        MutableLiveData<RequestState<PlaylistContentFilterApiResponse>>()
    val addPlaylistContentLiveData =
        MutableLiveData<RequestState<PlaylistContentFilterApiResponse>>()
    val playlistContentLiveData =
        MutableLiveData<RequestState<PlaylistContentFilterApiResponse>>()
    val playListLiveData = MutableLiveData<RequestState<PlaylistApiResponse>>()
    val playListDeleteLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val editPlaylistLiveData = MutableLiveData<RequestState<LoginResponse>>()

    fun fetchCategoryTagsByIds(
        categoryID: JsonArray,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.fetchCategoryTagsByIds(
            FetchCategoryTagsByIdsRequest().apply {
                this.categoryIds = categoryID
            },
            baseView,
            disposable,
            fetchCategoryTagsByIdsResponse
        )
    }

    fun playlistContentFilterApi(
        categoryId: Int,
        currentPage: Int,
        perPage: Int,
        search: String,
        subCategoryIds: JsonArray,
        tagsId: JsonArray,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.playlistContentFilterApi(
            PlaylistContentFilterRequest().apply {
                this.categoryId = categoryId
                this.currentPage = currentPage
                this.perPage = perPage
                this.search = search
                this.subCategoryIds = subCategoryIds
                this.tagsId = tagsId
            },
            baseView,
            disposable,
            playlistContentFilterLiveData
        )
    }

    fun addPlaylistApi(
        contentDetails: ArrayList<AddPlaylistRequest.ContentDetail> = ArrayList<AddPlaylistRequest.ContentDetail>(),
        languageId: Int,
        title: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.addPlaylistApi(
            AddPlaylistRequest().apply {
                this.contentDetails = contentDetails
                this.languageId = languageId
                this.title = title
            },
            baseView,
            disposable,
            addPlaylistContentLiveData
        )
    }

    fun editPlaylistApi(
        contentDetails: ArrayList<AddPlaylistRequest.ContentDetail> = ArrayList<AddPlaylistRequest.ContentDetail>(),
        deletedContentIds: JsonArray,
        id: Int,
        languageId: Int,
        title: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.editPlaylistApi(
            EditPlaylistRequest().apply {
                this.contentDetails = contentDetails
                this.deletedContentIds = deletedContentIds
                this.id = id
                this.languageId = languageId
                this.title = title
            },
            baseView,
            disposable,
            editPlaylistLiveData
        )
    }

    fun playlistContentApi(
        programId: Int,
        currentPage: Int,
        perPage: Int,
        search: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.programId] = programId
        mainRepository.playlistContentApi(
            params,
            PagingRequest().apply {
                this.currentPage = currentPage
                this.perPage = perPage
                this.search = search
            },
            baseView,
            disposable,
            playlistContentLiveData
        )
    }

    fun playListDeleteApi(
        playListId: Int,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.id] = playListId
        mainRepository.playListDeleteApi(
            params,
            baseView,
            disposable,
            playListDeleteLiveData
        )
    }

    fun playListApi(
        currentPage: Int,
        perPage: Int,
        search: String,
        sort: PlaylistRequest.Sort,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        mainRepository.playListApi(
            PlaylistRequest().apply {
                this.currentPage = currentPage
                this.perPage = perPage
                this.search = search
                this.sort = sort
            },
            baseView,
            disposable,
            playListLiveData
        )
    }

    fun fetchHomeContentDataAPI(
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
//        mainRepository.fetchHomeContentDataAPI(
//            FetchHomeDataRequest().apply {
//                current_page = currentPage
//                per_page = perPageCount
//                search.add(SearchHomeDataModel(searchField, searchKeyword))
//                sort = SortHomeDataModel(sortField, sortOrder)
//            },
//            queryMap,
//            baseView,
//            disposable,
//            fetchHomeContentDataResponse
//        )
    }


}