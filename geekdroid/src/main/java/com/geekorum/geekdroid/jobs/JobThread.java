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
import android.os.Process;
import com.geekorum.geekdroid.utils.ProcessPriority;

/**
 * A thread that execute a job from a {@link ThreadedJobService}
 * @Deprecated Use androidx.work
 */
@Deprecated
public abstract class JobThread extends Thread {
    private final ThreadedJobService jobService;
    private final JobParameters parameters;

    protected JobThread(ThreadedJobService jobService, JobParameters parameters) {
        this.jobService = jobService;
        this.parameters = parameters;
    }

    protected void setProcessPriority(@ProcessPriority int processPriority) {
        Process.setThreadPriority(processPriority);
    }

    protected void completeJob(boolean needReschedule) {
        jobService.completeJob(parameters, needReschedule);
    }

}
