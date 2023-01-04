package com.practicum.playlistmaker.activity.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchCompat
    private lateinit var shareButton: ImageView
    private lateinit var supportButton: ImageView
    private lateinit var offerButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initVariables()
        setListeners()
        themeSwitcher.isChecked = (applicationContext as App).isDarkTheme
    }

    private fun initVariables() {
        themeSwitcher = findViewById(R.id.theme_switcher)
        shareButton = findViewById(R.id.share_button)
        supportButton = findViewById(R.id.support_button)
        offerButton = findViewById(R.id.offer_button)
    }

    private fun setListeners() {
        themeSwitcher.setOnCheckedChangeListener() { switcher, checked ->
            if (switcher?.isPressed == true) (applicationContext as App).switchAndSaveTheme(checked)
        }

        shareButton.setOnClickListener {
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_share_url))
                putExtra(Intent.EXTRA_TITLE, getString(R.string.settings_share_title))
            }, getString(R.string.settings_share_intent_title))
            startActivity(share)
        }

        supportButton.setOnClickListener {
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse(getString(R.string.settings_support_uri))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.settings_support_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_support_text))

            }, getString(R.string.settings_support_intent_title))
            startActivity(share)
        }

        offerButton.setOnClickListener {
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.settings_offer_uri))
            }, getString(R.string.settings_offer_intent_title))
            startActivity(share)
        }

    }
}