package com.foreverinlove.helpers

class LatLongTrimmerHelper {
    fun trim(lat: String, long: String, result: (newLat: String, newLong: String) -> Unit) {
        var latStr = lat
        var longStr = long

        val strOne = latStr.split(".").lastOrNull() ?: ""
        val lengthOne = strOne.length
        latStr = latStr.replace(strOne, "")

        val strTwo = longStr.split(".").lastOrNull() ?: ""
        val lengthTwo = strTwo.length
        longStr = longStr.replace(strTwo, "")

        if (lengthOne == 0) latStr += "000000"
        else if (lengthOne == 1) latStr += strOne + "00000"
        else if (lengthOne == 2) latStr += strOne + "0000"
        else if (lengthOne == 3) latStr += strOne + "000"
        else if (lengthOne == 4) latStr += strOne + "00"
        else if (lengthOne == 5) latStr += strOne + "0"
        else if (lengthOne == 6) latStr += strOne
        else if (lengthOne > 6) latStr += strOne.take(6)

        if (lengthTwo == 0) longStr += "000000"
        else if (lengthTwo == 1) longStr += strTwo + "00000"
        else if (lengthTwo == 2) longStr += strTwo + "0000"
        else if (lengthTwo == 3) longStr += strTwo + "000"
        else if (lengthTwo == 4) longStr += strTwo + "00"
        else if (lengthTwo == 5) longStr += strTwo + "0"
        else if (lengthTwo == 6) longStr += strTwo
        else if (lengthTwo > 6) longStr += strTwo.take(6)

        result(latStr, longStr)
    }
}