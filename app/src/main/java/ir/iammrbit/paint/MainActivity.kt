package ir.iammrbit.paint

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import ir.iammrbit.paint.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


//    TODO private var mImageButtonCurrentBrushSize : ImageButton? = null
class MainActivity : AppCompatActivity() {
    val LifecycleOwner.lifecycleScope: LifecycleCoroutineScope
        get() = lifecycle.coroutineScope
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
        val ibUndo : ImageButton = binding.ibUndo
        ibUndo.setOnClickListener {
            drawingView?.onClickUndo()
        }
        val ibRedo : ImageButton = binding.ibRedo
        ibRedo.setOnClickListener {
            drawingView?.onClickRedo()
        }
        val saveBtn : ImageButton = binding.ibSave
        saveBtn.setOnClickListener {
            if (isReadStorageAllowed()){
                lifecycleScope.launch{
                    val flDrawingView:FrameLayout = binding.flDrawingViewContainer
                    saveBitMapFile(getBitMapFromView(flDrawingView))
                }
            }
        }




    }

    private fun isReadStorageAllowed() : Boolean {
        val result = ContextCompat.checkSelfPermission(this
            , Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
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
                ,Manifest.permission.WRITE_EXTERNAL_STORAGE
            ))
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
    private fun getBitMapFromView(view: View) : Bitmap {
        val returnedBitMap = Bitmap.createBitmap(view.width , view.height , Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitMap)
        val bgDrawable = view.background
        if (bgDrawable != null)
            bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitMap
    }

    private suspend fun saveBitMapFile(mBitmap: Bitmap?) : String{
        var result = ""
        withContext(Dispatchers.IO){
            if (mBitmap != null){
                try {
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG , 90, bytes)

                    val f = File(externalCacheDir?.absoluteFile.toString()
                        + File.separator + "PaintApp" + System.currentTimeMillis()/1000 + ".png")

                    val fo = FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()

                    result = f.absolutePath

                    runOnUiThread{
                        if (result.isNotEmpty()){
                            Toast.makeText(this@MainActivity
                            ,"File saved successfully : $result"
                            ,Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@MainActivity
                                ,"Something went wrong while saving the file !" +
                                        "Please feedback to @mo99me99 on telegram"
                                ,Toast.LENGTH_LONG).show()
                        }
                    }

                }catch (e : Exception){
                    e.printStackTrace()
                }
            }
        }
        return result
    }

}