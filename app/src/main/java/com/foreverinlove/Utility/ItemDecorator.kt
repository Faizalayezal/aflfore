package com.foreverinlove.utility

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView




class ItemDecorator(private val mSpace: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position: Int =parent.getChildAdapterPosition(view)
        if (position != 0) outRect.left = mSpace

    }

}