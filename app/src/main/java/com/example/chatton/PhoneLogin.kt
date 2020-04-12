package com.example.chatton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.*

class PhoneLogin : AppCompatActivity() {

    lateinit var SendVerificationCodeButton : Button
    lateinit var VerifyButton : Button
    lateinit var InputPhoneNumber: EditText
    lateinit var InputVerificaitonCode: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_login)

        SendVerificationCodeButton = findViewById(R.id.send_ver_code_button)
        VerifyButton = findViewById(R.id.verify_button)
        InputPhoneNumber = findViewById(R.id.phone_number_input)
        InputVerificaitonCode = findViewById(R.id.verification_code_input)

        SendVerificationCodeButton.setOnClickListener { view ->
            SendVerificationCodeButton.setVisibility(View.INVISIBLE)
            InputPhoneNumber.setVisibility(View.INVISIBLE)

            VerifyButton.setVisibility(View.VISIBLE)
            InputVerificaitonCode.setVisibility(View.VISIBLE)

        }
    }
}
