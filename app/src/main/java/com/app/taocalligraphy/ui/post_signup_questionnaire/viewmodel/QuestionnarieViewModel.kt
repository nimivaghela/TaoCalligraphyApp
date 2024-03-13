package com.app.taocalligraphy.ui.post_signup_questionnaire.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.GiveAnswerToQuestionRequest
import com.app.taocalligraphy.models.response.question_data_models.QuestionnairesResultResponse
import com.app.taocalligraphy.models.response.question_data_models.answer_data_model.QuestionnaireAnswerResponse
import com.app.taocalligraphy.models.response.question_data_models.fetch_question_data_model.FetchQuestionListResponse
import com.app.taocalligraphy.repository.QuestionnarieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class QuestionnarieViewModel @Inject constructor(
    private val questionnarieRepository: QuestionnarieRepository
) :
    BaseViewModel() {

    var mQuestionCount = 0
    var mCloudAnimValue = 0f
    var mCloudAnimRange = 80f
    var mBirdAnimValue = 0f
    var mBirdAnimRange = 30f

    var suggestedData : QuestionnairesResultResponse? = null

    val getQuestionListDataResponse =
        MutableLiveData<RequestState<FetchQuestionListResponse>>()

    val giveAnswerDataResponse =
        MutableLiveData<RequestState<QuestionnaireAnswerResponse>>()

    val questionnairesResultResponse =
        MutableLiveData<RequestState<QuestionnairesResultResponse>>()

    fun getQuestionListData(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        questionnarieRepository.getQuestionListData(
            baseView,
            disposable,
            getQuestionListDataResponse
        )
    }

    fun giveAnswerToQuestionAPI(
        baseView: BaseView,
        disposable: CompositeDisposable,
        questionId: Int,
        orderNo: Int,
        ageRange: String,
        gender: String,
        deletedKeywordsIds: ArrayList<Int>,
        keywordsIds: ArrayList<Int>,
    ) {
        questionnarieRepository.giveAnswerToQuestion(GiveAnswerToQuestionRequest().apply {
            this.questionId = questionId
            this.orderNo = orderNo
            this.ageRange = ageRange
            this.gender = gender
            this.deletedKeywordsIds = deletedKeywordsIds
            this.keywordsIds = keywordsIds
        }, baseView, disposable, giveAnswerDataResponse)
    }


    fun fetchQuestionnaireResultAPI(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        questionnarieRepository.fetchQuestionnaireResultAPI(
            baseView,
            disposable,
            questionnairesResultResponse
        )
    }
}