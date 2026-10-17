package com.example.cron.factory;

import com.example.cron.task.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * Factory responsible for creating {@link Job} instances based on configuration.
 */
public final class JobFactory {

    private static final Logger logger = LoggerFactory.getLogger(JobFactory.class);

    private JobFactory() {
        // Utility class
    }

    /**
     * Creates a Job instance.
     *
     * @param className fully qualified class name implementing {@link Job}
     * @param argument  optional single String argument passed to a matching constructor
     * @return a new Job instance
     * @throws RuntimeException if instantiation fails
     */
    public static Job createJob(String className, String argument) {
        try {
            Class<?> clazz = Class.forName(className);
            if (!Job.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException(className + " does not implement Job interface");
            }

            if (argument != null) {
                // Look for a constructor that accepts a single String argument
                Constructor<?> ctor = clazz.getConstructor(String.class);
                return (Job) ctor.newInstance(argument);
            } else {
                // No-arg constructor
                return (Job) clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            logger.error("Failed to create job for class {}: {}", className, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}