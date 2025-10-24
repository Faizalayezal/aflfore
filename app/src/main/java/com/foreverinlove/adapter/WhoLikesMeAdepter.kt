package com.foreverinlove.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.network.response.LikesListDataResponse


class WhoLikesMeAdepter(
    val context: Activity,
    private val listener: onClick,
    private var likesuser: ArrayList<LikesListDataResponse>
) :
    RecyclerView.Adapter<WhoLikesMeAdepter.Viewholder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_who_likes_me, parent, false)
        return Viewholder(view)
    }


    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.apply {
            const.animation = AnimationUtils.loadAnimation(context, R.anim.fad_scale)

            Glide.with(userimg.context)
                .load(likesuser[position].user?.user_images?.getOrNull(0)?.url ?: "").into(userimg)

            if (likesuser[position].like_status == "like") {
                Glide.with(usersymbol.context).load(R.drawable.likess).into(usersymbol)
            } else if (likesuser[position].like_status == "super_like") {
                Glide.with(usersymbol.context).load(R.drawable.superlikes).into(usersymbol)

            }
            itemView.setOnClickListener {
                listener.openDetail(likesuser[position], position)
            }

        }


    }

    override fun getItemCount(): Int {
        return likesuser.size
    }

    interface onClick {
        fun openDetail(data: LikesListDataResponse, position: Int)
    }

    fun Itemremove(position: Int) {
        if (likesuser.size > position) {
            likesuser.removeAt(position)
            notifyItemRemoved(position)

        }

    }


    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimg: ImageView = itemView.findViewById(R.id.userimg)
        val usersymbol: ImageView = itemView.findViewById(R.id.usersymbol)
        val const: ConstraintLayout = itemView.findViewById(R.id.constrent)
    }
}