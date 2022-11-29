/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2022 by Frederic-Charles Barthelery.
 *
 * This file is part of Geekdroid.
 *
 * Geekdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Geekdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Geekdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.geekorum.geekdroid.preferences

import android.media.Ringtone
import android.media.RingtoneManager
import android.preference.Preference
import android.preference.RingtonePreference
import androidx.core.net.toUri
import androidx.preference.PreferenceManager
import com.geekorum.geekdroid.R

/**
 * Helper class to set the summary of an [android.preference.RingtonePreference] to its actual value.
 * @deprecated use androidx.preference.Preference.SummaryProvider
 */
@Deprecated("Use androidx.preference.Preference.SummaryProvider")
class RingtonePreferenceSummaryBinder : Preference.OnPreferenceChangeListener {
    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val stringValue = newValue.toString()

        when (preference) {
            is RingtonePreference -> setRingtonePreferenceSummary(preference, stringValue)
        }
        return true
    }

    private fun setRingtonePreferenceSummary(preference: Preference, stringValue: String) {
        val summary = when {
            // Empty values correspond to 'silent' (no ringtone).
            stringValue.isEmpty() -> preference.context.getString(R.string.geekdroid_pref_ringtone_silent)
            else -> {
                val ringtone: Ringtone? = RingtoneManager.getRingtone(preference.context, stringValue.toUri())
                ringtone?.getTitle(preference.context)
            }
        }
        preference.summary = summary
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     */
    fun bindPreferenceSummaryToValue(preference: Preference) {
        // Set the listener to watch for value changes.
        preference.onPreferenceChangeListener = this

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
            PreferenceManager
                .getDefaultSharedPreferences(preference.context)
                .getString(preference.key, "")!!)
    }

}
