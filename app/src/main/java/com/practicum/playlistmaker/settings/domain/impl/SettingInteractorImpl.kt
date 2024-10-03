package com.practicum.playlistmaker.settings.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSetting(isChecked: Boolean) {
        settingsRepository.updateThemeSetting(ThemeSettings(isChecked))
    }

    override fun isSystemInDarkTheme(): Boolean {
        return AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
    }


}