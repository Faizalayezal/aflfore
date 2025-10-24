package com.foreverinlove.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.foreverinlove.network.model.Resource
import com.foreverinlove.network.repository.MainRepository
import com.foreverinlove.objects.TempUserDataObject
import com.foreverinlove.utility.BaseViewModel
import com.foreverinlove.utility.dataStoreGetUserData
import com.foreverinlove.utility.dataStoreSetUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhaseViewModel @Inject constructor(
    application: Application, private val repository: MainRepository
) : BaseViewModel(application) {


    var tempUserDataObject: TempUserDataObject? = null
    private var isFinalPhase = false

    fun start(isFinalPhase: Boolean = false) {
        this.isFinalPhase = isFinalPhase
        viewModelScope.launch {
            context.dataStoreGetUserData()
                .catch { it.printStackTrace() }
                .collect {

                    tempUserDataObject = it

                }
        }
    }


    fun updateUserData(
        data: TempUserDataObject,
        onCompleted: ((isSuccess: Boolean) -> Unit)? = null,
    ) {

        viewModelScope.launch {
            Log.d("TAG", "updateUserData321: "+data.ukeysmoking)
            Log.d("TAG", "updateUserData321: "+data.ukeydrugs)
            Log.d("TAG", "updateUserData321: "+data.ukeydrink)

            tempUserDataObject?.let { context.dataStoreSetUserData(data) }

            if (isFinalPhase) {
                when (val response = repository.updatePhaseData(

                    education = data.ukeyeducation,
                    lookingFor = data.ukeylooking_for,
                    dietaryLifestyle = data.ukeydietary_lifestyle,
                    pets = data.ukeypets,
                    arts = data.ukeyarts,
                    language = data.ukeylanguage,
                    interests = data.ukeyinterests,
                    drink = data.ukeydrink,
                    drugs = data.ukeydrugs,
                    horoscope = data.ukeyhoroscope,
                    religion = data.ukeyreligion,
                    politicalLeaning = data.ukeypolitical_leaning,
                    relationshipStatus = data.ukeyrelationship_status,
                    lifeStyle = data.ukeylife_style,
                    firstDateIceBreaker = data.ukeyfirst_date_ice_breaker,
                    covidVaccine = data.ukeycovid_vaccine,
                    smoking = data.ukeysmoking,

                    token = data.token,

                    firstName = data.first_name,
                    lastName = data.last_name,
                    dob = data.dob,
                    email = data.email,
                    gender = data.gender,
                    jobTitle = data.job_title,
                    about = data.about,
                    lookingForQuery = data.ukeylooking_for,
                    usersLookingForQuery = data.ukeylooking_for,
                )) {
                    is Resource.Error -> {
                        onCompleted?.invoke(false)
                    }
                    is Resource.Success -> {
                        onCompleted?.invoke(true)
                    }
                }
            }


        }

    }


}