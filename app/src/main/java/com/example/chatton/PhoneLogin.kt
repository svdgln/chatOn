@file:Suppress("DEPRECATION")

package com.example.chatton

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class PhoneLogin : AppCompatActivity() {

    private lateinit var SendVerificationCodeButton : Button
    private lateinit var VerifyButton : Button
    private lateinit var InputPhoneNumber: EditText
    private lateinit var InputVerificaitonCode: EditText
    private lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private  lateinit var storedVerificationId : String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var auth :FirebaseAuth
    private lateinit var loadingaBar : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)

        auth = FirebaseAuth.getInstance()

        SendVerificationCodeButton = findViewById(R.id.send_ver_code_button)
        VerifyButton = findViewById(R.id.verify_button)
        InputPhoneNumber = findViewById(R.id.phone_number_input)
        InputVerificaitonCode = findViewById(R.id.verification_code_input)
        loadingaBar = ProgressDialog(this)

        SendVerificationCodeButton.setOnClickListener {

            val phoneNumber = InputPhoneNumber.text.toString()

            if(TextUtils.isEmpty(phoneNumber)){
                val toast =  Toast.makeText(applicationContext, "Please enter your phone number first", Toast.LENGTH_LONG)
                toast.show()
            }
            else{
                loadingaBar.setTitle("Phone Verification");
                loadingaBar.setMessage("Please wait, while we are authenticating using your phone...");
                loadingaBar.setCanceledOnTouchOutside(false);
                loadingaBar.show();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber, // Phone number to verify
                    60, // Timeout duration
                    TimeUnit.SECONDS, // Unit of timeout
                    this, // Activity (for callback binding)
                    callbacks) // OnVerificationStateChangedCallbacks

            }

        }

        VerifyButton.setOnClickListener {
            SendVerificationCodeButton.setVisibility(View.INVISIBLE)
            InputPhoneNumber.setVisibility(View.INVISIBLE)

            val verificationCode = InputVerificaitonCode.text.toString()
            if(TextUtils.isEmpty(verificationCode)){
                val toast =  Toast.makeText(applicationContext, "Please write verification code first", Toast.LENGTH_LONG)
                toast.show()
            }
            else{
                loadingaBar.setTitle("Code Verification")
                loadingaBar.setMessage("Please wait.... ")
                loadingaBar.setCanceledOnTouchOutside(false)
                loadingaBar.show()

                val credential = PhoneAuthProvider.getCredential(storedVerificationId, verificationCode)
                signInWithPhoneAuthCredential(credential)
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)

            }

            override fun onVerificationFailed(e: FirebaseException) {
                loadingaBar.dismiss()
                val toast =  Toast.makeText(applicationContext, "Invalid Phone Number,Please enter correct phone number with your country code", Toast.LENGTH_LONG)
                toast.show()
                SendVerificationCodeButton.setVisibility(View.VISIBLE)
                InputPhoneNumber.setVisibility(View.VISIBLE)

                VerifyButton.setVisibility(View.INVISIBLE)
                InputVerificaitonCode.setVisibility(View.INVISIBLE)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                storedVerificationId = verificationId
                resendToken = token
                loadingaBar.dismiss()

                val toast =  Toast.makeText(applicationContext, "Code has been sent, please check", Toast.LENGTH_LONG)
                toast.show()
                SendVerificationCodeButton.setVisibility(View.INVISIBLE)
                InputPhoneNumber.setVisibility(View.INVISIBLE)

                VerifyButton.setVisibility(View.VISIBLE)
                InputVerificaitonCode.setVisibility(View.VISIBLE)
            }

    }


}
    //You can login with your phone with signInWithPhoneAuthCredential() method easily.
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    loadingaBar.dismiss()
                    val toast =  Toast.makeText(applicationContext, "Succesful. You are logged in", Toast.LENGTH_LONG)
                    toast.show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()


                } else {
                    val message = task.exception.toString()
                    val toast =  Toast.makeText(applicationContext, "Error :"+ message, Toast.LENGTH_LONG)
                    toast.show()

                }
            }
    }




}
