package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.SuperLikePlanResponse
import com.foreverinlove.network.response.SuperLikePurchaseResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetSuperLikeViewModel @Inject constructor(
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
                    callApiSuperLikeListData("super_like")

                }

            }
        }
    }

  //      'super_like'
    sealed class SuperLikeListResponseEvent{
        class Success(val result: SuperLikePlanResponse): SuperLikeListResponseEvent()
        class Failure(val errorText:String): SuperLikeListResponseEvent()
        object Loading: SuperLikeListResponseEvent()
        object Empty: SuperLikeListResponseEvent()
    }

    private val _superLikeListConversion = MutableStateFlow<SuperLikeListResponseEvent>(
        SuperLikeListResponseEvent.Empty)
    val superLikeListConversion: StateFlow<SuperLikeListResponseEvent> = _superLikeListConversion

    fun callApiSuperLikeListData(type:String) {
        viewModelScope.launch {
            _superLikeListConversion.value = SuperLikeListResponseEvent.Loading
            when(val quotesResponse = repository.getSuperLikeList(tempUserDataObject?.token?:"",type)) {
                is Resource.Error -> _superLikeListConversion.value =
                    SuperLikeListResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _superLikeListConversion.value = SuperLikeListResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }

    //  'requested','accepted','rejected'
    sealed class SupeLikeResponseEvent{
        class Success(val result: SuperLikePlanResponse): SupeLikeResponseEvent()
        class Failure(val errorText:String): SupeLikeResponseEvent()
        object Loading: SupeLikeResponseEvent()
        object Empty: SupeLikeResponseEvent()
    }
    sealed class SupeLikePurchaseResponseEvent{
        class Success(val result: SuperLikePurchaseResponse): SupeLikePurchaseResponseEvent()
        class Failure(val errorText:String): SupeLikePurchaseResponseEvent()
        object Loading: SupeLikePurchaseResponseEvent()
        object Empty: SupeLikePurchaseResponseEvent()
    }

    private val _superLikeConversion = MutableStateFlow<SupeLikePurchaseResponseEvent>(
        SupeLikePurchaseResponseEvent.Empty)
    val superLikeConversion: StateFlow<SupeLikePurchaseResponseEvent> = _superLikeConversion

    fun callApiConfirmData(pid:String) {
        viewModelScope.launch {
            _superLikeConversion.value = SupeLikePurchaseResponseEvent.Loading
            when(val quotesResponse = repository.superLike(tempUserDataObject?.token?:"",pid)) {
                is Resource.Error -> _superLikeConversion.value =
                    SupeLikePurchaseResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _superLikeConversion.value = SupeLikePurchaseResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }




}