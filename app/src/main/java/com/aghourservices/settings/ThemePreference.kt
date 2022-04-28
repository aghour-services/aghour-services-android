package com.aghourservices.settings

import android.content.Context
import androidx.preference.PreferenceManager
import com.aghourservices.constants.Constants.Companion.DARK_STATUS

class ThemePreference(context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getInt(DARK_STATUS, 0)
        set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()
}
