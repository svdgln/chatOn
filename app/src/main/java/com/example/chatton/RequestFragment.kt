package com.example.chatton

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_request.*

/**
 * A simple [Fragment] subclass.
 */
class RequestFragment : Fragment() {

    var arrayListName = ArrayList<String>()
    var arrayListStatus = ArrayList<String>()
    private lateinit var ChatRequestRef: DatabaseReference
    private lateinit var RootRef: DatabaseReference
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var auth: FirebaseAuth
    lateinit var receiverUserID:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_request, container, false)
        ChatRequestRef = FirebaseDatabase.getInstance().reference.child("Chat Requests")
        RootRef = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        val listview = view.findViewById<ListView>(R.id.list_view)
        RetrieveAndDisplayGroup(listview,inflater.context)

        return view

    }


    private fun RetrieveAndDisplayGroup(listview: ListView, context: Context) {
        RootRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentuserID = auth.currentUser?.uid
                val iterator = dataSnapshot.child("Chat Requests").child(currentuserID.toString()).children.iterator()
                var ListName = ArrayList<String>()
                var ListStatus = ArrayList<String>()
                while (iterator.hasNext()) {
                    iterator.next().key?.let {
                            if (dataSnapshot.child("Chat Requests").child(currentuserID.toString()).child(it).child("Request_Type")
                                    .getValue()!!.equals("received")
                            ) {
                                ListName.add(
                                    dataSnapshot.child("Users").child(it).child("name").getValue()
                                        .toString()
                                )
                                ListStatus.add(
                                    dataSnapshot.child("Users").child(it).child("status").getValue()
                                        .toString()
                                )

                                receiverUserID= it

                            }
                    }
                }
                if (!ListName.isEmpty()) {
                    arrayListName.clear()
                    arrayListStatus.clear()
                    arrayListName.addAll(ListName)
                    arrayListStatus.addAll(ListStatus)
                    listview.adapter = Adapter(
                        context,
                        R.layout.request,
                        arrayListName,
                        arrayListStatus,
                        receiverUserID
                    )
                }
                //   Adapter.notifyDataSetChanged()
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })}

    class Adapter(var mCtx: Context, var resources: Int, var name: List<String> , var status: List<String> , var receiverUserID:String) :
        ArrayAdapter<String>(mCtx, resources, name) {

        private lateinit var ChatRequestRef: DatabaseReference
        private lateinit var RootRef: DatabaseReference
        private lateinit var ContactsRef: DatabaseReference
        private var currentUser = FirebaseAuth.getInstance().currentUser
        private lateinit var auth: FirebaseAuth
        private lateinit var currentUserID:String

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
            val view: View = layoutInflater.inflate(resources, null)

            val titleTextView: TextView = view.findViewById(R.id.text)
            val subtitleTextView: TextView = view.findViewById(R.id.subtext)
            val accept_buton: Button = view.findViewById(R.id.accept_buton)
            val cancel_buton: Button = view.findViewById(R.id.cancel_buton)

            ChatRequestRef = FirebaseDatabase.getInstance().reference.child("Chat Requests")
            ContactsRef = FirebaseDatabase.getInstance().reference.child("Contacts")
            RootRef = FirebaseDatabase.getInstance().reference
            auth = FirebaseAuth.getInstance()
            currentUser = auth.currentUser
            currentUserID = auth.currentUser?.uid.toString()
            val alert = AlertDialog.Builder(context)

            accept_buton.setOnClickListener {
                alert.setTitle("Exit")
                alert.setMessage("You Sure?")
                alert.setCancelable(false)
                alert.setPositiveButton("No") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()

                }

                alert.setNegativeButton("yes") { dialogInterface: DialogInterface, i: Int ->
                    AcceptChatRequest()
                }
                alert.show()

            }

            cancel_buton.setOnClickListener{
                alert.setTitle("Exit")
                alert.setMessage("You Sure?")
                alert.setCancelable(false)
                alert.setPositiveButton("No") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()

                }

                alert.setNegativeButton("yes") { dialogInterface: DialogInterface, i: Int ->
                    CancelChatRequest()
                }
                alert.show()
            }


            var mItem: String = name[position]
            titleTextView.text = mItem
            var mItem2: String= status[position]
            subtitleTextView.text = mItem2



            return view
        }

        fun AcceptChatRequest() {
            RootRef.child("Contacts").child(currentUserID).child(receiverUserID).child("contact")
                .setValue("Saved")
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful){

                        RootRef.child("Contacts").child(receiverUserID).child(currentUserID).child("contact")
                            .setValue("Saved")
                            .addOnCompleteListener(OnCompleteListener { task ->
                                if (task.isSuccessful){
                                    ChatRequestRef.child(currentUserID).child(receiverUserID).removeValue().addOnCompleteListener(
                                        OnCompleteListener { task ->
                                            if (task.isSuccessful){
                                                ChatRequestRef.child(receiverUserID).child(currentUserID).removeValue().addOnCompleteListener(
                                                    OnCompleteListener { task ->
                                                        if (task.isSuccessful){
                                                            Toast.makeText(context, "You and " + receiverUserID + " are friends ", Toast.LENGTH_LONG).show()
                                                        }

                                                    })
                                            }

                                        })


                                }
                            })

                    }
                })


        }
        fun CancelChatRequest(){
            ChatRequestRef.child(currentUserID).child(receiverUserID).removeValue().addOnCompleteListener(
                OnCompleteListener { task ->
                    if (task.isSuccessful){
                        ChatRequestRef.child(receiverUserID).child(currentUserID).removeValue().addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (task.isSuccessful){
                                    Toast.makeText(context, "You deleted ", Toast.LENGTH_LONG).show()
                                }
                            })
                    }
                })

        }
    }

}

