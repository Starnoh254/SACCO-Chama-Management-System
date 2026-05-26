package com.starnoh.sacco_management.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private static final Logger log = LoggerFactory.getLogger(LogUtil.class);

    public static void info(String message){
        log.info(message);
    }

    public static void error(String message ) {
        log.error(message);
    }
}
