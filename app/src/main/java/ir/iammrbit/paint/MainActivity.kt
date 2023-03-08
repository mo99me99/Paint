package ir.iammrbit.paint

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.get
import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import ir.iammrbit.paint.databinding.ActivityMainBinding

//    TODO private var mImageButtonCurrentBrushSize : ImageButton? = null
class MainActivity : AppCompatActivity() {
    private var drawingView:DrawingView? = null
    private var mImageButtonCurrentPaint : ImageButton? = null
    private val openGalleryLauncher : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if (result.resultCode == RESULT_OK && result.data!=null){
                val imageBG : ImageView = binding.ivBackground
                imageBG.setImageURI(result.data?.data)
            }
        }

    private val requestPermission : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions ->
                permissions.entries.forEach{
                    val permissionName = it.key
                    val isGranted = it.value
                    if (isGranted){
                        Toast.makeText(
                            this ,
                            "Permission granted. Now you can read the storage files."
                            ,Toast.LENGTH_SHORT)
                            .show()
                        val pickIntent = Intent(
                            Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        openGalleryLauncher.launch(pickIntent)


                    }else{
                        if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                            Toast.makeText(
                                this
                                , "Oops you just didn't accept the permission."
                                ,Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }

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

        val ibGallery : ImageButton = binding.ibGallery
        ibGallery.setOnClickListener {
        requestStoragePermission()
        }




    }
    private fun requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this
            ,Manifest.permission.READ_EXTERNAL_STORAGE)
        ){
            showRationalDialog("Paint" , "Paint needs to Access your External Storage")
        }else{
            requestPermission.launch(arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ))//TODO - add writing external storage
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
    private fun showRationalDialog(
        title : String,
        message : String
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}