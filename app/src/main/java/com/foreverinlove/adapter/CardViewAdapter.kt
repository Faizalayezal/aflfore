package com.foreverinlove.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.network.response.DiscoverData


class CardViewAdapter(
    val context: Activity,
    private val listener: OnClick,
    private var users: ArrayList<DiscoverData>,
) :
    RecyclerView.Adapter<CardViewAdapter.CardViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_swipe_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.apply {
            txtName.text = users[position].first_name+","
            txtlocaton.text= users[position].address
            age.text = users[position].age.toString()

            away.text = users[position].calculatedDistance?:""
            Log.d("TAG", ("onBindViewHddfolder: " + users[position].calculatedDistance))

            //away.text = users[position].distance+" "+distanceType+""+" away"


            Glide.with(ivUserImage.context).load(users[position].user_images?.firstOrNull()?.url?:"").into(ivUserImage)

            ivUserImage.setOnClickListener {
                listener.openDetail(users[position])
            }

        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun addItems(listTop: java.util.ArrayList<DiscoverData>) {
        users.clear()
        users.addAll(listTop)
        notifyDataSetChanged()

    }

    interface OnClick{
        fun openDetail(data:DiscoverData)
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txtName: TextView = itemView.findViewById(R.id.tv_user_name)
        var txtlocaton: TextView = itemView.findViewById(R.id.location)
        var age: TextView = itemView.findViewById(R.id.tv_user_name_age)
        var ivUserImage: ImageView = itemView.findViewById(R.id.iv_user_image)
        var away: TextView = itemView.findViewById(R.id.txtAway)
    }
}