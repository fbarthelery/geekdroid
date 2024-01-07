/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2024 by Frederic-Charles Barthelery.
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
package com.geekorum.geekdroid.views

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.internal.ViewUtils


// see https://medium.com/androiddevelopers/windowinsets-listeners-to-layouts-8f9ccc8fa4d1
// Lazy implementation by using the Material one
@SuppressLint("RestrictedApi")
fun View.doOnApplyWindowInsets(block: (View, WindowInsetsCompat, ViewUtils.RelativePadding) -> WindowInsetsCompat) {
    ViewUtils.doOnApplyWindowInsets(this, ViewUtils.OnApplyWindowInsetsListener(block))
}
