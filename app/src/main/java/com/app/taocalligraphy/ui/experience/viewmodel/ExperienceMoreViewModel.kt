package com.app.taocalligraphy.ui.experience.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.response.InitialUserExperienceDetail
import com.app.taocalligraphy.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class ExperienceMoreViewModel @Inject constructor(private val mainRepository: MainRepository) :
    BaseViewModel() {

    var userExperienceDetail: InitialUserExperienceDetail? = null
    var isFromConfigChanges = false
    var selectedCategory = -1

    private val getInitialUserExperienceDetailRes =
        MutableLiveData<RequestState<InitialUserExperienceDetail>>()

    val getInitialUserExperienceDetail: LiveData<RequestState<InitialUserExperienceDetail>>
        get() = getInitialUserExperienceDetailRes

    fun getInitialUserExperienceDetail(categoryId:Int, baseView: BaseView, disposable: CompositeDisposable) {
        mainRepository.getInitialUserExperienceDetail(
            categoryId,
            baseView,
            disposable,
            getInitialUserExperienceDetailRes
        )
    }

}