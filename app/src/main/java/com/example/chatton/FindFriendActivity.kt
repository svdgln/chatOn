package com.example.chatton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.android.synthetic.main.activity_find_friend.*
import kotlinx.android.synthetic.main.activity_main.*

class FindFriendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friend)

        val recycler_view:RecyclerView = findViewById(R.id.recycler_view)
        recycler_view.layoutManager = LinearLayoutManager(this)
        setTitle("Find Friend")


    }


}

