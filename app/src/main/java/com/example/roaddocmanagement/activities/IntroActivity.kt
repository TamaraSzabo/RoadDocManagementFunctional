package com.example.roaddocmanagement.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import com.example.roaddocmanagement.R

@Suppress("DEPRECATION")
class IntroActivity : BaseActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        val btnRegister = findViewById<Button>(R.id.btn_register_intro)
        val btnSingIn = findViewById<Button>(R.id.btn_sign_in_intro)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val intentRegister = Intent(this, RegisterActivity::class.java)
        val intentSingIn = Intent(this, SignInActivity::class.java)
        btnRegister.setOnClickListener {
            startActivity(intentRegister)
        }

        btnSingIn.setOnClickListener {
            startActivity(intentSingIn)
        }
    }
}