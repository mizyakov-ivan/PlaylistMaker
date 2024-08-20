package com.practicum.playlistmaker.settings.ui.activity

import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.creator.CreatorSettings
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var buttonShare: Button
    private lateinit var buttonSupport: Button
    private lateinit var buttonUserAgreement: Button
    private lateinit var viewModelSettings: SettingsViewModel
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var buttonArrowBackSettings: androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModelSettings = ViewModelProvider(
            this, SettingsViewModel.getSettingsViewModelFactory()
        )[SettingsViewModel::class.java]

        initViews()

        setListeners()

        //Отображение актуального состояния переключателя
        viewModelSettings.observeThemeSwitcherChecked().observe(this) { isChecked ->
            themeSwitcher.isChecked = isChecked
        }
    }

    private fun initViews() {
        buttonArrowBackSettings = findViewById(R.id.toolbarSetting)
        themeSwitcher = findViewById(R.id.switchTheme)
        buttonShare = findViewById(R.id.share_button)
        buttonSupport = findViewById(R.id.help_button)
        buttonUserAgreement = findViewById(R.id.user_agreement)
    }

    private fun setListeners() {
        //Реализация переключения темы
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModelSettings.clickThemeSwitcher(isChecked)
        }

        //Реализация нажатия кнопки "Поделиться приложением"
        buttonShare.setOnClickListener() {
            viewModelSettings.clickShareApp()
        }

        //Реализация кнопки "Поддержка"
        buttonSupport.setOnClickListener() {
            viewModelSettings.clickApplySupport()

        }

        //Реализация кнопки пользовательское соглашение
        buttonUserAgreement.setOnClickListener() {
            viewModelSettings.clickViewUserAgreement()
        }

        //Обработка нажатия на ToolBar "<-" и переход
        // на главный экран через закрытие экрана "Настройки"
        buttonArrowBackSettings.setOnClickListener() {
            CreatorSettings.getSettingsNavigationRouter(this).backView()
        }
    }
}

