/**
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2020 by Frederic-Charles Barthelery.
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
package com.geekorum.geekdroid

import androidx.room.TypeConverter

/**
 * Extensions for the Room architecture component libraries
 */


/**
 * Generic TypeConverters for Room.
 */
class RoomConverters {

    @TypeConverter
    fun fromPlainString(data: String?): List<String> {
        if (data == null) {
            return emptyList()
        }
        return data.split(", ")
    }

    @TypeConverter
    fun listToPlainString(data: List<String>?): String {
        if (data == null) {
            return ""
        }
        return data.joinToString(", ")
    }
}
