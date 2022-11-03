package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shareButton = findViewById<ImageButton>(R.id.share_button)
        val supportButton = findViewById<ImageButton>(R.id.support_button)
        val agreementButton = findViewById<ImageButton>(R.id.agreement_button)

        shareButton.setOnClickListener {
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "https://practicum.yandex.ru/android-developer/")
                putExtra(Intent.EXTRA_TITLE, "Лучший курс по Android разработке!")
            }, "Share link")
            startActivity(share)
        }

        supportButton.setOnClickListener {
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("a.a.martynov@yandex.ru"))
                putExtra(Intent.EXTRA_SUBJECT, "Сообщение разработчикам и разработчицам приложения Playlist Maker")
                putExtra(Intent.EXTRA_TEXT, "Спасибо разработчикам и разработчицам за крутое приложение!")

            }, "Send message")
            startActivity(share)
        }

        agreementButton.setOnClickListener {
            val share = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse("https://yandex.ru/legal/practicum_offer/")
            }, "Open agreement")
            startActivity(share)
        }

    }
}