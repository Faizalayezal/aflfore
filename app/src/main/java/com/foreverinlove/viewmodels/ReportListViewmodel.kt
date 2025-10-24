package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.ApplictionClass
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.ReasonListResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportListViewmodel @Inject constructor
    (application: Application, private val repository: MainRepository)
    : BaseViewModel(application){


    sealed class ReasonListEvent{
        class Success(val result: ReasonListResponse): ReasonListEvent()
        class Failure(val errorText:String): ReasonListEvent()
        object Loading: ReasonListEvent()
        object Empty: ReasonListEvent()
    }

    private var userDataObject:TempUserDataObject?=null
   private var isCallingAllowed=true
    init {
        viewModelScope.launch {
            context.dataStoreGetUserData().catch {
                it.printStackTrace()
            }.collect {
                userDataObject=it

                if(userDataObject!=null && isCallingAllowed){
                    getReasonList(userDataObject?.token?:"")
                    isCallingAllowed=false
                }
            }
        }
    }


    private val _conversionReason = MutableStateFlow<ReasonListEvent>(ReasonListEvent.Empty)
    val conversionReason: StateFlow<ReasonListEvent> = _conversionReason

    private fun getReasonList(token:String) {
        GlobalScope.launch(Dispatchers.IO) {
            _conversionReason.value = ReasonListEvent.Loading
            when(val quotesResponse = repository.getReasonList(token)) {
                is Resource.Error -> _conversionReason.value =
                    ReasonListEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!


                    (context as ApplictionClass).setListOfReason(quote)

                    _conversionReason.value = ReasonListEvent.Success(
                        quote
                    )
                }
            }
        }
    }
}