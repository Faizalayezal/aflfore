package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.PagesResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PagesViewModel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application){
   private var tempUserDataObject: TempUserDataObject? = null


   fun start() {
       viewModelScope.launch {
           context.dataStoreGetUserData()
               .catch{
                   it.printStackTrace()
               }
               .collect {

                   tempUserDataObject = it
                   callApiData()

               }
       }
   }

    sealed class PagesResponseEvent{
        class Success(val result: PagesResponse): PagesResponseEvent()
        class Failure(val errorText:String): PagesResponseEvent()
        object Loading: PagesResponseEvent()
        object Empty: PagesResponseEvent()
    }

    //mutuble value edit mate
    private val _pagesConversion = MutableStateFlow<PagesResponseEvent>(
        PagesResponseEvent.Empty)
    val pagesConversion: StateFlow<PagesResponseEvent> = _pagesConversion

    fun callApiData() {
        viewModelScope.launch {
            _pagesConversion.value = PagesResponseEvent.Loading
            when(val quotesResponse = repository.addPages()) {
                is Resource.Error -> _pagesConversion.value =
                    PagesResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _pagesConversion.value = PagesResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }
}