package com.zingplay.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by thuydtm on 11:22 PM 7/10/2021
 */
public class StatisticalLogger {
    private static final Logger log = LoggerFactory.getLogger(StatisticalLogger.class);
    public static Logger getInstance(){
        return log;
    }
}
