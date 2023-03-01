package ir.iammrbit.paint

import android.app.Dialog
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import ir.iammrbit.paint.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var drawingView:DrawingView? = null

    private var mImageButtonCurrentPaint : ImageButton? = null

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        drawingView = binding.drawingView
        drawingView?.setSizeForBrush(20F)

        mImageButtonCurrentPaint = binding.llColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this , R.drawable.pallet_pressed))


        val ibBrush : ImageButton = binding.ibBrush
        ibBrush.setOnClickListener {
            showBrushSizeChooserDialog()
        }


    }

    private fun showBrushSizeChooserDialog(){
        var brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush size : ")

        val smallBtn : ImageButton= brushDialog.findViewById<ImageButton>(R.id.ib_small_brush)
        smallBtn.setOnClickListener{
            drawingView?.setSizeForBrush(10F)
            brushDialog.dismiss()
        }

        val mediumBtm : ImageButton= brushDialog.findViewById<ImageButton>(R.id.ib_medium_brush)
        mediumBtm.setOnClickListener{
            drawingView?.setSizeForBrush(20F)
            brushDialog.dismiss()
        }

        val largeBtn : ImageButton= brushDialog.findViewById<ImageButton>(R.id.ib_large_brush)
        largeBtn.setOnClickListener{
            drawingView?.setSizeForBrush(30F)
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    fun paintClicked(view :View){
//        Toast.makeText(this , "Clicked paint " , Toast.LENGTH_SHORT).show()
        if (view == mImageButtonCurrentPaint)
            return //do nothing
        val imageButton = view as ImageButton
        val colorTag = imageButton.tag.toString()
        drawingView?.setColor(colorTag)
        imageButton.setImageDrawable(
            ContextCompat.getDrawable(this , R.drawable.pallet_pressed)
        )
        mImageButtonCurrentPaint?.setImageDrawable(ContextCompat.getDrawable(this , R.drawable.pallet_normal))
        mImageButtonCurrentPaint = view

    }
}