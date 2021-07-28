package com.yilnz.selenium.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timer {
    private static final Logger logger = LoggerFactory.getLogger(Timer.class);
    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("[surfing-selenium]timer intercepted error", e);
        }
    }
}
