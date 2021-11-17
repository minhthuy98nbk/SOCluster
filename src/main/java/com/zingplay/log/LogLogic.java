package com.zingplay.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogLogic {
    private static final Logger log = LoggerFactory.getLogger(LogLogic.class);
    public static Logger getInstance(){
        return log;
    }
}
