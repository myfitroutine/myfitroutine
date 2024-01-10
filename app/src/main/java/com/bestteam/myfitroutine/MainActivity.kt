package com.bestteam.myfitroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bestteam.myfitroutine.View.MainFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentFrame, MainFragment())
            .commit()
    }
}