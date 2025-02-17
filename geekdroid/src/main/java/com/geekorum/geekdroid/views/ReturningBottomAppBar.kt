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
package com.geekorum.geekdroid.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ScrollingView
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomappbar.BottomAppBar

/**
 * A [BottomAppBar] that returned on screen when you reach the end of the scrolling view
 */
class ReturningBottomAppBar : BottomAppBar {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun getBehavior(): BottomAppBar.Behavior {
        return Behavior()
    }

    class Behavior : BottomAppBar.Behavior() {

        override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: BottomAppBar,
                                    target: View, dxConsumed: Int, dyConsumed: Int,
                                    dxUnconsumed: Int, dyUnconsumed: Int, type: Int,
                                    consumed: IntArray) {

            val isWithinEndOfScroll = when(target) {
                is NestedScrollView -> target.scrollY > (target.getScrollRange() - child.measuredHeight)
                else -> dyConsumed == 0
            }
            // override dyConsummed as if we were scrolling up.
            val newDyConsumed = if (isWithinEndOfScroll) {
                -1
            } else {
                dyConsumed
            }
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, newDyConsumed, dxUnconsumed,
                dyUnconsumed,
                type, consumed)
        }

        private fun NestedScrollView.getScrollRange(): Int {
            var scrollRange = 0
            if (childCount > 0) {
                val child = getChildAt(0)
                val lp = child.layoutParams as MarginLayoutParams
                val childSize = child.height + lp.topMargin + lp.bottomMargin
                val parentSpace = height - paddingTop - paddingBottom
                scrollRange = Math.max(0, childSize - parentSpace)
            }
            return scrollRange
        }

    }

}
