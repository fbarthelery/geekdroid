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
package com.geekorum.geekdroid.views.recyclerview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

/**
 * An {@link RecyclerView.ItemAnimator} that only run for the 1st layout.
 */
public abstract class FirstLayoutItemAnimator extends DefaultItemAnimator {

    private final RecyclerView recyclerView;
    private RecyclerView.ItemAnimator nextItemAnimator;

    public FirstLayoutItemAnimator(final RecyclerView recyclerView, final RecyclerView.ItemAnimator nextItemAnimator) {
        this.recyclerView = recyclerView;
        this.nextItemAnimator = nextItemAnimator;
    }

    @Override
    public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        boolean result = shouldAnimateView(viewHolder);
        if (result) {
            result = animateAdd(viewHolder, postLayoutInfo);
        }

        // set listener to change ItemAnimator when finished
        if (result) {
            isRunning(() -> {
                if (recyclerView.getItemAnimator() == FirstLayoutItemAnimator.this) {
                    recyclerView.setItemAnimator(nextItemAnimator);
                }
            });
        } else {
            dispatchAnimationFinished(viewHolder);
        }
        return result;
    }

    protected boolean shouldAnimateView(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    protected abstract boolean animateAdd(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo layoutInfo);
}
