package com.example.cron.util;

import com.cronutils.model.Cron;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
// minor polish
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.CronType;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Utility class that parses a cron expression and calculates the delay
 * until the next execution.
 */
public final class CronExpressionParser {

    private static final CronParser PARSER = new CronParser(
            CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));

    private CronExpressionParser() {
        // Utility class
    }

    /**
     * Returns the delay in milliseconds from now until the next execution
     * defined by the cron expression.
     *
     * @param cronExpression a valid Quartz cron expression
     * @return delay in milliseconds
     * @throws IllegalArgumentException if the expression cannot be parsed
     */
    public static long getDelayUntilNextExecution(String cronExpression) {
        Cron cron = PARSER.parse(cronExpression);
        cron.validate();

        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        ZonedDateTime now = ZonedDateTime.now();
        Optional<ZonedDateTime> next = executionTime.nextExecution(now);
        if (next.isEmpty()) {
            throw new IllegalArgumentException("Cron expression does not have a next execution time: " + cronExpression);
        }
        Duration duration = Duration.between(now, next.get());
        return duration.toMillis();
    }
}