package com.example.chatton

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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
    lateinit var listview: ListView


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
            val type = iterator.next().getValue().toString()

            if (from.equals(currentUserID)) {
                sendMessage.add(message)
            }

            else if (from.equals(MessengerUserID)) {
                receivedMessage.add(message)
            }

            else {
                Toast.makeText(applicationContext, "You have no messages", Toast.LENGTH_LONG).show()
            }
        }

        listview.adapter = ChatActivity.Adapter(this, R.layout.custom_messages, sendMessage , receivedMessage)
        listview.post(Runnable { listview.setSelection(listview.getCount() - 1) })
        //listview.smoothScrollToPosition(5);

    }

    class Adapter(var mCtx: Context, var resources: Int, var send: List<String>, var resend: List<String>) :
        ArrayAdapter<String>(mCtx, resources, send) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater: LayoutInflater = LayoutInflater.from(mCtx)
            val view:View = layoutInflater.inflate(resources, null)

            val sendMessage: TextView = view.findViewById(R.id.sender_message_text)
            val receivedMessage: TextView = view.findViewById(R.id.receiver_message_text)


            if (!send.isEmpty()) {
                var mItem: String = send[position]
                sendMessage.text = mItem
            }

            if (!resend.isEmpty()) {

                var mItem2: String = resend[position]
                receivedMessage.text = mItem2
            }

            return view
        }
    }
}
