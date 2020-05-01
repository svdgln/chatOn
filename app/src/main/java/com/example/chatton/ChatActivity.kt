package com.example.chatton

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private lateinit var MessengerUserName:String
    private lateinit var MessengerUserID:String
    private lateinit var currentUserID: String
    private lateinit var auth: FirebaseAuth
    private lateinit var UserRef: DatabaseReference
    private lateinit var RootRef: DatabaseReference
    private lateinit var GroupRef: DatabaseReference
    private lateinit var MessegeRef: DatabaseReference
    var sendMessage = ArrayList<String>()
    var receivedMessage = ArrayList<String>()
    var Allmessages = ArrayList<String>()
    lateinit var listview: ListView
    lateinit var file_buton:Button
    lateinit var image_buton:Button


    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        MessengerUserName = intent.extras?.get("name").toString()
        MessengerUserID = intent.extras?.get("uid").toString()
        setTitle(MessengerUserName)

        val buton = findViewById<Button>(R.id.buton)
        val edit_text = findViewById<EditText>(R.id.edit_text)
        auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser!!.uid
        UserRef = FirebaseDatabase.getInstance().reference.child("Users")
        RootRef = FirebaseDatabase.getInstance().reference
        MessegeRef = FirebaseDatabase.getInstance().reference.child("Messages").child(currentUserID)
        listview = findViewById<ListView>(R.id.list_view)
        file_buton = findViewById(R.id.file_buton)


        buton.setOnClickListener{
            sendMessege()
        }

    }

    private fun sendMessege(){
        val mesegeText:String=edit_text.text.toString()

        if (TextUtils.isEmpty(mesegeText)){
            Toast.makeText(applicationContext, "Please write a messege", Toast.LENGTH_LONG).show()
        }
        else run {
            val messegeSenderRef = "Messages/" + currentUserID + "/" + MessengerUserID
            val messegeReceiverRef = "Messages/" + MessengerUserID + "/" + currentUserID

            val userMessageKeyRef:DatabaseReference = RootRef.child("Messages").child(messegeSenderRef).child(messegeReceiverRef).push()
            val messagePushID:String = userMessageKeyRef.key.toString()

            var messageTextBody : HashMap<String, String> = HashMap<String, String> ()
            messageTextBody.put("message",mesegeText)
            messageTextBody.put("type","text")
            messageTextBody.put("from",currentUserID)
            messageTextBody.put("to",MessengerUserID)

            var messageBodyDetail : HashMap<String, HashMap<String,String>> = HashMap<String, HashMap<String,String>> ()
            messageBodyDetail.put(currentUserID + "/" + messagePushID , messageTextBody)
            messageBodyDetail.put(MessengerUserID + "/" + messagePushID , messageTextBody)

            RootRef.child("Messages").updateChildren(messageBodyDetail as Map<String, Any>).addOnCompleteListener{task ->
                if (task.isSuccessful){
                    Toast.makeText(applicationContext, "Message sent", Toast.LENGTH_LONG).show()
                }
            }

        }
        edit_text.setText("")
    }

    override fun onStart() {
        super.onStart()
        MessegeRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(datasnapshot: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(datasnapshot: DataSnapshot, p1: String?) {
                if (datasnapshot.exists()) {
                    DisplayMessage(datasnapshot)
                }
            }

            override fun onChildAdded(datasnapshot: DataSnapshot, p1: String?) {
                if (datasnapshot.exists()) {
                    DisplayMessage(datasnapshot)
                }
            }

            override fun onChildRemoved(datasnapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun DisplayMessage(dataSnapshot: DataSnapshot) {
        val iterator = dataSnapshot.children.iterator()
        val List = ArrayList<String>()
        while (iterator.hasNext()) {
            val from = iterator.next().getValue().toString()
            val message = iterator.next().getValue().toString()
            val to = iterator.next().getValue().toString()
            val type = iterator.next().getValue().toString()


            if (from.equals(currentUserID) && to.equals(MessengerUserID)) {
                sendMessage.add(message)
            }

            else if (from.equals(MessengerUserID) && to.equals(currentUserID)) {
                receivedMessage.add(message)
            }

            Allmessages.add(message)

        }
        listview.adapter = ChatActivity.Adapter(this, R.layout.custom_messages, sendMessage , receivedMessage , Allmessages)
        listview.post(Runnable { listview.setSelection(listview.getCount() - 1) })

        //listview.smoothScrollToPosition(5);

    }

    class Adapter(var mCtx: Context, var resources: Int, var send: List<String>, var received: List<String> , var all: List<String>) :
        ArrayAdapter<String>(mCtx, resources, send) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
            val view:View = layoutInflater.inflate(resources, null)

            val sendMessage: TextView = view.findViewById(R.id.sender_message_text)
            val receivedMessage: TextView = view.findViewById(R.id.receiver_message_text)
            var index1:Int = 0
            var index2:Int = 0

            if (!all.isEmpty()) {
                if (!send.isEmpty() && position < send.size) {
                    var mItem: String = send[position]
                    sendMessage.text = mItem
                    index1 += 1
                }

                  if (!received.isEmpty() && position < received.size) {

                    var mItem2: String = received[position]
                    receivedMessage.text = mItem2
                    index2 += 1
                }
            }

            return view
        }
    }

}


/*

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = Adapter(getModels())

    class Adapter(val messages: MutableList<MessageList>) : RecyclerView.Adapter<Adapter.ModelViewHolder>() {
        private lateinit var userMessageList:List<MessageList>
        private lateinit var mAuth: FirebaseAuth
        private lateinit var userRef:DatabaseReference

        class ModelViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val sendMessage: TextView = view.findViewById(R.id.sender_message_text)
            val receivedMessage: TextView = view.findViewById(R.id.receiver_message_text)

            fun bindItems(item: MessageList) {
                sendMessage.setText(item.send)
                receivedMessage.setText(item.received)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_messages, parent, false)

            mAuth = FirebaseAuth.getInstance()

            return ModelViewHolder(view)
        }

        override fun getItemCount(): Int {
            return messages.size
        }

        override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {

            val messageSenderID = mAuth.currentUser?.uid
            val messages = userMessageList.get(po)

            holder.bindItems(messages.get(position))
        }

    }

 */



