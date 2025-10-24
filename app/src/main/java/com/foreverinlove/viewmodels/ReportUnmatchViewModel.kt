package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.ViewedMeListResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportUnmatchViewModel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application) {

    var tempUserDataObject: TempUserDataObject? = null

    fun start() {
        viewModelScope.launch {
            context.dataStoreGetUserData().catch { it.printStackTrace() }.collect {
                tempUserDataObject = it
            }
        }
    }

    sealed class ResponseEvent {
        class Success(val result: ViewedMeListResponse) : ResponseEvent()
        class Failure(val errorText: String) : ResponseEvent()
        object Loading : ResponseEvent()
        object Empty : ResponseEvent()
    }

    private val _reportUnMatchConversion = MutableStateFlow<ResponseEvent>(
        ResponseEvent.Empty
    )
    val reportUnMatchConversion: StateFlow<ResponseEvent> = _reportUnMatchConversion

    fun callApiData(userId: String, reportId: String, type: String) {
        GlobalScope.launch {
            _reportUnMatchConversion.value = ResponseEvent.Loading
            when (val quotesResponse = repository.reportUnmatchedPerson(
                tempUserDataObject?.token ?: "",
                userId,
                reportId,
                type
            )) {
                is Resource.Error -> _reportUnMatchConversion.value =
                    ResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _reportUnMatchConversion.value = ResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }

}