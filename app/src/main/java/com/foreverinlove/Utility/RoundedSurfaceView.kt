package com.foreverinlove.Utility

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.view.SurfaceView

class RoundedSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {

    private val clipPath = Path()

    init {
        setWillNotDraw(false)
    }

    /*override fun onDraw(canvas: Canvas?) {
        canvas?.clipPath(clipPath)
        super.onDraw(canvas)
    }*/

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        clipPath.reset()
        clipPath.addCircle(w / 2f, h / 2f, Math.min(w, h) / 2f, Path.Direction.CW)
        clipPath.close()
    }
}