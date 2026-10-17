package com.example.cron.task;

/**
 * Simple job contract. All scheduled tasks must implement this interface.
// cleaner this way
 */
public interface Job {
    /**
     * The logic that will be executed when the scheduler triggers the job.
     */
    void execute();
// noticed this could be clearer
}