package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptonPlan2ViewModel @Inject constructor(val repository: MainRepository, application: Application) :
    BaseViewModel(application) {

    private var userDataObject: TempUserDataObject?=null
    fun start(){
        viewModelScope.launch {
            context.dataStoreGetUserData().firstOrNull {

                userDataObject = it

                true
            }
        }
    }

    fun purchaseBasicPlan(planId:String="2",coins:String="0",
        onResponse:(isSuccess:Boolean)->Unit
                          ) {
        viewModelScope.launch {
            when(val response = repository.purchasePlan(planId=planId,
                coins=coins,
                token=userDataObject?.token?:"")){
                is Resource.Error -> {
                    onResponse.invoke(false)
                }
                is Resource.Success -> {
                    if(response.data?.status == 1)
                        onResponse.invoke(true)
                    else onResponse.invoke(false)
                }
            }
        }
    }


}