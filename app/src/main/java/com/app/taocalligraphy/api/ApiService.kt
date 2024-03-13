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
import com.app.taocalligraphy.other.Constants
import com.google.gson.JsonObject
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @GET("${Constants.API_VERSION}/pages/terms-conditions")
    suspend fun getTermsAndConditions(): Response<CommonResponseModel<StaticContentResponseModel>>

    @GET("${Constants.API_VERSION}/static-pages")
    fun getStaticContent(@Query("type") type: String): Observable<CommonResponseModel<StaticContentResponseModel>>

    @POST("${Constants.API_VERSION}/user/notification/list")
    fun fetchNotificationListApi(@Body params: NotificationRequest): Observable<CommonResponseModel<FetchNotificationListDataResponse>>

    @JvmSuppressWildcards
    @POST("${Constants.API_VERSION}/user/program/history")
    fun fetchProgramHistoryAPI(
        @QueryMap query: Map<String, Any?>,
        @Body params: ProgramHistoryDataRequest
    ): Observable<CommonResponseModel<FetchProgramHistoryData>>

    @JvmSuppressWildcards
    @POST("${Constants.API_VERSION}/user/content/history/")
    fun fetchMeditationHistoryAPI(
        @QueryMap query: Map<String, Any?>,
        @Body params: MeditationHistoryDataRequest
    ): Observable<CommonResponseModel<FetchMeditationHistoryData>>

    //    @JvmSuppressWildcards
    @PUT("${Constants.API_VERSION}/user/notification")
    fun readNotificationByID(@Body params: ReadNotificationRequest): Observable<CommonResponseModel<ReadNotificationDataByIdResponse>>

    //    @JvmSuppressWildcards
//    @DELETE("${Constants.API_VERSION}/user/notification")
//    fun deleteNotificationByID(@Body params: DeleteNotificationRequest): Observable<CommonResponseModel<JsonObject>>

    @JvmSuppressWildcards
    @HTTP(
        method = "DELETE",
        path = "${Constants.API_VERSION}/user/notification",
        hasBody = true
    )
    fun deleteNotificationByID(
        @Body params: DeleteNotificationRequest
    ): Observable<CommonResponseModel<JsonObject>>

    //    https://tao-calligraphy-api.apps.openxcell.dev/tao_calligraphy/v1/user/meditation-timer/details?meditationTimerId=1

    @JvmSuppressWildcards
    @GET("${Constants.API_VERSION}/user/meditation-timer/details/")
    fun fetchMeditationTimerDetailByID(@QueryMap query: Map<String, Any?>): Observable<CommonResponseModel<MeditationListApiResponse.MeditationDetail>>

    @GET("${Constants.API_VERSION}/wellness-category/{id}")
    fun fetchCategoryDetailByID(@Path("id") id: Int): Observable<CommonResponseModel<FetchCategoryDetailByIdResponse>>

    @PATCH("${Constants.API_VERSION}/user/profile/picture")
    fun removeProfilePictureAPI(): Observable<CommonResponseModel<JsonObject>>

    @GET("${Constants.API_VERSION}/user/left-menu")
    fun getLeftMenuDataApi(): Observable<CommonResponseModel<LeftMenuResponse>>

    @GET("${Constants.API_VERSION}/user/stats/")
    fun fetchDailyStatsData(@QueryMap query: Map<String, String?>): Observable<CommonResponseModel<FetchDailyStatDataReponse>>

    @GET("${Constants.API_VERSION}/user/stats/")
    fun fetchWeekStatsData(@QueryMap query: Map<String, String?>): Observable<CommonResponseModel<FetchWeekStatDataReponse>>

    @GET("${Constants.API_VERSION}/user/stats/")
    fun fetchMonthStatsData(@QueryMap query: Map<String, String?>): Observable<CommonResponseModel<FetchMonthStatDataReponse>>

    @POST("${Constants.API_VERSION}/user/favorites/programs")
    fun fetchFavoriteProgramDataAPI(@Body params: FavoriteProgramRequest): Observable<CommonResponseModel<FavoriteProgramResponse>>

    @POST("${Constants.API_VERSION}/user/favorites/contents")
    fun fetchFavoriteContentDataAPI(@Body params: FavoriteContentRequest): Observable<CommonResponseModel<FavoriteContentResponse>>

    @JvmSuppressWildcards
    @POST("${Constants.API_VERSION}/user/category-details/programs/")
    fun fetchCategoryProgramListByID(
        @QueryMap query: Map<String, Any?>,
        @Body params: FetchCategoryProgramByIDRequest
    ): Observable<CommonResponseModel<FetchCategoryProgramByIDResponse>>

    @POST("${Constants.API_VERSION}/user/category-details/tags")
    fun fetchCategoryTagsById(
        @Body params: FetchCategoryTagsByIdRequest
    ): Observable<CommonResponseModel<FetchCategoryTagsByIdResponse>>

    @POST("${Constants.API_VERSION}/user/category-details/tags")
    fun fetchCategoryTagsByIds(
        @Body params: FetchCategoryTagsByIdsRequest
    ): Observable<CommonResponseModel<FetchCategoryTagsByIdResponse>>

    @POST("${Constants.API_VERSION}/user/category-details/contents")
    fun fetchCategoryDetailContentByFilter(
        @Body params: FetchCategoryDetailContentByFilterRequest
    ): Observable<CommonResponseModel<FetchCategoryDetailContentByFilterResponse>>

    @JvmSuppressWildcards
    @POST("${Constants.API_VERSION}/user/home-page-content")
    fun fetchHomeContentDataAPI(
        @QueryMap query: Map<String, Any?>,
        @Body params: FetchHomeDataRequest
    ): Observable<CommonResponseModel<FetchHomeContentDataResponse>>

    @JvmSuppressWildcards
    @POST("${Constants.API_VERSION}/user/home-page-content/view-all")
    fun fetchViewAllContentDataAPI(
        @QueryMap query: Map<String, Any?>,
        @Body params: FetchHomeDataRequest
    ): Observable<CommonResponseModel<FetchHomeContentDataResponse>>

    @GET("${Constants.API_VERSION}/user/questionnaire-question")
    fun getQuestionListData():
            Observable<CommonResponseModel<FetchQuestionListResponse>>

    @GET("${Constants.API_VERSION}/user/journal/{JournalId}")
    fun fetchJournalDataById(@Path("JournalId") journalId: String?):
            Observable<CommonResponseModel<JournalDataModel>>

    @POST("${Constants.API_VERSION}/user/questionnaire-result")
    fun fetchQuestionnaireResultAPI(@Body json: JsonObject): Observable<CommonResponseModel<QuestionnairesResultResponse>>

    @POST("${Constants.API_VERSION}/user/referrals")
    fun userReferrals(@Body params: ReferralsRequest): Observable<CommonResponseModel<ReferralsResponse>>

    @GET("${Constants.API_VERSION}/user/daily-wisdom")
    fun fetchDailyWisdomDataAPI(@QueryMap query: Map<String, String>): Observable<CommonResponseModel<FetchDailyWisdomDataResponse>>

    @GET("${Constants.API_VERSION}/user/wellness-category")
    fun fetchCategoryData(): Observable<CommonResponseModel<FetchCategoryDataResponse>>

    @GET("${Constants.API_VERSION}/user/search")
    fun fetchSearchedKeywordData(): Observable<CommonResponseModel<FetchSearchedKeywordResponse>>

    @POST("${Constants.API_VERSION}/user/how-to-meditate")
    fun fetchHowToMeditateDataList(
        @Body params: FetchHowToMeditateRequest,
        @QueryMap queryMap: HashMap<String, Any>
    ): Observable<CommonResponseModel<FetchHowToMeditateDataListResponse>>

    //    https://tao-calligraphy-api.apps.openxcell.dev/tao_calligraphy/v1/user/journal
    @POST("${Constants.API_VERSION}/user/journal")
    fun createJournal(@Body params: CreateJournalRequest):
            Observable<CommonResponseModel<JournalDataModel>>

    @POST("${Constants.API_VERSION}/user/search")
    fun searchByAllDataApi(@Body params: SearchByAllDataRequest):
            Observable<CommonResponseModel<SearchDataByAllTypeResponse>>

    @POST("${Constants.API_VERSION}/user/search/content")
    fun contentSortingApi(@Body params: SearchByAllDataRequest):
            Observable<CommonResponseModel<MeditationContentListDataModel>>

    @POST("${Constants.API_VERSION}/user/search/program")
    fun programSortingApi(@Body params: SearchByAllDataRequest):
            Observable<CommonResponseModel<ProgramListDataModel>>

    @PUT("${Constants.API_VERSION}/user/journal")
    fun editJournal(@Body params: EditJournalRequest):
            Observable<CommonResponseModel<JournalDataModel>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/journal/list")
    fun fetchJournalListData(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<FetchJournalListDataModel>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/journal/pin")
    fun updatePinUnpinJournalStatus(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<JsonObject>>


    @DELETE("${Constants.API_VERSION}/user/journal")
    fun deleteJournalFromList(@Query("journalId") journalId: String?):
            Observable<CommonResponseModel<JsonObject>>

    @POST("${Constants.API_VERSION}/user/questionnaire-question-answer")
    fun giveAnswerToQuestion(@Body params: GiveAnswerToQuestionRequest):
            Observable<CommonResponseModel<QuestionnaireAnswerResponse>>

    @GET("${Constants.API_VERSION}/user/profile")
    fun userProfile(): Observable<CommonResponseModel<LoginResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/logout")
    fun userLogout(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<LoginResponse>>

    @POST("${Constants.API_VERSION}/user/signup")
    fun userSignUp(
        @Body params: SignUpRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    @POST("${Constants.API_VERSION}/user/verify-email-token")
    fun verifyEmailTokenApi(
        @Body params: VerifyEmailTokenRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    @POST("${Constants.API_VERSION}/user/login")
    fun userLogin(
        @Body params: LoginRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/resend-otp")
    fun resendOtpApi(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<LoginResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/forgot-password")
    fun forgotPasswordApi(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<LoginResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/reset-password")
    fun resetPasswordApi(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<LoginResponse>>

    @JvmSuppressWildcards
    @GET("${Constants.API_VERSION}/user/sound")
    fun soundApi(@QueryMap params: Map<String, Any?>):
            Observable<CommonResponseModel<SoundApiResponse>>

    @GET("${Constants.API_VERSION}/user/meditation-timer/background-image-list")
    fun backgroundImageListApi(): Observable<CommonResponseModel<SoundBackImageApiResponse>>

    @POST("${Constants.API_VERSION}/user/meditation-timer")
    fun meditationTimerAddApi(
        @Body params: MeditationTimerRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    @GET("${Constants.API_VERSION}/user/meditation-timer")
    fun meditationTimerListApi(): Observable<CommonResponseModel<MeditationListApiResponse>>

    @PUT("${Constants.API_VERSION}/user/meditation-timer")
    fun meditationTimerEditApi(
        @Body params: MeditationTimerEditRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @HTTP(
        method = "DELETE",
        path = "${Constants.API_VERSION}/user/meditation-timer",
        hasBody = true
    )
    fun meditationTimerDeleteApi(
        @FieldMap params: Map<String, Any?>
    ): Observable<CommonResponseModel<LoginResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/meditation-timer/clone")
    fun meditationTimerCloneApi(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<LoginResponse>>

    @JvmSuppressWildcards
    @GET("${Constants.API_VERSION}/user/content")
    fun getMeditationContent(@QueryMap params: Map<String, Any?>):
            Observable<CommonResponseModel<MeditationContentResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/content/pre-assessment-feedback")
    fun preAssessmentFeedback(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<Any>>

    @JvmSuppressWildcards
    @POST("${Constants.API_VERSION}/user/content/post-assessment-feedback")
    fun postAssessmentFeedback(@Body json: JsonObject):
            Observable<CommonResponseModel<Any>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/content/play-time")
    fun contentPlayTime(@FieldMap params: Map<String, Any?>):
            Call<ResponseBody>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/program/content-play-time")
    fun programContentPlayTime(@FieldMap params: Map<String, Any?>):
            Call<ResponseBody>


    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/content/favourites")
    fun favouriteMeditationContent(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<Any>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/content/like-dislike")
    fun likeDisLikeMeditationContent(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<Any>>

    @JvmSuppressWildcards
    @GET("${Constants.API_VERSION}/user/spoken-languages")
    fun getLanguages(): Observable<CommonResponseModel<LanguageListApiResponse>>

    @JvmSuppressWildcards
    @GET("${Constants.API_VERSION}/user/languages")
    fun getSettingsLanguages(): Observable<CommonResponseModel<LanguageListApiResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/regions")
    fun regionsApi(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<RegionListApiResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/country")
    fun countryApi(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<RegionListApiResponse>>

    @PUT("${Constants.API_VERSION}/user/profile")
    fun userEditProfile(@Body params: UserProfileRequest):
            Observable<CommonResponseModel<LoginResponse>>

    @PUT("${Constants.API_VERSION}/user/profile/public")
    fun userProfilePublic(@Body params: UserProfilePublicRequest):
            Observable<CommonResponseModel<LoginResponse>>

    @Multipart
    @PUT("${Constants.API_VERSION}/user/profile/picture")
    fun userProfilePictureEdit(@Part multipartFile: MultipartBody.Part):
            Observable<CommonResponseModel<UserProfileEditApiResponse>>


    @JvmSuppressWildcards
    @POST("${Constants.API_VERSION}/user/program/in-progress-program-list/")
    fun getForYouProgramList(
        @QueryMap query: Map<String, Any?>,
        @Body params: ProgramListRequest
    ): Observable<CommonResponseModel<ForYouProgramListResponse>>

    @JvmSuppressWildcards
    @POST("${Constants.API_VERSION}/user/program/in-progress-program-list/")
    fun getInProgressProgramList(
        @QueryMap query: Map<String, Any?>,
        @Body params: ProgramListRequest
    ): Observable<CommonResponseModel<InProgressProgramListResponse>>

    @JvmSuppressWildcards
    @POST("${Constants.API_VERSION}/user/program/all-program-list/")
    fun getCategoryBaseProgramList(
        @QueryMap query: Map<String, Any?>,
        @Body params: SearchByAllDataRequest
    ): Observable<CommonResponseModel<CategoryBaseProgramListResponse>>

    @JvmSuppressWildcards
    @POST("${Constants.API_VERSION}/user/program/favourite/")
    fun setProgramFavourite(
        @QueryMap query: Map<String, Any?>
    ): Observable<CommonResponseModel<Any>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/change-password")
    fun changePasswordApi(@FieldMap params: Map<String, Any?>):
            Observable<CommonResponseModel<LoginResponse>>

    @JvmSuppressWildcards
    @GET("${Constants.API_VERSION}/user/profile/public")
    fun getUserProfilePublicApi(@QueryMap params: Map<String, Any?>): Observable<CommonResponseModel<UserProfilePublicApiResponse>>

    @GET("${Constants.API_VERSION}/user/interests")
    fun getUserInterestsApi(): Observable<CommonResponseModel<UserInterestApiResponse>>

    @POST("${Constants.API_VERSION}/user/interests")
    fun userInterestApi(
        @Body params: UserInterestRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    @GET("${Constants.API_VERSION}/user/settings")
    fun getUserSettingsApi(): Observable<CommonResponseModel<UserSettingsApiResponse>>

    @GET("${Constants.API_VERSION}/user/module/permission")
    fun getUserModulePermissionApi(): Observable<CommonResponseModel<List<UserModulePermission>>>

    @GET("${Constants.API_VERSION}/user/module/permission")
    fun getAClModulePermissionApi(): Call<CommonResponseModel<List<UserModulePermission>>>

    @PUT("${Constants.API_VERSION}/user/settings")
    fun userSettingsApi(@Body params: UserSettingsRequest):
            Observable<CommonResponseModel<LoginResponse>>

    @POST("${Constants.API_VERSION}/user/feedback")
    fun userFeedbackApi(
        @Body params: UserFeedbackRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    @GET("${Constants.API_VERSION}/user/goals/screen2")
    fun getUserGoalsScreen2Api(): Observable<CommonResponseModel<UserGoalsScreenApiResponse>>

    @GET("${Constants.API_VERSION}/user/goals/screen1")
    fun getUserGoalsScreen1Api(): Observable<CommonResponseModel<UserGoalsScreenApiResponse>>

    @PUT("${Constants.API_VERSION}/user/goals")
    fun userGoalsApi(@Body params: UserGoalsRequest):
            Observable<CommonResponseModel<LoginResponse>>

    @JvmSuppressWildcards
    @GET("${Constants.API_VERSION}/user/program")
    fun getUserProgramApi(@QueryMap params: Map<String, Any?>):
            Observable<CommonResponseModel<UserProgramApiResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/program/progress-details")
    fun userProgressDetailsApi(
        @FieldMap params: Map<String, Any?>
    ): Observable<CommonResponseModel<UserProgressDetailApiResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/program")
    fun userProgramApi(
        @FieldMap params: Map<String, Any?>
    ): Observable<CommonResponseModel<UserProgressDetailApiResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/program/content-details")
    fun userProgramContentDetailsApi(
        @FieldMap params: Map<String, Any?>
    ): Observable<CommonResponseModel<UserProgramContentDetailApiRes>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @POST("${Constants.API_VERSION}/user/meditation-timer/play-details")
    fun userMeditationTimerPlayDetailsApi(
        @FieldMap params: Map<String, Any?>
    ): Call<ResponseBody>

    @POST("${Constants.API_VERSION}/user/playlist/content/filter")
    fun playlistContentFilterApi(
        @Body params: PlaylistContentFilterRequest
    ): Observable<CommonResponseModel<PlaylistContentFilterApiResponse>>

    @POST("${Constants.API_VERSION}/user/playlist")
    fun addPlaylistApi(
        @Body params: AddPlaylistRequest
    ): Observable<CommonResponseModel<PlaylistContentFilterApiResponse>>

    @POST("${Constants.API_VERSION}/user/playlist-list")
    fun playListApi(
        @Body params: PlaylistRequest
    ): Observable<CommonResponseModel<PlaylistApiResponse>>

    @JvmSuppressWildcards
    @GET("${Constants.API_VERSION}/user/playlist/contents")
    fun playlistContentApi(
        @QueryMap params: Map<String, Any?>
    ): Observable<CommonResponseModel<PlaylistContentFilterApiResponse>>

    @JvmSuppressWildcards
    @FormUrlEncoded
    @HTTP(
        method = "DELETE",
        path = "${Constants.API_VERSION}/user/playlist",
        hasBody = true
    )
    fun playListDeleteApi(
        @FieldMap params: Map<String, Any?>
    ): Observable<CommonResponseModel<LoginResponse>>


    @HTTP(
        method = "DELETE",
        path = "${Constants.API_VERSION}/user/delete-account",
        hasBody = true
    )
    fun deleteAccountApi(): Observable<CommonResponseModel<LoginResponse>>

    @PUT("${Constants.API_VERSION}/user/playlist")
    fun editPlaylistApi(
        @Body params: EditPlaylistRequest
    ): Observable<CommonResponseModel<LoginResponse>>

    @POST("${Constants.API_VERSION}/user/content/download")
    fun downloadContent(@Query("contentId") contentId: String):
            Call<ResponseBody>

    @POST("${Constants.API_VERSION}/user/alarm")
    fun addAlarm(
        @Body params: AddAlarmRequest
    ): Observable<CommonResponseModel<AlarmResponse>?>

    @GET("${Constants.API_VERSION}/user/alarm")
    fun getActiveAlarm(): Observable<CommonResponseModel<AlarmResponse>>

    @POST("${Constants.API_VERSION}/user/alarm/on-off")
    fun alarmOnOff(
    ): Observable<CommonResponseModel<AlarmResponse>?>

    @GET("${Constants.API_VERSION}/user/content/initial-experience")
    fun initialUserExperience(): Observable<CommonResponseModel<MeditationContentResponse>>

    @GET("${Constants.API_VERSION}/user/content/initial-experience/more")
    fun initialUserExperienceDetail(
        @Query("categoryId") categoryId: Int
    ): Observable<CommonResponseModel<InitialUserExperienceDetail>>

    @POST("${Constants.API_VERSION}/user/downloads")
    fun getUserDownloads(
        @Query("categoryId") categoryId: Int,
        @Body json: JsonObject
    ): Call<CommonResponseModel<UserDownloads>>

    @JvmSuppressWildcards
    @GET("${Constants.API_VERSION}/user/content")
    fun getUserDownloadsContent(@QueryMap params: Map<String, Any>):
            Call<CommonResponseModel<MeditationContentResponse>>

    @HTTP(method = "DELETE", path = "${Constants.API_VERSION}/user/downloads", hasBody = true)
    fun deleteUserDownload(
        @Body json: JsonObject
    ): Call<Any>

    @GET("${Constants.API_VERSION}/user/program/linked-programs")
    fun getLinkedProgram(
        @Query("programId") programId: Int,
        @Query("isRedirectedFromHistory") isRedirectedFromHistory: Boolean
    ): Observable<CommonResponseModel<LinkedProgramData>>

    @POST("${Constants.API_VERSION}/user/program/rating")
    fun getLinkedProgram(
        @Body json: JsonObject
    ): Observable<CommonResponseModel<Any>>

    @POST("${Constants.API_VERSION}/user/subscription")
    fun subscribeUser(
        @Body json: JsonObject
    ): Observable<CommonResponseModel<Any>>

    @GET("${Constants.API_VERSION}/user/subscription")
    fun getSubscriptionDetails(): Observable<CommonResponseModel<SubscriptionDetails>>

    @GET("${Constants.API_VERSION}/user/subscription/details")
    fun getSubscriptionStatus(): Observable<CommonResponseModel<SubscriptionDetails>>

    @PUT("${Constants.API_VERSION}/user/free/trial")
    fun startFreeTrial(): Observable<CommonResponseModel<Unit>>

    @POST("${Constants.API_VERSION}/user/category-details/view-all")
    fun getAllCategoryData(
        @Query("contentType") contentType: String,
        @Query("subCategoryId") subCategoryId: Int,
        @Body json: JsonObject
    ): Observable<CommonResponseModel<ForYouDataModel>>
}