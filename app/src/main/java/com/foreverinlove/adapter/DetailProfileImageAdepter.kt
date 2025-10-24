package com.foreverinlove.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.R
import com.foreverinlove.network.response.DiscoverData
import com.foreverinlove.network.response.RoomList
import com.foreverinlove.objects.ImageOrVideoData

class DetailProfileImageAdepter(
    private val context: Context,
    private val imgUrls: ArrayList<ImageOrVideoData>,
) :


    RecyclerView.Adapter<DetailProfileImageAdepter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_imagevertical, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
//        Log.e("URLS: ", ImgUrls[i])
        val videoUri=imgUrls[i].isVideo

        if (imgUrls[i].isVideo) {
            Glide.with(context).load(imgUrls[i].imageUrl).override(800, 800)
                .into(viewHolder.img_android)
            viewHolder.play.visibility = View.VISIBLE
            viewHolder.play.setOnClickListener {

            }

        } else {

            Glide.with(context).load(imgUrls[i].imageUrl).override(800, 800)
                .into(viewHolder.img_android)
        }


    }

    override fun getItemCount(): Int {
        return imgUrls.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var img_android: ImageView
        var play: ImageView

        init {
            img_android = view.findViewById<View>(R.id.userimage) as ImageView
            play = view.findViewById<View>(R.id.play) as ImageView
        }
    }

}