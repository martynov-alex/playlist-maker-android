package com.practicum.playlistmaker.activity.library

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.activity.main.MainActivity

class LibraryActivity : AppCompatActivity() {
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        initVariables()
        initListeners()
    }

    private fun initVariables() {
        backButton = findViewById(R.id.back_button)
    }

    private fun initListeners() {
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}