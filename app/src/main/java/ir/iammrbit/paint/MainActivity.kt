package ir.iammrbit.paint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }
}