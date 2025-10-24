package com.foreverinlove.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.foreverinlove.databinding.ItemImageverticalBinding
import com.foreverinlove.objects.ImageOrVideoData
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DefaultAllocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class ImageListHorizontalAdapter(private val imgUrls: ArrayList<ImageOrVideoData>) :
    RecyclerView.Adapter<ImageListHorizontalAdapter.PaymentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder {
        val itemBinding =
            ItemImageverticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentHolder(itemBinding)
    }

    var simpleExoPlayer: SimpleExoPlayer? = null
    override fun onBindViewHolder(holder: PaymentHolder, position: Int) {
        holder.bindNew(imgUrls[position])
        holder.apply {

            if (imgUrls[position].isVideo) {

                CoroutineScope(Dispatchers.Main).launch {
                    Glide.with(itemBinding.userimage.context).load(imgUrls[position].imageUrl)
                        .override(800, 800)
                        .into(itemBinding.userimage)
                   /* val exoPlayer =
                        ExoPlayer.Builder(itemBinding.userimage.context).setRenderersFactory(
                            DefaultRenderersFactory(itemBinding.userimage.context).setEnableDecoderFallback(
                                true
                            )
                        ).build()*/

                    simpleExoPlayer =
                        SimpleExoPlayer.Builder(itemBinding.userimage.context,DefaultRenderersFactory(itemBinding.userimage.context).setEnableDecoderFallback(true))
                            .setLoadControl(DefaultLoadControl()).build()
                    itemBinding.playerView.visibility = View.VISIBLE
                    itemBinding.playerView.player = simpleExoPlayer
                    itemBinding.playerView.keepScreenOn = true


                    val mediaItem = MediaItem.fromUri(imgUrls[position].imageUrl)
                    simpleExoPlayer?.setMediaItem(mediaItem)
                    simpleExoPlayer?.prepare()
                    simpleExoPlayer?.play()
                    simpleExoPlayer?.pause()
                }

                //redme.Ximee ma notu chaltu
                /* simpleExoPlayer =
                     SimpleExoPlayer.Builder(itemBinding.userimage.context).build()
                 itemBinding.playerView.player = simpleExoPlayer
                 itemBinding.playerView.keepScreenOn = true

                 val mediaItem = MediaItem.fromUri(imgUrls[position].imageUrl)
                 simpleExoPlayer?.setMediaItem(mediaItem)
                 simpleExoPlayer?.prepare()
                 simpleExoPlayer?.play()
                 simpleExoPlayer?.pause()*/


                //media3
                /*Glide.with(itemBinding.userimage.context).load(imgUrls[position].imageUrl)
                        .override(800, 800)
                        .into(itemBinding.userimage)
                    itemBinding.playerView.visibility = View.VISIBLE
                    val player = ExoPlayer.Builder(itemBinding.userimage.context).build()

                    itemBinding.playerView.player = player
                    val mediaItem = androidx.media3.common.MediaItem.Builder()
                        .setUri(imgUrls[position].imageUrl)
                        .setMimeType(MimeTypes.APPLICATION_MP4)
                        .build()
                    /* val mediaSource = androidx.media3.exoplayer.source.ProgressiveMediaSource.Factory(
                         DefaultDataSource.Factory(itemBinding.userimage.context)
                     ).createMediaSource(mediaItem)*/
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()*/


            } else {
                Glide.with(itemBinding.userimage.context).load(imgUrls[position].imageUrl)
                    .override(800, 800)
                    .into(itemBinding.userimage)
            }

        }

    }


    override fun getItemCount(): Int = imgUrls.size
    fun SampleAdapter() {
        simpleExoPlayer?.pause()
    }

    fun offVideo() {
        simpleExoPlayer?.pause()

    }

    fun onVideo() {
        simpleExoPlayer?.play()

    }


    class PaymentHolder(val itemBinding: ItemImageverticalBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindNew(data: ImageOrVideoData) {

        }

    }


}




