package com.foreverinlove.utility

import android.content.Context
import android.content.SharedPreferences

class PrefrenceManager(var _context: Context) {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor

    // shared pref mode
    var PRIVATE_MODE = 0

    // This method to be used as soon as the fist time launch is completed to update the
    // shared preference
    // This method will return true of the app is launched for the first time. false if
    // launched already
    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }

    companion object {
        // Shared preferences file name
        private const val PREF_NAME = "splash-welcome"

        // Shared preference variable name
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
    }

    // Constructor
    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }
}
