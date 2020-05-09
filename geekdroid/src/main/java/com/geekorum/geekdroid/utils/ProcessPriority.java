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
package com.geekorum.geekdroid.utils;

import android.os.Process;
import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Describes the allowed value for a Process priority.
 */
@IntDef({Process.THREAD_PRIORITY_AUDIO, Process.THREAD_PRIORITY_BACKGROUND, Process.THREAD_PRIORITY_DEFAULT,
        Process.THREAD_PRIORITY_DISPLAY, Process.THREAD_PRIORITY_FOREGROUND, Process.THREAD_PRIORITY_LESS_FAVORABLE,
        Process.THREAD_PRIORITY_LOWEST, Process.THREAD_PRIORITY_MORE_FAVORABLE, Process.THREAD_PRIORITY_URGENT_AUDIO,
        Process.THREAD_PRIORITY_URGENT_DISPLAY })
@Retention(SOURCE)
public @interface ProcessPriority { }
