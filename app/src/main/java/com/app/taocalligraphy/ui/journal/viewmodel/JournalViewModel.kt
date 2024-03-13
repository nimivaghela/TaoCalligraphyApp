package com.app.taocalligraphy.ui.journal.viewmodel

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.CreateJournalRequest
import com.app.taocalligraphy.models.request.EditJournalRequest
import com.app.taocalligraphy.models.response.journal_data_models.FetchJournalListDataModel
import com.app.taocalligraphy.models.response.journal_data_models.JournalDataModel
import com.app.taocalligraphy.repository.JournalRepository
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(private val journalRepository: JournalRepository) :
    BaseViewModel() {

    // --------------------------------------- Journal List Activity Variables -----------------------------------------------

    var journalDataList: ArrayList<JournalDataModel> = arrayListOf()
    var searchJournalDataList: ArrayList<JournalDataModel> = arrayListOf()
    var responseJournalDataList: ArrayList<JournalDataModel> = arrayListOf()
    var pinJournalDataList: ArrayList<JournalDataModel> = arrayListOf()
    var unPinJournalDataList: ArrayList<JournalDataModel> = arrayListOf()
    var unPinJournalList: List<JournalDataModel> = arrayListOf()
    var pinJournalList: List<JournalDataModel> = arrayListOf()
    var searchText: String = ""

    // ----------------------------------------- Create Journal Activity ----------------------------------------------------------

    var initTitle = ""
    var initText = ""
    var isBoldSelect = false
    var isItalicSelect = false
    var isUnderlineSelect = false
    var isStrikethroughSelect = false
    var isFromShare: Boolean = false

    var currenttitle = ""
    var currentText = ""
    var journalData : JournalDataModel? = null

    val fetchJournalListDataResponse =
        MutableLiveData<RequestState<FetchJournalListDataModel>>()

    val updatePinUnpinJournalStatus =
        MutableLiveData<RequestState<JsonObject>>()

    val deleteJournalFromList =
        MutableLiveData<RequestState<JsonObject>>()

    val fetchJournalDataById =
        MutableLiveData<RequestState<JournalDataModel>>()

    val createJournalResponse =
        MutableLiveData<RequestState<JournalDataModel>>()

    val editJournalResponse =
        MutableLiveData<RequestState<JournalDataModel>>()

    fun fetchJournalListData(
        searchText: String?,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params["search"] = searchText!!
        journalRepository.fetchJournalListData(
            params,
            baseView,
            disposable,
            fetchJournalListDataResponse
        )
    }

    fun updatePinUnpinJournalStatus(
        journalId: String?,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params["journalId"] = journalId!!
        journalRepository.updatePinUnpinJournalStatus(
            params,
            baseView,
            disposable,
            updatePinUnpinJournalStatus
        )
    }

    fun deleteJournalFromList(
        journalId: String?,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        val params = HashMap<String, Any>()
        params["journalId"] = journalId!!
        journalRepository.deleteJournalFromList(
            journalId,
            baseView,
            disposable,
            deleteJournalFromList
        )
    }

    fun fetchJournalDataById(
        journalId: String?,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        journalRepository.fetchJournalDataById(
            journalId,
            baseView,
            disposable,
            fetchJournalDataById
        )
    }

    fun createJournalAPI(
        title: String?,
        journalEntry: String?,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        journalRepository.createJournal(
            CreateJournalRequest().apply {
                this.title = "" + title
                this.journalEntry = "" + journalEntry
            },
            baseView,
            disposable,
            createJournalResponse
        )
    }

    fun editJournalAPI(
        journalId: String?,
        title: String?,
        journalEntry: String?,
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        journalRepository.editJournal(
            EditJournalRequest().apply {
                this.journalId = journalId!!
                this.title = "" + title
                this.journalEntry = "" + journalEntry
            },
            baseView,
            disposable,
            editJournalResponse
        )
    }

}