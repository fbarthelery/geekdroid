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
package com.geekorum.geekdroid.app

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.use
import com.geekorum.geekdroid.databinding.ActivityBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * React like a [android.support.design.widget.BottomSheetDialogFragment] but is a separate [Activity].
 * This allows you to launch the bottom sheet easily from another external activity.
 */
abstract class BottomSheetDialogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomSheetDialogBinding
    private lateinit var behavior: BottomSheetBehavior<FrameLayout>
    private val callbackDelegator = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            bottomSheetCallback?.onSlide(bottomSheet, slideOffset)
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                finish()
            }
            bottomSheetCallback?.onStateChanged(bottomSheet, newState)
        }
    }

    var bottomSheetCallback: BottomSheetBehavior.BottomSheetCallback? = null

    var cancelable = true
        set(value) {
            field = value
            behavior.isHideable = value
        }

    private var cancelOnTouchOutside = true
        set(value) {
            canceledOnTouchOutsideSet = true
            field = value
        }
    private var canceledOnTouchOutsideSet = false


    override fun onCreate(savedInstanceState: Bundle?) {
        if (theme == null) {
            // TODO force a the default theme or crash if incorrect theme applied ?
            // setup our default theme
//            setTheme(R.style.Theme_Geekdroid_Light_BottomSheetDialogActivity)
        }
        super.onCreate(savedInstanceState)
        initializeBottomSheet()

        window.decorView // force initialize the window

        // only set if the window is non floating, but we have a floating window
        // so we need to set it manually
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)

    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(wrapInBottomSheet(layoutResID, null, null))
    }

    override fun setContentView(view: View?) {
        super.setContentView(wrapInBottomSheet(0, view, null))
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(wrapInBottomSheet(0, view, params))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeBottomSheet() {
        binding = ActivityBottomSheetDialogBinding.inflate(layoutInflater, null, false)
        behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.setBottomSheetCallback(callbackDelegator)
        behavior.isHideable = cancelable
        // We treat the CoordinatorLayout as outside the dialog though it is technically inside
        binding.touchOutside.setOnClickListener {
            if (cancelable && cancelOnTouchOutside) {
                finish()
            }
        }

        // Handle accessibility events
        ViewCompat.setAccessibilityDelegate(binding.bottomSheet, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                host: View,
                info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(host, info)
                if (cancelable) {
                    info.addAction(AccessibilityNodeInfoCompat.ACTION_DISMISS)
                    info.isDismissable = true
                } else {
                    info.isDismissable = false
                }
            }

            override fun performAccessibilityAction(host: View, action: Int, args: Bundle): Boolean {
                if (action == AccessibilityNodeInfoCompat.ACTION_DISMISS && cancelable) {
                    finish()
                    return true
                }
                return super.performAccessibilityAction(host, action, args)
            }
        })

        binding.bottomSheet.setOnTouchListener { _, _ ->
            // Consume the event and prevent it from falling through
            true
        }
    }


    private fun wrapInBottomSheet(layoutResId: Int, resourceView: View?, params: ViewGroup.LayoutParams?): View {
        var view = resourceView
        if (layoutResId != 0 && view == null) {
            view = layoutInflater.inflate(layoutResId, binding.coordinator, false)
        }
        if (params == null) {
            binding.bottomSheet.addView(view)
        } else {
            binding.bottomSheet.addView(view, params)
        }
        return binding.root
    }

    fun shouldFinishOnTouchOutside(): Boolean {
        if (!canceledOnTouchOutsideSet) {
            val a = obtainStyledAttributes(
                intArrayOf(android.R.attr.windowCloseOnTouchOutside)).use {
                cancelOnTouchOutside = it.getBoolean(0, true)
            }
            canceledOnTouchOutsideSet = true
        }
        return cancelOnTouchOutside
    }

    override fun setFinishOnTouchOutside(finish: Boolean) {
        super.setFinishOnTouchOutside(finish)
        cancelOnTouchOutside = finish
    }

}
