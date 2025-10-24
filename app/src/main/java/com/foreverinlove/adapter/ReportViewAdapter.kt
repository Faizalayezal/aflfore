package com.foreverinlove.adapter

import android.annotation.SuppressLint
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
import com.foreverinlove.network.response.ReasonData

class ReportViewAdapter(
    val context: Activity,
    private var users: ArrayList<ReasonData>
) :
    RecyclerView.Adapter<ReportViewAdapter.CardViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_report, parent, false)
        return CardViewHolder(view)
    }

    private var tempImageView : ImageView? = null
    private var tempInt : Int? = null

    override fun onBindViewHolder(
        holder: CardViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.apply {
            txt.text = users[position].name

            if (tempInt == position) {
                Glide.with(chake.context).load(R.mipmap.chakebtn).into(chake)
            } else {
                Glide.with(chake.context).load(R.mipmap.unchackbtn).into(chake)
            }

            liner.setOnClickListener {

                Glide.with(chake.context).load(R.mipmap.chakebtn).into(chake)

                if (tempImageView != null) {
                    Glide.with(chake.context).load(R.mipmap.unchackbtn).into(tempImageView!!)
                }

                tempInt = position
                tempImageView = chake
            }


        }


    }
    fun getSelectedId(): Int? {

        if(tempInt==null) return null
        return users[tempInt!!].id

    }


    override fun getItemCount(): Int {
        return users.size
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txt: TextView = itemView.findViewById(R.id.userName)
        var chake: ImageView = itemView.findViewById(R.id.imgCheck)
        var liner: LinearLayout = itemView.findViewById(R.id.liner)
    }
}