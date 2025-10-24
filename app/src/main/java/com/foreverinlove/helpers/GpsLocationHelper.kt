package com.foreverinlove.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*

private const val TAG = "GpsLocationHelper"
@SuppressLint("MissingPermission")
class GpsLocationHelper(val activity: Activity, val onLocationFetched:(lat:String,long:String)->Unit) {

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    init {
        var latitude = "321651"

        //

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        val request = LocationRequest.create()
        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations){
                    Log.d(TAG, "onLocationResult: test$location")
                    onLocationFetched.invoke(location.latitude.toString(),location.longitude.toString())
                }
            }
        }

        mFusedLocationClient!!.requestLocationUpdates(request,
            callback,
            Looper.getMainLooper())
    }

}