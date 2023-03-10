package com.example.roaddocmanagement.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.example.roaddocmanagement.databinding.ActivityIntroBinding

@Suppress("DEPRECATION")
class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val intentRegister = Intent(this, RegisterActivity::class.java)
        val intentSingIn = Intent(this, SignInActivity::class.java)

        binding.btnRegisterIntro.setOnClickListener {
            startActivity(intentRegister)
        }

        binding.btnSignInIntro.setOnClickListener {
            startActivity(intentSingIn)
        }
    }
}