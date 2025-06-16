package com.example.zebrasdk.camera.mlkit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View

class BarcodeBoxView(
    context: Context
) : View(context) {

    private var resultState=false
    private val paint = Paint()

    private var mRect = RectF()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val cornerRadius = 10f
        paint.style = Paint.Style.STROKE
        paint.color =if (resultState)Color.GREEN else Color.RED
        paint.strokeWidth = 5f
        canvas?.drawRoundRect(mRect, cornerRadius, cornerRadius, paint)
    }

    fun setRect(rect: RectF,resultState:Boolean) {
        this.resultState = resultState
        mRect = rect
        invalidate()
        requestLayout()
    }
}