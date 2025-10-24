package com.foreverinlove.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.foreverinlove.R
import com.foreverinlove.dialog.ChipGroupHelper
import com.foreverinlove.network.response.AddtionalQueObject
import com.foreverinlove.network.response.ChildHobbyData
import com.foreverinlove.network.response.HobbiesListData
import com.foreverinlove.objects.PhaseListObject
import com.google.android.material.chip.ChipGroup

class AddtionalFilterAdepter(
    val context: Activity,
    private val Listener: OnListScroll,
    private var users: ArrayList<ChildHobbyData>
) :
    RecyclerView.Adapter<AddtionalFilterAdepter.AdditionalViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdditionalViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_addional_filter, parent, false)
        return AdditionalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdditionalViewHolder, position: Int) {
        holder.apply {
            txtName.text = users[position].sub_hobbie_name

            val newPos: Int = if (users.size == (position + 1)) position
            else position + 1

         /*   setChipLayout(
                txtchip, users[position].data, users[position].maxCount, newPos
            )
*/

        }
    }
    /*private val itemEventListener = object : Onlistscrool{
        override fun onClick(position: Int) {
            Listener.onClick(position)
        }


    }*/


    interface OnListScroll {
        fun onClick(position: Int)
    }


    override fun getItemCount(): Int {
        return users.size
    }

    inner class AdditionalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txtName: TextView = itemView.findViewById(R.id.title)
        var txtchip: ChipGroup = itemView.findViewById(R.id.chip)

    }
}