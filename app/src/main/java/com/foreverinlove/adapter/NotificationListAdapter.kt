package com.foreverinlove.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.network.response.Notifacation

class NotificationListAdapter(
    val context: Activity,
    private var newusers: List<Notifacation>,
    val Listner: onClick
) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_notification_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            itemClick.animation = AnimationUtils.loadAnimation(context, R.anim.fad_scale)


            username.text = newusers[position].title
            likemsg.text = newusers[position].message

            Glide.with(userimg.context).load(newusers[position].user_image?.firstOrNull()?.url).into(userimg)


            if (newusers.size - 1 == (position)) {
                line.visibility = View.GONE
            } else {
                line.visibility = View.VISIBLE

            }
            when(newusers[position].type){
                "like" -> {Glide.with(userimg.context).load(R.mipmap.likenoti).into(msgicon)}
                "match" -> {Glide.with(userimg.context).load(R.mipmap.matchnoti).into(msgicon)}
               // "custom" -> {Glide.with(userimg.context).load(R.mipmap.padding).into(msgicon)}
                "change_password" -> {Glide.with(userimg.context).load(R.mipmap.chnagepass).into(msgicon)}
                "review_later" -> {Glide.with(userimg.context).load(R.mipmap.reviewlatternoti).into(msgicon)}
                "super_like" -> {Glide.with(userimg.context).load(R.mipmap.supernoti).into(msgicon)}
               // "message" -> {Glide.with(userimg.context).load(R.mipmap.padding).into(msgicon)}
                "room_published" -> {Glide.with(userimg.context).load(R.mipmap.roompublishnoti).into(msgicon)}
                "Request To Private chat" -> {Glide.with(userimg.context).load(R.mipmap.privatechatnoti).into(msgicon)}
                "Private Chat Request Confirm" -> {Glide.with(userimg.context).load(R.mipmap.acceptnoti).into(msgicon)}
                "private_chat_request_rejected" -> {Glide.with(userimg.context).load(R.mipmap.rejectnoti).into(msgicon)}
                "user report" -> {Glide.with(userimg.context).load(R.mipmap.userreportnoti).into(msgicon)}
                "single_video_call" -> {Glide.with(userimg.context).load(R.mipmap.videocallnoti).into(msgicon)}
                "user view" -> {Glide.with(userimg.context).load(R.mipmap.viewnoti).into(msgicon)}
                "new_match" -> {Glide.with(userimg.context).load(R.mipmap.matchnoti).into(msgicon)}
                "Room Joined" -> {Glide.with(userimg.context).load(R.mipmap.joinnoti).into(msgicon)}
                "Subscribe Plan" -> {Glide.with(userimg.context).load(R.mipmap.subscribenoti).into(msgicon)}
                "Group Call Request" -> {Glide.with(userimg.context).load(R.mipmap.groupcallnoti).into(msgicon)}
                "user plan expiring" -> {Glide.with(userimg.context).load(R.mipmap.expiresubnoti).into(msgicon)}
                null -> {}
            }


            itemClick.setOnClickListener {
                Listner.itemClick(newusers[position], position)

            }


        }
    }

    interface onClick {
        fun itemClick(notifacation: Notifacation, position: Int)

    }

    override fun getItemCount(): Int {
        return newusers.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimg: ImageView = itemView.findViewById(R.id.userimg)
        val msgicon: ImageView = itemView.findViewById(R.id.msgicon)
        val username: TextView = itemView.findViewById(R.id.userName)
        val likemsg: TextView = itemView.findViewById(R.id.like)
        val line: View = itemView.findViewById(R.id.yellowliine)
        val itemClick: LinearLayout = itemView.findViewById(R.id.itemchatlist)



    }
}