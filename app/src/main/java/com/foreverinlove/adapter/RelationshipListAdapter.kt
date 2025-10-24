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
import com.foreverinlove.network.response.AddtionalQueObject
import com.foreverinlove.objects.NotificationList
import com.foreverinlove.objects.PhaseListObject

class RelationshipListAdapter(
    val context: Activity,
    private var newusers: ArrayList<AddtionalQueObject>
) :
    RecyclerView.Adapter<RelationshipListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.itemreltionship, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            txtrelationship.text = newusers[position].title



        }
    }

    override fun getItemCount(): Int {
        return newusers.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtrelationship: TextView = itemView.findViewById(R.id.txtraletionship)
    }
}