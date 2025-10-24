package com.foreverinlove.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import com.foreverinlove.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


@SuppressLint("InflateParams")
object EducationDialog {

    fun Activity.openEducationPicker(editText: EditText, titel: String) {
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
        val txtSave = bottomSheetView.findViewById<TextView>(R.id.txtSave)
        val txttitle = bottomSheetView.findViewById<TextView>(R.id.txtTitle)

        txttitle.text = titel
        val savbtn = true


        var selectedPos = 1


      /*  val suggestions: Array<String> = arrayOf<String>(
            "High School",
            "Trade School",
            "College Student",
            "Bachelors",
            "Masters",
            "PhD",
        )*/
        val suggestions = arrayOf("High School",
            "Trade School",
            "College Student",
            "Bachelors",
            "Masters",
            "PhD",)


        var selectedName = 1
        for (i in suggestions.indices) {
            if (suggestions[i] == editText.text.toString()) {
                selectedName = i
            }
        }

        numberPicker.minValue = 0
        numberPicker.maxValue = 5
        numberPicker.displayedValues = suggestions
        numberPicker.value = 5

        numberPicker.setOnValueChangedListener(object : NumberPicker.OnValueChangeListener,
            com.shawnlin.numberpicker.NumberPicker.OnValueChangeListener {
            override fun onValueChange(p0: NumberPicker?, p1: Int, p2: Int) {


            }

            override fun onValueChange(
                picker: com.shawnlin.numberpicker.NumberPicker?,
                oldVal: Int,
                newVal: Int
            ) {

                Log.d("TAG", "onValueChange2: "+oldVal+""+newVal)
                selectedPos = newVal


                //binding.edHeight.setText(stringArray[newVal - 1])
            }
        })

        txtSave.setOnClickListener {

            editText.setText(suggestions[selectedPos])
            bottomSheetDialog.dismiss()
        }



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