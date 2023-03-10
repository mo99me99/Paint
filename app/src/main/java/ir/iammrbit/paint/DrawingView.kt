package ir.iammrbit.paint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.util.Stack


class DrawingView(context: Context , attrs : AttributeSet)
    : View(context , attrs){

    private var mDrawPath : CustomPath? = null
    private var mCanvasBitMap : Bitmap? = null
    private var mDrawPaint : Paint? = null
    private var mCanvasPaint : Paint? = null
    private var mBrushSize : Float = 1F
    var color = Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPaths = Stack<CustomPath>()


    init {
        setUpDrawing()
    }

    fun onClickUndo(){
        if (mPaths.isNotEmpty()){
            mUndoPaths.push(mPaths.removeLast())
            invalidate()
        }
    }
    fun onClickRedo(){
        if (mUndoPaths.isNotEmpty()) {
            mPaths.add(mUndoPaths.pop())
            invalidate()
        }
    }


    private fun setUpDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color , mBrushSize )
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
//        mBrushSize = 20F
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitMap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitMap!!)
    }


    //Change Canvas to Canvas? if fails
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitMap!! ,0F,0F  , mCanvasPaint)

        for (path in mPaths){
            mDrawPaint!!.strokeWidth = path!!.brushThickness
            mDrawPaint!!.color = path!!.color
            canvas.drawPath(path!!, mDrawPaint!!)
        }

        if (!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y
        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!! ,touchY!! )
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath!!.lineTo(touchX!! ,touchY!! )
            }
            MotionEvent.ACTION_UP -> {
                if (mUndoPaths.isNotEmpty())
                    mUndoPaths.removeLast()
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize )
            }
            else -> return false
        }
        invalidate()
        return true

    }

    fun setSizeForBrush(newSize : Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP
            , newSize,resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun setColor(newColor: String){
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }
    internal inner class CustomPath(var color: Int , var brushThickness : Float)
        : android.graphics.Path(){

    }

}