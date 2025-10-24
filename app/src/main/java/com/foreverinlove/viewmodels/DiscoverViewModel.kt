package com.foreverinlove.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.AddReviewResponse
import com.foreverinlove.network.response.DiscoverUserListResponse
import com.foreverinlove.network.response.SwipeResponse
import com.foreverinlove.objects.DiscoverFilterObject
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DiscoverViewModel"

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    application: Application,
    private val repository: MainRepository
) : BaseViewModel(application) {

    private var tempDataObject: TempUserDataObject? = null
    private var userObject: TempUserDataObject? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            context.dataStoreGetUserData().catch { it.printStackTrace() }
                .collect {
                    tempDataObject = it

                    // Log.d("TAG", "testUserGender>>11>>" + it.gender)
                }
        }
    }

    fun start() {

        viewModelScope.launch(Dispatchers.IO) {
            context.dataStoreGetUserData().catch { it.printStackTrace() }
                .collect {
                    userObject = it
                    callNotification()

                }
        }


    }


    sealed class DiscoverUserListEvent {
        class Success(val result: DiscoverUserListResponse) : DiscoverUserListEvent()
        class SuccessNext(val result: DiscoverUserListResponse) : DiscoverUserListEvent()
        class Failure(val errorText: String) : DiscoverUserListEvent()
        object Loading : DiscoverUserListEvent()
        object LoadingNext : DiscoverUserListEvent()
        object Empty : DiscoverUserListEvent()
    }

    private val _discoverUserListConversion =
        MutableStateFlow<DiscoverUserListEvent>(DiscoverUserListEvent.Empty)
    val discoverUserListConversion: StateFlow<DiscoverUserListEvent> = _discoverUserListConversion

    private var tempData: DiscoverFilterObject? = null
    private var pageNo = 1
    fun getDiscoverUserList(
        discoverObj: DiscoverFilterObject,
        token: String,
        logLocation: Int
    ) {

        discoverObj.isApplyFilter =
            if (discoverObj.isApplyFilter.isNullOrEmpty() && discoverObj.isApplyFilter != "1") "1" else "0"



        // pageNo = discoverObj.page?.toIntOrNull() ?: 1
        pageNo = 1


        tempData = discoverObj
        callApi(discoverObj, false, token, logLocation)
    }

    private fun callApi(data: DiscoverFilterObject, isNext: Boolean, token: String, lineNo: Int) {

        Log.d(TAG, "callApi: testFlowLineCheck>>$lineNo>>$token")

        viewModelScope.launch(Dispatchers.IO) {
            if (isNext) _discoverUserListConversion.value = DiscoverUserListEvent.LoadingNext
            else _discoverUserListConversion.value = DiscoverUserListEvent.Loading

            when (val quotesResponse =
                repository.getDiscoverUserList(token, data)) {
                is Resource.Error -> _discoverUserListConversion.value =
                    DiscoverUserListEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!

                    if (isNext) _discoverUserListConversion.value =
                        DiscoverUserListEvent.SuccessNext(quote)
                    else _discoverUserListConversion.value = DiscoverUserListEvent.Success(quote)

                }
            }
        }
    }

    fun getNextPage() {
        Log.d(TAG, "getNextPagesafsd: "+"call"+tempData)
        if (tempData != null) {
            tempData?.page = pageNo.toString()

            viewModelScope.launch {
                context.dataStoreGetUserData().firstOrNull {
                    Log.d(TAG, "getNextPage321: "+"call")
                    callApi(tempData!!, false, it.token, 117)
                    true
                }
            }
        }

    }


    sealed class SwipeEvent {
        class Success(val result: SwipeResponse) : SwipeEvent()
        class Failure(val errorText: String) : SwipeEvent()
        object Loading : SwipeEvent()
        object Empty : SwipeEvent()
    }

    private val _swipeConversion = MutableStateFlow<SwipeEvent>(SwipeEvent.Empty)
    val swipeConversion: StateFlow<SwipeEvent> = _swipeConversion

    fun swipeProfile(status: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _swipeConversion.value = SwipeEvent.Loading
            when (val call =
                repository.getSwipeProfile(tempDataObject?.token ?: "", status, userId)) {
                is Resource.Error -> {
                    _swipeConversion.value = SwipeEvent.Failure(call.message ?: "")
                }
                is Resource.Success -> {
                    if (call.data == null) {
                        _swipeConversion.value = SwipeEvent.Failure(call.message ?: "")
                    } else {
                        _swipeConversion.value = SwipeEvent.Success(call.data)
                    }
                }
            }
        }
    }


    sealed class ReviewEvent {
        class Success(val result: AddReviewResponse) : ReviewEvent()
        class Failure(val errorText: String) : ReviewEvent()
        object Loading : ReviewEvent()
        object Empty : ReviewEvent()
    }

    private val _reviewConversion = MutableStateFlow<ReviewEvent>(ReviewEvent.Empty)
    val reviewConversion: StateFlow<ReviewEvent> = _reviewConversion

    fun reviewProfile(userId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            _reviewConversion.value = ReviewEvent.Loading
            when (val call = repository.ReviewLatterProfile(tempDataObject?.token ?: "", userId)) {
                is Resource.Error -> {
                    _reviewConversion.value = ReviewEvent.Failure(call.message ?: "")
                }
                is Resource.Success -> {
                    if (call.data == null) {
                        _reviewConversion.value = ReviewEvent.Failure(call.message ?: "")
                    } else {
                        _reviewConversion.value = ReviewEvent.Success(call.data)
                    }
                }
            }
        }
    }

    suspend fun callNotification() {
        _reviewConversion.value = ReviewEvent.Loading
        when (val call = repository.readNotification(userObject?.token ?: "")) {
            is Resource.Error -> {
                _reviewConversion.value = ReviewEvent.Failure(call.message ?: "")
            }
            is Resource.Success -> {
                if (call.data == null) {
                    _reviewConversion.value = ReviewEvent.Failure(call.message ?: "")
                } else {
                    _reviewConversion.value = ReviewEvent.Success(call.data)
                }
            }
        }
    }


}