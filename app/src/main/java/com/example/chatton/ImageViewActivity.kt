package com.example.chatton

import android.app.DownloadManager
import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import jp.wasabeef.picasso.transformations.ColorFilterTransformation
import java.io.*
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.*
import java.util.jar.Manifest


class ImageViewActivity : AppCompatActivity() {

    lateinit var imageUrl:String
    lateinit var copyImage:String
    var height:Int = 0
    var width:Int = 0
    lateinit var newImage:Bitmap
    var WRITE_EXTERNAL_STORAGE_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        val download_buton:Button= findViewById(R.id.download_buton)
        val orginal_filter:ImageView=findViewById(R.id.orginal_filter)
        val blur_filter:ImageView=findViewById(R.id.blur_filter)
        val color_red_filter:ImageView=findViewById(R.id.color_red_filter)
        val grayscale_filter:ImageView=findViewById(R.id.grayscale_filter)
        val blue_filter:ImageView=findViewById(R.id.blue_filter)
        val green_filter:ImageView=findViewById(R.id.green_filter)
        val black_filter:ImageView=findViewById(R.id.black_filter)
        val white_filter:ImageView=findViewById(R.id.white_filter)

        val image_viewer= findViewById<ImageView>(R.id.image_viewer)
        imageUrl = intent.getStringExtra("url")
        Picasso.get().load(imageUrl).into(image_viewer)

        Glide.with(this).load(imageUrl).into(orginal_filter);
        Picasso.get().load(imageUrl).transform(BlurTransformation(this, 25, 1)).into(blur_filter)
        Picasso.get().load(imageUrl).transform(ColorFilterTransformation(Color.argb(90,255,0,0))).into(color_red_filter)
        Picasso.get().load(imageUrl).transform(ColorFilterTransformation(Color.argb(90,0,0,255))).into(blue_filter)
        Picasso.get().load(imageUrl).transform(ColorFilterTransformation(Color.argb(90,0,255,0))).into(green_filter)
        Picasso.get().load(imageUrl).transform(ColorFilterTransformation(Color.argb(90,0,0,0))).into(black_filter)
        Picasso.get().load(imageUrl).transform(ColorFilterTransformation(Color.argb(90,255,255,255))).into(white_filter)
        Glide.with(this).load(imageUrl).into(grayscale_filter);
        val colorMatrix =  ColorMatrix();
        colorMatrix.setSaturation(0.0f);
        val filter =  ColorMatrixColorFilter(colorMatrix);
        grayscale_filter.colorFilter = filter;




        orginal_filter.setOnClickListener(View.OnClickListener {
            Glide.with(this).clear(image_viewer)
            Glide.with(this).load(imageUrl).into(image_viewer);
        })

        blur_filter.setOnClickListener(View.OnClickListener {
            Glide.with(this).clear(image_viewer)
            Picasso.get()
                .load(imageUrl)
                .transform(BlurTransformation(this, 25, 1))
                .into(image_viewer) })


       grayscale_filter.setOnClickListener(View.OnClickListener {
           Glide.with(this).load(imageUrl).into(grayscale_filter);
           val colorMatrix =  ColorMatrix();
           colorMatrix.setSaturation(0.0f);
           val filter =  ColorMatrixColorFilter(colorMatrix);
           image_viewer.colorFilter = filter;
       })

        color_red_filter.setOnClickListener(View.OnClickListener {
            Picasso.get().load(imageUrl).transform(ColorFilterTransformation(Color.argb(90,255,0,0))).into(image_viewer)
        })

        blue_filter.setOnClickListener(View.OnClickListener {
            Picasso.get().load(imageUrl).transform(ColorFilterTransformation(Color.argb(90,0,0,255))).into(image_viewer)
        })
        green_filter.setOnClickListener(View.OnClickListener {
            Picasso.get().load(imageUrl).transform(ColorFilterTransformation(Color.argb(90,0,255,0))).into(image_viewer)
        })
        black_filter.setOnClickListener(View.OnClickListener {
            Picasso.get().load(imageUrl).transform(ColorFilterTransformation(Color.argb(90,0,0,0))).into(image_viewer)

        })
        white_filter.setOnClickListener(View.OnClickListener {
            Picasso.get().load(imageUrl).transform(ColorFilterTransformation(Color.argb(90,255,255,255))).into(image_viewer)

        })

        download_buton.setOnClickListener(View.OnClickListener {
            DownloadAndSaveImageTask(this).execute(imageUrl)







            /*
            val bitmap:Bitmap= (image_viewer.getDrawable() as BitmapDrawable).bitmap
            val filepath: File = Environment.getDownloadCacheDirectory()
            val dir:File = (filepath.absoluteFile) as File
            dir.mkdirs()
            val imagename:String = imageUrl + ".PNG"
            var file = File(dir,imagename)
            val out:OutputStream

            try {

                out = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG,100,out)
                out.flush()
                out.close()

            }catch (e:Exception) {

            }

             */

        })




    }

    class DownloadAndSaveImageTask(context: Context) : AsyncTask<String, Unit, Unit>() {
        private var mContext: WeakReference<Context> = WeakReference(context)

        override fun doInBackground(vararg params: String?) {
            val url = params[0]
            val requestOptions = RequestOptions().override(100)
                .downsample(DownsampleStrategy.CENTER_INSIDE)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)

            mContext.get()?.let {
                val bitmap = Glide.with(it)
                    .asBitmap()
                    .load(url)
                    .apply(requestOptions)
                    .submit()
                    .get()

                try {
                    var file = it.getDir("Images", Context.MODE_PRIVATE)
                    if (!file.exists()) {
                        file.mkdir()
                    }
                    file = File(file, "img.png")
                    val out = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    out.flush()
                    out.close()
                    Log.i("Seiggailion", "Image saved.")

                } catch (e: Exception) {
                    Log.i("Seiggailion", "Failed to save image.")
                }
            }
        }
    }

    private fun saveImageToInternalStorage(drawableId:Int):Uri{
        // Get the image from drawable resource as drawable object
        val drawable = ContextCompat.getDrawable(applicationContext,drawableId)

        // Get the bitmap from drawable object
        val bitmap = (drawable as BitmapDrawable).bitmap

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)


        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return Uri.parse(file.absolutePath)
    }






}


/*

newImage = (image_viewer.getDrawable() as BitmapDrawable).bitmap

if (width > 0 && height > 0) {

    for (i in 0..width!!) {
        for (j in 0..height!!) {
            val oldPixel: Int = newImage.getPixel(i, j)
            val oldRed: Int = Color.red(oldPixel)
            val oldBlue: Int = Color.blue(oldPixel)
            val oldGreen: Int = Color.green(oldPixel)
            val oldAlpha: Int = Color.alpha(oldPixel)

            val intensity: Int = (oldRed + oldBlue + oldGreen) / 3
            val newRed: Int = intensity
            val newBlue: Int = intensity
            val newGreen: Int = intensity

            val newPixel: Int = Color.argb(oldAlpha, newRed, newGreen, newBlue)
            val newBitmap: Bitmap = newImage
            newBitmap.setPixel(i, j, newPixel)

            grey_filter.setImageBitmap(newBitmap)

        }
    }

}
else{
    grey_filter.setImageBitmap(newImage)
}
*/            /*
            if (width > 0 && height > 0) {

                for (i in 0..width!!) {
                    for (j in 0..height!!) {
                        val oldPixel: Int = newImage.getPixel(i, j)
                        val oldRed: Int = Color.red(oldPixel)
                        val oldBlue: Int = Color.blue(oldPixel)
                        val oldGreen: Int = Color.green(oldPixel)
                        val oldAlpha: Int = Color.alpha(oldPixel)

                        val intensity: Int = (oldRed + oldBlue + oldGreen) / 3
                        val newRed: Int = intensity
                        val newBlue: Int = intensity
                        val newGreen: Int = intensity

                        val newPixel: Int = Color.argb(oldAlpha, newRed, newGreen, newBlue)
                        val newBitmap: Bitmap = newImage
                        newBitmap.setPixel(i, j, newPixel)

                        grey_filter.setImageBitmap(newBitmap)

                    }
                }

            }
            else{
                grey_filter.setImageBitmap(newImage)
            }
*/
