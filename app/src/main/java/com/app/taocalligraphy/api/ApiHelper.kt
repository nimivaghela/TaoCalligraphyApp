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

interface ApiHelper {

    suspend fun getTermsAndConditions(): Response<CommonResponseModel<StaticContentResponseModel>>

    fun getStaticContent(type: String): Observable<CommonResponseModel<StaticContentResponseModel>>

    fun fetchJournalListData(params: Map<String, Any?>):
            Observable<CommonResponseModel<FetchJournalListDataModel>>

    fun fetchProgramHistoryAPI(map: Map<String, Any?>, params: ProgramHistoryDataRequest):
            Observable<CommonResponseModel<FetchProgramHistoryData>>

    fun fetchMeditationHistoryAPI(map: Map<String, Any?>, params: MeditationHistoryDataRequest):
            Observable<CommonResponseModel<FetchMeditationHistoryData>>

    fun fetchNotificationListApi(params: NotificationRequest):
            Observable<CommonResponseModel<FetchNotificationListDataResponse>>

    fun readNotificationByID(params: ReadNotificationRequest):
            Observable<CommonResponseModel<ReadNotificationDataByIdResponse>>

    fun deleteNotificationByID(params: DeleteNotificationRequest):
            Observable<CommonResponseModel<JsonObject>>

    fun fetchDailyStatsData(params: Map<String, String?>):
            Observable<CommonResponseModel<FetchDailyStatDataReponse>>

    fun fetchWeekStatsData(params: Map<String, String?>):
            Observable<CommonResponseModel<FetchWeekStatDataReponse>>

    fun fetchMonthStatsData(params: Map<String, String?>):
            Observable<CommonResponseModel<FetchMonthStatDataReponse>>

    fun fetchCategoryDetailByID(id: Int):
            Observable<CommonResponseModel<FetchCategoryDetailByIdResponse>>

    fun fetchCategoryProgramByID(map: Map<String, Any?>, params: FetchCategoryProgramByIDRequest):
            Observable<CommonResponseModel<FetchCategoryProgramByIDResponse>>

    fun fetchCategoryTagsById(params: FetchCategoryTagsByIdRequest):
            Observable<CommonResponseModel<FetchCategoryTagsByIdResponse>>

    fun fetchCategoryTagsByIds(params: FetchCategoryTagsByIdsRequest):
            Observable<CommonResponseModel<FetchCategoryTagsByIdResponse>>

    fun fetchCategoryDetailContentByFilter(params: FetchCategoryDetailContentByFilterRequest):
            Observable<CommonResponseModel<FetchCategoryDetailContentByFilterResponse>>

    fun fetchDailyWisdomDataAPI(params: Map<String, String>):
            Observable<CommonResponseModel<FetchDailyWisdomDataResponse>>

    fun fetchHomeContentDataAPI(
        map: Map<String, Any?>,
        params: FetchHomeDataRequest
    ): Observable<CommonResponseModel<FetchHomeContentDataResponse>>

    fun fetchViewAllContentDataAPI(
        map: Map<String, Any?>,
        params: FetchHomeDataRequest
    ): Observable<CommonResponseModel<FetchHomeContentDataResponse>>

    fun fetchFavoriteProgramDataAPI(params: FavoriteProgramRequest):
            Observable<CommonResponseModel<FavoriteProgramResponse>>

    fun fetchFavoriteContentDataAPI(params: FavoriteContentRequest):
            Observable<CommonResponseModel<FavoriteContentResponse>>

    fun fetchQuestionnaireResultAPI(json: JsonObject):
            Observable<CommonResponseModel<QuestionnairesResultResponse>>

    fun updatePinUnpinJournalStatus(params: Map<String, Any?>):
            Observable<CommonResponseModel<JsonObject>>

    fun deleteJournalFromList(journalId: String?):
            Observable<CommonResponseModel<JsonObject>>

    fun fetchJournalDataById(journalId: String?):
            Observable<CommonResponseModel<JournalDataModel>>

    fun createJournal(params: CreateJournalRequest):
            Observable<CommonResponseModel<JournalDataModel>>

    fun editJournal(params: EditJournalRequest):
            Observable<CommonResponseModel<JournalDataModel>>

    fun getQuestionListData():
            Observable<CommonResponseModel<FetchQuestionListResponse>>

    fun fetchCategoryData():
            Observable<CommonResponseModel<FetchCategoryDataResponse>>

    fun fetchHowToMeditateDataList(
        params: FetchHowToMeditateRequest,
        map: HashMap<String, Any>
    ):
            Observable<CommonResponseModel<FetchHowToMeditateDataListResponse>>

    fun giveAnswerToQuestion(param: GiveAnswerToQuestionRequest):
            Observable<CommonResponseModel<QuestionnaireAnswerResponse>>

    fun userSignUp(
        params: SignUpRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun getStaticData(
        params: SignUpRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun verifyEmailTokenApi(
        params: VerifyEmailTokenRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun userLogin(
        params: LoginRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun resendOtpApi(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<LoginResponse>>

    fun forgotPasswordApi(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<LoginResponse>>

    fun resetPasswordApi(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<LoginResponse>>

    fun fetchSearchedKeywordApi():
            Observable<CommonResponseModel<FetchSearchedKeywordResponse>>

    fun searchAllTypeDataApi(
        params: SearchByAllDataRequest
    ): Observable<CommonResponseModel<SearchDataByAllTypeResponse>>

    fun contentSortingAPI(
        params: SearchByAllDataRequest
    ): Observable<CommonResponseModel<MeditationContentListDataModel>>

    fun programSortingAPI(
        params: SearchByAllDataRequest
    ): Observable<CommonResponseModel<ProgramListDataModel>>

    fun userProfile(): Observable<CommonResponseModel<LoginResponse>>

    fun callRemoveProfilePictureAPI(): Observable<CommonResponseModel<JsonObject>>

    fun userLogout(params: Map<String, Any?>): Observable<CommonResponseModel<LoginResponse>>

    fun soundApi(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<SoundApiResponse>>

    fun backgroundImageListApi(): Observable<CommonResponseModel<SoundBackImageApiResponse>>

    fun meditationTimerAddApi(
        params: MeditationTimerRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun meditationTimerListApi(): Observable<CommonResponseModel<MeditationListApiResponse>>

    fun meditationTimerEditApi(
        params: MeditationTimerEditRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun meditationTimerDeleteApi(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<LoginResponse>>

    fun meditationTimerCloneApi(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<LoginResponse>>

    fun fetchMeditationTimerDetailByIDApi(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<MeditationListApiResponse.MeditationDetail>>

    fun getMeditationContent(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<MeditationContentResponse>>

    fun getLanguages(): Observable<CommonResponseModel<LanguageListApiResponse>>

    fun getSettingsLanguages(): Observable<CommonResponseModel<LanguageListApiResponse>>

    fun regionsApi(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<RegionListApiResponse>>

    fun countryApi(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<RegionListApiResponse>>

    fun userEditProfile(
        params: UserProfileRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun userProfilePictureEdit(
        params: MultipartBody.Part
    ): Observable<CommonResponseModel<UserProfileEditApiResponse>>

    fun userReferrals(
        params: ReferralsRequest
    ): Observable<CommonResponseModel<ReferralsResponse>>

    fun preAssessmentFeedback(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<Any>>

    fun postAssessmentFeedback(
        json: JsonObject
    ): Observable<CommonResponseModel<Any>>

    fun contentPlayTime(
        params: Map<String, Any?>
    ): Call<ResponseBody>

    fun programContentPlayTime(
        params: Map<String, Any?>
    ): Call<ResponseBody>

    fun favouriteMeditationContent(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<Any>>

    fun likeDisLikeMeditationContent(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<Any>>

    fun userProfilePublic(
        params: UserProfilePublicRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun changePasswordApi(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<LoginResponse>>

    fun getUserProfilePublicApi(params: Map<String, Any?>): Observable<CommonResponseModel<UserProfilePublicApiResponse>>

    fun getUserInterestsApi(): Observable<CommonResponseModel<UserInterestApiResponse>>

    fun userInterestApi(
        params: UserInterestRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun getUserSettingsApi(): Observable<CommonResponseModel<UserSettingsApiResponse>>

    fun getUserModulePermissionApi(): Observable<CommonResponseModel<List<UserModulePermission>>>

    fun userSettingsApi(
        params: UserSettingsRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun userFeedbackApi(
        params: UserFeedbackRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun getUserGoalsScreen2Api(): Observable<CommonResponseModel<UserGoalsScreenApiResponse>>

    fun getLeftMenuDataApi(): Observable<CommonResponseModel<LeftMenuResponse>>

    fun getUserGoalsScreen1Api(): Observable<CommonResponseModel<UserGoalsScreenApiResponse>>

    fun userGoalsApi(
        params: UserGoalsRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    fun getForYouProgramList(
        query: Map<String, Any?>,
        params: ProgramListRequest
    ): Observable<CommonResponseModel<ForYouProgramListResponse>>

    fun getInProgressProgramList(
        query: Map<String, Any?>,
        params: ProgramListRequest
    ): Observable<CommonResponseModel<InProgressProgramListResponse>>

    fun getCategoryBaseProgramList(
        query: Map<String, Any?>,
        params: SearchByAllDataRequest
    ): Observable<CommonResponseModel<CategoryBaseProgramListResponse>>

    fun getUserProgramApi(
        params: Map<String, Any?>,
    ): Observable<CommonResponseModel<UserProgramApiResponse>>

    fun userProgressDetailsApi(
        params: Map<String, Any?>,
    ): Observable<CommonResponseModel<UserProgressDetailApiResponse>>

    fun setProgramFavourite(
        query: Map<String, Any?>
    ): Observable<CommonResponseModel<Any>>

    fun userProgramApi(
        params: Map<String, Any?>,
    ): Observable<CommonResponseModel<UserProgressDetailApiResponse>>

    fun userProgramContentDetailsApi(
        params: Map<String, Any?>,
    ): Observable<CommonResponseModel<UserProgramContentDetailApiRes>>

    fun userMeditationTimerPlayDetailsApi(
        params: Map<String, Any?>
    ): Call<ResponseBody>

    fun playlistContentFilterApi(
        params: PlaylistContentFilterRequest
    ): Observable<CommonResponseModel<PlaylistContentFilterApiResponse>>

    fun addPlaylistApi(
        params: AddPlaylistRequest
    ): Observable<CommonResponseModel<PlaylistContentFilterApiResponse>>

    fun playListApi(
        params: PlaylistRequest
    ): Observable<CommonResponseModel<PlaylistApiResponse>>

    fun playlistContentApi(
        params: Map<String, Any?>,
        body: PagingRequest
    ): Observable<CommonResponseModel<PlaylistContentFilterApiResponse>>

    fun playListDeleteApi(
        params: Map<String, Any?>
    ): Observable<CommonResponseModel<LoginResponse>>

    fun deleteAccountApi(): Observable<CommonResponseModel<LoginResponse>>

    fun editPlaylistApi(
        params: EditPlaylistRequest
    ): Observable<CommonResponseModel<LoginResponse>>


    fun downloadContent(
        contentId: String
    ): Call<ResponseBody>

    fun addAlarm(
        params: AddAlarmRequest
    ): Observable<CommonResponseModel<AlarmResponse>?>

    fun getActiveAlarm(): Observable<CommonResponseModel<AlarmResponse>>

    fun alarmOnOff(
    ): Observable<CommonResponseModel<AlarmResponse>?>

    fun initialUserExperience(): Observable<CommonResponseModel<MeditationContentResponse>>

    fun initialUserExperienceDetail(categoryId: Int): Observable<CommonResponseModel<InitialUserExperienceDetail>>

    fun getUserDownloads(json: JsonObject): Call<CommonResponseModel<UserDownloads>>

    fun getUserDownloadsContent(
        params: Map<String, Any>
    ): Call<CommonResponseModel<MeditationContentResponse>>

    fun deleteUserDownloads(json: JsonObject): Call<Any>

    fun getLinkedProgram(
        programId: Int,
        isFromHistoryCompletedProgram: Boolean
    ): Observable<CommonResponseModel<LinkedProgramData>>

    fun sendProgramFeedback(
        json: JsonObject
    ): Observable<CommonResponseModel<Any>>

    fun subscribeUser(json: JsonObject): Observable<CommonResponseModel<Any>>

    fun getSubscriptionDetails(): Observable<CommonResponseModel<SubscriptionDetails>>

    fun getSubscriptionStatus(): Observable<CommonResponseModel<SubscriptionDetails>>

    fun startFreeTrial(): Observable<CommonResponseModel<Unit>>

    fun getAllCategoryData(
        contentType: String,
        subCategoryId: Int,
        json: JsonObject
    ): Observable<CommonResponseModel<ForYouDataModel>>

}
