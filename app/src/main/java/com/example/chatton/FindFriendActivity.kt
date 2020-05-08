package com.example.chatton

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_find_friend.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_request.*
import java.text.FieldPosition

class FindFriendActivity : AppCompatActivity() {

    private lateinit var RootRef: DatabaseReference
    var arrayListName = ArrayList<String>()
    var arrayListStatus = ArrayList<String>()
    var arrayListUserID = ArrayList<String>()
    var arrayListUserImage = ArrayList<String>()
    private lateinit var auth: FirebaseAuth
    private lateinit var UserID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friend)
        setTitle("Find Friend")
        //we create values
        auth = FirebaseAuth.getInstance()
        RootRef = FirebaseDatabase.getInstance().reference.child("Users")
        val listview = findViewById<ListView>(R.id.listView)
        RetrieveAndDisplayGroup(listview,this)

        //when you click list's item, you go to RequestActivity with userID value
        listview.setOnItemClickListener { parent, view, position, id ->
            val User = parent.getItemAtPosition(position)
            for (i in 0 until arrayListName.size) {
                if(User.equals(arrayListName.get(i))) {
                    UserID = arrayListUserID.get(i)
                }
            }
            val intent = Intent(this, RequestActivity::class.java)
            intent.putExtra("ID",UserID)
            startActivity(intent)
        }
    }
    //we called Users root children in the firebase.
    private fun RetrieveAndDisplayGroup(listview: ListView, context: Context) {
        RootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val iterator = dataSnapshot.children.iterator()
                var ListName = ArrayList<String>()
                var ListStatus = ArrayList<String>()
                var ListImage = ArrayList<String>()
                var ListUserID = ArrayList<String>()
                while (iterator.hasNext()) {
                    iterator.next().key?.let {
                        ListName.add(dataSnapshot.child(it).child("name").getValue().toString())
                        ListStatus.add(dataSnapshot.child(it).child("status").getValue().toString())
                        ListUserID.add(it)

                        if (dataSnapshot.child(it).hasChild("image")) {
                            ListImage.add(
                                dataSnapshot.child(it).child("image").getValue()
                                    .toString()
                            )
                        } else{
                            ListImage.add("https://firebasestorage.googleapis.com/v0/b/chattondatabase.appspot.com/o/download.png?alt=media&token=fb8f8243-496e-4422-bf82-8b32173dfb8a")
                        }
                    }
                }
                arrayListName.clear()
                arrayListStatus.clear()
                arrayListName.addAll(ListName)
                arrayListStatus.addAll(ListStatus)
                arrayListUserID.addAll(ListUserID)
                arrayListUserImage.addAll(ListImage)
                listview.adapter = Adapter(context, R.layout.user_display_layout, arrayListName , arrayListStatus , arrayListUserImage)
                //   Adapter.notifyDataSetChanged()
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })}
        //We created a adapter to show list's elements.
        class Adapter(var mCtx: Context, var resources: Int, var itemName: List<String> , var itemStatus: List<String> , var itemImage: List<String>) :
        ArrayAdapter<String>(mCtx, resources, itemName) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
                val view: View = layoutInflater.inflate(resources, null)

                val titleTextView: TextView = view.findViewById(R.id.text)
                val subtitleTextView: TextView = view.findViewById(R.id.subtext)
                val profileImage:CircleImageView=view.findViewById(R.id.profile_image)
                val send_message_buton:Button= view.findViewById(R.id.send_message_buton)
                send_message_buton.visibility = View.INVISIBLE
                send_message_buton.isEnabled = false

                var mItem: String = itemName[position]
                titleTextView.text = mItem
                var mItemStatus:String=itemStatus[position]
                subtitleTextView.text= mItemStatus

                var mItemImage:String = itemImage[position]
                Picasso.get().load(mItemImage).into(profileImage)
                return view
            }
        }
    }



