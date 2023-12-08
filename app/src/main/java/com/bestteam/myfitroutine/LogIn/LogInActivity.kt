package com.bestteam.myfitroutine.LogIn

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bestteam.myfitroutine.MainActivity
import com.bestteam.myfitroutine.SignUp.SignUpActivity
import com.bestteam.myfitroutine.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val signUpButton = binding.loginSignupButton
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val logInButton = binding.loginButton
        logInButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPw.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else if (email.isEmpty()){
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()){
                Toast.makeText(this,"비밀번호를 입력해주세요.",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun signIn(email: String, password: String) {
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
//                    Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                    val mainIntent = Intent(this, MainActivity::class.java)
                    startActivity(mainIntent)
                    finish()
                } else {
                    Toast.makeText(this, "로그인에 실패하였습니다.\n 이메일과 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
