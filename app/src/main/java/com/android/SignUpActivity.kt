package com.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.android.instagram.R

class SignUpActivity : AppCompatActivity() {
    lateinit var btnSignIn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignIn=findViewById(R.id.signinLink_btn)

        btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}