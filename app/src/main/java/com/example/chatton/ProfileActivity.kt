package com.example.chatton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        InitializeFields()
    }

    fun InitializeFields(){
        val updateAccountSetting = findViewById<Button>(R.id.update_settings_button)
        val userName = findViewById<EditText>(R.id.set_user_name)
        val userStatus = findViewById<EditText>(R.id.set_profile_status)
        val CircleImageView = findViewById<CircleImageView>(R.id.set_profile_image)
    }

}
