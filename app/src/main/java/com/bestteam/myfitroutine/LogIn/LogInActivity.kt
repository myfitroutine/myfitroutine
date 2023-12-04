package com.bestteam.myfitroutine.LogIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.SignUp.SignUpActivity
import com.bestteam.myfitroutine.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
    private var auth : FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signUpButton = binding.loginSignupButton

        signUpButton.setOnClickListener{
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}