package com.example.chatton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

class GroupChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        val buton = findViewById<Button>(R.id.buton)
        val text = findViewById<TextView>(R.id.text)
        val edit_text = findViewById<TextView>(R.id.edit_text)

        val currentGroupName = intent.extras?.get("Group Name").toString()
        Toast.makeText(applicationContext, "You Entered " + currentGroupName + " Group", Toast.LENGTH_LONG).show()
        setTitle("Group: " + currentGroupName)


    }
}
