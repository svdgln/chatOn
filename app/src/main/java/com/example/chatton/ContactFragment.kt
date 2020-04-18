package com.example.chatton

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class ContactFragment : Fragment() {
    private lateinit var ContactRef: DatabaseReference
    private lateinit var RootRef: DatabaseReference
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var auth: FirebaseAuth
    var arrayList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_contact, container, false)
        ContactRef = FirebaseDatabase.getInstance().reference.child("Contacts")
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
                val iterator = dataSnapshot.child("Contacts").children.iterator()
                var ListName = ArrayList<String>()
                while (iterator.hasNext()) {
                    val i =0
                    iterator.next().key?.let {
                        if (!it.equals(currentuserID)) {
                            ListName.add(dataSnapshot.child("Users").child(it).child("name").getValue().toString())
                        }
                    }
                }
                arrayList.clear()
                arrayList.addAll(ListName)
                listview.adapter = Adapter(context, R.layout.row, arrayList)
                //   Adapter.notifyDataSetChanged()
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })}

    class Adapter(var mCtx: Context, var resources: Int, var itemName: List<String> ) :
        ArrayAdapter<String>(mCtx, resources, itemName) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
            val view: View = layoutInflater.inflate(resources, null)

            val titleTextView: TextView = view.findViewById(R.id.text1)
            var mItem: String = itemName[position]
            titleTextView.text = mItem
            return view
        }
    }

}
