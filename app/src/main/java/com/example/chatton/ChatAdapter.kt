package com.example.chatton

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
class ChatAdapter(var mCtx: Context, var resources:Int, var items:List<ChatModel>):ArrayAdapter<ChatModel>(mCtx, resources, items) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater:LayoutInflater = LayoutInflater.from(mCtx)
        val view:View = layoutInflater.inflate(resources, null)

        val titleTextView: TextView = view.findViewById(R.id.text1)
        val descriptionTextView:TextView = view.findViewById(R.id.text2)


        var mItem:ChatModel = items[position]
        titleTextView.text = mItem.title
        descriptionTextView.text = mItem.description

        return view


        //return super.getView(position, convertView, parent)
    }

}