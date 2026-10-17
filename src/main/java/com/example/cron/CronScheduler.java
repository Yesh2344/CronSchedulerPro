package com.example.cron;

import com.example.cron.config.AppConfig;
import com.example.cron.factory.JobFactory;
import com.example.cron.task.Job;
import com.example.cron.util.CronExpressionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * Main entry point for the CronSchedulerPro application.
 */
public class CronScheduler {

    private static final Logger logger = LoggerFactory.getLogger(CronScheduler.class);
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public static void main(String[] args) {
        CronScheduler scheduler = new CronScheduler();
        scheduler.start();
    }

    /**
     * Starts the scheduler by loading configuration and registering jobs.
     */
    public void start() {
        try {
            AppConfig config = AppConfig.getInstance();
            String jobsCsv = config.get("jobs");
            if (jobsCsv == null || jobsCsv.isBlank()) {
                logger.warn("No jobs defined in configuration. Exiting.");
                shutdown();
                return;
            }

            List<String> jobIds = Arrays.asList(jobsCsv.split("\\s*,\\s*"));
            for (String jobId : jobIds) {
                scheduleJob(jobId.trim());
            }

            logger.info("CronScheduler started with {} job(s).", jobIds.size());

            // Add shutdown hook to cleanly stop the executor
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        } catch (Exception e) {
            logger.error("Failed to start CronScheduler: {}", e.getMessage(), e);
            shutdown();
        }
    }

    private void scheduleJob(String jobId) {
        AppConfig config = AppConfig.getInstance();
        String cronExpr = config.get("job." + jobId + ".cron");
        String type = config.getOrDefault("job." + jobId + ".type",
                "com.example.cron.task.PrintJob");
        String message = config.get("job." + jobId + ".message");

        if (cronExpr == null || cronExpr.isBlank()) {
            logger.warn("Job '{}' has no cron expression; skipping.", jobId);
            return;
        }

        Job job = JobFactory.createJob(type, message);
        long initialDelay = CronExpressionParser.getDelayUntilNextExecution(cronExpr);
        long period = computePeriod(cronExpr);

        logger.info("Scheduling job '{}' with cron '{}' (initialDelay={}ms, period={}ms)",
                jobId, cronExpr, initialDelay, period);

        executorService.scheduleAtFixedRate(() -> {
            try {
                job.execute();
            } catch (Exception e) {
                logger.error("Job '{}' execution failed: {}", jobId, e.getMessage(), e);
            }
        }, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * Computes the repeat period in milliseconds based on a cron expression.
     * For simplicity, this implementation assumes a fixed interval (e.g., every N seconds/minutes).
     * A full implementation would calculate the exact next execution each time.
     *
     * @param cronExpression the cron expression
     * @return period in milliseconds
     */
    private long computePeriod(String cronExpression) {
        // Very naive parsing: look for a pattern like "0/15 * * * * ?" meaning every 15 seconds
        // This is sufficient for demonstration purposes.
        try {
            String[] parts = cronExpression.split("\\s+");
            if (parts.length >= 2 && parts[0].contains("/")) {
                String[] step = parts[0].split("/");
                int seconds = Integer.parseInt(step[1]);
                return seconds * 1000L;
            }
        } catch (Exception e) {
            logger.warn("Unable to compute period from cron '{}', defaulting to 60 seconds", cronExpression);
        }
        return 60_000L; // default to 60 seconds
    }

    /**
     * Gracefully shuts down the executor service.
     */
    private void shutdown() {
        logger.info("Shutting down CronScheduler...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warn("Forcing shutdown of executor service.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted during shutdown.", e);
            Thread.currentThread().interrupt();
        }
    }
}