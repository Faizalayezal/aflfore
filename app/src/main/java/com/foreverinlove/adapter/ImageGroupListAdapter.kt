package com.foreverinlove.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.network.response.RoomMemberList

class ImageGroupListAdapter(
    val context: Activity,
    val Listner: onClick,
    val id: Int,
    private var newusers: ArrayList<RoomMemberList>
) :
    RecyclerView.Adapter<ImageGroupListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.itemgroupchatperson, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            /* if (newusers[position].isUserOnline=="1") {
                 userindector.visibility=View.VISIBLE
             }
             else{
                 userindector.visibility=View.GONE
             }*/
            userindector.visibility = if (newusers[position].isUserOnline == "1") View.VISIBLE
            else View.GONE

            if ((newusers[position].user?.id ?: "") == id) {
                username.text = "You"
            } else {
                username.text = newusers[position].user?.first_name

            }

            Glide.with(userimg.context)
                .load(newusers[position].user?.user_images?.firstOrNull()?.url).into(userimg)

            userimg.setOnClickListener {
                Listner.OpenPop(newusers[position], position)
            }

        }
    }


    interface onClick {
        fun OpenPop(data: RoomMemberList, position: Int)
    }

    override fun getItemCount(): Int {
        return newusers.size
    }

    fun replesList(it: List<RoomMemberList>) {
        val ab = ArrayList<RoomMemberList>(it)
        newusers = ab
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimg: ImageView = itemView.findViewById(R.id.user_img)
        val userindector: ImageView = itemView.findViewById(R.id.user_indector)
        val username: TextView = itemView.findViewById(R.id.user_name)

    }
}


