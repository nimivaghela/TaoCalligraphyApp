package com.app.taocalligraphy.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.app.taocalligraphy.R

class TriangleBackgroundView : View {
    var paint: Paint? = null
    var bgPaint: Paint? = null
    var contextArc: Context? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        init()
    }

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context) {
        init()
    }

    private fun init() {
        paint = Paint()
        paint!!.strokeWidth = 1f
        paint!!.isAntiAlias = true
        paint!!.strokeCap = Paint.Cap.SQUARE
        paint!!.style = Paint.Style.FILL
        paint!!.color = resources.getColor(R.color.white)

        bgPaint = Paint()
        bgPaint!!.setStyle(Paint.Style.FILL)
        bgPaint!!.setColor(resources.getColor(R.color.gold))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val h = measuredHeight
        val w = measuredWidth

        canvas!!.drawRect(0f, 0f, w.toFloat(), h.toFloat(), bgPaint!!)

        val path = Path()
        path.moveTo(0f, h.toFloat())
        path.lineTo(w.toFloat(), h.toFloat())
        path.lineTo(0f, 0f)
        path.lineTo(0f, h.toFloat())
        path.close()
        canvas!!.drawPath(path, paint!!)
    }


}