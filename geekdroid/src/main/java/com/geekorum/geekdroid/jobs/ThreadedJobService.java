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
package com.geekorum.geekdroid.jobs;

import android.app.job.JobParameters;
import android.app.job.JobService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Execute each job of the {@link JobService} in a separate thread.
 * @Deprecated Use androidx.work
 */
@Deprecated
public abstract class ThreadedJobService extends JobService {
    private Map<JobParameters, Future<?>> tasks = Collections.synchronizedMap(new HashMap<JobParameters, Future<?>>());
    private ExecutorService executorService;

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        JobThread jobThread = createJobThread(params);
        if (jobThread == null) {
            return false;
        }
        Future<?> task = executorService.submit(jobThread);
        tasks.put(params, task);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Future<?> task = tasks.remove(params);
        if (task != null) {
            task.cancel(true);
            return true;
        }
        return false;
    }

    void completeJob(JobParameters jobParameters, boolean needReschedule) {
        Future<?> task = tasks.remove(jobParameters);
        if (task != null) {
            jobFinished(jobParameters, needReschedule);
        }
    }

    protected abstract JobThread createJobThread(JobParameters jobParameters);
}
