package com.aghourservices.settings

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager

class ThemePreference : Application() {

    class AppTheme(context: Context) {
        companion object {
            private const val DARK_STATUS = "Aghour_Dark_Mode"
        }

        private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        var darkMode = preferences.getInt(DARK_STATUS, 0)
            set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()
    }
}
