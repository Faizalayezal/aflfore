package com.foreverinlove.adapter

import android.app.Activity
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
import com.foreverinlove.network.response.OldMessageData

class UserChatListAdapter(
    val context: Activity,
    private val listener: onClick,
    private val uid: Int,
    private var newusers: List<OldMessageData>
) :
    RecyclerView.Adapter<UserChatListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_chat_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
          //  itemchatlist.animation = AnimationUtils.loadAnimation(context, R.anim.fad_scale)

            username.text = newusers[position].user_name
            userlastmsg.text = newusers[position].lastseen
            msgtime.text = FormatDateUseCase.getTimeAgoFromDate(newusers[position].created_at?:"")


            userlastmsg.text = newusers[position].message

            msgcount.text = newusers[position].unread_message_count.toString()

            Glide.with(userimg.context).load(newusers[position].user_image_url?.firstOrNull()?.url).into(userimg)
            val senderid=newusers[position].sender_id
            if(senderid==uid || newusers[position].unread_message_count.toString()=="0"){
                                msgcount.visibility=View.GONE
                            }else{
                                msgcount.visibility=View.VISIBLE

                            }
            itemchatlist.setOnClickListener {
                listener.openDetail(newusers[position], position)
            }


        }
    }


    override fun getItemCount(): Int {
        return newusers.size
    }

    interface onClick {
        fun openDetail(data: OldMessageData, position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimg: ImageView = itemView.findViewById(R.id.userimg)
        val itemchatlist: LinearLayout = itemView.findViewById(R.id.itemchatlist)
        val username: TextView = itemView.findViewById(R.id.userName)
        val userlastmsg: TextView = itemView.findViewById(R.id.lastmsg)
        val msgtime: TextView = itemView.findViewById(R.id.msgtime)
        val msgcount: TextView = itemView.findViewById(R.id.msgcount)

    }
}