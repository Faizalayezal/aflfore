package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.ApplictionClass
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.CurrentFreeUserPlanResponse
import com.foreverinlove.network.response.CurrentUserPlanResponse
import com.foreverinlove.network.response.SubscriptionPlanListResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionPlanListViewModel @Inject constructor(
    application: Application,
    private val repository: MainRepository
) : BaseViewModel(application) {

    private var userDataObject: TempUserDataObject? = null

    fun getCurrentUserPlan() {
        viewModelScope.launch {
            context.dataStoreGetUserData().firstOrNull {
                userDataObject = it

                getUserCurrentPlan(userDataObject?.token ?: "")
                getSubscriptionPlanList(userDataObject?.token ?: "")

                true
            }
        }
    }


    sealed class SubscriptionPlanListEvent {
        class Success(val result: SubscriptionPlanListResponse) : SubscriptionPlanListEvent()
        class Failure(val errorText: String) : SubscriptionPlanListEvent()
        object Loading : SubscriptionPlanListEvent()
        object Empty : SubscriptionPlanListEvent()
    }

    private val _subscriptionPlanReason = MutableStateFlow<SubscriptionPlanListEvent>(
        SubscriptionPlanListEvent.Empty
    )
    val subscriptionPlanReason: StateFlow<SubscriptionPlanListEvent> = _subscriptionPlanReason
    private fun getSubscriptionPlanList(token: String) {
        GlobalScope.launch(Dispatchers.IO) {
            _subscriptionPlanReason.value = SubscriptionPlanListEvent.Loading
            when (val quotesResponse = repository.getSubscriptionPlanList(token)) {
                is Resource.Error -> _subscriptionPlanReason.value =
                    SubscriptionPlanListEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!


                    (context as ApplictionClass).setListOfSubscriptionList(quote)

                    _subscriptionPlanReason.value = SubscriptionPlanListEvent.Success(
                        quote
                    )
                }
            }
        }
    }

    sealed class GetUserCurrentPlan {
        class Success(val result: CurrentUserPlanResponse) : GetUserCurrentPlan()
        class Failure(val errorText: String) : GetUserCurrentPlan()
        object Loading : GetUserCurrentPlan()
        object Empty : GetUserCurrentPlan()
    }

    private val _getUserCurrentPlanFlow = MutableStateFlow<GetUserCurrentPlan>(
        GetUserCurrentPlan.Empty
    )
    val getUserCurrentPlanFlow: StateFlow<GetUserCurrentPlan> = _getUserCurrentPlanFlow

    private fun getUserCurrentPlan(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _getUserCurrentPlanFlow.value = GetUserCurrentPlan.Loading
            when (val quotesResponse = repository.getCurrentUserPlan(token)) {
                is Resource.Error -> _getUserCurrentPlanFlow.value =
                    GetUserCurrentPlan.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!

                    _getUserCurrentPlanFlow.value = GetUserCurrentPlan.Success(
                        quote
                    )
                }
            }
        }
    }



    sealed class GetFreePlan {
        class Success(val result: CurrentFreeUserPlanResponse) : GetFreePlan()
        class Failure(val errorText: String) : GetFreePlan()
        object Loading : GetFreePlan()
        object Empty : GetFreePlan()
    }

    private val _getFreePlanFlow = MutableStateFlow<GetFreePlan>(
        GetFreePlan.Empty
    )
    val getFreePlanFlow: StateFlow<GetFreePlan> = _getFreePlanFlow

     fun getFreePlan() {
        viewModelScope.launch(Dispatchers.IO) {
            _getFreePlanFlow.value = GetFreePlan.Loading
            when (val quotesResponse = repository.freePlan(userDataObject?.token ?: "")) {
                is Resource.Error -> _getFreePlanFlow.value =GetFreePlan
                    .Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!

                    _getFreePlanFlow.value = GetFreePlan.Success(
                        quote
                    )
                }
            }
        }
    }


}