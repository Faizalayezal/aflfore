package com.foreverinlove.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.chatmodual.FormatDateUseCase
import com.foreverinlove.network.response.PrivateUserSendChatDataList


class AcceptedChatAdepter(
    val context: Activity,
    val Listener: onClick,
    private val uid: Int,
    private var chatUser: List<PrivateUserSendChatDataList>
) :
    RecyclerView.Adapter<AcceptedChatAdepter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_accepted, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {

            Glide.with(context)
                .load(chatUser[position].get_request_to_user?.user_images?.firstOrNull()?.url)
                .into(userImg)

            userName.text = chatUser[position].get_request_to_user?.first_name
            userLstMsg.text = chatUser[position].last_message
            userTime.text = FormatDateUseCase.getTimeAgoFromDate(chatUser[position].created_at!!)


           // userCount.text = chatUser[position].message_count.toString()
             userCount.text = chatUser[position].unread_message_count.toString()


            val senderid = chatUser[position].sender_id
            Log.d(
                "TAG",
                "onBindViewHoldersdffd:senderid->" + senderid + ">>uid->" + uid + ">>message_count->" + chatUser[position].message_count
            )

            if (senderid == uid || chatUser[position].unread_message_count.toString() == "0") {
                userCount.visibility = View.GONE
            } else {
                userCount.visibility = View.VISIBLE

            }

            clickCard.setOnClickListener {
                Listener.openDetail(chatUser[position], position)
            }

        }


    }

    override fun getItemCount(): Int {
        return chatUser.size
    }

    fun interface onClick {
        fun openDetail(
            data: PrivateUserSendChatDataList,
            position: Int
        )
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImg: ImageView = itemView.findViewById(R.id.userimg)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userLstMsg: TextView = itemView.findViewById(R.id.lastmsg)
        val userTime: TextView = itemView.findViewById(R.id.msgtime)
        val userCount: TextView = itemView.findViewById(R.id.msgcount)
        val clickCard: LinearLayout = itemView.findViewById(R.id.clickCard)

    }
}