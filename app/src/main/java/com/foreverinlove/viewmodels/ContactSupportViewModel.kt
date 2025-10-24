package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.AddContectSupportResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactSupportViewModel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application) {

    var tempUserDataObject: TempUserDataObject? = null

    fun start() {
        viewModelScope.launch {
            context.dataStoreGetUserData().catch { it.printStackTrace() }.collect {
                tempUserDataObject = it
            }
        }
    }

    sealed class ContactSupportEvent {
        class Success(val result: AddContectSupportResponse) : ContactSupportEvent()
        class Failure(val errorText: String) : ContactSupportEvent()
        object Loading : ContactSupportEvent()
        object Empty : ContactSupportEvent()
    }

    private val _contactSupportConversion = MutableStateFlow<ContactSupportEvent>(
        ContactSupportEvent.Empty
    )
    val contactSupportConversion: StateFlow<ContactSupportEvent> = _contactSupportConversion

    fun callApiData(name: String, email: String, description: String) {
        GlobalScope.launch {
            _contactSupportConversion.value = ContactSupportEvent.Loading
            when (val quotesResponse = repository.addContactSupport(
                tempUserDataObject?.token ?: "",
                name,
                email,
                description
            )) {
                is Resource.Error -> _contactSupportConversion.value =
                    ContactSupportEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _contactSupportConversion.value = ContactSupportEvent.Success(
                        quote
                    )
                }
            }

        }
    }

}