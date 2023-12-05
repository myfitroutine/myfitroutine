package com.bestteam.myfitroutine.SignUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bestteam.myfitroutine.LogIn.LogInActivity
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.databinding.ActivityLoginBinding
import com.bestteam.myfitroutine.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var auth: FirebaseAuth? = null
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        binding.signupButton.setOnClickListener {
            if (allcheck())
            {
                signup()
            }
        }
    }

    private fun allcheck(): Boolean {

        val email = binding.signupEmail.text.toString()
        val pw = binding.signupPw.text.toString()
        val checkpw = binding.signupPwAgain.text.toString()
        val age = binding.signupAge.text.toString()
        val kg = binding.signupKg.text.toString()
        val cm = binding.signupCm.text.toString()
        val name = binding.signupName.text.toString()
        val male = binding.signupMaleBox.isChecked
        val female = binding.signupFemaleBox.isChecked


        if (email.isEmpty()) {
            binding.signupEmail.error = "이메일을 입력해주세요."
            return false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.signupEmail.error = "이메일 형식이 아닙니다."
            return false
        } else if (pw.isEmpty()) {
            binding.signupPw.error = "비밀번호를 입력해주세요."
            return false
        } else if (!pwFilter(pw)) {
            binding.signupPw.error = "최소 하나 이상의 특수문자를 입력해 주세요."
            return false
        } else if (!isValidPassword(pw)) {
            binding.signupPw.error = "비밀번호는 최소 7글자 이상이어야 하며, 영어, 숫자, 특수 문자를 포함해야 합니다."
            return false
        } else if (checkpw != pw) {
            binding.signupPwAgain.error = "비밀번호가 일치하지않습니다."
            return false
        } else if (cm.isEmpty()) {
            binding.signupCm.error = "키를 입력해주세요. "
            return false
        } else if (name.isEmpty()) {
            binding.signupName.error = "이름을 입력해주세요. "
        } else if (kg.isEmpty()) {
            binding.signupKg.error = "몸무게를 입력해주세요. "
        } else if (!male && !female) {
            Toast.makeText(this, "성별을 선택해주세요.", Toast.LENGTH_LONG).show()
            return false
        } else if (male && female) {
            Toast.makeText(this, "성별이 두가지 모두 선택 되어있습니다.", Toast.LENGTH_LONG).show()
            return false
        }

        val cmToInt = cm.toDoubleOrNull()
        val kgToInt = kg.toDoubleOrNull()
        val ageToInt = age.toIntOrNull()

        if (cmToInt == null || kgToInt == null || ageToInt == null) {
            Toast.makeText(this, "키, 몸무게, 나이는 숫자로 입력해주세요.", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }


    fun pwFilter(text: String): Boolean {
        val specialWord = setOf(
            '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '-', '<', '>', '=', '+', '?')
        return text.any {
            it in specialWord
        }

    }

    private fun isValidPassword(pw: String): Boolean {
            return pw.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}\$".toRegex())

    }

    private fun signup() {
        val email = binding.signupEmail.text.toString()
        val pw = binding.signupPw.text.toString()

        auth?.createUserWithEmailAndPassword(email, pw)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth?.currentUser
                    if (user != null) {
                        saveUserData(user.uid)
                        Toast.makeText(this, "회원 가입 성공", Toast.LENGTH_SHORT).show()
                        intent = Intent(this,LogInActivity :: class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "회원 가입 실패: 사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "회원 가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData(uid: String) {
        val name = binding.signupName.text.toString()
        val age = binding.signupAge.text.toString().toInt()
        val kg = binding.signupKg.text.toString().toDouble()
        val cm = binding.signupCm.text.toString().toDouble()
        val gender = if (binding.signupMaleBox.isChecked) "male" else "female"

        val userData = UserData(name, age, kg, cm, gender)

        val userCollection = firestore.collection("UserData")
        val userDocument = userCollection.document(uid)

        userDocument.set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "사용자 데이터 저장 성공", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "사용자 데이터 저장 실패: $e", Toast.LENGTH_SHORT).show()
            }
    }


}



