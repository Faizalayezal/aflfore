package com.foreverinlove.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.foreverinlove.R
import com.foreverinlove.databinding.ItemIndicatorBinding


class IndicatorAdapter(private val list: ArrayList<Boolean>) :
    RecyclerView.Adapter<IndicatorAdapter.PaymentHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder {
        val itemBinding = ItemIndicatorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: PaymentHolder, position: Int) {
            holder.bindNew(list[position])

    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun update(pos: Int) {
        for(i in list.indices){
            list[i]=false
        }
        list[pos]=true
        notifyDataSetChanged()
    }

    class PaymentHolder(val itemBinding: ItemIndicatorBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindNew(data: Boolean) {
            if(data) itemBinding.imgIndicator.setImageResource(R.color.white)
            else itemBinding.imgIndicator.setImageResource(R.color.timecolor)
        }
    }
}
