package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.settings.ui.router.SettingsNavigationRouter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var buttonShare: Button
    private lateinit var buttonSupport: Button
    private lateinit var buttonUserAgreement: Button
    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var buttonArrowBackSettings: androidx.appcompat.widget.Toolbar

    private val viewModelSettings: SettingsViewModel by viewModel()
    private val settingsNavigationRouter = SettingsNavigationRouter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
            settingsNavigationRouter.backView()
        }
    }
}

