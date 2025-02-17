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
package com.geekorum.geekdroid.bindings

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Rect
import android.graphics.Typeface
import android.view.TouchDelegate
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.geekorum.geekdroid.R
import com.geekorum.geekdroid.views.CheckableImageView

/**
 * Various adapters for the Android Data Binding library
 */


@BindingAdapter("srcResource")
fun setImageResources(imageView: ImageView, resourceId: Int) {
    imageView.setImageResource(resourceId)
}

@BindingAdapter("grayscale")
fun setImageGrayscale(imageView: ImageView, greyscale: Boolean) {
    if (greyscale) {
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        imageView.colorFilter = ColorMatrixColorFilter(colorMatrix)
    } else {
        imageView.clearColorFilter()
    }
}

@BindingAdapter("backgroundResource")
fun setBackgroundResource(view: View, resourceId: Int) {
    view.setBackgroundResource(resourceId)
}

@BindingAdapter("textStyle")
fun setTextStyle(view: TextView, style: Int) {
    view.setTypeface(Typeface.create(view.typeface, style), style)
}

@BindingAdapter("touchHeight", "touchWidth")
fun setTouchArea(view: View, touchHeight: Float, touchWidth: Float) {
    val parent = view.parent
    if (parent is View) {
        val parentView = parent as View
        parentView.post {
            val delegateArea = Rect()
            view.getHitRect(delegateArea)
            val deltaLeftRight = (touchWidth - delegateArea.width()) / 2
            delegateArea.right += deltaLeftRight.toInt()
            delegateArea.left -= deltaLeftRight.toInt()
            val deltaTopBottom = (touchHeight - delegateArea.height()) / 2
            delegateArea.bottom += deltaTopBottom.toInt()
            delegateArea.top -= deltaTopBottom.toInt()
            val delegate = TouchDelegate(delegateArea, view)
            parentView.touchDelegate = delegate
        }
    }
}

@BindingAdapter("onCheckedChanged")
fun setListener(checkableImageView: CheckableImageView,
                newListener: CheckableImageView.OnCheckedChangeListener?) {
    checkableImageView.setOnCheckedChangeListener(newListener)
}

@BindingAdapter("onPageSelected")
fun setListener(viewPager: ViewPager, listener: OnPageSelectedListener?) {
    var newListener: OnPageChangeListener? = null
    if (listener != null) {
        newListener = object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                listener.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        }
    }
    val oldListener = ListenerUtil.trackListener(viewPager,
            newListener, R.id.onPageSelectedListener)
    if (oldListener != null) {
        viewPager.removeOnPageChangeListener(oldListener)
    }
    if (newListener != null) {
        viewPager.addOnPageChangeListener(newListener)
    }
}

interface OnPageSelectedListener {
    fun onPageSelected(position: Int)
}
