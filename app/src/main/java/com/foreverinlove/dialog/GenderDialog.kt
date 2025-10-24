package com.foreverinlove.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import com.foreverinlove.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


@SuppressLint("InflateParams")
object GenderDialog {

    @SuppressLint("MissingInflatedId")
    fun Activity.opneGenderDialog(editText: EditText) {
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {}
            override fun onStateChanged(p0: View, p1: Int) {}
        }
        val bottomSheetView =
            this.layoutInflater.inflate(R.layout.dialog_gender, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)

        bottomSheetDialog.setContentView(bottomSheetView)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView.parent as View)
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)
        val txtMale = bottomSheetView.findViewById<TextView>(R.id.txtMale)

        txtMale.setOnClickListener {
            bottomSheetDialog.dismiss()
            editText.setText("Male")
          //  binding.edgender.setText("Male")
        }
        val txtFemale = bottomSheetView.findViewById<TextView>(R.id.txtFemale)
        txtFemale.setOnClickListener {
            bottomSheetDialog.dismiss()
            editText.setText("Female")

           // binding.edgender.setText("Female")
        }
        val txtTransgender = bottomSheetView.findViewById<TextView>(R.id.txtTransgender)
        txtTransgender.setOnClickListener {
            bottomSheetDialog.dismiss()
            editText.setText("Transgender")

                // binding.edgender.setText("Other")


        }

        val txtTransexual = bottomSheetView.findViewById<TextView>(R.id.txtSexual)
        txtTransexual.setOnClickListener {
            bottomSheetDialog.dismiss()
            editText.setText("Transexual")

            // binding.edgender.setText("Other")


        }
        val txtNonbinary = bottomSheetView.findViewById<TextView>(R.id.txtNonbinary)
        txtNonbinary.setOnClickListener {
            bottomSheetDialog.dismiss()
            editText.setText("Non-Binary")

            // binding.edgender.setText("Other")


        }


        if (editText.text.toString() == "Transgender") {
            txtTransgender.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.gol_select_layered,
                0,
                0,
                0
            )
        } else if (editText.text.toString() == "Female") {
            txtFemale.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.gol_select_layered,
                0,
                0,
                0
            )
        } else if (editText.text.toString() == "Male") {
            txtMale.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.gol_select_layered,
                0,
                0,
                0
            )
        } else if (editText.text.toString() == "Transexual") {
            txtTransexual.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.gol_select_layered,
                0,
                0,
                0
            )
        }
        else if (editText.text.toString() == "Non-Binary") {
            txtNonbinary.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.gol_select_layered,
                0,
                0,
                0
            )
        }
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.show()

    }
}



