package com.foreverinlove.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreverinlove.R
import com.foreverinlove.objects.Datas

enum class SubsTypes {
    SubscriptionDesign,//int
    PrimiumDesign,//bool

}

class SubScriptionDiscoverAdapter(
    val context: Activity,
    private var users: List<Datas>,

    ) :
    RecyclerView.Adapter<SubScriptionDiscoverAdapter.DescViewHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_subsciptionlayout, parent, false)
        return DescViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: DescViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.apply {
            txtdesc.text = users[position].desc1


        }


    }


    override fun getItemCount(): Int {
        return users.size
    }

    class DescViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var txtdesc: TextView = itemView.findViewById(R.id.txtdesc)
        var subsymbol: ImageView = itemView.findViewById(R.id.subsymbol)
    }
}