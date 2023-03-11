package com.example.roaddocmanagement.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.roaddocmanagement.R
import com.example.roaddocmanagement.databinding.ActivitySignInBinding
import com.example.roaddocmanagement.models.User
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }

        setupActionBar(binding.toolbarSignInActivity)
    }

    fun signInSuccess(user: User){
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun signInRegisteredUser(){
        val email: String = binding.etEmailSignIn.text.toString().trim{it <= ' '}
        val password: String = binding.etPasswordSignIn.text.toString().trim{it <= ' '}

        if (validateForm(email,password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task->
                    hideProgressDialog()
                    if (task.isSuccessful){
                        //Sign in success, update UI with the signed-in user's  information
                        Log.d("Sign in", "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this,MainActivity::class.java))
                    }else{
                        //If sign in fails, display a message to the user
                        Log.w("Sign in", "signInWithEmail:failure",task.exception)
                        Toast.makeText(baseContext,"Authentication failed.",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }
            else -> {
                true
            }

        }
    }
}