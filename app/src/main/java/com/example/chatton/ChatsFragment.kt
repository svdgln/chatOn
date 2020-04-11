package com.example.chatton

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

/**
 * A simple [Fragment] subclass.
 */
class ChatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
        var listview = view?.findViewById<ListView>(R.id.chat_list)
        var list = mutableListOf<ChatModel>()
        list.add(ChatModel("Adriana Liman", "Selam gülüm", R.drawable.adriana))
        list.add(ChatModel("Ian Somerhalder", "Selam şekerim", R.drawable.ian))
        list.add(ChatModel("Brad Pil", " Neyse sen meşgulsün", R.drawable.brad))

        if (listview != null) {
            //TODO: Following code causes error. This kelimesinde hata veriyor.
           //  listview.adapter=ChatAdapter(this, R.layout.row, list)
        }

    }

}