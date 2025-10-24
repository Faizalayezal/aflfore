package com.foreverinlove.groupvideocall

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.foreverinlove.databinding.ItemGridVideoBinding
import com.foreverinlove.network.response.GetMemberListData
import io.agora.rtc2.Constants
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas

private const val TAG = "GroupvideocallAdepter"

@SuppressLint("NotifyDataSetChanged")
class GroupvideocallAdepter(
    val context: Activity,
    private var mRtcEngine: RtcEngine?
) : RecyclerView.Adapter<GroupvideocallAdepter.ViewHolder>() {

    private var isFirstTime = true


    // private var users= ArrayList<GroupVideoCallUserList>()
    private var users = ArrayList<GetMemberListData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemGridVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mRtcEngine?.setRemoteDefaultVideoStreamType(Constants.VIDEO_STREAM_LOW)

        holder.apply {

            Log.d(TAG, "onBindViewHolder: $users")

            val uid = users[position].u_id

            if (uid != null) {
                val name = users[position].first_name
                Log.d(
                    TAG,
                    "onBindViewsdffHolder4052: " + name + "--->" + users[position].first_name
                )

                if (name == null || name == "") {
                    binding.txtName.visibility = View.GONE

                } else {
                    binding.txtName.visibility = View.VISIBLE
                    binding.txtName.text = name
                }

            }


            val surfaceView11 = RtcEngine.CreateRendererView(itemView.context)
           // val surfaceView11 = SurfaceViewRenderer(itemView.context)

            // mRtcEngine!!.muteRemoteVideoStream(uid, false)
            if (uid != null) {
                mRtcEngine!!.muteRemoteVideoStream(uid.toInt(), false)
            }

            if (isFirstTime) {
                mRtcEngine!!.enableAudioVolumeIndication(5000, 3, false)
                mRtcEngine!!.adjustRecordingSignalVolume(400)
                isFirstTime = false
            }

            binding.groupvideocallperson.addView(
                surfaceView11,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            /*mRtcEngine!!.setupRemoteVideo(
                VideoCanvas(
                    surfaceView11,
                    VideoCanvas.RENDER_MODE_HIDDEN,
                    uid
                )
            )*/

            mRtcEngine!!.setupRemoteVideo(
                uid?.let {
                    VideoCanvas(
                        surfaceView11,
                        VideoCanvas.RENDER_MODE_HIDDEN,
                        it.toInt()
                    )
                }
            )
            surfaceView11.tag = uid
            Log.d("atoz", "onBindViewHolder: " + uid)

            surfaceView11!!.z = 5F
        }
    }


    override fun getItemCount(): Int {
        return users.size
    }

    /*fun updatelist(uidList: ArrayList<GroupVideoCallUserList>) {
        users.clear()
        users.addAll(uidList)
        notifyDataSetChanged()
        Log.d(TAG, "updatelist: testFlowAbInsideAdapter>>${users.size}")
    }*/

    fun getList(): ArrayList<GetMemberListData> = users

    fun addItem(groupVideoCallUserList: GetMemberListData) {
        users.add(groupVideoCallUserList)
        Log.d(TAG, "updatelist: testFlowAbInsideAdapter>>ADDINGITEM>>${groupVideoCallUserList}")
        notifyDataSetChanged()
    }

    fun removeItem(it: GetMemberListData) {
        users.remove(it)
        notifyDataSetChanged()
    }

    fun updateItem(uId: String?, name: String?) {
        //?: null hoy to return
        val foundItem = users.find { it.u_id == uId } ?: return

        val foundIndex = users.indexOf(foundItem)

        users[foundIndex].first_name = name ?: ""
        //perticuler item refresh krva mate
        notifyItemChanged(foundIndex)
        notifyDataSetChanged()
        // notifyDataSetChanged()
        Log.d(TAG, "updasdsdteItem: " + name)
    }
    /*
        fun updateListData(roomJoinMemberList: List<ConsumeGroupVideoCallRoomJoinMember>?) {
            users.
        }*/

    class ViewHolder(val binding: ItemGridVideoBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(paymentBean: GetMemberListData) {

            binding.txtName.text = paymentBean.first_name ?: ""
        }

    }
}