package com.example.chatton

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import jp.wasabeef.picasso.transformations.ColorFilterTransformation
import java.io.*
import java.lang.ref.WeakReference


@Suppress("DEPRECATION")
class ImageViewActivity : AppCompatActivity() {

    lateinit var imageUrl:String
    lateinit var copyImage:String
    lateinit var image_viewer:ImageView
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

        image_viewer= findViewById<ImageView>(R.id.image_viewer)
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
            val bitmap:Bitmap = (image_viewer.getDrawable() as BitmapDrawable).bitmap
            var file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            file = File(file, "ChattOnPictures")
            file.mkdirs()
            val index = file.listFiles().size+1
            var newFile = File(file, "image" + index.toString() + ".png")
            val os: OutputStream
            try {
                os = FileOutputStream(newFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                val toast= Toast.makeText(this,"Image Saved",Toast.LENGTH_LONG).show()
                os.flush()
                os.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                val toast= Toast.makeText(this,"Error File",Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                e.printStackTrace()
                val toast= Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
            }
        })




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
