package com.foreverinlove.chatmodual

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.foreverinlove.R

sealed class ImageBorderOption {
    object NOTHING : ImageBorderOption()
    object NOTCROP : ImageBorderOption()
    object CIRCLE : ImageBorderOption()
    object NORMAL : ImageBorderOption()
    class RadiusEditProfile(val radiusNew: Int = 10000) : ImageBorderOption()
}

object ImageViewExt {

    fun ImageView.loadImageWithGlide(
        urlLink: String,
        loadOption: ImageBorderOption = ImageBorderOption.NORMAL
    ) {
      //  logIt("loadImageWithGlide: testAb>>$urlLink")

        if (urlLink == "") return

        when (loadOption) {
            is ImageBorderOption.NOTHING -> {
                Glide.with(context).load(urlLink)
                    .placeholder(CircularProgressDrawable(context).apply {
                        strokeWidth = 8f
                        centerRadius = 40f
                        setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.sendcolor2, null))
                        start()
                    })
                    .into(this)
            }
            is ImageBorderOption.NORMAL -> {
                Glide.with(context).load(urlLink)
                    .placeholder(CircularProgressDrawable(context).apply {
                        strokeWidth = 8f
                        centerRadius = 40f
                        setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.sendcolor2, null))
                        start()
                    })
                    .centerCrop()
                    .into(this)
            }
            is ImageBorderOption.NOTCROP -> {
                Glide.with(context).load(urlLink)
                    .placeholder(CircularProgressDrawable(context).apply {
                        strokeWidth = 8f
                        centerRadius = 40f
                        setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.sendcolor2, null))
                        start()
                    })
                    .into(this)
            }
            is ImageBorderOption.CIRCLE -> {
                Glide.with(context).load(urlLink)
                    .placeholder(CircularProgressDrawable(context).apply {
                        strokeWidth = 8f
                        centerRadius = 40f
                        setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.sendcolor2, null))
                        start()
                    })
                    .circleCrop()
                    .into(this)
            }
            is ImageBorderOption.RadiusEditProfile -> {

                val rad:Int=if(loadOption.radiusNew==10000)resources.getDimension(R.dimen.radiusImage).toInt() else loadOption.radiusNew

                Glide.with(context).load(urlLink)
                    .placeholder(CircularProgressDrawable(context).apply {
                        strokeWidth = 8f
                        centerRadius = 40f
                        setColorSchemeColors(ResourcesCompat.getColor(resources, R.color.sendcolor2, null))
                        start()
                    })
                    .centerCrop()
                    .transform(CenterCrop(), RoundedCorners(rad))
                    .into(this)
            }

        }
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
    }

    fun ImageView.setDrawable(int: Int?, loadOption: ImageBorderOption = ImageBorderOption.NORMAL) {
        if (int == null) setImageDrawable(null)

        when (loadOption) {
            is ImageBorderOption.NORMAL -> {
                Glide.with(context).load(int)
                    .into(this)
            }
            is ImageBorderOption.CIRCLE -> {
                Glide.with(context).load(int)
                    .circleCrop()
                    .into(this)
            }
            is ImageBorderOption.RadiusEditProfile -> {
                val rad:Int=if(loadOption.radiusNew==10000)resources.getDimension(R.dimen.radiusImage).toInt() else loadOption.radiusNew

                Glide.with(context).load(int)
                    .centerCrop()
                    .transform(CenterCrop(), RoundedCorners(rad))
                    .into(this)
            }

            else -> {}
        }
    }

    fun ImageView.setBitmap(
        bitmap: Bitmap?,
        loadOption: ImageBorderOption = ImageBorderOption.NORMAL
    ) {
        if (bitmap == null) return


        when (loadOption) {
            is ImageBorderOption.NORMAL -> {
                Glide.with(context).load(bitmap)
                    .into(this)
            }
            is ImageBorderOption.CIRCLE -> {
                Glide.with(context).load(bitmap)
                    .circleCrop()
                    .into(this)
            }
            is ImageBorderOption.RadiusEditProfile -> {
                val rad:Int=if(loadOption.radiusNew==10000)resources.getDimension(R.dimen.radiusImage).toInt() else loadOption.radiusNew
                Glide.with(context).load(bitmap)
                    .centerCrop()
                    .transform(CenterCrop(), RoundedCorners(rad))
                    .into(this)
            }

            else -> {}
        }
    }


}