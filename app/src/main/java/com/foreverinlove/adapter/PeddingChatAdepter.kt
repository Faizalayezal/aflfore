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
import com.foreverinlove.network.response.PrivateUserSendChatDataList
import com.foreverinlove.network.response.ViewedMeData


class PeddingChatAdepter(
    val context: Activity,
    private var chatuser: List<PrivateUserSendChatDataList>
) :
    RecyclerView.Adapter<PeddingChatAdepter.Viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_received, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.apply {

            Glide.with(userimg.context).load(chatuser[position].get_request_to_user?.user_images?.firstOrNull()?.url).into(userimg)
            username.text = chatuser[position].get_request_to_user?.first_name

        }


    }

    override fun getItemCount(): Int {
        return chatuser.size
    }

    interface onClick{
        fun openDetail(data: ViewedMeData)
    }

   inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimg: ImageView = itemView.findViewById(R.id.userimg)
        val username: TextView = itemView.findViewById(R.id.userName)


    }
}