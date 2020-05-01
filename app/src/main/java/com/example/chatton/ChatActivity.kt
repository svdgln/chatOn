package com.example.chatton

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private lateinit var MessengerUserName: String
    private lateinit var MessengerReceiverID: String
    private lateinit var MessageSenderID: String
    private lateinit var auth: FirebaseAuth
    private lateinit var UserRef: DatabaseReference
    private lateinit var RootRef: DatabaseReference
    private lateinit var GroupRef: DatabaseReference
    private lateinit var MessegeRef: DatabaseReference
    var sendMessage = ArrayList<String>()
    var receivedMessage = ArrayList<String>()
    var Allmessages = ArrayList<String>()
    lateinit var userMessageList: RecyclerView
    lateinit var file_buton: Button
    lateinit var image_buton: Button
    var messagList = ArrayList<Messages>()
    lateinit var messageAdapter: MessageAdapter


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        MessengerUserName = intent.extras?.get("name").toString()
        MessengerReceiverID = intent.extras?.get("uid").toString()
        setTitle(MessengerUserName)

        val buton = findViewById<Button>(R.id.buton)
        val edit_text = findViewById<EditText>(R.id.edit_text)
        auth = FirebaseAuth.getInstance()
        MessageSenderID = auth.currentUser!!.uid
        UserRef = FirebaseDatabase.getInstance().reference.child("Users")
        RootRef = FirebaseDatabase.getInstance().reference
        MessegeRef =
            FirebaseDatabase.getInstance().reference.child("Messages").child(MessageSenderID)
                .child(MessengerReceiverID)

        messageAdapter = MessageAdapter(messagList)
        userMessageList = findViewById<RecyclerView>(R.id.chats_list)
        userMessageList.layoutManager = LinearLayoutManager(this)
        userMessageList.adapter = messageAdapter

        file_buton = findViewById(R.id.file_buton)


        buton.setOnClickListener {
            sendMessege()
        }

    }

    private fun sendMessege() {
        val mesegeText: String = edit_text.text.toString()

        if (TextUtils.isEmpty(mesegeText)) {
            Toast.makeText(applicationContext, "Please write a messege", Toast.LENGTH_LONG).show()
        } else run {
            val messegeSenderRef = "Messages/" + MessageSenderID + "/" + MessengerReceiverID
            val messegeReceiverRef = "Messages/" + MessengerReceiverID + "/" + MessageSenderID

            val userMessageKeyRef: DatabaseReference =
                RootRef.child("Messages").child(MessageSenderID).child(MessengerReceiverID).push()
            val messagePushID: String = userMessageKeyRef.key.toString()

            var messageTextBody: HashMap<String, String> = HashMap<String, String>()
            messageTextBody.put("message", mesegeText)
            messageTextBody.put("type", "text")
            messageTextBody.put("from", MessageSenderID)
            // messageTextBody.put("to",MessengerReceiverID)

            var messageBodyDetail: HashMap<String, Any> = HashMap<String, Any>()
            messageBodyDetail.put(messegeSenderRef + "/" + messagePushID, messageTextBody)
            messageBodyDetail.put(messegeReceiverRef + "/" + messagePushID, messageTextBody)

            RootRef.updateChildren(messageBodyDetail)
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, "Message sent", Toast.LENGTH_LONG).show()
                    }

                })

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
                    // DisplayMessage(datasnapshot)
                }
            }

            override fun onChildAdded(datasnapshot: DataSnapshot, p1: String?) {
                RetrieveAndDisplayGroup(userMessageList , this@ChatActivity)

            }

            override fun onChildRemoved(datasnapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun RetrieveAndDisplayGroup(chatList: RecyclerView, context: Context) {
        MessegeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentuserID:String = auth.currentUser!!.uid
                val iterator = dataSnapshot.children.iterator()
                messagList.clear()
                while (iterator.hasNext()) {
                    val i =0
                    iterator.next().key?.let {
                            val from = dataSnapshot.child(it).child("from").getValue().toString()
                            val message =
                                dataSnapshot.child(it).child("message").getValue().toString()
                            val type = dataSnapshot.child(it).child("type").getValue().toString()
                            messagList.add(Messages(from, message, type))

                    }
                }

                messageAdapter.notifyDataSetChanged()

            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })}


    data class Messages(val from: String, val message: String, val type: String) {

    }

    class MessageAdapter(val userMessageList: ArrayList<Messages>) :
        RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

        private lateinit var auth: FirebaseAuth
        private lateinit var UserRef: DatabaseReference

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var receiver_message = itemView.findViewById<TextView>(R.id.receiver_message_text)
            var sender_message = itemView.findViewById<TextView>(R.id.sender_message_text)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent?.context)
                .inflate(R.layout.custom_messages, parent, false)
            auth = FirebaseAuth.getInstance()
            return ViewHolder(v)
        }

        override fun getItemCount(): Int {
            return userMessageList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val messageSenderID: String = auth.currentUser!!.uid
            val message: Messages = userMessageList.get(position)
            val fromUserID = message.from
            val fromUserMessageType = message.type

            UserRef = FirebaseDatabase.getInstance().reference.child("Users").child(fromUserID)
            UserRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    //for image
                }

            })
            if (fromUserMessageType.equals("text")) {
                holder.receiver_message.visibility = View.INVISIBLE

                if (fromUserID.equals(messageSenderID)) {
                    holder.sender_message.setBackgroundResource(R.drawable.sender_messages)
                    holder.sender_message.setTextColor(Color.BLACK)
                    holder.sender_message.setText(message.message)
                } else {
                    holder.sender_message.visibility = View.INVISIBLE
                    holder.receiver_message.visibility = View.VISIBLE

                    holder.receiver_message.setBackgroundResource(R.drawable.receiver_messages)
                    holder.receiver_message.setTextColor(Color.BLACK)
                    holder.receiver_message.setText(message.message)
                }
            }


            //holder.receiver_message.text = contact.send
            //holder.sender_message.text = contact.received

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



