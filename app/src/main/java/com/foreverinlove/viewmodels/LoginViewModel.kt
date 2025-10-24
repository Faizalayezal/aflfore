package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.SignInResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreSetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: MainRepository, application: Application
) : BaseViewModel(application) {


    sealed class GetLoginEvent {
        class Success(val result: SignInResponse) : GetLoginEvent()
        class Failure(val errorText: String) : GetLoginEvent()
        object Loading : GetLoginEvent()
        object Empty : GetLoginEvent()
    }

    private val _loginConversion = MutableStateFlow<GetLoginEvent>(GetLoginEvent.Empty)
    val loginConversion: StateFlow<GetLoginEvent> = _loginConversion

    fun getLoginStatus(phone: String) {

        viewModelScope.launch {
            _loginConversion.value = GetLoginEvent.Loading
            when (val data = repository.getSignIn(phone)) {
                is Resource.Error -> {
                    _loginConversion.value = GetLoginEvent.Failure(data.message!!)
                }
                is Resource.Success -> {

                    if (data.data?.status == 1) {
                        viewModelScope.launch {
                            val tempData = TempUserDataObject(
                                login_otp = data.data.data?.login_otp ?: "",
                                token_type = data.data.data?.token_type ?: "",
                                session_id = data.data.data?.session_id ?: "",
                                token = data.data.data?.token ?: "",
                                userStatus = data.data.data?.userStatus ?: "",
                                phone = phone,
                            )
                            context.dataStoreSetUserData(tempData)
                        }

                        _loginConversion.value = GetLoginEvent.Success(
                            data.data
                        )
                    } else {
                        _loginConversion.value = GetLoginEvent.Failure(
                            data.data?.message ?: ""
                        )
                    }


                }
            }

        }

    }


}