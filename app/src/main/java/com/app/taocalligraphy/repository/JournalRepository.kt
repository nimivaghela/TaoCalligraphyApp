package com.app.taocalligraphy.repository

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.ApiHelper
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.api.repo.CallbackWrapper
import com.app.taocalligraphy.models.ApiError
import com.app.taocalligraphy.models.CommonResponseModel
import com.app.taocalligraphy.models.ErrorModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.CreateJournalRequest
import com.app.taocalligraphy.models.request.EditJournalRequest
import com.app.taocalligraphy.models.response.journal_data_models.FetchJournalListDataModel
import com.app.taocalligraphy.models.response.journal_data_models.JournalDataModel
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

class JournalRepository @Inject constructor(private val apiHelper: ApiHelper) {

    fun fetchJournalListData(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<FetchJournalListDataModel>>
    ) {
        apiHelper.fetchJournalListData(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                callback.value = RequestState(progress = true)
            }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<FetchJournalListDataModel>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<FetchJournalListDataModel>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                        )
                }
            }).addTo(disposable)
    }

    fun updatePinUnpinJournalStatus(
        param: Map<String, Any?>,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<JsonObject>>
    ) {
        apiHelper.updatePinUnpinJournalStatus(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                callback.value = RequestState(progress = true)
            }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<JsonObject>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<JsonObject>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                        )
                }
            }).addTo(disposable)
    }

    fun deleteJournalFromList(
        journalId: String?,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<JsonObject>>
    ) {
        apiHelper.deleteJournalFromList(journalId).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                callback.value = RequestState(progress = true)
            }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<JsonObject>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<JsonObject>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                        )
                }
            }).addTo(disposable)
    }

    fun fetchJournalDataById(
        journalId: String?,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<JournalDataModel>>
    ) {
        apiHelper.fetchJournalDataById(journalId).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                callback.value = RequestState(progress = true)
            }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<JournalDataModel>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<JournalDataModel>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                        )
                }
            }).addTo(disposable)
    }

    fun createJournal(
        param: CreateJournalRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<JournalDataModel>>
    ) {
        apiHelper.createJournal(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<JournalDataModel>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<JournalDataModel>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                        )
                }
            }).addTo(disposable)
    }

    fun editJournal(
        param: EditJournalRequest,
        baseView: BaseView,
        disposable: CompositeDisposable,
        callback: MutableLiveData<RequestState<JournalDataModel>>
    ) {
        apiHelper.editJournal(param).observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { callback.value = RequestState(progress = true) }
            .subscribeWith(object :
                CallbackWrapper<CommonResponseModel<JournalDataModel>>(baseView) {
                override fun onApiSuccess(response: CommonResponseModel<JournalDataModel>) {
                    callback.value = RequestState(progress = false, apiResponse = response)
                }

                override fun onApiError(e: ErrorModel?) {
                    callback.value =
                        RequestState(
                            progress = false,
                            error = ApiError(e?.errorCode, "", e?.message.toString(), e?.type ?: "")
                        )
                }
            }).addTo(disposable)
    }


}