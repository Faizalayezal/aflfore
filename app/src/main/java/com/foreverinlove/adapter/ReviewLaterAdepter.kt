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

class ReviewLaterAdepter(
    val context: Activity,
    private val listener:onClick,
    private var reviewsuser: ArrayList<ReviewData>
) :
    RecyclerView.Adapter<ReviewLaterAdepter.Viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_review_later, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.apply {
            const.animation = AnimationUtils.loadAnimation(context, R.anim.fad_scale)



            username.text=reviewsuser[position].get_request_from_user?.first_name
            Glide.with(userimg.context).load(reviewsuser[position].get_request_from_user?.user_images?.firstOrNull()?.url).into(userimg)


            itemView.setOnClickListener {
                listener.openDetail(reviewsuser[position],position)
            }
        }

    }

    override fun getItemCount(): Int {
        return reviewsuser.size
    }
    interface onClick{
        fun openDetail(data: ReviewData,position: Int)
    }
    fun Itemremove(position: Int) {
        if (reviewsuser.size > position) {
            reviewsuser.removeAt(position)
            notifyItemRemoved(position)

        }

    }


    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimg: ImageView = itemView.findViewById(R.id.userimg)
        val username: TextView = itemView.findViewById(R.id.username)
        val const: ConstraintLayout = itemView.findViewById(R.id.cons)

    }
}