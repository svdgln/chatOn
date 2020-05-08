package com.example.chatton

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_register.*
import java.util.regex.Pattern

@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var RootRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //DEFINITION
        //val register_mail = findViewById<EditText>(R.id.register_mail)
        //val register_password = findViewById<EditText>(R.id.register_password)
        val register_buton = findViewById<Button>(R.id.register_buton)
        val have_account = findViewById<TextView>(R.id.have_account)

        auth = FirebaseAuth.getInstance()
        RootRef = FirebaseDatabase.getInstance().reference

        have_account.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        register_buton.setOnClickListener{
            CretateNewAccount()
        }

    }

    // TODO Password Validation https://gist.github.com/KalpeshTalkar/e6a50efd08a3d5d142e47da559936bb7
    private fun CretateNewAccount() {
        val mail = register_mail.text.toString()
        val password = register_password.text.toString()
        val password2 = register_password2.text.toString()

        if (TextUtils.isEmpty(mail)) {
            val toast = Toast.makeText(applicationContext, "Please Enter Mail" , Toast.LENGTH_LONG)

            toast.show()
        }
        if (TextUtils.isEmpty(password)) {
            val toast = Toast.makeText(applicationContext, "Please Enter Password", Toast.LENGTH_LONG)
            toast.show()
        }

        if (TextUtils.isEmpty(password2)) {
            val toast = Toast.makeText(applicationContext, "Please Enter Password", Toast.LENGTH_LONG)
            toast.show()
        }
        if (password!=password2) {
            val toast = Toast.makeText(applicationContext, "Passwords don't match. Please re-enter passwords", Toast.LENGTH_LONG)
            toast.show()
        }
        else{
            auth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener ( this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {

                    val deviceToken = FirebaseInstanceId.getInstance().token
                    val currentuserID = auth.currentUser?.uid
                    if (currentuserID != null) {
                        RootRef.child("User").child(currentuserID).setValue("")
                    }

                    RootRef.child("Users").child(currentuserID.toString()).child("device_token").setValue(deviceToken)

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    val toast = Toast.makeText(applicationContext, "Account Created Successfully ", Toast.LENGTH_LONG)
                    toast.show()
                } else {
                    val massege = task.exception.toString()
                    val toast = Toast.makeText(applicationContext, "Error: " + massege, Toast.LENGTH_LONG)
                    toast.show()
                }
            })
        }
    }


}