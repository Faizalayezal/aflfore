package com.foreverinlove.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.network.response.MessageConversationList


class ItemChatListAdapter(
    val context: Activity,
    private val listener: onClick,
    val isEmptyUpdater: (Boolean) -> Unit
) :
    RecyclerView.Adapter<ItemChatListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_chat_newuser_list, parent, false)
        return ViewHolder(view)
    }

    private var newusers: List<MessageConversationList>? = null
    fun setListData(newusers: List<MessageConversationList>) {
        this.newusers = newusers

        notifyDataSetChanged()

        Log.d("TAG", "setListData: testflowflag>>" + this.newusers?.isEmpty())

        isEmptyUpdater.invoke(this.newusers?.isEmpty() == true)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemData = newusers?.getOrNull(position)

        holder.apply {


            username.text = itemData?.user_name ?: ""

            userindector.visibility = if (itemData?.isUserOnline == "1") View.VISIBLE
            else View.GONE

            Glide.with(userimg.context).load(itemData?.image?.firstOrNull()?.url).into(userimg)

            //Log.d("matchid", "onBindViewHolder: " + itemData?.match_id)

            userimg.setOnClickListener {
                itemData?.let {
                    listener.openDetail(it, position)
                }
            }

            /*  var type: String
              val queryCategory: Query =
                  FirebaseDatabase.getInstance().reference.child("onlineStatus")
              queryCategory.keepSynced(true)
              queryCategory.addValueEventListener(object : ValueEventListener {
                  override fun onDataChange(snapshot: DataSnapshot) {
                      if (snapshot.hasChild("onlineStatus")) {
                          type = snapshot.child("onlineStatus").child("status").value.toString()
                          if (type == "0") {
                              userindector.visibility = View.VISIBLE
                          } else {
                              userindector.visibility = View.GONE
                          }

                      }


                  }

                  override fun onCancelled(error: DatabaseError) {

                  }

              })*/


        }
    }

    override fun getItemCount(): Int {
        return newusers?.size ?: 0
    }

    interface onClick {
        fun openDetail(data: MessageConversationList, position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userimg: ImageView = itemView.findViewById(R.id.user_img)
        val userindector: ImageView = itemView.findViewById(R.id.user_indector)
        val username: TextView = itemView.findViewById(R.id.user_name)
        val const: ConstraintLayout = itemView.findViewById(R.id.constrent)


    }
}