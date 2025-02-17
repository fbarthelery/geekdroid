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
package com.geekorum.geekdroid.views.behaviors;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import android.view.animation.DecelerateInterpolator;

/**
 * Simple animator that make items scroll from the bottom of the RecyclerView.
 */
public class SwingBottomItemAnimator extends DefaultItemAnimator {

    private RecyclerView recyclerView;

    public SwingBottomItemAnimator(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        runEnterAnimation(holder);
        return true;
    }

    private void runEnterAnimation(final RecyclerView.ViewHolder holder) {
        final int containerHeight =  recyclerView.getHeight();
        holder.itemView.setTranslationY(containerHeight);
        holder.itemView.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dispatchAddFinished(holder);
                    }
                })
                .start();
    }

}
