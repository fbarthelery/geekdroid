/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2025 by Frederic-Charles Barthelery.
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
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import com.geekorum.geekdroid.databinding.ViewBannerExtendedBinding
import com.geekorum.geekdroid.databinding.ViewBannerSimpleBinding
import com.geekorum.geekdroid.views.doOnApplyWindowInsets
import com.google.android.material.card.MaterialCardView
import com.google.android.material.internal.ViewUtils
import com.google.android.material.shape.MaterialShapeDrawable

/**
 * Simple container for Material banners.
 *
 * Add it in your layout where you want to display a banner, then use [show] and [hide] methods
 * to control the banner.
 */
class BannerContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val layoutInflater = LayoutInflater.from(context)
    private val backgroundDrawable: MaterialShapeDrawable = MaterialShapeDrawable.createWithElevationOverlay(context)

    init {
        background = backgroundDrawable
    }

    fun hide() {
        removeAllViews()
    }

    fun show(banner: BannerSpec) {
        removeAllViews()

        val binding = when {
            (banner.positiveBtn != null && banner.negativeBtn != null)
                    || (banner.icon != null)
            -> createExtendedBanner(banner)
            else -> createSimpleBanner(banner)
        }
    }

    private fun createSimpleBanner(banner: BannerSpec): ViewBannerSimpleBinding {
        val view = ViewBannerSimpleBinding.inflate(layoutInflater,
            this, true)
        bindSimpleBanner(view, banner)
        return view
    }

    private fun bindSimpleBanner(binding: ViewBannerSimpleBinding, banner: BannerSpec) {
        binding.message.text = banner.message
        when (banner.positiveBtn) {
            null -> binding.positiveBtn.visibility = View.GONE
            else -> {
                val (text, listener) = banner.positiveBtn
                binding.positiveBtn.text = text
                binding.positiveBtn.setOnClickListener(listener)
            }
        }
    }


    private fun createExtendedBanner(banner: BannerSpec): ViewBannerExtendedBinding {
        val view = ViewBannerExtendedBinding.inflate(layoutInflater,
            this, true)
        bindExtendedBanner(view, banner)
        return view
    }

    private fun bindExtendedBanner(binding: ViewBannerExtendedBinding, banner: BannerSpec) {
        binding.message.text = banner.message
        binding.icon.setImageIcon(banner.icon?.toIcon(binding.icon.context))
        if (banner.icon == null) {
            binding.icon.visibility = View.GONE
        }
        when (banner.positiveBtn) {
            null -> binding.positiveBtn.visibility = View.GONE
            else -> {
                val (text, listener) = banner.positiveBtn
                binding.positiveBtn.text = text
                binding.positiveBtn.setOnClickListener(listener)
            }
        }
        when (banner.negativeBtn) {
            null -> binding.negativeBtn.visibility = View.GONE
            else -> {
                val (text, listener) = banner.negativeBtn
                binding.negativeBtn.text = text
                binding.negativeBtn.setOnClickListener(listener)
            }
        }
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        // just dispatch insets to children if any
        if (insets.isConsumed) {
            return insets
        }
        var consumedInsets = insets
        forEach {
            consumedInsets = it.dispatchApplyWindowInsets(consumedInsets)
            if (consumedInsets.isConsumed) {
                return@forEach
            }
        }
        return consumedInsets
    }

    override fun setElevation(elevation: Float) {
        if (background != null && background == backgroundDrawable) {
            backgroundDrawable.elevation = elevation
        } else {
            post { setElevation(elevation) }
        }
    }

    override fun getElevation(): Float = backgroundDrawable.elevation
}


/**
 * Describe a banner
 */
data class BannerSpec(
    val message: String,
    val positiveBtn: ButtonSpec? = null,
    val negativeBtn: ButtonSpec? = null,
    val icon: IconCompat? = null
)

data class ButtonSpec(
    val text: String,
    val onClickListener: View.OnClickListener
)
