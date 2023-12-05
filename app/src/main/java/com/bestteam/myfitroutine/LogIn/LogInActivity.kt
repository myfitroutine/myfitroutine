package com.bestteam.myfitroutine.LogIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bestteam.myfitroutine.MainActivity
import com.bestteam.myfitroutine.SignUp.SignUpActivity
import com.bestteam.myfitroutine.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.log

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val signUpButton = binding.loginSignupButton

        signUpButton.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val logInButton = binding.loginButton
        logInButton.setOnClickListener{
            Toast.makeText(this,"로그인에 성공하였습니다.",Toast.LENGTH_LONG).show()현
            val mainintent = Intent(this,MainActivity::class.java)
            startActivity(mainintent)
        }
    }
}
