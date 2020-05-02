package com.example.chatton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class ImageViewActivity : AppCompatActivity() {

    lateinit var imageUrl:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        val image_viewer= findViewById<ImageView>(R.id.image_viewer)
        imageUrl = intent.getStringExtra("url")

        Picasso.get().load(imageUrl).into(image_viewer)
    }
}
