package com.bestteam.myfitroutine.LogIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bestteam.myfitroutine.MainActivity
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.databinding.ActivityFirstMainBinding
import com.bestteam.myfitroutine.databinding.ActivityLoginBinding
import com.bestteam.myfitroutine.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth

class FirstMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstMainBinding
    private var auth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirstMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val goLoginPG = binding.startButton
        val currentUser = auth?.currentUser

        goLoginPG.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            goLoginPG.setBackgroundResource(R.drawable.login_signup_button)
        }
        if (currentUser != null) {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
    }
}