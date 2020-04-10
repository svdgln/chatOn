package com.example.chatton

import android.R.id
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //DEFINE VARIABLE
        val chat_buton = findViewById<Button>(R.id.chat_buton)
        val group_buton = findViewById<Button>(R.id.group_buton)
        val contact_buton = findViewById<Button>(R.id.contact_buton)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        //FRAGMENT PART
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.view, ChatsFragment())
        fragmentTransaction.commit()
        chat_buton.setOnClickListener {
            changeFragment(ChatsFragment()) }
        group_buton.setOnClickListener {
            changeFragment(GroupFragment()) }
        contact_buton.setOnClickListener {
            changeFragment(ContactFragment()) }


    }

    fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.view, fragment)
        fragmentTransaction.commit()
    }
    override fun onStart() {
        super.onStart()
        if (currentUser == null) {
            sendUserToLoginActivity();
        }

    }

    private fun sendUserToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }



}