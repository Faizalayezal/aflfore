package com.foreverinlove.utility

import com.foreverinlove.network.response.AddtionalQueListData
import com.foreverinlove.network.response.GenderObject
import com.foreverinlove.network.response.PendingPopupData

object MyListDataHelper {

    private var allData : AddtionalQueListData? = null

    fun setAllData(allData : AddtionalQueListData){
        this.allData=allData
    }

    fun getAllData() = allData


    private var allDataGender : List<GenderObject>? = null

    fun setAllDataGender(allData : List<GenderObject>){
        this.allDataGender=allData
    }

    fun getAllDataGender() = allDataGender
}
object PopupListHelper {

    private var allData : List<PendingPopupData>? = null

    fun setAllData(allData : List<PendingPopupData>?){
        this.allData=allData
    }

    fun getAllData() = allData

    fun removeSinglePopupData(screenId:String){
        allData?.forEachIndexed { index, pendingPopupData->
            val item = pendingPopupData.screen_data?.find { it.screen_id == (screenId.toIntOrNull()?:-1) }
            item?.let{
                val indexTwo = pendingPopupData.screen_data.indexOf(it)

                allData?.getOrNull(index)?.screen_data?.getOrNull(indexTwo)?.id = null
                allData?.getOrNull(index)?.screen_data?.getOrNull(indexTwo)?.popup_id = null
                allData?.getOrNull(index)?.screen_data?.getOrNull(indexTwo)?.screen_id = null
                allData?.getOrNull(index)?.screen_data?.getOrNull(indexTwo)?.created_at = null
                allData?.getOrNull(index)?.screen_data?.getOrNull(indexTwo)?.updated_at = null
                allData?.getOrNull(index)?.screen_data?.getOrNull(indexTwo)?.name = null
                allData?.getOrNull(index)?.screen_data?.getOrNull(indexTwo)?.description = null
                allData?.getOrNull(index)?.screen_data?.getOrNull(indexTwo)?.icon = null
                allData?.getOrNull(index)?.screen_data?.getOrNull(indexTwo)?.txt_color = null
                allData?.getOrNull(index)?.screen_data?.getOrNull(indexTwo)?.bg_color = null

            }
        }
    }
}

//MyListDataHelper.getAllData()