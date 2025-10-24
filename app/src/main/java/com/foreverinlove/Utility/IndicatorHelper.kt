package com.foreverinlove.utility

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.foreverinlove.adapter.IndicatorAdapter

class IndicatorHelper(val rcvMain:RecyclerView,val indicatorAdapter: IndicatorAdapter) {

    init {
        rcvMain.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val pos=(rcvMain.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                indicatorAdapter.update(pos)
            }
        })
    }

}