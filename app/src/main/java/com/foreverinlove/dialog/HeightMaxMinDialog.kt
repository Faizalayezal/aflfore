package com.foreverinlove.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import com.foreverinlove.R
import com.foreverinlove.objects.DiscoverFilterObject
import com.foreverinlove.utility.ActivityExt.showToast
import com.foreverinlove.utility.MyListDataHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip


@SuppressLint("InflateParams")
object HeightMaxMinDialog {

    @SuppressLint("SetTextI18n")
    fun Activity.openHeightMaxMinDialog(
        chip1:Chip,
        chip2:Chip,
        editText: EditText,
        tempDiscoverData: DiscoverFilterObject?
    ) {
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}
            override fun onStateChanged(p0: View, p1: Int) {}
        }
        val bottomSheetView =
            this.layoutInflater.inflate(R.layout.dialog_minmaxheightpicker, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)

        bottomSheetDialog.setContentView(bottomSheetView)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView.parent as View)
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)

        val numberPicker =
            bottomSheetView.findViewById<com.shawnlin.numberpicker.NumberPicker>(R.id.number_picker)

        val numberPicker2 =
            bottomSheetView.findViewById<com.shawnlin.numberpicker.NumberPicker>(R.id.number_picker2)

        val txtSave = bottomSheetView.findViewById<TextView>(R.id.txtSave)

        val listHeighTop = MyListDataHelper.getAllData()?.height

        val stringArray = arrayOfNulls<String>(listHeighTop?.size ?: 1)
        val stringArray2 = arrayOfNulls<String>(listHeighTop?.size ?: 1)

        var selectedPos = 1
        var selectedPos2 = 1

        listHeighTop?.forEachIndexed { index, addtionalQueObject ->
            stringArray[index] = addtionalQueObject.title
        }
        listHeighTop?.forEachIndexed { index, addtionalQueObject ->
            stringArray2[index] = addtionalQueObject.title
        }

        var selectedName = 1
        var selectedName2 = 1

        for (i in stringArray.indices) {
            if (stringArray[i] == editText.text.toString()) {
                selectedName = i + 1
            }

        }
        for (i in stringArray2.indices) {
            if (stringArray2[i] == editText.text.toString()) {
                selectedName2 = i + 1
            }

        }

        numberPicker.minValue = 1
        numberPicker.maxValue = listHeighTop?.size ?: 5
        numberPicker.displayedValues = stringArray
        numberPicker.value = 1

        numberPicker2.minValue = 1
        numberPicker2.maxValue = listHeighTop?.size ?: 5
        numberPicker2.displayedValues = stringArray2
        numberPicker2.value = 1

        Log.d("TAG", "openHeightMaxMinDialog: " + selectedName + "-->" + selectedName2)

        numberPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener,
            com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener {
            override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {


            }

            override fun onValueChange(
                picker: com.shawnlin.numberpicker.NumberPicker?,
                oldVal: Int,
                newVal: Int
            ) {
                selectedPos = newVal

                Log.d("TAG", "onValueChange99: " + selectedPos)
                // tempDiscoverData?.minHeight= newVal.toString()

                //binding.edHeight.setText(stringArray[newVal - 1])
            }
        })
        numberPicker2.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener,
            com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener {
            override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {


            }

            override fun onValueChange(
                picker: com.shawnlin.numberpicker.NumberPicker?,
                oldVal: Int,
                newVal: Int
            ) {
                selectedPos2 = newVal
                //  tempDiscoverData?.maxHeight= newVal.toString()

            }
        })

        txtSave.setOnClickListener {
            if (selectedPos.toString().toIntOrNull()!! > (selectedPos2.toString().toIntOrNull()
                    ?: 0) || selectedPos.toString().toIntOrNull() == selectedPos2.toString()
                    .toIntOrNull()
            ) {
                showToast("please select proper Height")
            } else {
                tempDiscoverData?.minHeight = selectedPos.toString()
                tempDiscoverData?.maxHeight = selectedPos2.toString()
                chip1.text=stringArray[selectedPos - 1]
                chip2.text=stringArray2[selectedPos2-1]
            //    editText.setText(stringArray[selectedPos - 1]+" "+stringArray2[selectedPos2-1])
                //  editText.setText(stringArray[selectedPos - 1]+" "+stringArray2[selectedPos2-1])

                bottomSheetDialog.dismiss()
            }

        }




        try {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.show()

            this.runOnUiThread {
                Log.d("TAG", "openHeightMaxMinDialog: " + tempDiscoverData?.minHeight)
                Log.d("TAG", "openHeightMaxMinDialog142: " + selectedPos)
                numberPicker.value = tempDiscoverData?.minHeight?.toIntOrNull() ?: 0
                numberPicker2.value = tempDiscoverData?.maxHeight?.toIntOrNull() ?: 0
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}