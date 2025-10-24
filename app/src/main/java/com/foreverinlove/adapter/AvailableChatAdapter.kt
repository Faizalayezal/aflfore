package com.foreverinlove.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.network.response.RoomList

class AvailableChatAdapter(
    val context: Activity,
    val Linear: onClick,
    private var users: ArrayList<RoomList>
) :
    RecyclerView.Adapter<AvailableChatAdapter.CardViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.itemavailablechatlist, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.apply {
            users[position].let{roomList->
                txtgroupName.text = roomList.room_name
                txtmembercount.text= roomList.total_users+" members"

                Glide.with(imguserimg1.context).load(roomList.room_icon).into(imguserimg1)
                Glide.with(imguserimg2.context).load(roomList.room_icon1).into(imguserimg2)

                card.setOnClickListener {
                    Linear.openClick(roomList.room_id,roomList.room_name)
                }
            }

        }
    }


    interface onClick{
        fun openClick(roomId: Int?,roomname:String?)
    }
    override fun getItemCount(): Int {
        return users.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txtgroupName: TextView = itemView.findViewById(R.id.groupname)
        var txtmembercount: TextView = itemView.findViewById(R.id.membercount)
        var imguserimg1: ImageView = itemView.findViewById(R.id.circleImageView1)
        var imguserimg2: ImageView = itemView.findViewById(R.id.circleImageView2)
        var card: CardView = itemView.findViewById(R.id.cardchat)
    }
}


