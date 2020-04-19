package com.example.chatton

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var auth: FirebaseAuth
    private lateinit var RootRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //DEFINE VARIABLE
        val chat_buton = findViewById<Button>(R.id.chat_buton)
        val group_buton = findViewById<Button>(R.id.group_buton)
        val contact_buton = findViewById<Button>(R.id.contact_buton)
        val request_buton = findViewById<Button>(R.id.request_buton)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        RootRef = FirebaseDatabase.getInstance().reference

        //FRAGMENT PART
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.view, ChatsFragment())
        fragmentTransaction.commit()
        chat_buton.setOnClickListener {
            changeFragment(ChatsFragment()) }
        group_buton.setOnClickListener {
            changeFragment(GroupFragment()) }
        contact_buton.setOnClickListener {
            changeFragment(ContactFragment()) }
        request_buton.setOnClickListener{
            changeFragment(RequestFragment())
        }


        //TOOLBAR
        setSupportActionBar(toolbar)
        setTitle("ChatOn")
    }

    fun changeFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.view, fragment)
        fragmentTransaction.commit()
    }
    override fun onStart() {
        super.onStart()
        if (currentUser == null) {
            sendUserToLoginActivity();
        }
        else {
            VeryUserExistance()
        }
    }

    private fun VeryUserExistance() {
        val currentuserID = auth.currentUser?.uid
        if (currentuserID != null) {
            RootRef.child(currentuserID).addValueEventListener(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child("name").exists()) {
                        val toast = Toast.makeText(applicationContext, "Wellcome", Toast.LENGTH_LONG)
                        toast.show()
                    }

                }override fun onCancelled(error: DatabaseError) {
                    //print error.message
                }
            })
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.main_logout -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.main_settings -> {
                auth.signOut()
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.main_profile -> {
                sendUserToProfileActivity()
                return true
            }
            R.id.main_group -> {
                requestNewGroup()
                return true
            }
            R.id.main_find_friend -> {
                val intent = Intent(this, FindFriendActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestNewGroup() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Enter Group Name: ")
        val groupNameField = EditText(this)
        groupNameField.setHint("e.g Coding Cafe")
        alert.setView(groupNameField)
        alert.setPositiveButton("Create") { dialogInterface: DialogInterface, i: Int ->
            val groupName = groupNameField.text.toString()
            if (TextUtils.isEmpty(groupName)) {
                Toast.makeText(applicationContext, "Please Write Group Name", Toast.LENGTH_LONG).show()
            } else{
                CreateNewGroup(groupName)
            }
        }
        alert.setNegativeButton("Cancel") {dialogInterface: DialogInterface, i: Int ->
            dialogInterface.cancel()}
        alert.show()
    }

    private fun CreateNewGroup(groupName: String) {
        RootRef.child("Group").child(groupName).setValue("").addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, groupName + " is created.", Toast.LENGTH_LONG).show()
            } else {
            }
        }
            

    }

    private fun sendUserToLoginActivity() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }
    private fun sendUserToProfileActivity() {
        val profileIntent = Intent(this, ProfileActivity::class.java)
        startActivity(profileIntent)
    }
    private fun sendUserToSettingsActivity() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }


}


