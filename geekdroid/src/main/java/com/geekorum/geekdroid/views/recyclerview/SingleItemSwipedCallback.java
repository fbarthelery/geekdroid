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

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * An {@link ItemTouchHelper.Callback} that help to apply a {@link RecyclerView.ItemDecoration}
 * on the item swiped.
 */
public abstract class SingleItemSwipedCallback extends ItemTouchHelper.SimpleCallback {

    private RecyclerView.ViewHolder swipingItem;
    private OnSwipingItemListener listener;

    public SingleItemSwipedCallback(int swipeDirs) {
        super(0, swipeDirs);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            swipingItem = viewHolder;
            if (listener != null) {
                listener.onSwipingItem(swipingItem);
            }
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (swipingItem == viewHolder) {
            swipingItem = null;
            if (listener != null) {
                listener.onSwipingItem(null);
            }
        }
    }

    public void setOnSwipingItemListener(OnSwipingItemListener listener) {
        this.listener = listener;
    }

    /**
     * Listener to get the swiping item.
     */
    public interface OnSwipingItemListener {
        void onSwipingItem(@Nullable RecyclerView.ViewHolder item);
    }

}
