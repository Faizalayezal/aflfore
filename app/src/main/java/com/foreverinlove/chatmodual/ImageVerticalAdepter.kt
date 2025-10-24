package com.foreverinlove.chatmodual


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.foreverinlove.MediaListActivity
import com.foreverinlove.chatmodual.ImageViewExt.loadImageWithGlide
import com.foreverinlove.databinding.ItemMediaListHorizontalBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.iceteck.silicompressorr.SiliCompressor


class ImageVerticalAdepter(private val list: ArrayList<MediaListActivity.MediaObject>) :
    RecyclerView.Adapter<ImageVerticalAdepter.PaymentHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder {
        val itemBinding = ItemMediaListHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        /* val itemBinding = ActivityWatchBinding.inflate(
             LayoutInflater.from(parent.context),
             parent,
             false
         )*/
        return PaymentHolder(itemBinding, updatePlayerListener)
    }

    override fun onBindViewHolder(holder: PaymentHolder, position: Int) {
        holder.bindNew(list[position], position)
    }

    override fun getItemCount(): Int = list.size

    class PaymentHolder(
        val itemBinding: ItemMediaListHorizontalBinding,
        val updatePlayerListener: UpdatePlayerListener
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {

        /* class PaymentHolder(val itemBinding: ActivityWatchBinding,val updatePlayerListener:UpdatePlayerListener) :
             RecyclerView.ViewHolder(itemBinding.root) {*/

        fun bindNew(data: MediaListActivity.MediaObject, position: Int) {

            Log.d("TAG", "bindNew: testflowTestAdapter>>" + data.string + ">>" + data.isUrl)


            var simpleExoPlayer: SimpleExoPlayer? = null


            if (data.string.contains("videos")) {
                simpleExoPlayer =
                    SimpleExoPlayer.Builder(itemView.context).setSeekBackIncrementMs(5000)
                        .setSeekForwardIncrementMs(5000).build()
                simpleExoPlayer.playWhenReady = true
                itemBinding.idExoPlayerVIew.player = simpleExoPlayer
                //   val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
                val mediaItem = MediaItem.fromUri(data.string)
                // val mediaSource =HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mediaItem)

                simpleExoPlayer.setMediaItem(mediaItem)
                //   exoPlayer.seekTo()
                //  exoPlayer.playWhenReady
                simpleExoPlayer.prepare()
                simpleExoPlayer.play()
                simpleExoPlayer.pause()

                Log.d("playedr", "bindNew: " + simpleExoPlayer)


                val playerListener = object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)

                        updatePlayerListener.onUpdatePlayer(
                            itemBinding.idExoPlayerVIew.player as ExoPlayer,
                            position
                        )

                    }
                }

                simpleExoPlayer.addListener(playerListener)

                itemBinding.apply {
                    idExoPlayerVIew.player = simpleExoPlayer
                    idExoPlayerVIew.visibility = View.VISIBLE

                    imgMain.visibility = View.GONE


                }

            } else {
                itemBinding.apply {

                    Log.d("TAG", "bindNew: testItem>>${java.io.File(data.string).exists()}>>${data.string}")

                    imgMain.loadImageWithGlide(data.string, ImageBorderOption.NOTCROP)
                    idExoPlayerVIew.visibility = View.GONE
                    imgPlayButton.visibility = View.GONE
                    imgMain.visibility = View.VISIBLE
                }
            }


            /* if (data.string.contains("videos")) {
                 val trackSelectorDef: TrackSelector = DefaultTrackSelector()
                 val absPlayerInternal =ExoPlayerFactory.newSimpleInstance(
                     itemBinding.idExoPlayerVIew.context,
                     trackSelectorDef
                 )
                 val defdataSourceFactory =
                     DefaultDataSourceFactory(itemBinding.imgMain.context, "xocherries")
                 val uriOfContentUrl = Uri.parse(data.string)
                 val mediaSource: MediaSource = ProgressiveMediaSource.Factory(defdataSourceFactory)
                     .createMediaSource(uriOfContentUrl)
                 absPlayerInternal.prepare(mediaSource)
                 absPlayerInternal.playWhenReady = false




                 val playerListener=object : Player.EventListener {
                     override fun onIsPlayingChanged(isPlaying: Boolean) {
                         super.onIsPlayingChanged(isPlaying)

                         itemBinding.idExoPlayerVIew.player?.let {
                             updatePlayerListener.onUpdatePlayer(
                                 it, position
                             )
                         }

                     }
                 }

                 absPlayerInternal.addListener(playerListener)

                 itemBinding.apply {
                     idExoPlayerVIew.player = absPlayerInternal
                     idExoPlayerVIew.visibility=View.VISIBLE

                     imgMain.visibility=View.GONE


                 }
             } else {
                 itemBinding.apply {
                     imgMain.loadImageWithGlide(data.string, ImageBorderOption.NOTCROP)
                     idExoPlayerVIew.visibility=View.GONE
                     imgPlayButton.visibility=View.GONE
                     imgMain.visibility=View.VISIBLE
                 }
             }*/


        }
    }


    interface UpdatePlayerListener {
        fun onUpdatePlayer(player: Player, position: Int)
    }

    private val updatePlayerListener = object : UpdatePlayerListener {
        override fun onUpdatePlayer(player: Player, position: Int) {

            if (position != tempPos) {
                tempPlayer?.playWhenReady = false
                tempPlayer?.playbackState
            }

            tempPlayer = player
            tempPos = position
        }
    }

    fun checkIsItemPlaying() {
        tempPlayer?.playWhenReady = false
        tempPlayer?.playbackState
    }

    var tempPos = -1
    var tempPlayer: Player? = null


}