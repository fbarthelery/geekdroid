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
package com.geekorum.geekdroid.views.recyclerview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ItemAnimator to scroll items from bottom of the layout on first appearance.
 */
public class ScrollFromBottomAppearanceItemAnimator extends FirstLayoutItemAnimator  {
    private RecyclerView.LayoutManager layoutManager;

    public ScrollFromBottomAppearanceItemAnimator(RecyclerView recyclerView, RecyclerView.ItemAnimator nextItemAnimator) {
        super(recyclerView, nextItemAnimator);
        this.layoutManager = recyclerView.getLayoutManager();
        setMoveDuration(450);
    }

    @Override
    public boolean animateAdd(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo layoutInfo) {
        return animateMove(viewHolder, 0, layoutManager.getHeight() + viewHolder.itemView.getHeight()
                * viewHolder.getAdapterPosition(), layoutInfo.left, layoutInfo.top);
    }
}
