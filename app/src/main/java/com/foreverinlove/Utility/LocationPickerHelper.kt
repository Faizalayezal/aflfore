package com.foreverinlove.utility

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.foreverinlove.R
import com.foreverinlove.helpers.LatLongTrimmerHelper
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

private const val TAG = "LocationPickerHelper"

class LocationPickerHelper {

    var activity: Activity? = null

    fun initialize(activity: Activity) {
        this.activity = activity
        val apiKey = activity.getString(R.string.google_search_api_key)
        if (!Places.isInitialized()) {
            Places.initialize(activity, apiKey)
        }
        Places.createClient(activity)
    }

    fun openLocationPicker(resultLauncher: ActivityResultLauncher<Intent>) {
        if (activity == null) {
            return
        }

        val fields: List<Place.Field> = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        val intent: Intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        ).build(activity!!)
        //.setCountry("NG") //NIGERIA
        resultLauncher.launch(intent)
    }

    fun getDataFromResult(
        result: ActivityResult?, setResult: (address: String, lat: String, long: String) -> Unit
    ) {
        val data: Intent? = result?.data

        data?.let {
            val place = Autocomplete.getPlaceFromIntent(data)

            var address = place.address ?: ""
            address = removeLocationCodeFromAddress(address)

            val latStr = (place.latLng?.latitude ?: 0.0).toString()
            val longStr = (place.latLng?.longitude ?: 0.0).toString()


            //. pchhi na 6 point j jay "LatLongTrimmerHelper"
            LatLongTrimmerHelper().trim(latStr, longStr) { newLat, newLong ->
                setResult(
                    address,
                    newLat,
                    newLong
                )
                Log.d(TAG, "Place: 166>>$newLat, $newLong")
            }

        }
    }

    private fun removeLocationCodeFromAddress(string: String): String {
        var address = string
        //removing 7RR2+9RC place code from address
        val list = address.split(",")

        list.forEach { s ->
            Log.d(
                TAG,
                "getDataFromResult: testlogic>>" + s.getOrNull(4) + ">>" + s.getOrNull(5) + ">>" + s
            )

            if ((s.getOrNull(4) ?: "").toString() == "+") {
                address = address.replace(s, "")
            } else if ((s.getOrNull(5) ?: "").toString() == "+") {
                address = address.replace(s, "")
            }

        }
        address = address.replaceFirst(",,", "")
        if (address.getOrNull(0).toString() == ",") {
            address = address.replaceFirst(", ", "")
        }
        return address
    }
}