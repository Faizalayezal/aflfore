package com.foreverinlove.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreverinlove.R
import com.foreverinlove.objects.HobbyList


@SuppressLint("NotifyDataSetChanged")
class HobbyAdepter(
    val context: Activity,
    private val Listener: hoobyclick,
    private var likesuser: ArrayList<HobbyList>
) :
    RecyclerView.Adapter<HobbyAdepter.Viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_hobby, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        holder.apply {
            username.text = likesuser[position].hobbyrname

            rootview.setOnClickListener {
                Listener.onClick(likesuser[position])
            }

        }


    }
    interface hoobyclick{
        fun onClick(hobbyList: HobbyList)
    }

    override fun getItemCount(): Int {
        return likesuser.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.hobbyname)
        val rootview: LinearLayout=itemView.findViewById(R.id.rootView)

    }
   /* fun updatelist(newlist:List<HobbyList>){
        likesuser.clear()
        likesuser.addAll(newlist)
        notifyDataSetChanged()

    }*/
}