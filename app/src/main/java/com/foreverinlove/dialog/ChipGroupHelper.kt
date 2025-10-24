package com.foreverinlove.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.foreverinlove.R
import com.foreverinlove.network.Utility.selectedIdd1
import com.foreverinlove.network.Utility.selectedIdd2
import com.foreverinlove.network.Utility.selectedIdd3
import com.foreverinlove.network.response.AddtionalQueObject
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.TextViewExt.textColor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ChipGroupHelper"


@SuppressLint("InflateParams")
class ChipGroupHelper {
    enum class StyleTypes {
        Phase1,
        Phase2,
        Phase3,
        Phase4,
        PhaseAddtional,
    }

    interface ChipSelectedListener {
        fun onSelectedListChange(list: ArrayList<String>)
    }

    interface UpdateItemRemovalListener {
        fun onItemRemoved(name: String, list: ArrayList<String>?)
    }


    sealed class ListType {
        class ParentList(val listOfInterests: List<AddtionalQueObject>) : ListType()
        class ChildList(val listOfChildHobbies: List<AddtionalQueObject>) : ListType()
    }

    class Builder(private val activity: Activity) {
        /*
        .setChipLayout()
        .setSelectedListener()
        .setList()
        .setCountTextView()
        .setAlreadySelectedIds()
        .setBottomSheet()
        .setClickable()
        .setMaxSelected
        .setIsCurrentBuilderChild()
        .setCloseIconVisible()
        .setRemoveListener()
        .setStyleColor()
        .setViewsForClickedNewInterestPicker()
        .build()
        */

        private var selectedCount = 0
        private val selectedItemsList = ArrayList<String>()
        private var checkChanging = false

        private lateinit var listType: ListType
        private var txtCount: TextView? = null
        private var selectedId1 = ""
        private var selectedId2 = ""
        private var selectedId3 = ""
        private var isBottomSheet = false
        private var isOpneSheet = false
        private lateinit var chipGroupMain: ChipGroup
        private var listener: ChipSelectedListener? = null

        private var isClickable = true
        private var isCloseIconVisible = false
        private var itemRemoveListener: UpdateItemRemovalListener? = null
        private var llInterests: View? = null
        private var titleString: String? = null
        private var open: Boolean? = null
        private var isCurrentBuilderChild = false

        fun setChipLayout(chipGroup: ChipGroup): Builder {
            this.chipGroupMain = chipGroup
            return this
        }

        var color: StyleTypes? = null
        fun setStyleColor(color: StyleTypes): Builder {
            this.color = color
            return this
        }

        fun setSelectedListener(listener: ChipSelectedListener?): Builder {
            this.listener = listener
            return this
        }

        fun setList(listOfInterests: ListType): Builder {
            this.listType = listOfInterests
            return this
        }

        fun setCountTextView(txtCount: TextView?): Builder {
            this.txtCount = txtCount
            return this
        }


        fun setAlreadySelectedIds(
            selectedId1: String,
            selectedId2: String,
            selectedId3: String
        ): Builder {
            this.selectedId1 = selectedId1
            this.selectedId2 = selectedId2
            this.selectedId3 = selectedId3

            return this
        }


        fun setBottomSheet(isBottomSheet: Boolean): Builder {
            this.isBottomSheet = isBottomSheet
            return this
        }

        fun setClickable(b: Boolean): Builder {
            isClickable = b
            return this
        }

        fun setIsCurrentBuilderChild(b: Boolean): Builder {
            isCurrentBuilderChild = b
            return this
        }

        var maxSelectedCount = 0
        fun setMaxSelected(b: Int): Builder {
            maxSelectedCount = b
            return this
        }

        fun setCloseIconVisible(b: Boolean): Builder {
            isCloseIconVisible = b
            return this
        }

        private var isEditProfileFlow = false
        fun setIsEditProfileFlow(b: Boolean): Builder {
            isEditProfileFlow = b
            return this
        }

        var showAllAtFirst = true
        fun setAllShowOnFirst(b: Boolean = true): Builder {
            showAllAtFirst = b
            return this
        }

        fun setRemoveListener(itemRemoveListener: UpdateItemRemovalListener?): Builder {
            this.itemRemoveListener = itemRemoveListener
            return this
        }

        fun setViewsForClickedNewInterestPicker(llInterests: View?, titleString: String?): Builder {
            this.llInterests = llInterests
            this.titleString = titleString
            return this
        }


        fun build(): Builder {

            if (!this::chipGroupMain.isInitialized) return this

            chipGroupMain.removeAllViews()
            // txtCount?.text = "0/3"
            // txtCount?.textColor(R.color.c515151cffffff)
            selectedItemsList.clear()
            selectedCount = 0

            Log.d(TAG, "build: testFlowForever>>187")
            if (!isClickable) {
                chipGroupMain.setOnCheckedStateChangeListener { group, _ ->
                    for (index in 0 until group.childCount) {
                        Log.d(TAG, "build: testFlowForever>>191>>$index")
                        try {
                            val chip: Chip = group.getChildAt(index) as Chip


                            chip.isChecked = true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    //logIt("transformIntoInterestPicker: testgAJbfsdjk>>")
                }
            }

            listType.let {
                when (it) {
                    is ListType.ChildList -> {
                        if (showAllAtFirst) {
                            for (i in it.listOfChildHobbies.indices) {
                                val newChip =
                                    buildChip(it.listOfChildHobbies[i].title ?: "")
                                CoroutineScope(Dispatchers.Main).launch {
                                    if (newChip != null) {
                                        chipGroupMain.addView(newChip)
                                        logThisLine(211)
                                        if ((it.listOfChildHobbies[i].id
                                                ?: 0).toString() == selectedId1 ||
                                            (it.listOfChildHobbies[i].id
                                                ?: 0).toString() == selectedId2 ||
                                            (it.listOfChildHobbies[i].id
                                                ?: 0).toString() == selectedId3
                                        ) {
                                            newChip.performClick()
                                        }
                                    }
                                }

                            }
                        } else if (isEditProfileFlow) {
                            for (i in it.listOfChildHobbies.indices) {
                                val newChip =
                                    buildChip(it.listOfChildHobbies[i].title ?: "")
                                CoroutineScope(Dispatchers.Main).launch {
                                    Log.d(
                                        "TAG",
                                        "build: testflowchipChild>>" + it.listOfChildHobbies[i].title + ">>" + llInterests + ">>" + newChip?.text.toString() + ">>${it.listOfChildHobbies[i].id}>>${selectedId1}"
                                    )

                                    if (newChip != null) {

                                        if ((it.listOfChildHobbies[i].id
                                                ?: 0).toString() == selectedId1 ||
                                            (it.listOfChildHobbies[i].id
                                                ?: 0).toString() == selectedId2 ||
                                            (it.listOfChildHobbies[i].id
                                                ?: 0).toString() == selectedId3
                                        ) {
                                            newChip.isChecked = true
                                            newChip.isClickable = false
                                            newChip.isFocusable = false
                                            chipGroupMain.addView(newChip)
                                            logThisLine(246)
                                        }
                                    }
                                }

                            }
                        }
                    }

                    is ListType.ParentList -> {
                        for (i in it.listOfInterests.indices) {
                            val newChip = buildChip(it.listOfInterests[i].title)
                            if (newChip != null) {
                                Log.d(TAG, "build: testflowabbb219>>${it.listOfInterests[i].id}")
                                chipGroupMain.addView(newChip)
                                logThisLine(260)
                                if ((it.listOfInterests[i].id ?: 0).toString() == selectedId1 ||
                                    (it.listOfInterests[i].id ?: 0).toString() == selectedId2 ||
                                    (it.listOfInterests[i].id ?: 0).toString() == selectedId3
                                ) {
                                    newChip.performClick()
                                }
                            }
                        }
                    }


                }
            }


            llInterests?.let { thisView ->
                var listTop: java.util.ArrayList<String>? = ArrayList()

                if (selectedId1 != "") listTop?.add(selectedId1)
                if (selectedId2 != "") listTop?.add(selectedId2)
                if (selectedId3 != "") listTop?.add(selectedId3)

                Log.d(
                    "TAG",
                    "build: testflow>>207>>$selectedId1>>$selectedId2>>$selectedId3>>" + listTop?.size
                )
                val bottomSheetView =
                    activity.layoutInflater.inflate(R.layout.bottomlookingfor, null)
                val bottomSheetDialog =
                    BottomSheetDialog(activity, R.style.AppBottomSheetDialogTheme)

                bottomSheetDialog.setContentView(bottomSheetView)
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView.parent as View)

                val chipGroup = bottomSheetDialog.findViewById<ChipGroup>(R.id.chipGroup)
                val txtSave = bottomSheetDialog.findViewById<TextView>(R.id.txtSave)
                //  val txtCount = bottomSheetDialog.findViewById<TextView>(R.id.txtCount)
                val txtTitle = bottomSheetDialog.findViewById<TextView>(R.id.txtTitle)

                if (listTop != null && listTop.size != 0 && !isEditProfileFlow) {
                    this.chipGroupMain.removeAllViews()
                    for (i in listTop.indices) {
                        if (listTop[i] != "") {

                            //buildChip(listTop[i])
                            color?.let { types ->
                                val chip = when (types) {
                                    StyleTypes.Phase1 -> {
                                        activity.layoutInflater.inflate(
                                            R.layout.itemintrestgrid,
                                            this.chipGroupMain,
                                            false
                                        ) as Chip
                                    }

                                    StyleTypes.Phase2 -> {
                                        activity.layoutInflater.inflate(
                                            R.layout.itemintrestgrid2,
                                            this.chipGroupMain,
                                            false
                                        ) as Chip
                                    }

                                    StyleTypes.Phase3 -> {
                                        activity.layoutInflater.inflate(
                                            R.layout.itemintrestgrid3,
                                            this.chipGroupMain,
                                            false
                                        ) as Chip
                                    }

                                    StyleTypes.Phase4 -> {
                                        activity.layoutInflater.inflate(
                                            R.layout.itemintrestgrid4,
                                            this.chipGroupMain,
                                            false
                                        ) as Chip
                                    }

                                    StyleTypes.PhaseAddtional -> {
                                        activity.layoutInflater.inflate(
                                            R.layout.itemintrestgrid_additonal,
                                            this.chipGroupMain,
                                            false
                                        ) as Chip
                                    }

                                }


                                when (val tempList = listType) {
                                    is ListType.ChildList -> {
                                        tempList.listOfChildHobbies.find {
                                            it.id.toString() == listTop!![i]
                                        }?.let { findItem ->
                                                chip.text = findItem.title
                                            }

                                       val ids= tempList.listOfChildHobbies.find {
                                            it.id.toString() == listTop!![i]
                                        }?.id

                                        Log.d(TAG, "buisdfsdfsdld: "+ids)
                                    }

                                    is ListType.ParentList -> {
                                        chip.text = listTop!![i]
                                    }

                                }

                                chip.textColor(R.color.white)
                                chip.closeIconTint =
                                    ContextCompat.getColorStateList(
                                        activity,
                                        R.color.chip_background_state_bottom2
                                    )
                                //chip.closeIcon = ContextCompat.getDrawable(activity, R.drawable.icon_feather_plus_circle)
                                //chip.closeIconSize = activity.resources.getDimension(R.dimen.closeImageSize)
                                chip.isChecked = true
                                chip.isCloseIconVisible = isCloseIconVisible

                                chip.setOnCloseIconClickListener {
                                    if (!isClickable) {
                                        if (this.chipGroupMain.childCount == 1) {
                                            activity.showToast("minimum 3 selected")
                                        } else {
                                            itemRemoveListener?.onItemRemoved(
                                                chip.text.toString(),
                                                listTop
                                            )
                                            this.chipGroupMain.removeView(chip)
                                        }
                                    } else {
                                        this.chipGroupMain.removeView(chip)
                                    }
                                }
                                logThisLine(370)
                                this.chipGroupMain.addView(chip)
                            }
                        }
                    }
                }


                txtSave?.setOnClickListener {

                    this.chipGroupMain.removeAllViews()

                    val tempTopList = ArrayList<String>()
                    val tempTopListID = ArrayList<Int>()

                    if (listTop != null) {

                        selectedId1 = ""
                        selectedId2 = ""
                        selectedId3 = ""

                        for (i in listTop!!.indices) {

                            color?.let {
                                val chip = when (it) {
                                    StyleTypes.Phase1 -> {
                                        activity.layoutInflater.inflate(
                                            R.layout.itemintrestgrid,
                                            this.chipGroupMain,
                                            false
                                        ) as Chip
                                    }

                                    StyleTypes.Phase2 -> {
                                        activity.layoutInflater.inflate(
                                            R.layout.itemintrestgrid2,
                                            this.chipGroupMain,
                                            false
                                        ) as Chip
                                    }

                                    StyleTypes.Phase3 -> {
                                        activity.layoutInflater.inflate(
                                            R.layout.itemintrestgrid3,
                                            this.chipGroupMain,
                                            false
                                        ) as Chip
                                    }

                                    StyleTypes.Phase4 -> {
                                        activity.layoutInflater.inflate(
                                            R.layout.itemintrestgrid4,
                                            this.chipGroupMain,
                                            false
                                        ) as Chip
                                    }

                                    StyleTypes.PhaseAddtional -> {
                                        activity.layoutInflater.inflate(
                                            R.layout.itemintrestgrid_additonal,
                                            this.chipGroupMain,
                                            false
                                        ) as Chip
                                    }
                                }

                                chip.text = listTop!![i]

                                chip.textColor(R.color.white)
                                chip.closeIconTint = ContextCompat.getColorStateList(
                                    activity,
                                    R.color.chip_background_state_bottom2
                                )
                                //chip.closeIcon = ContextCompat.getDrawable(activity, R.drawable.icon_feather_plus_circle)
                                //chip.closeIconSize = activity.resources.getDimension(R.dimen.closeImageSize)
                                chip.isChecked = true
                                chip.isCloseIconVisible = isCloseIconVisible


                                chip.setOnCloseIconClickListener {
                                    listTop?.let { listTop ->
                                        val newList = ArrayList(listTop)
                                        newList.remove(chip.text.toString())
                                        Log.d(TAG, "build321231: " + newList)
                                        listener?.onSelectedListChange(newList)
                                        this.chipGroupMain.removeView(chip)
                                    }
                                }

                                tempTopList.add(listTop!![i])
                                when (listType) {
                                    is ListType.ChildList -> {

                                        (listType as ListType.ChildList)
                                            .listOfChildHobbies.forEach {

                                                if (it.title == listTop!![i]) {
                                                    when (i) {
                                                        0 -> selectedId1 = it.id.toString()
                                                        1 -> selectedId2 = it.id.toString()
                                                        2 -> selectedId3 = it.id.toString()
                                                    }

                                                    selectedIdd1=selectedId1
                                                    selectedIdd2=selectedId2
                                                    selectedIdd3=selectedId3
                                                    Log.d(TAG, "buildsdfdsf:509 "+selectedId1)
                                                    Log.d(TAG, "buildsdfdsf:510 "+selectedId2)
                                                    Log.d(TAG, "buildsdfdsf:511 "+selectedId3)
                                                }
                                            }
                                    }

                                    is ListType.ParentList -> {}
                                }



                                logThisLine(476)
                                this.chipGroupMain.addView(chip)
                            }
                        }

                    }
                    Log.d(TAG, "build312: " + tempTopList)

                    listener?.onSelectedListChange(tempTopList)
                    bottomSheetDialog.dismiss()
                }

                Log.d(
                    TAG,
                    "build: testSelectedItemsAre>>${selectedId1}>>${selectedId2}>>${selectedId3}"
                )

                if (selectedId1 != "" || selectedId2 != "" || selectedId3 != "") (thisView as? EditText)?.setText(
                    "   "
                )
                else (thisView as? EditText)?.setText("")

                thisView.setOnClickListener {
                    try {
                        txtTitle?.text = titleString
                        listTop = null

                        when (listType) {
                            is ListType.ChildList -> {
                                if (chipGroup != null
                                /*&& txtCount != null*/) {

                                    val listener = object : ChipSelectedListener {
                                        override fun onSelectedListChange(list: ArrayList<String>) {
                                            Log.d(TAG, "onSelectedListChange321: " + list)
                                            listTop = list
                                            listener?.onSelectedListChange(list)


                                            if (list.isNotEmpty()) (it as? EditText)?.setText("   ")
                                            else (it as? EditText)?.setText("")
                                            Log.d(
                                                TAG,
                                                "onSelectedListChange: testflowCLickItems>>NEW"
                                            )
                                            list.forEach {
                                                Log.d(
                                                    TAG,
                                                    "onSelectedListChange: testflowCLickItems>>$it"
                                                )
                                            }

                                        }
                                    }

                                    Log.d(
                                        "TAG",
                                        "build: testflow>>" + (listType as ListType.ChildList).listOfChildHobbies.size
                                    )

                                    Builder(activity)
                                        .setChipLayout(chipGroup)
                                        .setSelectedListener(listener)
                                        .setList(ListType.ChildList((listType as ListType.ChildList).listOfChildHobbies))
                                        // .setCountTextView(txtCount)
                                        .setMaxSelected(maxSelectedCount)
                                        .setAlreadySelectedIds(
                                            selectedId1,
                                            selectedId2,
                                            selectedId3
                                        )
                                        .setBottomSheet(true)
                                        .setClickable(true)
                                        .setStyleColor(StyleTypes.PhaseAddtional)
                                        .setCloseIconVisible(false)
                                        .setViewsForClickedNewInterestPicker(null, null)
                                        .setRemoveListener(null)
                                        .setIsCurrentBuilderChild(true)
                                        .build()
                                }

                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                                bottomSheetDialog.show()
                            }

                            is ListType.ParentList -> {

                            }

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            return this
        }

        private fun logThisLine(i: Int) {
            Log.d(TAG, "logThisLine: testChipGroup >>$i")
        }


        private fun buildChip(name: String): Chip? {

            var chip: Chip? = null

            color?.let {

                chip = if (isBottomSheet) {
                    activity.layoutInflater.inflate(
                        R.layout.iemintrestgridbottom,
                        chipGroupMain,
                        false
                    ) as Chip
                } else {
                    when (it) {
                        StyleTypes.Phase1 -> {
                            activity.layoutInflater.inflate(
                                R.layout.itemintrestgrid,
                                chipGroupMain,
                                false
                            ) as Chip
                        }

                        StyleTypes.Phase2 -> {
                            activity.layoutInflater.inflate(
                                R.layout.itemintrestgrid2,
                                chipGroupMain,
                                false
                            ) as Chip
                        }

                        StyleTypes.Phase3 -> {
                            activity.layoutInflater.inflate(
                                R.layout.itemintrestgrid3,
                                chipGroupMain,
                                false
                            ) as Chip
                        }

                        StyleTypes.Phase4 -> {
                            activity.layoutInflater.inflate(
                                R.layout.itemintrestgrid4,
                                chipGroupMain,
                                false
                            ) as Chip
                        }

                        StyleTypes.PhaseAddtional -> {
                            activity.layoutInflater.inflate(
                                R.layout.itemintrestgrid_additonal,
                                chipGroupMain,
                                false
                            ) as Chip
                        }
                    }
                }
            }

            chip?.text = name
            chip?.setTextColor(
                ContextCompat.getColorStateList(
                    activity.layoutInflater.context,
                    R.color.chip_text_state3
                )
            )

            if (isCloseIconVisible && !isCurrentBuilderChild) {
                chip?.textColor(R.color.white)
                chip?.closeIconTint =
                    ContextCompat.getColorStateList(
                        activity,
                        R.color.chip_background_state_bottom2
                    )
                //chip.closeIcon =ContextCompat.getDrawable(activity, R.drawable.icon_feather_plus_circle)
                // chip.closeIconSize = activity.resources.getDimension(R.dimen.closeImageSize)
                chip?.isChecked = true
                chip?.isCloseIconVisible = isCloseIconVisible
                chip?.setOnCloseIconClickListener {
                    if (!isClickable) {
                        if (chipGroupMain.childCount == 1) {
                            activity.showToast("minimum 3 selected")
                        } else {
                            itemRemoveListener?.onItemRemoved(
                                chip?.text.toString(),
                                selectedItemsList
                            )
                            chipGroupMain.removeView(chip)
                        }
                    } else {
                        chipGroupMain.removeView(chip)
                    }
                }
            } else {
                chip?.setOnClickListener { vie ->
                    val b = (vie as Chip).isChecked
                    if (b) {
                        selectedCount += 1
                        selectedItemsList.add(vie.text.toString())
                    } else {
                        selectedCount -= 1
                        for (ii in selectedItemsList.indices) {
                            if (selectedItemsList[ii] == vie.text.toString()) {
                                selectedItemsList.removeAt(ii)
                                break
                            }
                        }
                    }
                    Log.d(TAG, "buildChip3212:725 " + selectedCount + "-->" + maxSelectedCount)
                    Log.d(TAG, "buildChip3212:726 " + vie)

                    if (selectedCount == maxSelectedCount) {
                        Log.d(TAG, "buildChip741: " + "231")

                        selectedCount = (maxSelectedCount - 1)
                        selectedItemsList.removeAt((maxSelectedCount - 1))
                        vie.isChecked = false
                        checkChanging = true
                    }
                    txtCount?.text = "(${selectedCount}/3)"

                    if (selectedCount == 3) txtCount?.textColor(R.color.phas1)
                    else txtCount?.textColor(R.color.c515151cffffff)
                    Log.d(TAG, "buildChipsadfa: " + selectedItemsList)

                    listener?.onSelectedListChange(selectedItemsList)
                }
            }

            return chip
        }

        fun openBottomSheetNow() {
            llInterests?.performClick()
        }

        private var selectedForChildren1 = ""
        private var selectedForChildren2 = ""
        private var selectedForChildren3 = ""

        fun setSelectedIdsForChildren(s: String, s1: String, s2: String): Builder {
            this.selectedForChildren1 = s
            this.selectedForChildren2 = s1
            this.selectedForChildren3 = s2

            return this
        }

    }

    class GetSeparatedIds(
        alreadySelectedList: ArrayList<String>, onComplete: (
            selectedId1: String,
            selectedId2: String,
            selectedId3: String
        ) -> Unit
    ) {

        init {
            var selectedId1 = ""
            var selectedId2 = ""
            var selectedId3 = ""

            for (i in alreadySelectedList.indices) {
                when (i) {
                    0 -> selectedId1 = alreadySelectedList[i]
                    1 -> selectedId2 = alreadySelectedList[i]
                    2 -> selectedId3 = alreadySelectedList[i]
                }
            }

            onComplete(selectedId1, selectedId2, selectedId3)

        }

    }

}