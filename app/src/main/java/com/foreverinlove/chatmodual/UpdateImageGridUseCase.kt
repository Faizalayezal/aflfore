package com.foreverinlove.chatmodual

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.foreverinlove.Constant
import com.foreverinlove.chatmodual.ImageViewExt.loadImageWithGlide
import com.foreverinlove.chatmodual.ImageViewExt.setDrawable
import com.foreverinlove.databinding.ItemChatMessageImageListReceiverBinding
import com.foreverinlove.databinding.ItemChatMessageImageListSenderBinding
import com.foreverinlove.objects.ChatMessageObject


class UpdateImageGridUseCase {

    fun updateData(binder: ItemChatMessageImageListReceiverBinding, data: ChatMessageObject) {
        customUpdate(img1=binder.img1, img2=binder.img2, img3=binder.img3, img4=binder.img4, imgPlay1=binder.imgPlay1, imgPlay2=binder.imgPlay2, imgPlay3=binder.imgPlay3, imgPlay4=binder.imgPlay4, txt4=binder.txt4,txt2=binder.txt2, imgOpacity=binder.imgOpacity,imgOpacity2=binder.imgOpacity2, data=data,clParent1=binder.clParent1,clParent2=binder.clParent2, img11 = binder.img11, img22 = binder.img22, imgPlay11 = binder.imgPlay11, imgPlay22 = binder.imgPlay22)
    }
    fun updateData(binder: ItemChatMessageImageListSenderBinding, data: ChatMessageObject) {
        customUpdate(img1=binder.img1, img2=binder.img2, img3=binder.img3, img4=binder.img4, imgPlay1=binder.imgPlay1, imgPlay2=binder.imgPlay2, imgPlay3=binder.imgPlay3, imgPlay4=binder.imgPlay4, txt4=binder.txt4,txt2=binder.txt2, imgOpacity=binder.imgOpacity,imgOpacity2=binder.imgOpacity2, data=data,clParent1=binder.clParent1,clParent2=binder.clParent2, img11 = binder.img11, img22 = binder.img22, imgPlay11 = binder.imgPlay11, imgPlay22 = binder.imgPlay22)
    }

    private fun customUpdate(img1:ImageView, img2:ImageView, img3:ImageView, img4:ImageView, imgPlay1:ImageView, imgPlay2:ImageView, imgPlay3:ImageView, imgPlay4:ImageView, txt4:TextView,txt2:TextView, imgOpacity:ImageView,imgOpacity2:ImageView, data: ChatMessageObject,clParent1:ConstraintLayout,clParent2:ConstraintLayout,img11:ImageView,img22:ImageView,imgPlay11:ImageView,imgPlay22:ImageView){

       /* img1.setDrawable(null)
        img2.setDrawable(null)
        img3.setDrawable(null)
        img4.setDrawable(null)*/

        imgPlay1.visibility = View.GONE
        imgPlay2.visibility = View.GONE
        imgPlay3.visibility = View.GONE
        imgPlay4.visibility = View.GONE

        imgPlay11.visibility = View.GONE
        imgPlay22.visibility = View.GONE

        if (data.filesArray?.size == 2) {
            clParent2.visibility = View.VISIBLE
            clParent1.visibility = View.GONE
            Log.d("TAG", "customUpdate: "+"aaaaaa")
            img11.setDrawable(null)
            img22.setDrawable(null)
        } else {
            clParent1.visibility = View.VISIBLE
            clParent2.visibility = View.GONE
            img1.setDrawable(null)
            img2.setDrawable(null)
            img3.setDrawable(null)
            img4.setDrawable(null)
            Log.d("TAG", "customUpdate: "+"ssss")

        }

        if (data.filesArray?.size == 3) {
            clParent1.visibility = View.GONE
            clParent2.visibility = View.VISIBLE
            txt2.visibility = View.VISIBLE
            imgOpacity2.visibility = View.VISIBLE
            img11.setDrawable(null)
            img22.setDrawable(null)
        }else if (data.filesArray?.size == 2) {
            clParent1.visibility = View.GONE
            clParent2.visibility = View.VISIBLE
            txt2.visibility = View.GONE
            imgOpacity2.visibility = View.GONE
            img11.setDrawable(null)
            img22.setDrawable(null)
        } else {
            clParent1.visibility = View.VISIBLE
            clParent2.visibility = View.GONE
            txt2.visibility = View.GONE
            imgOpacity2.visibility = View.GONE

        }


        data.filesArray?.forEachIndexed { index, item ->

            when (index) {
                0 -> {
                    if (data.filesArray?.size == 2) {
                        img11.loadImageWithGlide(item.fileString)
                        imgPlay11.visibility =
                            if (Constant.isUrlVideo(item.fileString)) View.VISIBLE
                            else View.GONE

                        data.filesArray!!.forEachIndexed { index, it->
                            Log.d("TAG", "cudddstomUpdate88: "+it+"index->"+index)
                        }
                    } else {
                        img1.loadImageWithGlide(item.fileString)
                        imgPlay1.visibility =
                            if (Constant.isUrlVideo(item.fileString)) View.VISIBLE
                            else View.GONE
                    }
                    if (data.filesArray?.size == 3) {
                        img11.loadImageWithGlide(item.fileString)
                        imgPlay11.visibility =
                            if (Constant.isUrlVideo(item.fileString)) View.VISIBLE
                            else View.GONE
                    } else {
                        img1.loadImageWithGlide(item.fileString)
                        imgPlay1.visibility =
                            if (Constant.isUrlVideo(item.fileString)) View.VISIBLE
                            else View.GONE
                    }
                }
                1 -> {
                    if (data.filesArray?.size == 2) {
                        img22.loadImageWithGlide(item.fileString)
                        imgPlay22.visibility =
                            if (Constant.isUrlVideo(item.fileString)) View.VISIBLE
                            else View.GONE
                        data.filesArray!!.forEachIndexed { index, it->
                            Log.d("TAG", "cudddstomUpdate114: "+it+"index->"+index)
                        }
                    } else {
                        img2.loadImageWithGlide(item.fileString)
                        imgPlay2.visibility =
                            if (Constant.isUrlVideo(item.fileString)) View.VISIBLE
                            else View.GONE
                    }

                    if (data.filesArray?.size == 3) {
                        img22.loadImageWithGlide(item.fileString)
                        imgPlay22.visibility =
                            if (Constant.isUrlVideo(item.fileString)) View.VISIBLE
                            else View.GONE
                    } else {
                        img2.loadImageWithGlide(item.fileString)
                        imgPlay2.visibility =
                            if (Constant.isUrlVideo(item.fileString)) View.VISIBLE
                            else View.GONE
                    }
                }
                2 -> {
                    img3.loadImageWithGlide(item.fileString)
                    imgPlay3.visibility =
                        if (Constant.isUrlVideo(item.fileString)) View.VISIBLE
                        else View.GONE
                }
                3 -> {
                    img4.loadImageWithGlide(item.fileString)
                    imgPlay4.visibility =
                        if (Constant.isUrlVideo(item.fileString)) View.VISIBLE
                        else View.GONE
                }

            }
        }

        if ((data.filesArray?.size ?: 0) > 4) {
            val ab = (data.filesArray?.size ?: 0) - 4
            txt4.text = "+$ab"
            imgOpacity.visibility = View.VISIBLE
        } else {
            txt4.text = ""
            imgOpacity.visibility = View.GONE
        }
    }
}