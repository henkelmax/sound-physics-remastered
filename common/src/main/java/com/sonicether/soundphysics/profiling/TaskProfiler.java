package com.sonicether.soundphysics.profiling;

import java.lang.ref.WeakReference;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import com.sonicether.soundphysics.Loggers;

public class TaskProfiler {

    private static final int TASK_RING_BUFFER_SIZE = 100;      // Maximum number of task durations to store in ring buffer
    private static final int TASK_RING_TALLY_SIZE = 100;       // Maximum number of tasks to run before tallying results

    private final String identifier;                           // Identifier of the profiler for logging
    private final Deque<Double> durations;                     // Durations stored in milliseconds for each task
    private final AtomicInteger tally;                         // Total number of profiling tasks finished before report is fetched

    public TaskProfiler(String identifier) {
        this.identifier = identifier;
        this.durations = new ConcurrentLinkedDeque<>();
        this.tally = new AtomicInteger(0);
    }

    public TaskProfilerHandle profile() {
        return new TaskProfilerHandle();
    }

    public void addDuration(double duration) {
        if (durations.size() == TASK_RING_BUFFER_SIZE) {
            durations.poll();
        }

        durations.offer(duration);
        this.tally.incrementAndGet();
    }

    public double getTotalDuration() {
        return durations.stream().mapToDouble(Double::doubleValue).sum();
    }

    public double getAverageDuration() {
        return durations.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    public double getMinDuration() {
        return durations.stream().min(Double::compareTo).orElse(Double.MAX_VALUE);
    }

    public double getMaxDuration() {
        return durations.stream().max(Double::compareTo).orElse(Double.MIN_VALUE);
    }

    public void logResults() {
        Loggers.logProfiling("Profile for task '{}', total: {} ms, average: {} ms, min: {} ms, max: {} ms",
                identifier, getTotalDuration(), getAverageDuration(), getMinDuration(), getMaxDuration());
    }

    public void onTally(Runnable callback) {
        if (this.tally.get() >= TASK_RING_TALLY_SIZE) {
            callback.run();
            this.tally.set(0);
        }
    }

    // Handle

    public class TaskProfilerHandle {
        private final long startTime;
        private double duration;
        private WeakReference<TaskProfiler> owner;

        private TaskProfilerHandle() {
            this.startTime = System.nanoTime();
            this.owner = new WeakReference<>(TaskProfiler.this);
        }

        public void finish() {
            TaskProfiler aggregator = owner.get();

            if (aggregator == null) {
                return;
            }

            long endTime = System.nanoTime();
            this.duration = ((double) (endTime - startTime)) / 1_000_000L;

            aggregator.addDuration(this.duration);
        }

        public double getDuration() {
            return duration;
        }
    }

}
