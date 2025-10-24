package com.foreverinlove.utility

import android.content.Context
import android.os.Handler
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

class GridAutoFitLayoutManager : GridLayoutManager {
    private var mColumnWidth = 0
    private var mMaximumColumns: Int
    private var mLastCalculatedWidth = -1

    private lateinit var context: Context

    @JvmOverloads
    constructor(
        context: Context?,
        columnWidthDp: Int,
        maxColumns: Int = 99
    ) : super(context, 1) //Initially set spanCount to 1, will be changed automatically later.
    {
        this.context = context!!
        mMaximumColumns = maxColumns
        setColumnWidth(columnWidthDp)
    }

    private fun setColumnWidth(newColumnWidth: Int) {
        if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
            mColumnWidth = newColumnWidth
        }
    }

    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
        val totalSpace: Int = if (orientation == RecyclerView.VERTICAL) {
            width - paddingRight - paddingLeft
        } else {
            height - paddingTop - paddingBottom
        }
        val newSpanCount = min(
            mMaximumColumns,
            max(1, totalSpace / mColumnWidth)
        )
        queueSetSpanCountUpdate(newSpanCount)
        mLastCalculatedWidth = width
        super.onLayoutChildren(recycler, state)
    }

    private fun queueSetSpanCountUpdate(newSpanCount: Int) {

        Handler(context.mainLooper).post { spanCount = newSpanCount }

    }

}