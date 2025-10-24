package com.foreverinlove.utility

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object FragmentExt {
    fun FragmentManager.loadFragment(fragment: Fragment, layoutId: Int) {
        val ft = beginTransaction()
        ft.add(layoutId, fragment)
        ft.addToBackStack(null)
        ft.commitAllowingStateLoss()
    }
}