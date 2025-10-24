package com.foreverinlove.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import com.foreverinlove.Constant
import com.foreverinlove.R
import com.foreverinlove.utility.MyListDataHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip


@SuppressLint("InflateParams")
object HeightDialog {

    fun Activity.openHeightPicker(chip1: Chip, editText: EditText){
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}
            override fun onStateChanged(p0: View, p1: Int) {}
        }
        val bottomSheetView =
            this.layoutInflater.inflate(R.layout.dialog_heightpicker, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)

        bottomSheetDialog.setContentView(bottomSheetView)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView.parent as View)
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)

        val numberPicker =
            bottomSheetView.findViewById<com.shawnlin.numberpicker.NumberPicker>(R.id.number_picker)
        val txtSave = bottomSheetView.findViewById<TextView>(R.id.txtSave)

        val listHeighTop  =  MyListDataHelper.getAllData()?.height

        val stringArray = arrayOfNulls<String>(listHeighTop?.size?:1)

        var selectedPos = 1

        listHeighTop?.forEachIndexed { index, addtionalQueObject ->
            stringArray[index] = addtionalQueObject.title
        }
        Log.d("TAG", "openHeightPicker: "+selectedPos+"-->"+stringArray+"-->>"+listHeighTop)

        var selectedName=1
        for(i in stringArray.indices){
            if(stringArray[i]==editText.text.toString()){
                selectedName=i+1
            }
        }

        numberPicker.minValue = 1
        numberPicker.maxValue = listHeighTop?.size?:5
        numberPicker.displayedValues = stringArray
        numberPicker.value = 1

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
                //binding.edHeight.setText(stringArray[newVal - 1])
            }
        })

        txtSave.setOnClickListener {
            editText.setText(stringArray[selectedPos - 1])
            chip1.text = stringArray[selectedPos - 1]
            Constant.heightdata.value=chip1.text.toString()
            bottomSheetDialog.dismiss()
        }


        Log.d("TAG", "openHeightPicker->82: "+selectedName)

        try {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetDialog.show()

            this.runOnUiThread {
                numberPicker.value = selectedName
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}