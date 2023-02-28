package ir.iammrbit.paint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class DrawingView(context: Context , attrs : AttributeSet)
    : View(context , attrs){

    private var mDrawPath : CustomPath? = null
    private var mCanvasBitMap : Bitmap? = null
    private var mDrawPaint : Paint? = null
    private var mCanvasPaint : Paint? = null
    private var mBrushSize : Float = 0F
    var color = Color.BLACK
    private var canvas: Canvas? = null

    init {
        setUpDrawing()
    }


    private fun setUpDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color , mBrushSize )
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20F
    }
















    internal inner class CustomPath(var color: Int , var brushThickness : Float)
        : android.graphics.Path(){

    }

}