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

    private lateinit var RootRef: DatabaseReference
    var arrayList = ArrayList<String>()
    //private lateinit var Adapter: ArrayAdapter<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_group, container, false)
        RootRef = FirebaseDatabase.getInstance().reference.child("Group")
        val listview = view.findViewById<ListView>(R.id.list_view)
        RetrieveAndDisplayGroup(listview,inflater.context)

        listview.setOnItemClickListener { parent, view, position, id ->
            val groupName = parent.getItemAtPosition(position).toString()
            val intent = Intent(inflater.context, GroupChatActivity::class.java)
            intent.putExtra("Group Name",groupName)
            startActivity(intent)
        }

        return view
    }

    private fun RetrieveAndDisplayGroup(listview: ListView, context: Context) {
        RootRef.addValueEventListener(object : ValueEventListener {
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
































/*   val titleTextView2: TextView = view.findViewById(R.id.text2)
val titleTextView3: TextView = view.findViewById(R.id.text3)
val titleTextView4: TextView = view.findViewById(R.id.text4)
val titleTextView5: TextView = view.findViewById(R.id.text5)
val titleTextView6: TextView = view.findViewById(R.id.text6)
val titleTextView7: TextView = view.findViewById(R.id.text7)
val titleTextView8: TextView = view.findViewById(R.id.text8)
val titleTextView9: TextView = view.findViewById(R.id.text9)
val titleTextView10: TextView = view.findViewById(R.id.text10) */

// var counter = 0
/*       if (10 < items.size) {
    for (mItem in items) {
        when (mItem) {
            items[0] -> titleTextView.text
            items[1] -> titleTextView2.text
            items[2] -> titleTextView3.text
            items[3] -> titleTextView4.text
            items[4] -> titleTextView5.text
            items[5] -> titleTextView6.text
            items[6] -> titleTextView7.text
            items[7] -> titleTextView8.text
            items[8] -> titleTextView9.text
            items[9] -> titleTextView10.text
        }
    }
} else {
    if (counter < items.size) {
        titleTextView.text = items[counter]
        counter += 1 }
    if (counter < items.size) {
        titleTextView2.text = items[counter]
        counter += 1 }
    if (counter < items.size) {
        titleTextView3.text = items[counter]
        counter += 1 }
    if (counter < items.size) {
        titleTextView3.text = items[counter]
        counter += 1 }
    if (counter < items.size) {
        titleTextView4.text = items[counter]
        counter += 1 }
    if (counter < items.size) {
        titleTextView5.text = items[counter]
        counter += 1 }
    if (counter < items.size) {
        titleTextView6.text = items[counter]
        counter += 1 }
    if (counter < items.size) {
        titleTextView7.text = items[counter]
        counter += 1 }
    if (counter < items.size) {
        titleTextView8.text = items[counter]
        counter += 1 } }
return view } } }
*/
