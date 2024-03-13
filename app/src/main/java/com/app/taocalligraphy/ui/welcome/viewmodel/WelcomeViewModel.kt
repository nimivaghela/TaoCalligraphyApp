package com.app.taocalligraphy.ui.welcome.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.taocalligraphy.api.repo.BaseView
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.RequestState
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(private val mainRepository: MainRepository) :
    BaseViewModel() {

    var userExperience: MeditationContentResponse? = null
    var isFromConfigChanges = false

    private val getInitialUserExperienceRes =
        MutableLiveData<RequestState<MeditationContentResponse>>()

    val getInitialUserExperience: LiveData<RequestState<MeditationContentResponse>>
        get() = getInitialUserExperienceRes

    fun getInitialUserExperience(baseView: BaseView, disposable: CompositeDisposable) {
        mainRepository.getInitialUserExperience(baseView, disposable, getInitialUserExperienceRes)
    }

}