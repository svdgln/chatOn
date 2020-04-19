package com.example.chatton

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_group_chat.*
import java.text.SimpleDateFormat
import java.util.*

class GroupChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var UserRef: DatabaseReference
    private lateinit var GroupRef: DatabaseReference
    private lateinit var GroupMessegeRef: DatabaseReference
    private lateinit var currentUserName: String
    private lateinit var currentTime: String
    private lateinit var currentDate: String
    private lateinit var currentUserID: String
    private lateinit var scroll_view:ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)
        setTitle("Group Chat")

        //DEFINITON
        val buton = findViewById<Button>(R.id.buton)
     //   val text = findViewById<TextView>(R.id.text)
        val edit_text = findViewById<TextView>(R.id.edit_text)
        val currentGroupName = intent.extras?.get("Group Name").toString()
        Toast.makeText(applicationContext, "You Entered " + currentGroupName + " Group", Toast.LENGTH_LONG).show()
        setTitle("Group: " + currentGroupName)
        scroll_view = findViewById(R.id.scroll_view)

        //FIREBASE CONNECTION
        auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser!!.uid
        UserRef = FirebaseDatabase.getInstance().reference.child("Users")
        GroupRef = FirebaseDatabase.getInstance().reference.child("Group").child(currentGroupName)
        getUserInfo()

        buton.setOnClickListener{
            val messege = edit_text.text.toString()
            val messageKey = GroupRef.push().key
            if (TextUtils.isEmpty(messege)) {
                Toast.makeText(applicationContext, "Please write a message ..", Toast.LENGTH_LONG).show()
            } else{
                val ccalForDate = Calendar.getInstance()
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
                currentDate = simpleDateFormat.format(ccalForDate.time)

                val ccalForTime = Calendar.getInstance()
                val simpleTimeFormat = SimpleDateFormat("hh:mm a")
                currentTime = simpleTimeFormat.format(ccalForTime.time)

                var groupMassege : HashMap<String, Any> = HashMap<String, Any> ()
                GroupRef.updateChildren(groupMassege)
                GroupMessegeRef = GroupRef.child(messageKey.toString())

                var messageInfoMap : HashMap<String, Any> = HashMap<String, Any> ()
                messageInfoMap.put("name",currentUserName)
                messageInfoMap.put("message",messege)
                messageInfoMap.put("date",currentDate)
                messageInfoMap.put("time",currentTime)
                GroupMessegeRef.updateChildren(messageInfoMap)
            }

            edit_text.setText("")

            scroll_view.post(Runnable { scroll_view.fullScroll(View.FOCUS_DOWN) })
        }

    }

    override fun onStart() {
        super.onStart()
         GroupRef.addChildEventListener(object : ChildEventListener {
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

        while (iterator.hasNext()) {
            val chatDate = iterator.next().getValue().toString()
            val chatMessage = iterator.next().getValue().toString()
            val chatName = iterator.next().getValue().toString()
            val chatTime = iterator.next().getValue().toString()

            text.append(chatName+": \n" + chatMessage + "\n"  + chatTime +"         " + chatDate+ "\n\n\n")

            scroll_view.post {
                scroll_view.fullScroll(View.FOCUS_DOWN)
            }
        }
        //scroll_view.fullScroll(ScrollView.FOCUS_DOWN)

    }

    private fun getUserInfo() {
            UserRef.child(currentUserID).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        currentUserName = dataSnapshot.child("name").getValue().toString()
                    }

                }override fun onCancelled(error: DatabaseError) {
                    //print error.message
                }
            })
    }
}
