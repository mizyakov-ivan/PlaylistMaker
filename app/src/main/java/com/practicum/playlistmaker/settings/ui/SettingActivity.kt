package com.practicum.playlistmaker.settings.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R

const val THEME_KEY = "key_for_theme"
const val THEME = "day_night_theme"
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val backButton = findViewById<ImageView>(R.id.arrow_back)
        backButton.setOnClickListener {
            finish()
        }
        val switchTheme = findViewById<SwitchMaterial>(R.id.switchTheme)
        val sharedPrefs = getSharedPreferences(THEME, MODE_PRIVATE)
        val checked = sharedPrefs.getBoolean(THEME_KEY, false)
        switchTheme.isChecked = checked
        (applicationContext as App).switchTheme(checked)
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            sharedPrefs.edit().putBoolean(THEME_KEY, isChecked).apply()
        }
        fun showShareDialog() {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = getString(R.string.text_plain)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_developer))
            startActivity(Intent.createChooser(intent, getString(R.string.app_share)))
        }

        val shareButton = findViewById<Button>(R.id.share_button)
        shareButton.setOnClickListener {
            showShareDialog()
        }
        val supportButton = findViewById<Button>(R.id.help_button)
        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(getString(R.string.mail_to))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_letter))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.text_letter))
            }
            startActivity(supportIntent)
        }
        val agreementButton = findViewById<Button>(R.id.user_agreement)
        agreementButton.setOnClickListener {
            try {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreements_link)))
                startIntent(browserIntent, resources.getString(R.string.error_no_app_browser))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.error_no_app_browser),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startIntent(intent: Intent, message: String) {
        try {
            startActivity(Intent.createChooser(intent, message))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}

