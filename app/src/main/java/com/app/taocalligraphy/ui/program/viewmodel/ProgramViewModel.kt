package com.app.taocalligraphy.ui.program.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.ProgramListRequest
import com.app.taocalligraphy.models.request.SearchByAllDataRequest
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.models.response.program.*
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.ProgramRepository
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class ProgramViewModel @Inject constructor(private val repository: ProgramRepository) :
    BaseViewModel() {

    var userProgramList: UserProgramApiResponse? = null
    var isProgramJoined: Boolean = false
    var isFeedbackScreenVisible = false
    var isLastContentPlayed = false
    var selectedDayPos = 0
    val selectedExperience = ArrayList<FeedbackTag>()
    var linkedProgramData: LinkedProgramData? = null
    var bannerList = ArrayList<String>()
    var isFromConfigChanges = false
    var isMoveToDetail = false
    var selectedTab = TaoCalligraphy.instance.getString(R.string.program_capital)

    var currentPageForYouProgram = -1
    var isLoadingForYouPrograms = true
    var totalForYouProgramCount = 0
    var forYouProgramsList = ArrayList<ProgramDataModel?>()
    var inProgressProgramsList = ArrayList<InProgressProgramListResponse.InProgressProgramsList.Program?>()

    var categoryId = "0"
    var selectedPosition = -1
    var categoryList = ArrayList<CategoryDataModel>()

    var categoryProgramList = ArrayList<ProgramDataModel>()
    var totalCategoryBaseProgramCount = 0
    var isLoadingCategoryBasePrograms = true
    var currentPageInCategoryBaseProgram = -1
    var sortType = Constants.ratings
    var sortOrder = Constants.desc

    private val pageSize = 10

    var programContentList: ArrayList<UserProgramContentDetailApiRes.Data> = ArrayList()
    var programProgressList = ArrayList<UserProgressDetailApiResponse.Data>()

    val forYouProgramListLiveData = MutableLiveData<RequestState<ForYouProgramListResponse>>()
    val inProgressProgramListLiveData =
        MutableLiveData<RequestState<InProgressProgramListResponse>>()
    val categoryBaseProgramListLiveData =
        MutableLiveData<RequestState<CategoryBaseProgramListResponse>>()
    val getUserProgramListLiveData = MutableLiveData<RequestState<UserProgramApiResponse>>()
    val userProgressDetailListLiveData =
        MutableLiveData<RequestState<UserProgressDetailApiResponse>>()
    val userProgressListLiveData = MutableLiveData<RequestState<UserProgressDetailApiResponse>>()
    val userProgramContentListLiveData =
        MutableLiveData<RequestState<UserProgramContentDetailApiRes>>()

    fun getForYouProgramList(
        baseView: BaseView,
        mDisposable: CompositeDisposable
    ) {
        currentPageForYouProgram += 1
        val query = HashMap<String, Any>()
        query[Constants.Param.programType] = Constants.forYou
        val params = ProgramListRequest().apply {
            currentPage = currentPageForYouProgram
            perPage = pageSize
        }
        repository.getForYouProgramList(
            query, params, baseView, mDisposable, forYouProgramListLiveData
        )
    }

    fun getInProgressProgramList(
        programType: String,
        pageNo: Int,
        baseView: BaseView,
        mDisposable: CompositeDisposable
    ) {
        val query = HashMap<String, Any>()
        query[Constants.Param.programType] = programType
        val params = ProgramListRequest().apply {
            currentPage = pageNo
            perPage = pageSize
        }
        repository.getInProgressProgramList(
            query, params, baseView, mDisposable, inProgressProgramListLiveData
        )
    }

    fun getCategoryBaseProgramList(
        categoryId: String,
        baseView: BaseView,
        mDisposable: CompositeDisposable
    ) {
        currentPageInCategoryBaseProgram += 1
        val query = HashMap<String, Any>()
        query[Constants.Param.categoryId] = categoryId
        val params = SearchByAllDataRequest().apply {
            current_page = currentPageInCategoryBaseProgram
            per_page = pageSize
            sort = SearchByAllDataRequest.SortDataModel(sortType, sortOrder)
        }
        repository.getCategoryBaseProgramList(
            query, params, baseView, mDisposable, categoryBaseProgramListLiveData
        )
    }

    fun getUserProgramApi(
        programId: String,
        isFromHistoryCompletedProgram: Boolean,
        baseView: BaseView,
        mDisposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.programId] = programId
        params[Constants.Param.isRedirectedFromHistory] = isFromHistoryCompletedProgram
        repository.getUserProgramApi(params, baseView, mDisposable, getUserProgramListLiveData)
    }

    fun userProgressDetailsApi(
        programId: String,
        dayNo: String,
        isFromHistoryCompletedProgram: Boolean,
        baseView: BaseView,
        mDisposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.programId] = programId
        params[Constants.Param.dayNo] = dayNo
        params[Constants.Param.isRedirectedFromHistory] = isFromHistoryCompletedProgram
        repository.userProgressDetailsApi(
            params,
            baseView,
            mDisposable,
            userProgressDetailListLiveData
        )
    }

    fun userProgramApi(
        programId: String,
        baseView: BaseView,
        mDisposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.programId] = programId
        repository.userProgramApi(params, baseView, mDisposable, userProgressListLiveData)
    }

    fun userProgramContentDetailsApi(
        programId: String,
        dayNo: String,
        isFromHistoryCompletedProgram: Boolean,
        baseView: BaseView,
        mDisposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.programId] = programId
        params[Constants.Param.dayNo] = dayNo
        params[Constants.Param.isRedirectedFromHistory] = isFromHistoryCompletedProgram
        repository.userProgramContentDetailsApi(
            params,
            baseView,
            mDisposable,
            userProgramContentListLiveData
        )
    }

    val linkedProgramDataRes = MutableLiveData<RequestState<LinkedProgramData>>()

    fun getLinkedProgram(
        programId: Int,
        isFromHistoryCompletedProgram: Boolean,
        baseView: BaseView,
        mDisposable: CompositeDisposable
    ) {
        repository.getLinkedProgram(
            programId,
            isFromHistoryCompletedProgram,
            baseView,
            mDisposable,
            linkedProgramDataRes
        )
    }

    val sendProgramFeedbackData = MutableLiveData<RequestState<Any>>()

    fun sendProgramFeedback(
        programId: Int,
        currentRating: Int,
        tagsId: JsonArray,
        feedback: String,
        isFromHistoryCompletedProgram: Boolean,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val json = JsonObject()
        json.addProperty(Constants.Param.isRedirectedFromHistory, isFromHistoryCompletedProgram)
        json.addProperty(Constants.Param.id, programId)
        json.addProperty(Constants.Param.ratingNumber, currentRating)
        json.add("tagIds", tagsId)
        json.addProperty("feedbackTag", feedback)

        repository.sendProgramFeedback(
            json, baseView, disposable, sendProgramFeedbackData
        )
    }
}