package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.ReviewResponse
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
class ReviewLatterListViewModel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application){
   private var tempUserDataObject: TempUserDataObject? = null
   private var isFirstTime=true

    init {
        viewModelScope.launch {
            context.dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    tempUserDataObject=it
                    if(isFirstTime) {
                        isFirstTime=false
                        callApiData()
                    }
                }
        }

    }

    sealed class ReviewMeResponseEvent{
        class Success(val result: ReviewResponse): ReviewMeResponseEvent()
        class Failure(val errorText:String): ReviewMeResponseEvent()
        object Loading: ReviewMeResponseEvent()
        object Empty: ReviewMeResponseEvent()
    }

    //mutuble value edit mate
    private val _reviewMeListConversion = MutableStateFlow<ReviewMeResponseEvent>(
        ReviewMeResponseEvent.Empty)
    val reviewdMeListConversion: StateFlow<ReviewMeResponseEvent> = _reviewMeListConversion

    private fun callApiData() {
        viewModelScope.launch {
            _reviewMeListConversion.value = ReviewMeResponseEvent.Loading
            when(val quotesResponse = repository.getReviewLatterList(tempUserDataObject?.token?:"")) {
                is Resource.Error -> _reviewMeListConversion.value =
                    ReviewMeResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _reviewMeListConversion.value = ReviewMeResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }
}