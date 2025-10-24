package com.foreverinlove.objects

import com.foreverinlove.dialog.ChipGroupHelper
import com.foreverinlove.network.response.AddtionalQueObject

data class PhaseListObject (
    var name : String?=null,
    var list: List<AddtionalQueObject>?=null,
    var phase: ChipGroupHelper.StyleTypes,
    var alreadySelectedIds: String = "",
    var maxSelected: Int = 2
)

