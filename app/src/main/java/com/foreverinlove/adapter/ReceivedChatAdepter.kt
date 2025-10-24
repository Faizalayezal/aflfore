package com.foreverinlove.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.network.response.PrivateUserReacivedChatDataList


class ReceivedChatAdepter(
    val context: Activity,
    val Listener: onClick,
    private var chatuser: List<PrivateUserReacivedChatDataList>
) :
    RecyclerView.Adapter<ReceivedChatAdepter.Viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_received, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.apply {

            Glide.with(userimg.context)
                .load(chatuser[position].get_request_from_user?.user_images?.firstOrNull()?.url)
                .into(userimg)
            username.text = chatuser[position].get_request_from_user?.first_name

            cardClick.setOnClickListener {
                Listener.openDetail(chatuser[position], position)
            }
        }


    }

    /*
        chatuser[position].user_private_chat_id,chatuser[position].invite_msg,chatuser[position].match_id,chatuser[position].get_request_from_user?.user_images?.firstOrNull()?.url,chatuser[position].get_request_from_user?.first_name
    */
    override fun getItemCount(): Int {
        return chatuser.size
    }

   fun interface onClick {
        fun openDetail(data: PrivateUserReacivedChatDataList, position: Int)


    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimg: ImageView = itemView.findViewById(R.id.userimg)
        val username: TextView = itemView.findViewById(R.id.userName)
        val cardClick: LinearLayout = itemView.findViewById(R.id.cardClick)


    }
}