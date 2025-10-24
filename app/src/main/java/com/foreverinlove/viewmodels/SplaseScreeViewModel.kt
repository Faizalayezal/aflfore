package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.AddtionalQueListResponse
import com.foreverinlove.network.response.GenderObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.MyListDataHelper
import com.foreverinlove.utility.PopupListHelper
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplaseScreeViewModel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application) {

    var genderList = listOf(
        GenderObject(1, "Male"),
        GenderObject(2, "Female"),
        //  GenderObject(30, "Transgender"),
        // GenderObject(40, "Transexual"),
        GenderObject(3, "Non-Binary")
    )

    sealed class ResponseEvent {
        class Success(val result: AddtionalQueListResponse) : ResponseEvent()
        class Failure(val errorText: String) : ResponseEvent()
        object Loading : ResponseEvent()
        object Empty : ResponseEvent()
    }

    private val _addtionalQueConversion = MutableStateFlow<ResponseEvent>(
        ResponseEvent.Empty
    )
    val addtionalQueConversion: StateFlow<ResponseEvent> = _addtionalQueConversion

    fun callApiData() = viewModelScope.launch {

        val token = context.dataStoreGetUserData().firstOrNull()?.token ?: ""
        launch {
            when (val quotesResponse = repository.getPendingPopupList(
                token,
            )) {
                is Resource.Error -> {

                }

                is Resource.Success -> {
                    val quote = quotesResponse.data
                    PopupListHelper.setAllData(quote?.data)
                }
            }
        }.join()
        launch {
            _addtionalQueConversion.value = ResponseEvent.Loading
            when (val quotesResponse = repository.addtionalQuiestion(
                "",
            )) {
                is Resource.Error -> _addtionalQueConversion.value =
                    ResponseEvent.Failure(quotesResponse.message?:"")

                is Resource.Success -> {
                    try {

                        val quote = quotesResponse.data!!

                        MyListDataHelper.setAllData(quote.data)
                        MyListDataHelper.setAllDataGender(genderList)
                        _addtionalQueConversion.value = ResponseEvent.Success(
                            quote
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            }

        }.join()


    }
}





