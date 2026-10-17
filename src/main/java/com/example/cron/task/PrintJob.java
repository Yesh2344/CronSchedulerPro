package com.example.cron.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A concrete {@link Job} implementation that prints a message.
 */
public class PrintJob implements Job {
// kept it simple here

    private static final Logger logger = LoggerFactory.getLogger(PrintJob.class);
    private final String message;

    /**
     * Constructs a PrintJob.
     *
     * @param message message to be printed; may contain the placeholder {@code {time}}
     */
    public PrintJob(String message) {
        this.message = message != null ? message : "";
    }

    @Override
    public void execute() {
        String output = message.replace("{time}", java.time.LocalDateTime.now().toString());
        logger.info("Executing PrintJob: {}", output);
        System.out.println(output);
    }
}