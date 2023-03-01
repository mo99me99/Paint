package ir.iammrbit.paint

import android.app.Dialog
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import ir.iammrbit.paint.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var drawingView:DrawingView? = null

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        drawingView = binding.drawingView
        drawingView?.setSizeForBrush(20F)

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
}