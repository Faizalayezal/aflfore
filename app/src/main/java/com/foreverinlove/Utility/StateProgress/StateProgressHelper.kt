package com.foreverinlove.utility.stateProgress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.foreverinlove.R
import de.hdodenhof.circleimageview.CircleImageView


class StateProgressHelper {

    class Builder {

        private lateinit var mainView: LinearLayout
        private var activeColor: Int = R.color.phaseColor1
        private var notActiveColor: Int = R.color.progress_color
        private var maxItems: Int = 3
        private var selectedItem: Int = 1
        private var isAlreadyBuild = false

        fun build():Builder{

            mainView.removeAllViews()

            if(maxItems>=selectedItem){
                for(i in 1..maxItems){
                    val inflater = LayoutInflater.from(mainView.context)
                    val view: View =
                        inflater.inflate(R.layout.item_state_progress, mainView as ViewGroup, false)

                    val lineView = view.findViewById<View>(R.id.line)
                    val circleView = view.findViewById<CircleImageView>(R.id.imgCircleIndicator)

                    if(i==1) lineView.visibility = View.GONE
                    else lineView.visibility = View.VISIBLE

                    if(i<=selectedItem){
                        lineView.setBackgroundColor(ContextCompat.getColor(mainView.context,activeColor))
                        circleView.setColorFilter(ContextCompat.getColor(mainView.context,activeColor))
                    }else{
                        lineView.setBackgroundColor(ContextCompat.getColor(mainView.context,notActiveColor))
                        circleView.setColorFilter(ContextCompat.getColor(mainView.context,notActiveColor))
                    }

                    mainView.addView(view)
                }
            }

            isAlreadyBuild = true
            return this
        }


        fun setHorizontalMainView(mainView: LinearLayout):Builder {
            this.mainView = mainView
            return this
        }

        fun setActiveColor(int: Int):Builder{
            activeColor = int
            return this
        }

        fun setMaxItems(int: Int):Builder{
            maxItems = int
            return this
        }

        fun setSelected(int: Int):Builder{
            selectedItem=int

            if(isAlreadyBuild){
                build()
            }

            return this
        }
    }

}