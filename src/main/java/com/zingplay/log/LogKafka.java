package com.zingplay.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogKafka {
    private static final Logger log = LoggerFactory.getLogger(LogKafka.class);
    public static Logger getInstance(){
        return log;
    }
}
