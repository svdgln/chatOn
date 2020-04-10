package com.example.chatton

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_group.*

/**
 * A simple [Fragment] subclass.
 */
class GroupFragment : Fragment() {
    companion object {
        fun newInstance(): GroupFragment {
            return GroupFragment() } }
    private lateinit var RootRef: DatabaseReference
    private var arrayList= ArrayList<String>()
    private lateinit var Adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_group, container, false)
        val activity = activity as Context
        RootRef = FirebaseDatabase.getInstance().reference.child("Group")
        val listview = view.findViewById<ListView>(R.id.list_view)
        Adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1,arrayList )
        listview.adapter = Adapter
        RetrieveAndDisplayGroup()
        return view
    }


    private fun RetrieveAndDisplayGroup() {
        RootRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val iterator = dataSnapshot.children.iterator()
                var set = HashSet<String>()
                while (iterator.hasNext()) {
                    iterator.next().key?.let { set.add(it) }
                    }
               // arrayList.clear()
                arrayList.addAll(set)
                Adapter.notifyDataSetChanged()
                }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}


