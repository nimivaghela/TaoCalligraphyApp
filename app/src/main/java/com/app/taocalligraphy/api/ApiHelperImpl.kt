package com.app.taocalligraphy.api

import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.StaticContentResponseModel
import com.app.taocalligraphy.models.UserModulePermission
import com.app.taocalligraphy.models.request.*
import com.app.taocalligraphy.models.response.FetchDailyStatDataReponse
import com.app.taocalligraphy.models.response.InitialUserExperienceDetail
import com.app.taocalligraphy.models.response.LoginResponse
import com.app.taocalligraphy.models.response.SubscriptionDetails
import com.app.taocalligraphy.models.response.alarm.AlarmResponse
import com.app.taocalligraphy.models.response.favorite_models.FavoriteContentResponse
import com.app.taocalligraphy.models.response.favorite_models.FavoriteProgramResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_by_id.FetchCategoryDetailByIdResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_content_by_filter.FetchCategoryDetailContentByFilterResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_content_by_filter.VideoDetail
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.FetchCategoryDataResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_program_list_by_id.FetchCategoryProgramByIDResponse
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_tags_by_id.FetchCategoryTagsByIdResponse
import com.app.taocalligraphy.models.response.history.FetchMeditationHistoryData
import com.app.taocalligraphy.models.response.history.FetchProgramHistoryData
import com.app.taocalligraphy.models.response.home_screen.FetchDailyWisdomDataResponse
import com.app.taocalligraphy.models.response.home_screen.FetchHomeContentDataResponse
import com.app.taocalligraphy.models.response.home_screen.ForYouDataModel
import com.app.taocalligraphy.models.response.how_to_meditate_response.FetchHowToMeditateDataListResponse
import com.app.taocalligraphy.models.response.journal_data_models.FetchJournalListDataModel
import com.app.taocalligraphy.models.response.journal_data_models.JournalDataModel
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.models.response.meditation_timer.MeditationListApiResponse
import com.app.taocalligraphy.models.response.meditation_timer.SoundApiResponse
import com.app.taocalligraphy.models.response.meditation_timer.SoundBackImageApiResponse
import com.app.taocalligraphy.models.response.notification_model.FetchNotificationListDataResponse
import com.app.taocalligraphy.models.response.notification_model.ReadNotificationDataByIdResponse
import com.app.taocalligraphy.models.response.playList.PlaylistApiResponse
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.models.response.profile.*
import com.app.taocalligraphy.models.response.program.*
import com.app.taocalligraphy.models.response.question_data_models.QuestionnairesResultResponse
import com.app.taocalligraphy.models.response.question_data_models.answer_data_model.QuestionnaireAnswerResponse
import com.app.taocalligraphy.models.response.question_data_models.fetch_question_data_model.FetchQuestionListResponse
import com.app.taocalligraphy.models.response.search_responses.fetch_searched_keyword_data_model.FetchSearchedKeywordResponse
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.MeditationContentListDataModel
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.ProgramListDataModel
import com.app.taocalligraphy.models.response.search_responses.search_by_all_type_data_model.SearchDataByAllTypeResponse
import com.app.taocalligraphy.models.response.stats_response.FetchMonthStatDataReponse
import com.app.taocalligraphy.models.response.stats_response.FetchWeekStatDataReponse
import com.app.taocalligraphy.models.response.user_downloads.UserDownloads
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {
    override fun fetchFavoriteProgramDataAPI(
        params: FavoriteProgramRequest
    ): Observable<CommonResponseModel<FavoriteProgramResponse>> =
        apiService.fetchFavoriteProgramDataAPI(params)

    override fun fetchProgramHistoryAPI(
        map: Map<String, Any?>,
        params: ProgramHistoryDataRequest
    ): Observable<CommonResponseModel<FetchProgramHistoryData>> =
        apiService.fetchProgramHistoryAPI(map, params)

    override fun fetchMeditationHistoryAPI(
        map: Map<String, Any?>,
        params: MeditationHistoryDataRequest
    ): Observable<CommonResponseModel<FetchMeditationHistoryData>> =
        apiService.fetchMeditationHistoryAPI(map, params)

    override fun callRemoveProfilePictureAPI(): Observable<CommonResponseModel<JsonObject>> =
        apiService.removeProfilePictureAPI()

    override fun getLeftMenuDataApi(): Observable<CommonResponseModel<LeftMenuResponse>> =
        apiService.getLeftMenuDataApi()

    override fun fetchMeditationTimerDetailByIDApi(params: Map<String, Any?>): Observable<CommonResponseModel<MeditationListApiResponse.MeditationDetail>> =
        apiService.fetchMeditationTimerDetailByID(params)

    override fun fetchNotificationListApi(params: NotificationRequest): Observable<CommonResponseModel<FetchNotificationListDataResponse>> =
        apiService.fetchNotificationListApi(params)

    override fun readNotificationByID(params: ReadNotificationRequest): Observable<CommonResponseModel<ReadNotificationDataByIdResponse>> =
        apiService.readNotificationByID(params)

    override fun deleteNotificationByID(params: DeleteNotificationRequest): Observable<CommonResponseModel<JsonObject>> =
        apiService.deleteNotificationByID(params)

    override fun fetchWeekStatsData(params: Map<String, String?>): Observable<CommonResponseModel<FetchWeekStatDataReponse>> =
        apiService.fetchWeekStatsData(params)

    override fun fetchMonthStatsData(params: Map<String, String?>): Observable<CommonResponseModel<FetchMonthStatDataReponse>> =
        apiService.fetchMonthStatsData(params)

    override fun fetchDailyStatsData(params: Map<String, String?>): Observable<CommonResponseModel<FetchDailyStatDataReponse>> =
        apiService.fetchDailyStatsData(params)

    override fun fetchFavoriteContentDataAPI(
        params: FavoriteContentRequest
    ): Observable<CommonResponseModel<FavoriteContentResponse>> =
        apiService.fetchFavoriteContentDataAPI(params)

    override fun fetchCategoryDetailByID(id: Int): Observable<CommonResponseModel<FetchCategoryDetailByIdResponse>> =
        apiService.fetchCategoryDetailByID(id)

    override fun fetchCategoryTagsById(params: FetchCategoryTagsByIdRequest): Observable<CommonResponseModel<FetchCategoryTagsByIdResponse>> =
        apiService.fetchCategoryTagsById(params)

    override fun fetchCategoryTagsByIds(params: FetchCategoryTagsByIdsRequest): Observable<CommonResponseModel<FetchCategoryTagsByIdResponse>> =
        apiService.fetchCategoryTagsByIds(params)

    override fun fetchCategoryDetailContentByFilter(params: FetchCategoryDetailContentByFilterRequest): Observable<CommonResponseModel<FetchCategoryDetailContentByFilterResponse>> =
        apiService.fetchCategoryDetailContentByFilter(params)

    override fun fetchCategoryProgramByID(
        map: Map<String, Any?>,
        params: FetchCategoryProgramByIDRequest
    ): Observable<CommonResponseModel<FetchCategoryProgramByIDResponse>> =
        apiService.fetchCategoryProgramListByID(map, params)

    override fun fetchQuestionnaireResultAPI(json: JsonObject): Observable<CommonResponseModel<QuestionnairesResultResponse>> =
        apiService.fetchQuestionnaireResultAPI(json)

    override fun fetchDailyWisdomDataAPI(params: Map<String, String>): Observable<CommonResponseModel<FetchDailyWisdomDataResponse>> =
        apiService.fetchDailyWisdomDataAPI(params)

    override fun fetchHomeContentDataAPI(
        map: Map<String, Any?>,
        params: FetchHomeDataRequest
    ): Observable<CommonResponseModel<FetchHomeContentDataResponse>> =
        apiService.fetchHomeContentDataAPI(map, params)

    override fun fetchViewAllContentDataAPI(
        map: Map<String, Any?>,
        params: FetchHomeDataRequest
    ): Observable<CommonResponseModel<FetchHomeContentDataResponse>> =
        apiService.fetchViewAllContentDataAPI(map, params)

    override fun fetchSearchedKeywordApi(): Observable<CommonResponseModel<FetchSearchedKeywordResponse>> =
        apiService.fetchSearchedKeywordData()

    override fun fetchHowToMeditateDataList(
        params: FetchHowToMeditateRequest,
        map: HashMap<String, Any>

    ): Observable<CommonResponseModel<FetchHowToMeditateDataListResponse>> =
        apiService.fetchHowToMeditateDataList(params, map)

    override fun contentSortingAPI(params: SearchByAllDataRequest): Observable<CommonResponseModel<MeditationContentListDataModel>> =
        apiService.contentSortingApi(params)

    override fun programSortingAPI(params: SearchByAllDataRequest): Observable<CommonResponseModel<ProgramListDataModel>> =
        apiService.programSortingApi(params)

    override fun searchAllTypeDataApi(params: SearchByAllDataRequest): Observable<CommonResponseModel<SearchDataByAllTypeResponse>> =
        apiService.searchByAllDataApi(params)

    override fun fetchCategoryData(): Observable<CommonResponseModel<FetchCategoryDataResponse>> =
        apiService.fetchCategoryData()

    override fun createJournal(params: CreateJournalRequest): Observable<CommonResponseModel<JournalDataModel>> =
        apiService.createJournal(params)

    override fun editJournal(params: EditJournalRequest): Observable<CommonResponseModel<JournalDataModel>> =
        apiService.editJournal(params)

    override fun fetchJournalDataById(journalId: String?): Observable<CommonResponseModel<JournalDataModel>> =
        apiService.fetchJournalDataById(journalId)

    override fun deleteJournalFromList(journalId: String?): Observable<CommonResponseModel<JsonObject>> =
        apiService.deleteJournalFromList(journalId)

    override fun updatePinUnpinJournalStatus(params: Map<String, Any?>): Observable<CommonResponseModel<JsonObject>> =
        apiService.updatePinUnpinJournalStatus(params)


    override fun fetchJournalListData(params: Map<String, Any?>): Observable<CommonResponseModel<FetchJournalListDataModel>> =
        apiService.fetchJournalListData(params)

    override suspend fun getTermsAndConditions(): Response<CommonResponseModel<StaticContentResponseModel>> =
        apiService.getTermsAndConditions()

    override fun getStaticContent(type: String): Observable<CommonResponseModel<StaticContentResponseModel>> =
        apiService.getStaticContent(type)

    override fun getQuestionListData(): Observable<CommonResponseModel<FetchQuestionListResponse>> =
        apiService.getQuestionListData()

    override fun giveAnswerToQuestion(param: GiveAnswerToQuestionRequest): Observable<CommonResponseModel<QuestionnaireAnswerResponse>> =
        apiService.giveAnswerToQuestion(param)

    override fun userSignUp(params: SignUpRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.userSignUp(params)

    override fun getStaticData(params: SignUpRequest): Observable<CommonResponseModel<LoginResponse>> {
        TODO("Not yet implemented")
    }

    override fun verifyEmailTokenApi(params: VerifyEmailTokenRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.verifyEmailTokenApi(params)

    override fun userLogin(params: LoginRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.userLogin(params)

    override fun resendOtpApi(params: Map<String, Any?>): Observable<CommonResponseModel<LoginResponse>> =
        apiService.resendOtpApi(params)

    override fun forgotPasswordApi(params: Map<String, Any?>): Observable<CommonResponseModel<LoginResponse>> =
        apiService.forgotPasswordApi(params)

    override fun resetPasswordApi(params: Map<String, Any?>): Observable<CommonResponseModel<LoginResponse>> =
        apiService.resetPasswordApi(params)

    override fun userProfile(): Observable<CommonResponseModel<LoginResponse>> =
        apiService.userProfile()

    override fun userReferrals(referralsRequest: ReferralsRequest): Observable<CommonResponseModel<ReferralsResponse>> =
        apiService.userReferrals(referralsRequest)

    override fun userLogout(params: Map<String, Any?>): Observable<CommonResponseModel<LoginResponse>> =
        apiService.userLogout(params)

    override fun soundApi(params: Map<String, Any?>): Observable<CommonResponseModel<SoundApiResponse>> =
        apiService.soundApi(params)

    override fun backgroundImageListApi(): Observable<CommonResponseModel<SoundBackImageApiResponse>> =
        apiService.backgroundImageListApi()

    override fun meditationTimerAddApi(params: MeditationTimerRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.meditationTimerAddApi(params)

    override fun meditationTimerListApi(): Observable<CommonResponseModel<MeditationListApiResponse>> =
        apiService.meditationTimerListApi()

    override fun meditationTimerEditApi(params: MeditationTimerEditRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.meditationTimerEditApi(params)

    override fun meditationTimerDeleteApi(params: Map<String, Any?>): Observable<CommonResponseModel<LoginResponse>> =
        apiService.meditationTimerDeleteApi(params)

    override fun meditationTimerCloneApi(params: Map<String, Any?>): Observable<CommonResponseModel<LoginResponse>> =
        apiService.meditationTimerCloneApi(params)

    override fun getMeditationContent(params: Map<String, Any?>): Observable<CommonResponseModel<MeditationContentResponse>> =
        apiService.getMeditationContent(params)

    override fun preAssessmentFeedback(params: Map<String, Any?>): Observable<CommonResponseModel<Any>> =
        apiService.preAssessmentFeedback(params)

    override fun postAssessmentFeedback(json: JsonObject): Observable<CommonResponseModel<Any>> =
        apiService.postAssessmentFeedback(json)

    override fun contentPlayTime(params: Map<String, Any?>): Call<ResponseBody> =
        apiService.contentPlayTime(params)

    override fun programContentPlayTime(params: Map<String, Any?>): Call<ResponseBody> =
        apiService.programContentPlayTime(params)

    override fun favouriteMeditationContent(params: Map<String, Any?>): Observable<CommonResponseModel<Any>> =
        apiService.favouriteMeditationContent(params)

    override fun likeDisLikeMeditationContent(params: Map<String, Any?>): Observable<CommonResponseModel<Any>> =
        apiService.likeDisLikeMeditationContent(params)

    override fun getLanguages(): Observable<CommonResponseModel<LanguageListApiResponse>> =
        apiService.getLanguages()

    override fun getSettingsLanguages(): Observable<CommonResponseModel<LanguageListApiResponse>> =
        apiService.getSettingsLanguages()

    override fun regionsApi(params: Map<String, Any?>): Observable<CommonResponseModel<RegionListApiResponse>> =
        apiService.regionsApi(params)

    override fun countryApi(params: Map<String, Any?>): Observable<CommonResponseModel<RegionListApiResponse>> =
        apiService.countryApi(params)

    override fun userEditProfile(params: UserProfileRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.userEditProfile(params)

    override fun userProfilePictureEdit(params: MultipartBody.Part): Observable<CommonResponseModel<UserProfileEditApiResponse>> =
        apiService.userProfilePictureEdit(params)

    override fun userProfilePublic(params: UserProfilePublicRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.userProfilePublic(params)

    override fun changePasswordApi(params: Map<String, Any?>): Observable<CommonResponseModel<LoginResponse>> =
        apiService.changePasswordApi(params)

    override fun getUserProfilePublicApi(params: Map<String, Any?>): Observable<CommonResponseModel<UserProfilePublicApiResponse>> =
        apiService.getUserProfilePublicApi(params)

    override fun getUserInterestsApi(): Observable<CommonResponseModel<UserInterestApiResponse>> =
        apiService.getUserInterestsApi()

    override fun userInterestApi(params: UserInterestRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.userInterestApi(params)

    override fun getUserSettingsApi(): Observable<CommonResponseModel<UserSettingsApiResponse>> =
        apiService.getUserSettingsApi()

    override fun userSettingsApi(params: UserSettingsRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.userSettingsApi(params)

    override fun getUserModulePermissionApi(): Observable<CommonResponseModel<List<UserModulePermission>>> = apiService.getUserModulePermissionApi()

    override fun userFeedbackApi(params: UserFeedbackRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.userFeedbackApi(params)

    override fun getUserGoalsScreen2Api(): Observable<CommonResponseModel<UserGoalsScreenApiResponse>> =
        apiService.getUserGoalsScreen2Api()

    override fun getUserGoalsScreen1Api(): Observable<CommonResponseModel<UserGoalsScreenApiResponse>> =
        apiService.getUserGoalsScreen1Api()

    override fun userGoalsApi(params: UserGoalsRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.userGoalsApi(params)

    override fun getForYouProgramList(
        query: Map<String, Any?>,
        params: ProgramListRequest
    ): Observable<CommonResponseModel<ForYouProgramListResponse>> =
        apiService.getForYouProgramList(query, params)

    override fun getInProgressProgramList(
        query: Map<String, Any?>,
        params: ProgramListRequest
    ): Observable<CommonResponseModel<InProgressProgramListResponse>> =
        apiService.getInProgressProgramList(query, params)

    override fun getCategoryBaseProgramList(
        query: Map<String, Any?>,
        params: SearchByAllDataRequest
    ): Observable<CommonResponseModel<CategoryBaseProgramListResponse>> =
        apiService.getCategoryBaseProgramList(query, params)

    override fun getUserProgramApi(params: Map<String, Any?>): Observable<CommonResponseModel<UserProgramApiResponse>> =
        apiService.getUserProgramApi(params)

    override fun userProgressDetailsApi(params: Map<String, Any?>): Observable<CommonResponseModel<UserProgressDetailApiResponse>> =
        apiService.userProgressDetailsApi(params)


    override fun setProgramFavourite(query: Map<String, Any?>): Observable<CommonResponseModel<Any>> =
        apiService.setProgramFavourite(query)

    override fun userProgramApi(params: Map<String, Any?>): Observable<CommonResponseModel<UserProgressDetailApiResponse>> =
        apiService.userProgramApi(params)

    override fun userProgramContentDetailsApi(params: Map<String, Any?>): Observable<CommonResponseModel<UserProgramContentDetailApiRes>> =
        apiService.userProgramContentDetailsApi(params)

    override fun userMeditationTimerPlayDetailsApi(params: Map<String, Any?>): Call<ResponseBody> =
        apiService.userMeditationTimerPlayDetailsApi(params)

    override fun playlistContentFilterApi(params: PlaylistContentFilterRequest): Observable<CommonResponseModel<PlaylistContentFilterApiResponse>> =
        apiService.playlistContentFilterApi(params)

    override fun downloadContent(contentId: String): Call<ResponseBody> =
        apiService.downloadContent(contentId)

    override fun addPlaylistApi(params: AddPlaylistRequest): Observable<CommonResponseModel<PlaylistContentFilterApiResponse>> =
        apiService.addPlaylistApi(params)

    override fun playListApi(params: PlaylistRequest): Observable<CommonResponseModel<PlaylistApiResponse>> =
        apiService.playListApi(params)

    override fun playlistContentApi(
        params: Map<String, Any?>,
        body: PagingRequest
    ): Observable<CommonResponseModel<PlaylistContentFilterApiResponse>> =
        apiService.playlistContentApi(params)

    override fun playListDeleteApi(params: Map<String, Any?>): Observable<CommonResponseModel<LoginResponse>> =
        apiService.playListDeleteApi(params)

    override fun deleteAccountApi(): Observable<CommonResponseModel<LoginResponse>> =
        apiService.deleteAccountApi()

    override fun editPlaylistApi(params: EditPlaylistRequest): Observable<CommonResponseModel<LoginResponse>> =
        apiService.editPlaylistApi(params)

    override fun addAlarm(params: AddAlarmRequest): Observable<CommonResponseModel<AlarmResponse>?> =
        apiService.addAlarm(params)

    override fun getActiveAlarm(): Observable<CommonResponseModel<AlarmResponse>> =
        apiService.getActiveAlarm()

    override fun alarmOnOff(): Observable<CommonResponseModel<AlarmResponse>?> =
        apiService.alarmOnOff()

    override fun initialUserExperience(): Observable<CommonResponseModel<MeditationContentResponse>> =
        apiService.initialUserExperience()

    override fun initialUserExperienceDetail(categoryId: Int): Observable<CommonResponseModel<InitialUserExperienceDetail>> =
        apiService.initialUserExperienceDetail(categoryId)

    override fun getUserDownloads(json: JsonObject): Call<CommonResponseModel<UserDownloads>> =
        apiService.getUserDownloads(0, json)

    override fun getUserDownloadsContent(params: Map<String, Any>): Call<CommonResponseModel<MeditationContentResponse>> =
        apiService.getUserDownloadsContent(params)

    override fun deleteUserDownloads(json: JsonObject): Call<Any> =
        apiService.deleteUserDownload(json)

    override fun getLinkedProgram(
        programId: Int,
        isFromHistoryCompletedProgram: Boolean
    ): Observable<CommonResponseModel<LinkedProgramData>> =
        apiService.getLinkedProgram(programId, isFromHistoryCompletedProgram)

    override fun sendProgramFeedback(json: JsonObject): Observable<CommonResponseModel<Any>> =
        apiService.getLinkedProgram(json)

    override fun subscribeUser(json: JsonObject): Observable<CommonResponseModel<Any>> = apiService.subscribeUser(json)

    override fun getSubscriptionDetails(): Observable<CommonResponseModel<SubscriptionDetails>> = apiService.getSubscriptionDetails()

    override fun getSubscriptionStatus(): Observable<CommonResponseModel<SubscriptionDetails>> = apiService.getSubscriptionStatus()

    override fun startFreeTrial(): Observable<CommonResponseModel<Unit>> = apiService.startFreeTrial()

    override fun getAllCategoryData(
        contentType: String,
        subCategoryId: Int,
        json: JsonObject
    ): Observable<CommonResponseModel<ForYouDataModel>> {
        return apiService.getAllCategoryData(contentType, subCategoryId, json)
    }
}