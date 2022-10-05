package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val searchButton = findViewById<Button>(R.id.search_button)
        val libraryButton = findViewById<Button>(R.id.library_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(
                    this@MainActivity, "Нажали на кнопку поиска!", Toast.LENGTH_SHORT
                ).show()
            }
        }

        val libraryButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(
                    this@MainActivity, "Нажали на кнопку медиатеки!", Toast.LENGTH_SHORT
                ).show()
            }
        }

        settingsButton.setOnClickListener {
            Toast.makeText(
                this@MainActivity, "Нажали на кнопку медиатеки!", Toast.LENGTH_SHORT
            ).show()
        }

        searchButton.setOnClickListener(searchButtonClickListener)
        libraryButton.setOnClickListener(libraryButtonClickListener)
    }
}