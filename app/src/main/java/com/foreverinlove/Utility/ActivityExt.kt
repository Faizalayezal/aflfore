package com.foreverinlove.utility

import android.app.Activity
import android.content.Context
import android.content.Intent

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.foreverinlove.SignInActivity
import kotlinx.coroutines.runBlocking


object ActivityExt {




    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.handleSessionExpired(){
        runBlocking {
            showToast("Sorry your session has expired. Please login again.",true)

            dataStoreClearAll()

            this@handleSessionExpired.startActivity(Intent(this@handleSessionExpired,
                SignInActivity::class.java))
        }
    }

    fun Activity.showToast(string: String?,boolean: Boolean=false) {

        if(string == "An Error occurred")return

        if(string!=null) {
            if(boolean)
            Toast.makeText(this, string, Toast.LENGTH_LONG).show()
            else Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
        }

    }



}