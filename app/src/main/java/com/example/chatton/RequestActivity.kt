package com.example.chatton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isInvisible
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_request.*

class RequestActivity : AppCompatActivity() {
    private lateinit var UserRef: DatabaseReference
    private lateinit var ChatRequestRef: DatabaseReference
    private lateinit var ContactsRef: DatabaseReference
    private lateinit var RootRef: DatabaseReference
    private lateinit var NotificationRef: DatabaseReference
    private lateinit var userName: TextView
    private lateinit var userStatus: TextView
    private lateinit var current_state:String
    private lateinit var currentUserID:String
    private lateinit var auth: FirebaseAuth
    private lateinit var receiverUserID:String
    private lateinit var visit_profile_image:CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)
        receiverUserID = intent.extras?.get("ID").toString()
        setTitle("Send a Request")

        userName = findViewById<TextView>(R.id.visit_user_name)
        userStatus = findViewById<TextView>(R.id.visit_user_status)
        visit_profile_image=findViewById(R.id.visit_profile_image)
        val sendRequestButon:Button= findViewById<Button>(R.id.send_request_buton)
        val declineRequestButon:Button= findViewById<Button>(R.id.decline_request_buton)
        current_state = "new"
        auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser?.uid.toString()
        UserRef = FirebaseDatabase.getInstance().reference.child("Users")
        ChatRequestRef = FirebaseDatabase.getInstance().reference.child("Chat Requests")
        RootRef = FirebaseDatabase.getInstance().reference
        ContactsRef = FirebaseDatabase.getInstance().reference.child("Contacts")
        NotificationRef = FirebaseDatabase.getInstance().reference.child("Notifications")
        UserRef.child(receiverUserID).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.hasChild("name")) {
                        val name:String= dataSnapshot.child("name").getValue().toString()
                        val status:String= dataSnapshot.child("status").getValue().toString()
                        val image: String
                        if (dataSnapshot.hasChild("image")) {
                            image = dataSnapshot.child("image").getValue().toString()
                        }
                        else{
                            image = "https://firebasestorage.googleapis.com/v0/b/chattondatabase.appspot.com/o/download.png?alt=media&token=fb8f8243-496e-4422-bf82-8b32173dfb8a"
                        }
                        userName.setText(name)
                        userStatus.setText(status)

                        Picasso.get().load(image).into(visit_profile_image)
                        ManageChatRequest()
                    } } })
    }

    fun ManageChatRequest(){

        ChatRequestRef.child(currentUserID).addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(receiverUserID)) {
                    val Request_Type: String =
                        dataSnapshot.child(receiverUserID).child("Request_Type").getValue()
                            .toString()
                    if (Request_Type.equals("sent")) {
                        current_state = "request_sent"
                        send_request_buton.setText("Cancel Chat Request")
                    } else if (Request_Type.equals("received")) {
                        current_state = "request_sent"
                        send_request_buton.setText("Accept Chat Request")
                        decline_request_buton.visibility = View.VISIBLE
                        decline_request_buton.isEnabled = true
                        decline_request_buton.setOnClickListener {
                            CancelChatRequest()
                        }

                        send_request_buton.setOnClickListener{
                            AcceptChatRequest()
                        }
                    }
                }
                else {
                    ContactsRef.child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.hasChild(receiverUserID)) {
                                current_state = "friends"
                                send_request_buton.setText("Remove This Contact")
                            }
                        }

                    })
                }

            }
        })

        if (!receiverUserID.equals(currentUserID)) {
            send_request_buton.setOnClickListener{
                send_request_buton.isEnabled= false
                if (current_state == "new") {
                    sendChatRequest()
                }
                if (current_state == "request_sent") {
                    CancelChatRequest()
                }
                if (current_state == "request_received") {
                    AcceptChatRequest()
                }
                if (current_state == "friends") {
                    RemoveSpecificContact()
                }
            }
        } else{
            send_request_buton.visibility= View.INVISIBLE
        }
    }

    fun sendChatRequest() {
        ChatRequestRef.child(currentUserID).child(receiverUserID).child("Request_Type").setValue("sent")
            .addOnCompleteListener(this, OnCompleteListener<Void> { task ->

                if (task.isSuccessful){
                    ChatRequestRef.child(receiverUserID).child(currentUserID).child("Request_Type").setValue("received")
                        .addOnCompleteListener(this, OnCompleteListener<Void> { task ->
                            if (task.isSuccessful) {

                                var hashMap : HashMap<String, String> = HashMap<String, String> ()
                                hashMap.put("from", currentUserID)
                                hashMap.put("type", "request")

                                NotificationRef.child(receiverUserID).push().setValue(hashMap).addOnCompleteListener(
                                    OnCompleteListener {
                                        if (task.isSuccessful){
                                            send_request_buton.isEnabled = true
                                            current_state = "request_sent"
                                            send_request_buton.setText("Cancel Chat Request")
                                        }
                                    })


                            }
                        })
                }
            })
    }

    fun CancelChatRequest(){
        ChatRequestRef.child(currentUserID).child(receiverUserID).removeValue().addOnCompleteListener(this, OnCompleteListener<Void> { task ->
            if (task.isSuccessful) {
                ChatRequestRef.child(receiverUserID).child(currentUserID).removeValue().addOnCompleteListener(this, OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        send_request_buton.isEnabled =true
                        current_state = "new"
                        send_request_buton.setText("Send Message")
                        decline_request_buton.visibility = View.INVISIBLE
                        decline_request_buton.isEnabled = false
                    }
                })
            }
        })
    }

    fun AcceptChatRequest() {
        RootRef.child("Contacts").child(currentUserID).child(receiverUserID).child("contact").setValue("Saved")
            .addOnCompleteListener(this, OnCompleteListener<Void> { task ->
                if (task.isSuccessful){
                        ContactsRef.child(receiverUserID).child(currentUserID).child("contact").setValue("Saved")
                            .addOnCompleteListener(this, OnCompleteListener<Void> { task ->
                                if (task.isSuccessful) {
                                    ChatRequestRef.child(currentUserID).child(receiverUserID).removeValue()
                                        .addOnCompleteListener(this, OnCompleteListener<Void> { task ->
                                        if (task.isSuccessful) {
                                            ChatRequestRef.child(receiverUserID).child(currentUserID).removeValue()
                                                .addOnCompleteListener(this, OnCompleteListener<Void> { task ->
                                                send_request_buton.isEnabled=true
                                                current_state = "friends"
                                                send_request_buton.setText("Remove This Contact")
                                                decline_request_buton.visibility=View.INVISIBLE
                                                decline_request_buton.isEnabled=false
                                            })
                                        }
                                    })
                                }
                            })
                }
            })
    }

    fun RemoveSpecificContact(){
        ContactsRef.child(currentUserID).child(receiverUserID).removeValue().addOnCompleteListener(this, OnCompleteListener<Void> { task ->
            if (task.isSuccessful) {
                ContactsRef.child(receiverUserID).child(currentUserID).removeValue().addOnCompleteListener(this, OnCompleteListener<Void> { task ->
                    if (task.isSuccessful) {
                        send_request_buton.isEnabled =true
                        current_state = "new"
                        send_request_buton.setText("Send Message")
                        decline_request_buton.visibility = View.INVISIBLE
                        decline_request_buton.isEnabled = false
                    }
                })
            }
        })
    }
}
