package com.foreverinlove.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.network.response.RoomList

class RequestChatAdapter(
    val context: Activity,
    private var users: ArrayList<RoomList>
) :
    RecyclerView.Adapter<RequestChatAdapter.CardViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.itemrequestedchatlist, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.apply {
            txtgroupName.text = "You have sent a request to \n"+users[position].room_name

            Glide.with(imguserimg1.context).load(users[position].room_icon).into(imguserimg1)
            Glide.with(imguserimg2.context).load(users[position].room_icon1).into(imguserimg2)



        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txtgroupName: TextView = itemView.findViewById(R.id.groupname)
        var imguserimg1: ImageView = itemView.findViewById(R.id.circleImageView1)
        var imguserimg2: ImageView = itemView.findViewById(R.id.circleImageView2)
    }
}