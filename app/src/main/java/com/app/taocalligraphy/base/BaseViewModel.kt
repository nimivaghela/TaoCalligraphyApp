package com.app.taocalligraphy.base

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.FetchCategoryTagsByIdRequest
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.models.response.alarm.AlarmResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.FetchCategoryDataResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_tags_by_id.FetchCategoryTagsByIdResponse
import com.app.taocalligraphy.models.response.home_screen.FetchDailyWisdomDataResponse
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.models.response.user_downloads.UserDownloads
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.BaseRepository
import com.app.taocalligraphy.ui.downloads.OnDownloadSyncListener
import com.app.taocalligraphy.utils.OnUserDownloadsListener
import com.google.gson.JsonObject
import io.reactivex.disposables.CompositeDisposable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


open class BaseViewModel : ViewModel() {

    //    terms & cond , fav
    @Inject
    lateinit var baseRepository: BaseRepository

    val fetchDailyWisdomDataResponse = MutableLiveData<RequestState<FetchDailyWisdomDataResponse>>()
    val favouriteMeditationContentLiveData = MutableLiveData<RequestState<Any>>()
    val fetchCategoryDataResponse =
        MutableLiveData<RequestState<FetchCategoryDataResponse>>()
    val userLogoutLiveData = MutableLiveData<RequestState<LoginResponse>>()
    val fetchCategoryTagsByIdResponse =
        MutableLiveData<RequestState<FetchCategoryTagsByIdResponse>>()

    val activeAlarmResponse =
        MutableLiveData<RequestState<AlarmResponse>>()

    private val userDownloads = MutableLiveData<LiveData<List<MeditationContentResponse>>>()

    fun fetchAllCategoryData(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        baseRepository.fetchCategoryData(
            baseView,
            disposable,
            fetchCategoryDataResponse
        )
    }

    fun fetchDailyWisdomDataAPI(
        date: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val queryMap: HashMap<String, String> = HashMap()
        queryMap[Constants.Param.date] = date
        baseRepository.fetchDailyWisdomDataAPI(
            queryMap,
            baseView,
            disposable,
            fetchDailyWisdomDataResponse
        )
    }

    fun favoriteMeditationContent(
        contentId: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.contentId] = contentId
        baseRepository.favoriteMeditationContent(
            params,
            baseView,
            disposable,
            favouriteMeditationContentLiveData
        )
    }

    fun setProgramFavorite(
        programId: String,
        isFromHistoryCompletedProgram: Boolean,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.programId] = programId
        params[Constants.Param.isRedirectedFromHistory] = isFromHistoryCompletedProgram
        baseRepository.setProgramFavourite(
            params,
            baseView,
            disposable,
            favouriteMeditationContentLiveData
        )
    }

    fun userLogout(
        sessionId: String,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.sessionId] = sessionId
        baseRepository.userLogout(params, baseView, disposable, userLogoutLiveData)
    }

    fun fetchCategoryTagsById(
        categoryID: ArrayList<Int>,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        baseRepository.fetchCategoryTagsById(
            FetchCategoryTagsByIdRequest().apply {
                this.categoryIds = categoryID
            },
            baseView,
            disposable,
            fetchCategoryTagsByIdResponse
        )
    }

    fun getActiveAlarm(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        baseRepository.getActiveAlarm(
            baseView,
            disposable,
            activeAlarmResponse
        )
    }

    private fun setUserDownloads(
        contentId: String
    ) {
        val call = TaoCalligraphy.instance.apiHelperBase.downloadContent(contentId)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }
        })
    }

    fun addMeditationToStorage(meditationContentResponse: MeditationContentResponse) {
        meditationContentResponse.createdTime = System.currentTimeMillis()
        TaoCalligraphy.instance.meditationDbRepo.addMeditationContent(meditationContentResponse)
        //setUserDownloads(meditationContentResponse.id)
    }

    fun getTotalDownloads() = TaoCalligraphy.instance.meditationDbRepo.getTotalDownloads()

    fun getUserDownloadsFromStorage() {
        userDownloads.postValue(
            TaoCalligraphy.instance.meditationDbRepo.getAllMeditationContentsDesc(
                false
            )
        )
    }

    fun deleteUserDownload(id: String) {
        TaoCalligraphy.instance.meditationDbRepo.deleteMeditationContent(id)
    }

    fun updateUserDownload(isDeleted: Boolean, id: String) {
        TaoCalligraphy.instance.meditationDbRepo.updateMeditationData(isDeleted, id)
        userDownloads.postValue(
            TaoCalligraphy.instance.meditationDbRepo.getAllMeditationContentsDesc(
                false
            )
        )
    }

    fun getMeditationContent(id: String) =
        TaoCalligraphy.instance.meditationDbRepo.getMeditationData(id)

    fun deleteAllDownloads() {
        TaoCalligraphy.instance.meditationDbRepo.deleteAllMeditationContent()
    }

    fun getUserDownloads(
        viewModel: BaseViewModel,
        onUserDownloadsListener: OnUserDownloadsListener? = null,
        onDownloadSyncListener: OnDownloadSyncListener? = null
    ) {
        val sortJson = JsonObject()
        sortJson.addProperty("field", "")
        sortJson.addProperty("order", "")
        val jsonObject = JsonObject()
        jsonObject.addProperty("current_page", 0)
        jsonObject.addProperty("per_page", 10)
        jsonObject.addProperty("search", "")
        jsonObject.add("sort", sortJson)

        val call = TaoCalligraphy.instance.apiHelperBase.getUserDownloads(jsonObject)
        call.enqueue(object : Callback<CommonResponseModel<UserDownloads>> {
            override fun onResponse(
                call: Call<CommonResponseModel<UserDownloads>>,
                response: Response<CommonResponseModel<UserDownloads>>
            ) {
                if (onUserDownloadsListener != null) {
                    onUserDownloadsListener.onUserDownloadsCountListener(
                        response.body()?.data?.list?.size ?: 0
                    )
                } else {
                    if (response.body()?.data?.list?.isEmpty() == true)
                        onDownloadSyncListener?.onDownloadSync()
                    response.body()?.data?.list?.forEach {
                        downloadContent(it.contentId.toString(), viewModel, onDownloadSyncListener)
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponseModel<UserDownloads>>, t: Throwable) {
            }
        })
    }

    fun downloadContent(
        contentId: String,
        viewModel: BaseViewModel,
        onDownloadSyncListener: OnDownloadSyncListener? = null
    ) {
        val params = HashMap<String, Any>()
        params[Constants.Param.contentId] = contentId
        params[Constants.Param.languageId] = 1

        val call = TaoCalligraphy.instance.apiHelperBase.getUserDownloadsContent(params)
        call.enqueue(object : Callback<CommonResponseModel<MeditationContentResponse>> {
            override fun onResponse(
                call: Call<CommonResponseModel<MeditationContentResponse>>,
                response: Response<CommonResponseModel<MeditationContentResponse>>
            ) {
                if (response.code() == 404) {
                    deleteUserDownload(contentId)
                } else {
                    response.body()?.data?.let {
                        val contentFile: String? =  if (!it.contentFileForDownload.isNullOrEmpty()) {
                            it.contentFileForDownload
                        } else {
                            it.contentFile
                        }
                        addMeditationToStorage(it)
                        TaoCalligraphy.instance.getDownloadTracker()
                            ?.downloadFile(
                                it.title,
                                Uri.parse(contentFile),
                                "",
                                TaoCalligraphy.instance
                                    .buildRenderersFactory(),
                                viewModel
                            )
                        it.subtitleWithLanguages?.forEach { subtitleWithLanguage ->
                            TaoCalligraphy.instance.getDownloadTracker()
                                ?.downloadFile(
                                    subtitleWithLanguage.languageName,
                                    Uri.parse(subtitleWithLanguage.subTitleFile),
                                    "",
                                    TaoCalligraphy.instance
                                        .buildRenderersFactory(),
                                    viewModel
                                )
                        }
                    }
                }

                onDownloadSyncListener?.onDownloadSync()
            }

            override fun onFailure(
                call: Call<CommonResponseModel<MeditationContentResponse>>,
                t: Throwable
            ) {
            }
        })
    }

    fun deleteDownloadsFromServer(jsonObject: JsonObject) {
        val call = TaoCalligraphy.instance.apiHelperBase.deleteUserDownloads(jsonObject)
        call.enqueue(object : Callback<Any> {
            override fun onResponse(
                call: Call<Any>,
                response: Response<Any>
            ) {

            }

            override fun onFailure(
                call: Call<Any>,
                t: Throwable
            ) {
            }
        })
    }
}