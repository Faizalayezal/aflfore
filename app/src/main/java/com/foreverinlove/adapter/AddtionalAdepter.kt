package com.foreverinlove.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.foreverinlove.R
import com.foreverinlove.dialog.ChipGroupHelper
import com.foreverinlove.network.response.AddtionalQueObject
import com.foreverinlove.objects.PhaseListObject
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.ChipGroup

enum class PhaseType {
    Phase1,
    Phase2,
    Phase3,
    Phase4,
    AddtionalFilter,

}

private const val TAG = "AddtionalAdepter"
@SuppressLint("NotifyDataSetChanged")
class AddtionalAdepter(
    val context: Activity,
    val listenerTop : SelectedListener,
    private var likesuser: ArrayList<PhaseListObject>,
    private val phaseType: PhaseType,
    ) :
    RecyclerView.Adapter<AddtionalAdepter.Viewholder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_phase1, parent, false)
        return Viewholder(view)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {

        holder.apply {

            username.text = likesuser[position].name

            likesuser[position].list?.let {
                setChipLayout(
                    chipList, it, likesuser[position].maxSelected, likesuser[position].phase,
                    likesuser[position].name!!,likesuser[position].alreadySelectedIds
                )
            }

            border.strokeColor = when (phaseType) {
                PhaseType.Phase1 -> ContextCompat.getColor(context, R.color.datapiker)
                PhaseType.Phase2 -> ContextCompat.getColor(context, R.color.datapiker)
                PhaseType.Phase3 -> ContextCompat.getColor(context, R.color.datapiker)
                PhaseType.Phase4 -> ContextCompat.getColor(context, R.color.datapiker)
                PhaseType.AddtionalFilter -> ContextCompat.getColor(context, R.color.datapiker)
            }
        }


    }

    interface SelectedListener{
        fun onSelected(selectedIdList:ArrayList<String>,title:String,
                       allDataList: List<AddtionalQueObject>)
    }


    override fun getItemCount(): Int {
        return likesuser.size
    }

    private fun setChipLayout(
        chipGroup: ChipGroup, listOf: List<AddtionalQueObject>, maxCount: Int,
        color: ChipGroupHelper.StyleTypes, title: String,alreadySelectedIds:String

    ) {

        val listener = object : ChipGroupHelper.ChipSelectedListener {
            override fun onSelectedListChange(list: ArrayList<String>) {

                list.forEach {
                    Log.d(TAG, "onSelectedListChange: testItemSelected>>$it")
                }


                listenerTop.onSelected(list,title,listOf)
            }
        }

        var selectedId1 = ""
        var selectedId2 = ""
        var selectedId3 = ""

        alreadySelectedIds.split(",").forEachIndexed { index, string ->
            when (index) {
                0 -> selectedId1 = string
                1 -> selectedId2 = string
                2 -> selectedId3 = string
            }
        }

        ChipGroupHelper.Builder(context)
            .setChipLayout(chipGroup)
            .setSelectedListener(listener)
            .setList(
                ChipGroupHelper.ListType.ParentList(listOf),
            )
            .setMaxSelected(maxCount)
            .setCountTextView(null)
            .setAlreadySelectedIds(selectedId1, selectedId2, selectedId3)
            .setBottomSheet(false)
            .setClickable(true)
            .setIsCurrentBuilderChild(false)
            .setCloseIconVisible(false)
            .setRemoveListener(null)
            .setStyleColor(color)
            .setViewsForClickedNewInterestPicker(null, null)
            .build()
    }


    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.name)
        val chipList: ChipGroup = itemView.findViewById(R.id.smoke)
        val border: MaterialCardView = itemView.findViewById(R.id.ps1)

    }

}