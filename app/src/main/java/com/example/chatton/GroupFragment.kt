package com.example.chatton

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*


/**
 * A simple [Fragment] subclass.
 */
class GroupFragment : Fragment() {
    companion object {

        fun newInstance(): GroupFragment {
            return GroupFragment()
        }
    }

    private lateinit var GroupRef: DatabaseReference
    var arrayList = ArrayList<String>()
    //private lateinit var Adapter: ArrayAdapter<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_group, container, false)
        GroupRef = FirebaseDatabase.getInstance().reference.child("Group")
        val listview = view.findViewById<ListView>(R.id.list_view)
        RetrieveAndDisplayGroup(listview,inflater.context)
        //When you click any listview's item, it will be go GroupChatActivity with groupName value
        listview.setOnItemClickListener { parent, view, position, id ->
            val groupName = parent.getItemAtPosition(position).toString()
            val intent = Intent(inflater.context, GroupChatActivity::class.java)
            intent.putExtra("Group Name",groupName)
            startActivity(intent)
        }

        return view
    }
    //We called Group Root in the firebase.
    private fun RetrieveAndDisplayGroup(listview: ListView, context: Context) {
        GroupRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val iterator = dataSnapshot.children.iterator()
                var set = HashSet<String>()
                while (iterator.hasNext()) {
                    iterator.next().key?.let { set.add(it) }
                }
                arrayList.clear()
                arrayList.addAll(set)
                 listview.adapter = Adapter(context, R.layout.row, arrayList)
                //   Adapter.notifyDataSetChanged()
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })}}
    //We create an adapter to show as a list in the ArrayList's elements.
    class Adapter(var mCtx: Context, var resources: Int, var items: List<String>) :
        ArrayAdapter<String>(mCtx, resources, items) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater:LayoutInflater = LayoutInflater.from(mCtx)
            val view:View = layoutInflater.inflate(resources, null)
            val titleTextView: TextView = view.findViewById(R.id.text1)
            var mItem: String = items[position]
            titleTextView.text = mItem
            return view
        }
    }

