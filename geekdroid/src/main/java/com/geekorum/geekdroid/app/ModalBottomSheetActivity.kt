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
package com.geekorum.geekdroid.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.geekorum.geekdroid.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

/**
 * React like a [BottomSheetDialogFragment] but is a separate [Activity].
 * This allows you to launch the bottom sheet easily from another external activity.
 * This implementation use Compose and Material3 [ModalBottomSheet]
 *
 * The activity should use [Theme.Geekdroid.ModalBottomSheetDialogActivity][R.style.Theme_Geekdroid_ModalBottomSheetDialogActivity]
 * theme or equivalent to work correctly.
 */
open class ModalBottomSheetActivity : ComponentActivity() {
    private var _sheetContent: @Composable ColumnScope.() -> Unit by mutableStateOf({})

    private val dismissEventChannel = Channel<Unit>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                dismissEventChannel.consumeEach {
                    sheetState.hide()
                }
            }

            BackHandler(sheetState.targetValue != SheetValue.Hidden) {
                coroutineScope.launch {
                    sheetState.hide()
                }
            }

            // dismiss automatically when hidden
            var wasShown by remember {  mutableStateOf(false) }
            LaunchedEffect(sheetState.isVisible) {
                if (sheetState.isVisible) {
                    wasShown = true
                } else if (wasShown) {
                    finish()
                }
            }


            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {},
                dragHandle = null,
            ) {
                _sheetContent()
            }
        }
    }

    fun setSheetContent(
        content: @Composable ColumnScope.() -> Unit
    ) {
        _sheetContent = content
    }

    fun dismiss() {
        lifecycleScope.launch {
            dismissEventChannel.send(Unit)
        }
    }
}
