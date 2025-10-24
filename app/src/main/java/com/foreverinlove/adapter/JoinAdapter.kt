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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class JoinAdapter(
    val context: Activity,
    val Linear: onClick,
    private var users: ArrayList<RoomList>
) :
    RecyclerView.Adapter<JoinAdapter.CardViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.itemgroupchatlist, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.apply {
            txtgroupName.text = users[position].room_name
            txtmembercount.text = users[position].total_users+" members"
            msgcount.text = users[position].unread_message_count

            //currant date to last date count days
            val date = Calendar.getInstance().time
            val dateFormat: DateFormat = SimpleDateFormat("yyyy-mm-dd")
            val strDate: String = dateFormat.format(date)
            val endDateValue = users[position].date_to
            val date1: Date = dateFormat.parse(strDate) as Date
            val date2: Date = dateFormat.parse(endDateValue.toString()) as Date
            val difference = abs(date1.time - date2.time)
            val differenceDates = difference / (24 * 60 * 60 * 1000)
            val dayDifference = differenceDates.toString()

            userdays.text = if(dayDifference.toIntOrNull()==0) {
                "This group will be removed today."
            }else{
                "This group will continue for $dayDifference days."
            }

            Glide.with(imguserimg2.context).load(users[position].room_icon).into(imguserimg2)
            Glide.with(imguserimg1.context).load(users[position].room_icon1).into(imguserimg1)

             cardchat.setOnClickListener {
                 Linear.openChat(users[position])

             }

        }
    }
    interface onClick{
        fun openChat(roomList: RoomList)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txtgroupName: TextView = itemView.findViewById(R.id.groupname)
        var txtmembercount: TextView = itemView.findViewById(R.id.membercount)
        var userdays: TextView = itemView.findViewById(R.id.days)
        var msgcount: TextView = itemView.findViewById(R.id.msgcount)
        var imguserimg1: ImageView = itemView.findViewById(R.id.circleImageView1)
        var imguserimg2: ImageView = itemView.findViewById(R.id.circleImageView2)
        var cardchat: CardView = itemView.findViewById(R.id.cardchat)
    }
}