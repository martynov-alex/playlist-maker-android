package com.practicum.playlistmaker.activity.library

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.practicum.playlistmaker.R

class LibraryActivity : AppCompatActivity() {
    private lateinit var backButtonView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        initVariables()
        initListeners()
    }

    private fun initVariables() {
        backButtonView = findViewById(R.id.back_button)
    }

    private fun initListeners() {
        backButtonView.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

}