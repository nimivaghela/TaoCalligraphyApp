package com.app.taocalligraphy.ui.favorites.view_model

import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.request.FavoriteContentRequest
import com.app.taocalligraphy.models.request.FavoriteProgramRequest
import com.app.taocalligraphy.models.request.SortFavoriteDataModel
import com.app.taocalligraphy.models.response.favorite_models.FavoriteContentDataModel
import com.app.taocalligraphy.models.response.favorite_models.FavoriteContentResponse
import com.app.taocalligraphy.models.response.favorite_models.FavoriteProgramResponse
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.repository.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(private val favoriteRepository: FavoriteRepository) :
    BaseViewModel() {
    val fetchFavoriteProgramResponse =
        MutableLiveData<RequestState<FavoriteProgramResponse>>()

    val fetchFavoriteContentResponse =
        MutableLiveData<RequestState<FavoriteContentResponse>>()

    val meditationList = ArrayList<FavoriteContentDataModel?>()
    val programList = ArrayList<ProgramDataModel?>()

    var totalProgramCount = 0
    var totalContentCount = 0
    var perPageCount = 10

    var currentPageProgramCount = -1
    var currentPageContentCount = -1
    var sortField: String = Constants.latest
    var sortOrder: String = Constants.desc

    fun fetchFavoriteProgramData(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageProgramCount += 1
        favoriteRepository.fetchFavoriteProgramDataAPI(
            FavoriteProgramRequest().apply {
                this.currentPage = currentPageProgramCount
                this.perPage = perPageCount
                this.search = ""
            },
            baseView,
            disposable,
            fetchFavoriteProgramResponse
        )
    }


    fun fetchFavoriteContentData(
        baseView: BaseView,
        disposable: CompositeDisposable
    ) {
        currentPageContentCount += 1
        favoriteRepository.fetchFavoriteContentDataAPI(
            FavoriteContentRequest().apply {
                this.currentPage = currentPageContentCount
                this.perPage = perPageCount
                this.search = ""
                if (sortField.isNotEmpty() || sortOrder.isNotEmpty()) {
                    this.sort = SortFavoriteDataModel(sortField, sortOrder)
                }
            },
            baseView,
            disposable,
            fetchFavoriteContentResponse
        )
    }
}