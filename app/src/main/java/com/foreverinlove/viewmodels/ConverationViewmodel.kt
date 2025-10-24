package com.foreverinlove.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.network.response.GetmessageconversationResponse
import com.foreverinlove.network.response.MessageConversationList
import com.foreverinlove.network.response.OldMessageListResponse
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.google.firebase.database.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ConverationViewmodel"
@HiltViewModel
class ConverationViewmodel @Inject constructor(
    val repository: MainRepository,
    application: Application
) : BaseViewModel(application){
    var tempUserDataObject: TempUserDataObject? = null
    private var isFirstTime=true

    private lateinit var firebaseInstance: DatabaseReference
    fun start() {
        firebaseInstance = FirebaseDatabase.getInstance().reference
        isFirstTime=true
        viewModelScope.launch {
            context.dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {
                    tempUserDataObject=it
                    if(isFirstTime) {
                        isFirstTime=false
                        callApiData()
                        getOldUserMessageList()
                    }
                }
        }
    }

    sealed class ConversationResponseEvent{
        class Success(val result: GetmessageconversationResponse): ConversationResponseEvent()
        class Failure(val errorText:String): ConversationResponseEvent()
        object Loading: ConversationResponseEvent()
        object Empty: ConversationResponseEvent()
    }

    //mutuble value edit mate
    private val _conversionListConversion = MutableStateFlow<ConversationResponseEvent>(
        ConversationResponseEvent.Empty)
    val conversionListConversion: StateFlow<ConversationResponseEvent> = _conversionListConversion

    private fun callApiData() {
        viewModelScope.launch {
            _conversionListConversion.value = ConversationResponseEvent.Loading
            when(val quotesResponse = repository.getMessageConverterList(tempUserDataObject?.token?:"")) {
                is Resource.Error -> _conversionListConversion.value =
                    ConversationResponseEvent.Failure(quotesResponse.message!!)
                is Resource.Success -> {
                    val quote = quotesResponse.data!!
                    _conversionListConversion.value = ConversationResponseEvent.Success(
                        quote
                    )
                }
            }

        }
    }

    sealed class NewMessagesUserListEvent {
        class Success(val result: OldMessageListResponse) : NewMessagesUserListEvent()
        class Failure(val errorText: String) : NewMessagesUserListEvent()
        object Loading : NewMessagesUserListEvent()
        object Empty : NewMessagesUserListEvent()
    }

    private val _newMessagesUserListConversion = MutableStateFlow<NewMessagesUserListEvent>(
        NewMessagesUserListEvent.Empty)
    val newMessagesUserListConversion: StateFlow<NewMessagesUserListEvent> = _newMessagesUserListConversion




    fun getOldUserMessageList() {
        viewModelScope.launch(Dispatchers.IO) {
            _newMessagesUserListConversion.value = NewMessagesUserListEvent.Loading
            when (val data = repository.getUserMessagesList(tempUserDataObject?.token ?: "")) {
                is Resource.Error -> _newMessagesUserListConversion.value =
                    NewMessagesUserListEvent.Failure(data.message!!)
                is Resource.Success -> {
                    _newMessagesUserListConversion.value =NewMessagesUserListEvent.Success(
                        data.data!!
                    )
                }
            }
        }
    }
   // var status = OnlineStatusObj("", "")

    // online offline green dot
    private var onlineStatusFlowJob: Job?=null
    fun getOnlineStatusOfUsers(conversationLists: List<MessageConversationList>,onDataReceived:(List<MessageConversationList>)->Unit) {
        onlineStatusFlowJob?.cancel()
        onlineStatusFlowJob = viewModelScope.launch {
            conversationLists.forEachIndexed { index, messageConversationList ->
                findStatusFromFireBase(messageConversationList.user_id.toString()){
                    conversationLists[index].isUserOnline = it
                    onDataReceived.invoke(conversationLists)
                }
            }
        }
    }

    private fun findStatusFromFireBase(otherUserId: String,onDataReceived:(String)->Unit){
        firebaseInstance.child("onlineStatus").child(otherUserId).child("status").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val result = snapshot.getValue(String::class.java)
                    result?.let{
                        onDataReceived.invoke(it)
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



}