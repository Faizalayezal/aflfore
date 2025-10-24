package com.foreverinlove.dialog

import android.app.Activity
import android.app.DatePickerDialog
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateOfBirthDailog {

    fun Activity.openDateOfBirthDailog(editText: EditText, onOpen: (() -> Unit)? = null) {

        // val format = "dd-MM-yyyy"
        val format = "MM-dd-yyyy"

        val longMin: Long = 31556952000 * 18
        val minimumAge = (Calendar.getInstance().timeInMillis - longMin)
        val cal = Calendar.getInstance()
        cal.timeInMillis = minimumAge

        val maxDate = cal.time

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.US)
                editText.setText(sdf.format(myCalendar.time))
            }

        editText.setOnClickListener {
            onOpen?.invoke()
            DatePickerDialog(
                this, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate.time.also { datePicker.maxDate = it }
                show()
            }
        }

    }
}