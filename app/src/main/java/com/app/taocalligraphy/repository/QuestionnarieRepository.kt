package com.app.taocalligraphy.repository

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.GiveAnswerToQuestionRequest
import com.app.taocalligraphy.models.response.question_data_models.QuestionnairesResultResponse
import com.app.taocalligraphy.models.response.question_data_models.answer_data_model.QuestionnaireAnswerResponse
import com.app.taocalligraphy.models.response.question_data_models.fetch_question_data_model.FetchQuestionListResponse
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import retrofit2.HttpException
import javax.inject.Inject

class QuestionnarieRepository @Inject constructor(private val apiHelper: ApiHelper) {

    fun giveAnswerToQuestion(
        param: GiveAnswerToQuestionRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<QuestionnaireAnswerResponse>>
    ) = apiHelper.giveAnswerToQuestion(param).observeOn(AndroidSchedulers.mainThread())
        .doOnSubscribe {
            callback.value = RequestState(progress = true)
        }
        .subscribeWith(
            object : CallbackWrapper<CommonResponseModel<QuestionnaireAnswerResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<QuestionnaireAnswerResponse>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: ""))
                }
            }
        ).addTo(disposable)

    fun getQuestionListData(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchQuestionListResponse>>
    ) {
        apiHelper.getQuestionListData().observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                callback.value = RequestState(progress = true)
            }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchQuestionListResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchQuestionListResponse>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: ""))
                }
            }).addTo(disposable)
    }

    fun fetchQuestionnaireResultAPI(
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<QuestionnairesResultResponse>>
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("current_page", 0)
        jsonObject.addProperty("per_page", 5)
        jsonObject.addProperty("search", "")

        apiHelper.fetchQuestionnaireResultAPI(jsonObject)
            .observeOn(AndroidSchedulers.mainThread()).doOnSubscribe {
                callback.value = RequestState(progress = true)
            }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<QuestionnairesResultResponse>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<QuestionnairesResultResponse>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: ""))
                }
            }).addTo(disposable)
    }
}