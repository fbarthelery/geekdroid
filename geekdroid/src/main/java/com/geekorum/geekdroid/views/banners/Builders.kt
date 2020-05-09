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
package com.geekorum.geekdroid.views.banners

import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.IconCompat

/**
 * Helper class to build a [BannerSpec]
 */
class BannerBuilder(
    private val context: Context
) {
    private var message: String = ""
    private var icon: IconCompat? = null

    private var positiveBtn: ButtonSpec? = null
    private var negativeBtn: ButtonSpec? = null

    fun setMessage(msg: String): BannerBuilder {
        message = msg
        return this
    }

    fun setMessage(@StringRes msgId: Int): BannerBuilder {
        return setMessage(context.getString(msgId))
    }

    fun setIcon(icon: IconCompat?): BannerBuilder {
        this.icon = icon
        return this
    }

    fun setPositiveButton(
        @StringRes textId: Int, onClickListener: View.OnClickListener
    ): BannerBuilder {
        val text = context.getString(textId)
        return setPositiveButton(text, onClickListener)
    }

    fun setPositiveButton(text: String, onClickListener: View.OnClickListener): BannerBuilder {
        positiveBtn = ButtonSpec(text, onClickListener)
        return this
    }

    fun setNegativeButton(
        @StringRes textId: Int, onClickListener: View.OnClickListener
    ): BannerBuilder {
        val text = context.getString(textId)
        return setNegativeButton(text, onClickListener)
    }

    fun setNegativeButton(text: String, onClickListener: View.OnClickListener): BannerBuilder {
        negativeBtn = ButtonSpec(text, onClickListener)
        return this
    }

    fun build(): BannerSpec {
        return BannerSpec(message, positiveBtn, negativeBtn, icon)
    }
}



fun buildBanner(context: Context, buildSpec: BannerBuilderDsl.() -> Unit): BannerSpec {
    val dsl = BannerBuilderDsl(context)
    dsl.buildSpec()
    return dsl.build()
}

class BannerBuilderDsl internal constructor(
    private val context: Context
) {

    var message: String = ""
    var messageId = 0
    var icon: IconCompat? = null

    private var positiveBtn: ButtonSpec? = null
    private var negativeBtn: ButtonSpec? = null

    fun setPositiveButton(@StringRes textId: Int, onClickListener: (View) -> Unit) {
        val text = context.getString(textId)
        setPositiveButton(text, onClickListener)
    }

    fun setPositiveButton(text: String, onClickListener: (View) -> Unit) {
        positiveBtn = ButtonSpec(text,
            View.OnClickListener(onClickListener))
    }

    fun setNegativeButton(@StringRes textId: Int, onClickListener: (View) -> Unit) {
        val text = context.getString(textId)
        setNegativeButton(text, onClickListener)
    }

    fun setNegativeButton(text: String, onClickListener: (View) -> Unit) {
        negativeBtn = ButtonSpec(text,
            View.OnClickListener(onClickListener))
    }

    internal fun build(): BannerSpec {
        val msg = if (messageId != 0) context.getString(messageId) else message
        return BannerSpec(msg, positiveBtn, negativeBtn, icon)
    }
}
