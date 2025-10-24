package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.AddSuggestionResponse
import com.foreverinlove.network.response.SettingResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.dataStoreSetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,val repository: MainRepository
) : BaseViewModel(application) {

    sealed class GetLoginEvent {
        class Success(val result: SettingResponse) : GetLoginEvent()
        class Failure(val errorText: String) : GetLoginEvent()
        object Loading : GetLoginEvent()
        object Empty : GetLoginEvent()
    }
    var tempUserDataObject: TempUserDataObject? = null


    fun start() {
        viewModelScope.launch {
            context.dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {

                    tempUserDataObject = it


                }
        }
    }

    private val _settingConversion = MutableStateFlow<GetLoginEvent>(GetLoginEvent.Empty)
    val settingConversion: StateFlow<GetLoginEvent> = _settingConversion




    fun getSettings() {

        viewModelScope.launch {
            _settingConversion.value = GetLoginEvent.Loading
            when (val data = repository.getSetting(tempUserDataObject?.token?:"")) {
                is Resource.Error -> {
                    _settingConversion.value =GetLoginEvent.Failure(data.message!!)
                }
                is Resource.Success -> {


                    if (data.data!!.status == 1) {
                        GlobalScope.launch {
                            val tempData = TempUserDataObject(

                                token = data.data.data?.api_token ?: "",

                            )
                            context.dataStoreSetUserData(tempData)
                        }

                        _settingConversion.value =GetLoginEvent.Success(
                            data.data
                        )
                    } else {
                        _settingConversion.value = GetLoginEvent.Failure(
                            data.data.message ?: ""
                        )
                    }


                }
            }

        }

    }

    sealed class UpdateSettingType {
        class UpdateDistanceVisible(val distanceVisible: String) : UpdateSettingType()
        class UpdateShowNotification(val showNotification: String) : UpdateSettingType()
        class UpdateSendEmail(val email: String) : UpdateSettingType()
    }
    fun updateSettingData(type:UpdateSettingType) {
        GlobalScope.launch(Dispatchers.IO) {
            repository.updateSettingsData(tempUserDataObject?.token?:"", type)
        }
    }


    //Suggestion mate

    sealed class SuggestionEvent {
        class Success(val result: AddSuggestionResponse) : SuggestionEvent()
        class Failure(val errorText: String) : SuggestionEvent()
        object Loading : SuggestionEvent()
        object Empty : SuggestionEvent()
    }


    private val _suggestionConversion = MutableStateFlow<SuggestionEvent>(
        SuggestionEvent.Empty
    )
    val suggestionConversion: StateFlow<SuggestionEvent> = _suggestionConversion

    fun callApiData(description: String) {
        GlobalScope.launch {
            _suggestionConversion.value = SuggestionEvent.Loading
            when (val quotesResponse = repository.addSuggestion(
                tempUserDataObject?.token ?: "",
                description
            )) {
                is Resource.Error -> _suggestionConversion.value =
                    SuggestionEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _suggestionConversion.value = SuggestionEvent.Success(
                        quote
                    )
                }
            }

        }
    }



}