package com.app.taocalligraphy.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.app.taocalligraphy.R

class ArcViewLeft : View {

    var arcHeight = 0f
    var arcWidth = 0f
    var contextArc: Context? = null


    constructor(context: Context?) : super(context) {
        this.contextArc = context

    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        this.contextArc = context

    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.contextArc = context
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        arcWidth = w.toFloat()
        arcHeight = h.toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        /*val paint = Paint()
        paint.apply {
            color = Color.LTGRAY
            strokeWidth = 40f
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
        val arrowPath = Path()

        val rectF = RectF(
            arcWidth * 0.20f,
            0f,
            arcWidth*0.80f,
            arcHeight
        )
        arrowPath.addArc(rectF, 90f, 175f)
        canvas?.drawPath(arrowPath, paint)*/

        val x1: Int = (arcWidth / 2).toInt() + 60
        val y1: Int = -18
        val x2: Int = (arcWidth / 2).toInt() + 60
        val y2: Int = arcHeight.toInt() + 18

        val paint = Paint()
        paint.apply {
            color = contextArc?.let { ContextCompat.getColor(it, R.color.shimmer_light) } ?: run {
                Color.LTGRAY
            }
            strokeWidth = if (context.resources.getBoolean(R.bool.isTablet))
                20f
            else
                40f
            style = Paint.Style.STROKE
            isDither = true
            strokeJoin = Paint.Join.BEVEL
            strokeCap = Paint.Cap.SQUARE
            isAntiAlias = true
        }

        val path = Path()
        val midX: Int = x1 + ((x2 - x1) / 2)
        val midY: Int = y1 + ((y2 - y1) / 2)
        val xDiff: Float = (midX - x1).toFloat()
        val yDiff: Float = (midY - y1).toFloat()
        val angle = Math.atan2(yDiff.toDouble(), xDiff.toDouble()) * (180 / Math.PI) - 90
        val angleRadians = Math.toRadians(angle)
        val pointX = (midX - (arcWidth * 0.60f) * Math.cos(angleRadians)).toFloat()
        val pointY = (midY - (arcWidth * 0.60f) * Math.sin(angleRadians)).toFloat()

        path.moveTo(x1.toFloat(), y1.toFloat());
        path.cubicTo(x1.toFloat(), y1.toFloat(), pointX, pointY, x2.toFloat(), y2.toFloat());

        canvas?.drawPath(path, paint);

        /*
        /right: 540 0 1350.0 378.0 540 756
        /left: 540 0 -270.0 378.0 540 756
        *
        * */
    }
}