package com.bestteam.myfitroutine.SignUp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private val binding : ActivitySignupBinding = ActivitySignupBinding.inflate(layoutInflater)
    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }
}