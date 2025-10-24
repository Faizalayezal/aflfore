package com.foreverinlove.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.network.response.ReviewData
import com.foreverinlove.network.response.ViewedMeData


class WhoViewMeAdepter(
    val context: Activity,
    private val listener:onClick,
    private var likesuser: List<ViewedMeData>
) :
    RecyclerView.Adapter<WhoViewMeAdepter.Viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_who_view_me, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.apply {
            const.animation = AnimationUtils.loadAnimation(context, R.anim.fad_scale)

         //   Glide.with(userimg.context).load(likesuser[position].user_images?.get(0)?.url?:"").into(userimg)
            Glide.with(userimg.context).load(likesuser[position].user_images?.getOrNull(0)?.url?:"").into(userimg)

            username.text=likesuser[position].first_name

            itemView.setOnClickListener {
                listener.openDetail(likesuser[position],position)
            }

        }

    }

    override fun getItemCount(): Int {
        return likesuser.size
    }
    interface onClick{
        fun openDetail(data: ViewedMeData, position: Int)
    }

   inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimg: ImageView = itemView.findViewById(R.id.userimg)
        val username: TextView = itemView.findViewById(R.id.username)
       val const: ConstraintLayout = itemView.findViewById(R.id.constrent)

   }
}