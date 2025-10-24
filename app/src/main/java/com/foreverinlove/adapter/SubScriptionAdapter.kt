package com.foreverinlove.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreverinlove.R
import com.foreverinlove.objects.SubscriptionList


enum class SubsType {
    SubscriptionDesign,//int
    FreeTrialDesign,//bool
    PrimiumDesign

}

class SubScriptionAdapter(
    val context: Activity,
    private var users: ArrayList<SubscriptionList>,
    private val subsType: SubsType
) :
    RecyclerView.Adapter<SubScriptionAdapter.DescViewHolder?>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_subsciptionlayout, parent, false)
        return DescViewHolder(view)
    }

    override fun onBindViewHolder(holder: DescViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.apply {

            when (subsType) {

                SubsType.SubscriptionDesign -> {
                    if(position==1){
                        txtbesic.visibility=View.VISIBLE
                        txtpre.visibility=View.VISIBLE
                        subsymbol.visibility=View.GONE
                        subsymbol2.visibility=View.GONE

                    }else if (users[position].subsymbol)
                        subsymbol.setImageResource(R.drawable.rightbe)
                    else subsymbol.setImageResource(0)

                    txtdesc.text = users[position].desc1
                }

                else -> {}
            }
        }


    }


    /*
        override fun onBindViewHolder(holder: DescViewHolder, @SuppressLint("RecyclerView") position: Int) {
            holder.apply {

                when (subsType) {

                    SubsType.SubscriptionDesign -> {

                        if (users[position].subsymbol)
                            subsymbol.setImageResource(R.drawable.circlecheck)
                        else subsymbol.setImageResource(R.drawable.closechake)

                        txtdesc.text = users[position].desc1
                    }

                    SubsType.FreeTrialDesign -> {

                        subsymbol.setImageResource(R.mipmap.savenday)

                        txtdesc.text = users[position].desc1
                    }
                    SubsType.PrimiumDesign -> {
                        subsymbol.setImageResource(R.mipmap.premiumbtn)
                        txtdesc.text = users[position].desc1
                    }
                }
            }


        }
    */


    override fun getItemCount(): Int {
        return users.size
    }

    class DescViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var txtdesc: TextView = itemView.findViewById(R.id.txtdesc)
        var subsymbol: ImageView = itemView.findViewById(R.id.subsymbol)
        var subsymbol2: ImageView = itemView.findViewById(R.id.subsymbol2)
        var txtbesic: TextView = itemView.findViewById(R.id.besicTxt)
        var txtpre: TextView = itemView.findViewById(R.id.premiumTxt)
    }
}