package com.bestteam.myfitroutine.LogIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.databinding.ActivityFirstMainBinding
import com.bestteam.myfitroutine.databinding.ActivityLoginBinding
import com.bestteam.myfitroutine.databinding.FragmentMainBinding

class FirstMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirstMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFirstMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val goLoginPG = binding.startButton

        goLoginPG.setOnClickListener{

            val intent = Intent(this,LogInActivity::class.java)
            startActivity(intent)

            }

        }
}