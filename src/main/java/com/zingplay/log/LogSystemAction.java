package com.zingplay.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogSystemAction {
    private static final Logger log = LoggerFactory.getLogger(LogSystemAction.class);
    public static Logger getInstance(){
        return log;
    }
}
