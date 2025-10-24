package com.foreverinlove.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.onFidoData
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.screen.activity.RegisterFlowStatus
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.dataStoreSetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BioViewModel @Inject constructor(
    application: Application,
    val repository: MainRepository,

    ) : BaseViewModel(application) {


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

    fun updateUserData(data: TempUserDataObject) {
        viewModelScope.launch {
            val tempData = TempUserDataObject(
                about = data.about,
                registerFlowStatus = RegisterFlowStatus.Bio,
            )
            context.dataStoreSetUserData(tempData)
        }

    }


    sealed class OnfidoResponseEvent {
        class Success(val result: onFidoData) : OnfidoResponseEvent()
        class Failure(val errorText: String) : OnfidoResponseEvent()
        object Loading : OnfidoResponseEvent()
        object Empty : OnfidoResponseEvent()
    }

    private val _onFidoListConversion = MutableStateFlow<OnfidoResponseEvent>(
        OnfidoResponseEvent.Empty
    )
    val onFidoListConversion: StateFlow<OnfidoResponseEvent> = _onFidoListConversion

     fun callApiData(sdkToken:(String)->Unit) {
        viewModelScope.launch {
            _onFidoListConversion.value = OnfidoResponseEvent.Loading
            when(val quotesResponse =
                repository.onFido(firstName = tempUserDataObject?.first_name ?: "", lastName = tempUserDataObject?.last_name?:"")) {
                is Resource.Error -> _onFidoListConversion.value =
                    OnfidoResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    Log.d("TAG", "callAdfgdfgdfgpiData: "+quote.sdk_token)
                    sdkToken.invoke(quote.sdk_token?:"")
                    val tempData = TempUserDataObject(
                        sdk_token = quote.sdk_token?:"",
                    )
                    context.dataStoreSetUserData(tempData)
                    _onFidoListConversion.value = OnfidoResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }


}