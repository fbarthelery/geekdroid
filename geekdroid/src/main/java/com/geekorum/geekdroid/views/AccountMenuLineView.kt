/*
 * Geekdroid is a utility library for development on the Android
 * Platform.
 *
 * Copyright (C) 2017-2023 by Frederic-Charles Barthelery.
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

import android.accounts.Account
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.geekorum.geekdroid.R

/**
 * A simple view to simulate a spinner to select an Account in a Navigation menu.
 */
@Deprecated("Use a simple composable function")
class AccountMenuLineView
    @JvmOverloads constructor(context: Context,
                              attrs: AttributeSet? = null,
                              defStyleAttr: Int = 0
    ) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var accountText: TextView
    private lateinit var handleView: ImageView

    var isExpanded: Boolean = false
        set(expanded) {
            field = expanded
            refreshDrawableState()
        }

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(context, R.layout.view_account_menu_line, this)
        handleView = findViewById(R.id.handle)
        accountText = findViewById(R.id.account)
    }

    fun setAccount(account: Account) {
        accountText.text = account.name
    }

    public override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isExpanded) {
            View.mergeDrawableStates(drawableState, EXPANDED_STATE_SET)
        }
        return drawableState
    }

    internal class SavedState : BaseSavedState {
        internal var expanded: Boolean = false

        constructor(source: Parcel) : super(source) {
            expanded = source.readValue(javaClass.classLoader) as Boolean
        }

        constructor(superState: Parcelable) : super(superState)

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeValue(expanded)
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {

                override fun createFromParcel(`in`: Parcel): SavedState = SavedState(`in`)

                override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
            }
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = checkNotNull(super.onSaveInstanceState())
        return SavedState(superState).apply {
            expanded = isExpanded
        }
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState

        super.onRestoreInstanceState(ss.superState)
        isExpanded = ss.expanded
        requestLayout()
    }

    companion object {
        private val EXPANDED_STATE_SET = intArrayOf(android.R.attr.state_expanded)
    }


}
