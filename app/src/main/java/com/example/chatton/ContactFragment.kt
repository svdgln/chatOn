package com.example.chatton

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class ContactFragment : Fragment() {
    private lateinit var ContactRef: DatabaseReference
    private lateinit var RootRef: DatabaseReference
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var auth: FirebaseAuth
    var arrayListName = ArrayList<String>()
    var arrayListID = ArrayList<String>()
    var arrayListStatus = ArrayList<String>()
    var arrayListImage = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //we create values
        val view: View = inflater.inflate(R.layout.fragment_contact, container, false)
        ContactRef = FirebaseDatabase.getInstance().reference.child("Contacts")
        RootRef = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        val listview = view.findViewById<ListView>(R.id.list_view)

        RetrieveAndDisplayGroup(listview,inflater.context)



        return view
    }
    //we call the Contacts Root in the Firebase.
    //and we added these Contacts in ArrayLists.
    private fun RetrieveAndDisplayGroup(listview: ListView, context: Context) {
        RootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentuserID:String = auth.currentUser!!.uid
                val iterator = dataSnapshot.child("Contacts").child(currentuserID).children.iterator()
                var ListName = ArrayList<String>()
                var ListStatus = ArrayList<String>()
                var ListImage = ArrayList<String>()
                var ListID = ArrayList<String>()
                while (iterator.hasNext()) {
                    val i =0
                    iterator.next().key?.let {
                        if (!it.equals(currentuserID)) {
                            ListName.add(dataSnapshot.child("Users").child(it).child("name").getValue().toString())
                            ListStatus.add(dataSnapshot.child("Users").child(it).child("status").getValue().toString())
                            ListID.add(dataSnapshot.child("Users").child(it).child("uid").getValue().toString())
                            if (dataSnapshot.child("Users").child(it).hasChild("image")) {
                                ListImage.add(
                                    dataSnapshot.child("Users").child(it).child("image").getValue()
                                        .toString()
                                )
                            } else{
                                ListImage.add("https://firebasestorage.googleapis.com/v0/b/chattondatabase.appspot.com/o/download.png?alt=media&token=fb8f8243-496e-4422-bf82-8b32173dfb8a")
                            }
                        }
                    }
                }
                arrayListName.clear()
                arrayListID.clear()
                arrayListStatus.clear()
                arrayListImage.clear()
                arrayListName.addAll(ListName)
                arrayListID.addAll(ListID)
                arrayListStatus.addAll(ListStatus)
                arrayListImage.addAll(ListImage)
                //we call adapter to show ArrayLists like a list.
                listview.adapter = Adapter(context, R.layout.user_display_layout, arrayListName , arrayListID, arrayListStatus , arrayListImage)
                //   Adapter.notifyDataSetChanged()
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })}

    class Adapter(var mCtx: Context, var resources: Int, var itemName: List<String> , var itemID: List<String> , var itemStatus: List<String> ,var itemImage: List<String> ) :
        ArrayAdapter<String>(mCtx, resources, itemName) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
            val view: View = layoutInflater.inflate(resources, null)
            val send_message_buton:Button= view.findViewById<Button>(R.id.send_message_buton)

            send_message_buton.setOnClickListener{
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("name", itemName[position])
                intent.putExtra("uid",itemID[position])
                context.startActivity(intent)
            }

            val titleTextView: TextView = view.findViewById(R.id.text)
            val subTextView: TextView = view.findViewById(R.id.subtext)
            val profile_image: CircleImageView = view.findViewById(R.id.profile_image)



            var mItem: String = itemName[position]
            titleTextView.text = mItem

            var mItemS: String = itemStatus[position]
            subTextView.text = mItemS

            var mItemI: String = itemImage[position]
            Picasso.get().load(mItemI).into(profile_image)

            return view
        }
    }

}
