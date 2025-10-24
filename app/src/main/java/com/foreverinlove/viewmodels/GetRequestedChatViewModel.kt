package com.foreverinlove.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.PrivateChatListResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "GetRequestedChatViewMod"
@HiltViewModel
class GetRequestedChatViewModel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application) {

    var tempUserDataObject: TempUserDataObject? = null
    fun start() {
        tempUserDataObject = null
        viewModelScope.launch {
            context.dataStoreGetUserData().collect{
                if(tempUserDataObject==null){
                    tempUserDataObject=it
                    callApiPaddingListData("requested")
                    callApiAcceptedListData("accepted")
                }

            }
        }
    }

    sealed class AcceptedListResponseEvent{
        class Success(val result: PrivateChatListResponse): AcceptedListResponseEvent()
        class Failure(val errorText:String): AcceptedListResponseEvent()
        object Loading: AcceptedListResponseEvent()
        object Empty: AcceptedListResponseEvent()
    }

    private val _acceptedListConversion = MutableStateFlow<AcceptedListResponseEvent>(
        AcceptedListResponseEvent.Empty)
    val acceptedListConversion: StateFlow<AcceptedListResponseEvent> = _acceptedListConversion

    fun callApiAcceptedListData(status:String) {
        viewModelScope.launch {
            Log.d(TAG, "callApiAcceptedListData: test55>>")
            _acceptedListConversion.value = AcceptedListResponseEvent.Loading
            when(val quotesResponse = repository.getPrivateChatListList(tempUserDataObject?.token?:"",status)) {
                is Resource.Error -> _acceptedListConversion.value =
                    AcceptedListResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _acceptedListConversion.value = AcceptedListResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }



  //  'requested','accepted',    'rejected'
    sealed class PaddingListResponseEvent{
        class Success(val result: PrivateChatListResponse): PaddingListResponseEvent()
        class Failure(val errorText:String): PaddingListResponseEvent()
        object Loading: PaddingListResponseEvent()
        object Empty: PaddingListResponseEvent()
    }

    private val _paddingListConversion = MutableStateFlow<PaddingListResponseEvent>(
        PaddingListResponseEvent.Empty)
    val paddingListConversion: StateFlow<PaddingListResponseEvent> = _paddingListConversion

    fun callApiPaddingListData(status:String) {
        viewModelScope.launch {
            _paddingListConversion.value = PaddingListResponseEvent.Loading
            when(val quotesResponse = repository.getPrivateChatListList(tempUserDataObject?.token?:"",status)) {
                is Resource.Error -> _paddingListConversion.value =
                    PaddingListResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _paddingListConversion.value = PaddingListResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }




}