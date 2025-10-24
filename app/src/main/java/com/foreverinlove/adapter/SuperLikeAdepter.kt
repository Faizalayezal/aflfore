package com.foreverinlove.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.network.response.SuperLikePlanResponseData


class SuperLikeAdepter(
    val context: Activity,
    private var likesuser: List<SuperLikePlanResponseData>,
    val listner: OnClick,
) :
    RecyclerView.Adapter<SuperLikeAdepter.Viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_superlike, parent, false)
        return Viewholder(view)
    }

    private var tempLinearLayout:LinearLayout?=null

    var num: Int? = null
    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.apply {
            card.animation = AnimationUtils.loadAnimation(context, R.anim.fad_scale)


            productname.text = likesuser[position].product_name
            price.text = "$ "+likesuser[position].price

            when (num) {
                null -> {
                    Glide.with(context).load(R.mipmap.starsuper).into(userimg)
                    num = 1
                }
                1 -> {
                    Glide.with(context).load(R.mipmap.startsuper2).into(userimg)
                    num = 2
                }
                2 -> {
                    Glide.with(context).load(R.mipmap.ranking).into(userimg)
                    num = null
                }
            }




            card.setOnClickListener {

                listner.clickId(likesuser[position])

                tempLinearLayout?.background = card.context.getDrawable(R.drawable.super_like_item_unselected_bg)
                card.background = card.context.getDrawable(R.drawable.super_like_item_selected_bg)

                tempLinearLayout = card
            }
        }

    }
    interface OnClick{
        fun clickId(data:SuperLikePlanResponseData)
    }

    override fun getItemCount(): Int {
        return likesuser.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimg: ImageView = itemView.findViewById(R.id.imgsuper)
        val productname: TextView = itemView.findViewById(R.id.txtproductname)
        val price: TextView = itemView.findViewById(R.id.txtprice)
        val card: LinearLayout = itemView.findViewById(R.id.card)
    }
}