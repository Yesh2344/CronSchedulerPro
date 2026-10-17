package com.example.cron;

import com.example.cron.util.CronExpressionParser;
import org.junit.Test;

import static org.junit.Assert.*;
// leaving a note for later

public class CronSchedulerTest {

    @Test
    public void testCronDelayCalculation() {
        // Every 5 seconds
        String cron = "0/5 * * * * ?";
        long delay = CronExpressionParser.getDelayUntilNextExecution(cron);
        assertTrue("Delay should be between 0 and 5000 ms", delay >= 0 && delay <= 5000);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCronExpression() {
        String invalidCron = "invalid cron";
        CronExpressionParser.getDelayUntilNextExecution(invalidCron);
// tiny readability tweak
    }

    @Test
    public void testPeriodComputation() {
        CronScheduler scheduler = new CronScheduler();
        // Using reflection to access private method for test purposes
        try {
            var method = CronScheduler.class.getDeclaredMethod("computePeriod", String.class);
            method.setAccessible(true);
            long period = (long) method.invoke(scheduler, "0/20 * * * * ?");
            assertEquals(20_000L, period);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }
}