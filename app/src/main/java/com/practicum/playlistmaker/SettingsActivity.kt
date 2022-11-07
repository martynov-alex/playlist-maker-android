package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shareButton = findViewById<ImageView>(R.id.share_button)
        val supportButton = findViewById<ImageView>(R.id.support_button)
        val offerButton = findViewById<ImageView>(R.id.offer_button)

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