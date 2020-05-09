package com.example.chatton

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_profile.*

/**
 * A simple [Fragment] subclass.
 */
class chatFragment : Fragment() {

    private lateinit var RootRef: DatabaseReference
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var auth: FirebaseAuth
    var contacts = ArrayList<ContactList>()
    lateinit var currentuserID:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_chat, container, false)
        //Create values
        val chatList:RecyclerView = view.findViewById(R.id.chats_list)
        chatList.layoutManager = LinearLayoutManager(context)
        RootRef = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        if (currentUser != null) {
            RetrieveAndDisplayGroup(chatList, inflater.context)
        }




        return view
    }
    //First we go to the Firebase's Contact Root and learned which users are in contact.
    //After, we call these Users in the User Root.
    private fun RetrieveAndDisplayGroup(chatList: RecyclerView, context: Context) {
        RootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                currentuserID = auth.currentUser!!.uid

                val iterator = dataSnapshot.child("Contacts").child(currentuserID).children.iterator()
                var ListName = ArrayList<String>()
                var ListStatus = ArrayList<String>()
                var ListImage = ArrayList<String>()
                while (iterator.hasNext()) {
                    val i =0
                    iterator.next().key?.let {
                        if (!it.equals(currentuserID)) {
                            val uid = (dataSnapshot.child("Users").child(it).child("uid").getValue().toString())
                            val name = (dataSnapshot.child("Users").child(it).child("name").getValue().toString())
                            val status = (dataSnapshot.child("Users").child(it).child("status").getValue().toString())
                            var image:String
                            if (dataSnapshot.child("Users").child(it).hasChild("image")) {
                                image = (dataSnapshot.child("Users").child(it).child("image").getValue().toString())
                            }
                            else{
                                image = "https://firebasestorage.googleapis.com/v0/b/chattondatabase.appspot.com/o/download.png?alt=media&token=fb8f8243-496e-4422-bf82-8b32173dfb8a"
                            }
                            var state:String = ""
                            var date:String = ""
                            var time:String = ""
                            if (dataSnapshot.child("Users").child(it).child("userState").hasChild("state")){
                                state = dataSnapshot.child("Users").child(it).child("userState").child("state").getValue().toString()
                                date = dataSnapshot.child("Users").child(it).child("userState").child("date").getValue().toString()
                                time = dataSnapshot.child("Users").child(it).child("userState").child("time").getValue().toString()
                            }


                            contacts.add(ContactList(uid,name, status, image , state,date,time))

                        }
                    }
                }
                val adapter = CustomAdapter(contacts , context)
                //We call adapter
                chatList.adapter =adapter
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })}

    @Override
    override fun onStart(){
        super.onStart()


    }

    public fun goChatActivity(context: Context) {
            val intent = Intent(context, ChatActivity::class.java)
            startActivity(intent)

    }

}

//We created a data class for Custom Adapter.
data class ContactList(val uid:String, val name: String,
                       val status: String, val image: String ,
                       val state:String, val date:String,val time:String){


}

class CustomAdapter(val userList:ArrayList<ContactList> , val context: Context):
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    //In this Adapter, we will show in the ArrayList's elements using RecylerView.
    private lateinit var RootRef:DatabaseReference
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var auth: FirebaseAuth

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var textViewName =itemView.findViewById<TextView>(R.id.text_name)
        var textViewStatus =itemView.findViewById<TextView>(R.id.text_status)
        var profileImage =itemView.findViewById<CircleImageView>(R.id.profile_image)
        var lastSeen:TextView= itemView.findViewById(R.id.lastSeen)
        var online_buton:ImageView=itemView.findViewById(R.id.online_buton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.contacts,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        RootRef = FirebaseDatabase.getInstance().reference
        val contact:ContactList= userList[position]

        holder.textViewName.text = contact.name
        holder.textViewStatus.text = contact.status

        Picasso.get().load(contact.image).into(holder.profileImage);
        holder.itemView.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("name", contact.name)
                intent.putExtra("uid", contact.uid)
                context.startActivity(intent)
        })


        if (contact.state.equals("online")){
            holder.lastSeen.setText("online")
            holder.online_buton.visibility = View.VISIBLE

        }
        else{
            holder.online_buton.visibility = View.INVISIBLE
            holder.lastSeen.setText("offline\n " + "Last Seen: "  + contact.date + contact.time )
        }







    }


}


