package com.example.chatton

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var RootRef: DatabaseReference
    private lateinit var currentUserID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val updateAccountProfile= findViewById<Button>(R.id.update_settings_button)
      //  val userName = findViewById<EditText>(R.id.set_user_name)
      //  val userStatus = findViewById<EditText>(R.id.set_profile_status)
      //  val CircleImageView = findViewById<CircleImageView>(R.id.set_profile_image)

        auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser?.uid.toString()
        RootRef = FirebaseDatabase.getInstance().reference

        updateAccountProfile.setOnClickListener{
            UpdateProfile()
        }

        RetrieveUserInfo()

    }

    private fun RetrieveUserInfo() {
        RootRef.child("Users").child(currentUserID)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.hasChild("name")) {
                        val retrieveUserName = dataSnapshot.child("name").getValue().toString()
                        val retrieveStatus = dataSnapshot.child("status").getValue().toString()
                        set_user_name.setText(retrieveUserName)
                        set_profile_status.setText(retrieveStatus)
                    }
                    else {
                        val toast = Toast.makeText(applicationContext, "Please set your profile ", Toast.LENGTH_LONG)
                        toast.show()
                    }
                }override fun onCancelled(error: DatabaseError) {
                    //print error.message
                }
            })
    }

    private fun UpdateProfile() {
        val username = set_user_name.text.toString()
        val setStatus = set_profile_status.text.toString()

        if (TextUtils.isEmpty(username)) {
            val toast = Toast.makeText(applicationContext, "Please Write Your Name ", Toast.LENGTH_LONG)
            toast.show()
        }
        if (TextUtils.isEmpty(setStatus)) {
            val toast = Toast.makeText(applicationContext, "Please Write Your Status ", Toast.LENGTH_LONG)
            toast.show()
        }
        else{
            var hashMap : HashMap<String, String> = HashMap<String, String> ()
                hashMap.put("uid",currentUserID)
                hashMap.put("name",username)
                hashMap.put("status",setStatus)
            RootRef.child("Users").child(currentUserID).setValue(hashMap)
                .addOnCompleteListener(
                OnCompleteListener { task ->
                    when {
                        task.isSuccessful -> {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            val toast = Toast.makeText(applicationContext, "Profile Updated Successfully ", Toast.LENGTH_LONG)
                            toast.show()
                        }
                        else -> {
                            val toast = Toast.makeText(applicationContext, "Error ", Toast.LENGTH_LONG)
                            toast.show()
                        }
                    }
                })
        }
    }


}
