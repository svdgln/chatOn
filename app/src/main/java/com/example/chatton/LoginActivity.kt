package com.example.chatton

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
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
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private var currentUser = FirebaseAuth.getInstance().currentUser
    private lateinit var auth: FirebaseAuth
    lateinit var PhoneLoginButton : Button
    private lateinit var UserRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //val login_mail = findViewById<EditText>(R.id.login_mail)
        //val login_password = findViewById<EditText>(R.id.login_password)
        val login_buton = findViewById<Button>(R.id.login_buton)
        val new_account = findViewById<TextView>(R.id.new_account)
        PhoneLoginButton = findViewById(R.id.phone_login_button)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        UserRef = FirebaseDatabase.getInstance().reference.child("Users")

        new_account.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        login_buton.setOnClickListener{
            AllowUserToLogin()
        }
        PhoneLoginButton.setOnClickListener {
            val intent = Intent(this, PhoneLogin::class.java)
            startActivity(intent)
        }


    }

    private fun AllowUserToLogin() {
        val mail = login_mail.text.toString()
        val password = login_password.text.toString()

        if (TextUtils.isEmpty(mail)) {
            val toast = Toast.makeText(applicationContext, "Please Enter Mail", Toast.LENGTH_LONG)
            toast.show()
        }
        if (TextUtils.isEmpty(password)) {
            val toast =
                Toast.makeText(applicationContext, "Please Enter Password", Toast.LENGTH_LONG)
            toast.show()
        }
        else{
            auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener ( this, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {

                    val currentUserID = auth.currentUser?.uid
                    val deviceToken = FirebaseInstanceId.getInstance().token
                    UserRef.child(currentUserID.toString()).child("device_token").setValue(deviceToken).addOnCompleteListener(
                        OnCompleteListener {
                            if (task.isSuccessful){
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                val toast = Toast.makeText(applicationContext, "Logged in Successfully ", Toast.LENGTH_LONG)
                                toast.show()
                            }
                        })


                } else {
                  //  val errmessage = task.exception.toString()
                    val toastnotmember = Toast.makeText(applicationContext, "Please Create an Account " , Toast.LENGTH_LONG)
                    toastnotmember.show()

                    // val toast = Toast.makeText(applicationContext, "ErrorVar: " + errmessage, Toast.LENGTH_LONG)
                    // toast.show()
                }
            })
        }
    }
}