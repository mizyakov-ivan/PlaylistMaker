package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.net.Uri
import android.content.SharedPreferences
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val arrowBackButton= findViewById <ImageView> (R.id.arrow_back)

        arrowBackButton.setOnClickListener {
            val displayIntent = Intent(this, MainActivity::class.java)
            startActivity(displayIntent)
        }

        val switchTheme = findViewById<Switch>(R.id.themeSwitcher)

        switchTheme.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        switchTheme.setOnCheckedChangeListener { _ , isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        fun showShareDialog() {

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = resources.getString(R.string.text_plain)
            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.android_developer))
            startActivity(Intent.createChooser(intent, resources.getString(R.string.app_share)))
        }

        val shareButton = findViewById<Button>(R.id.share_button)
        shareButton.setOnClickListener {
            // Handle button click event here
            showShareDialog()
        }

        val supportButton = findViewById<Button>(R.id.help_button)
        supportButton.setOnClickListener{
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                data = Uri.parse(resources.getString(R.string.mail_to))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.email)))
                putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.theme_letter))
                putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.text_letter))
            }

            startActivity(supportIntent)
        }
        val agreementButton = findViewById<Button>(R.id.user_agreement)
        agreementButton.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreements_link)))
            startActivity(browserIntent)
        }
    }
}


