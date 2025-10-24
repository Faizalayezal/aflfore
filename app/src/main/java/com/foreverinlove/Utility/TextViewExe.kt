package com.foreverinlove.utility

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object TextViewExt {

    fun setOtpFlow(otpEt1: EditText, otpEt2: EditText, otpEt3: EditText, otpEt4: EditText) {

        otpEt1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) {
                if (otpEt1.text.toString().isEmpty()) {
                    otpEt1.context.hideKeyboard(otpEt1)
                } else {
                    otpEt2.requestFocus()
                }
            }
        })
        otpEt2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) {
                if (otpEt2.text.toString().isEmpty()) {
                    otpEt1.requestFocus()
                } else {
                    otpEt3.requestFocus()
                }
            }
        })
        otpEt3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) {
                if (otpEt3.text.toString().isEmpty()) {
                    otpEt2.requestFocus()
                } else {
                    otpEt4.requestFocus()
                }
            }
        })
        otpEt4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) {
                if (otpEt4.text.toString().isEmpty()) {
                    otpEt3.requestFocus()
                } else {
                    otpEt4.context.hideKeyboard(otpEt4)
                }
            }
        })

    }

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

    fun TextView.textColor(int: Int) {
        setTextColor(ContextCompat.getColor(context, int))
    }
}
