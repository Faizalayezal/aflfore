package com.foreverinlove.chatmodual

import android.text.format.DateUtils
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


object FormatDateUseCase {

    fun getYearFromDate(dateStr: String): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = formatter.parse(dateStr)
        val diff = System.currentTimeMillis() - (date?.time ?: 0)

        return (diff / 31_556_952_000).toInt().toString()
    }

    fun getDifferenceOfYears(
        dateStr1: String,
        timeStr1: String,
        dateStr2: String,
        timeStr2: String
    ): Long {

        Log.d("TAG", "getDifferenceOfYears: testsajhd>>$dateStr1>>$timeStr1>>$dateStr2>>$timeStr2")

        val formatter1 = SimpleDateFormat("dd-MM-yyyy")
        val date1 = formatter1.parse(dateStr1)
        val date2 = formatter1.parse(dateStr2)

        val formatter2 = SimpleDateFormat("hh:mm aa")
        val time1 = formatter2.parse(timeStr1)
        val time2 = formatter2.parse(timeStr2)

        return ((((date2?.time ?: 0) + (time2?.time ?: 0)) - ((date1?.time ?: 0) + (time1?.time
            ?: 0))))
    }

    fun getMills(dateStr: String): Long {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = formatter.parse(dateStr)

        return date?.time ?: 0
    }

    fun Long.toDateString(): String {
        val df = SimpleDateFormat("dd-MM-yyyy")
        return df.format(this)
    }

    fun Long.toTimeString(): String {
        val df = SimpleDateFormat("hh:mm aa")
        return df.format(this)
    }

    fun getTimeAgoFromDate(dateStr: String): String {
        try {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val date = formatter.parse(dateStr)
            val c = Calendar.getInstance()
            c.timeZone = TimeZone.getTimeZone("UTC")

            val ago = DateUtils.getRelativeTimeSpanString(
                date?.time ?: 0,
                c.timeInMillis,
                DateUtils.DAY_IN_MILLIS
            ).toString()

            return if (ago == "Today" || ago == "Yesterday") ago
            else getDate(date?.time ?: 0, "dd-MM-yyyy") ?: ""

            /* ago = ago.replace("hours", "hrs")
             ago = ago.replace("hour", "hr")
              ago = ago.replace("minutes", "mins")
              ago = ago.replace("minute", "min")*/
            /*if(ago=="In 1 min" || ago=="In 0 mins" || ago=="0 mins ago"){
            ago="Just now"
        }*/

            Log.d("TAG", "cc: $ago")

        } catch (e: Exception) {
            Log.d("TAG", "getTimeAgoFromDate: erroe while getting date")
            return ""
        }
    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun Long.getTimeAgoFromLong(): String {

        val ago = DateUtils.getRelativeTimeSpanString(
            this,
            System.currentTimeMillis(),
            DateUtils.DAY_IN_MILLIS
        ).toString()
//MINUTE_IN_MILLIS
        Log.d("TAG", "cccddd: $ago")
        return if(ago=="Today" || ago=="Yesterday") ago
        else getDate(this,"dd-MM-yyyy")?:""

        Log.d("TAG", "cdddccddd: $ago")

      /*  ago = ago.replace("hours", "hrs")
        ago = ago.replace("hour", "hr")
        ago = ago.replace("minutes", "mins")
        ago = ago.replace("minute", "min")*/

    }

    fun getTimeAgoFromDateOnly(dateStr: String): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = formatter.parse(dateStr)

        val c = Calendar.getInstance()
        c.timeZone = TimeZone.getTimeZone("UTC")

        val ago = DateUtils.getRelativeTimeSpanString(
            date?.time ?: 0,
            c.timeInMillis,
            DateUtils.DAY_IN_MILLIS
        ).toString()

      /*  if (ago.contains("hours") ||
            ago.contains("hour") ||
            ago.contains("minutes") ||
            ago.contains("minute") ||
            ago.contains("second") ||
            ago.contains("seconds")
        ) {
            ago = "Today"
        }*/

        return if(ago=="Today" || ago=="Yesterday") ago
        else getDate(date?.time ?: 0,"dd-MM-yyyy")?:""
    }

}