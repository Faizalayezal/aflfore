package com.foreverinlove.utility

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//Context get krva mate
@HiltViewModel
open class BaseViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    protected val context
    get() = getApplication<Application>()
}

//bethebethu